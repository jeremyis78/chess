/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.WHITE;

import org.apache.log4j.Logger;

/**
 * @author jeremy
 *
 */
public class Search {

	private static final Logger log = Logger.getLogger(Search.class);
	private static transient final boolean isDebug = log.isDebugEnabled();
	private static transient final boolean isTrace = log.isTraceEnabled();
	private static transient final boolean showPvUpdates = false; //logs principle variation changes

	public static final boolean SEARCH_DEBUG = false; //SEARCH_DB 
	public static final boolean DEBUG = false; //DB
	public static final boolean EVAL = false;
	public static final int MAXWINDOW = 1000000;   //To pass to alpha beta for initial window (+inf, -inf)
	public static final int CHECKMATE = 100000;    //value for checkmate
	public static final int DRAW = 0;              //value for draw
	private static final int MAX_NUM_GENERATED_MOVES = 70;

	private MoveGenerator moveGenerator = new MoveGenerator();
	private Evaluator evaluator = new Evaluator();

	/* 
	 * Initialize search's stack depth to this value
	 */
	private int stackSize = 0;
	
	/*
	 * Stop the search (ie, call eval()) when the depth reaches this limit 
	 */
	private int depthLimit = 0;
	
	/*
	 * The number of nodes expanded during the search (doesn't include leaf nodes currently) 
	 */
	private long nodeCount;
	
	/*
	 * The line of play currently being searched, indexed by depth 
	 * (currentMove[0]=firstMove, currentMove[1]=secondMove, etc) 
	 */
	private int[] currentMove;

	/*
	 * Holds the best moves found (that is, moves on the principle variation) in ascending order of depth
	 */
	private ScoredMove[] pvLine;
	
	/*
	 * Holds the current moves at this depth in the search.
	 * If depth is 0, it holds all moves from that position
	 * If depth is 1, it holds all moves available after an initial first move
	 * and so on.
	 */
	private ScoredMove[] rootMove;
	
	
	/**
	 * Holds a move and score for that move after a search
	 * @author jeremy
	 *
	 */
	public class ScoredMove {
		private int move;
		private int score;

		public ScoredMove(int move, int score) {
			super();
			this.move = move;
			this.score = score;
		}
		public int getMove() { return move; }
		public void setMove(int move) { this.move = move; }
		public int getScore() {	return score; }
		public void setScore(int score) { this.score = score; }
		public String toString(){
			return Util.displayMoveStr(move, false, false);// + "("+score+")";
		}
		public String format(){
			return Util.displayMoveStr(move, false, false) + "("+score+")";
		}
	}

	public Search() {
		super();
	}
	
	public Search(int stackSize) {
		super();
		setStackSize(stackSize);
	}
	
	public long getNodeCount() {
		return nodeCount;
	}

	public int getStackSize()
	{
		return stackSize;
	}
	
	public void setStackSize(int size) {
		this.stackSize = size;
		pvLine = new ScoredMove[this.stackSize];
		currentMove = new int[this.stackSize];
		rootMove = new ScoredMove[MAX_NUM_GENERATED_MOVES];
	}
	
	public int getDepthLimit() {
		return depthLimit;
	}

	public void setDepthLimit(int depthLimit)
	{
		this.depthLimit = depthLimit;
	}
	
	public void setMoveGenerator(MoveGenerator moveGenerator) {
		this.moveGenerator = moveGenerator;
	}

	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	public ScoredMove[] getRootMove() {
		return rootMove;
	}

	public void setRootMove(ScoredMove[] rootMove) {
		this.rootMove = rootMove;
	}

	public String getPVMoveLine()
	{
		StringBuilder line = new StringBuilder();
		for(int nodeIndex = 0;
				nodeIndex < depthLimit;
				nodeIndex++)
		{
			ScoredMove node = pvLine[nodeIndex];
			line.append(node).append(" ");
		}
		return line.toString();
	}

	/**
	 * Search for the minimax value of the given GameState.
	 * 
	 * @param g  state of the chess game
	 * @param side  side to move
	 * @return the minimax value of the search
	 */
	public int search(GameState g, int side){
	    return search(g, side, stackSize);
	}

	/**
	 * Search, up to the depth limit, for the minimax value of the given GameState
     *
	 * @param g  state of the chess game
	 * @param side  side to move
	 * @param depthLimit  stop the search when this depth is reached
	 * @return the minimax value of the search
	 */
	public int search(GameState g, int side, int depthLimit){
		setDepthLimit(depthLimit);
	    return alphabetaMaxWindow(g, side);
	}

	/**
	 * Performs a full-width alpha-beta search.
	 * 
	 * A full window search means:
	 * alpha = -Infinity
	 * beta = +Infinity
	 * 
	 * @param g  state of the chess game
	 * @param side  side to move
	 * @return the minimax value of the search
	 */
	protected int alphabetaMaxWindow(GameState g, int side) {
		return alphabeta(g, side, -MAXWINDOW, +MAXWINDOW);
	}

	/**
	 * <p>Performs an alpha-beta search.
	 * 
	 * <p>Where the goal is to find the exact value of the search between the
	 * lower bound (alpha) and the upper bound (beta). Therefore, calling alpha-beta has
	 * three possible outcomes: 
	 * The returned value will represent
	 *    <ul>
	 *    <li>SUCCESS: when the value is between alpha and beta
	 *    
	 *    <li>FAIL LOW: when the value is less than alpha (alpha is returned)
	 *    
	 *    <li>FAIL HIGH: when the value is greater than beta (beta is returned)
	 *    </ul>
	 * 
	 * @param g  state of the chess game
	 * @param side  side to move
	 * @param alpha  represents the lower bound of the target window
	 * @param beta  represents the upper bound of the target window
	 * @return the minimax value of the search
	 */
	protected int alphabeta(GameState g, int side, int alpha, int beta){
	    int minimaxValue;

	    int depth = 0;
	    //Now reset the legal moves to zero so everything
	    //works correctly for the search.
	    g.numberOfLegalMoves[depth] = 0;
	    if(side == Bitmap.WHITE){
	      minimaxValue = max(g, alpha, beta, side, depth);
	    } else {
	      minimaxValue = min(g, alpha, beta, side, depth);
	    }
	    return minimaxValue;
	}


	// max  - returns max value from state 'g' 
	//	     
	//
	// alpha - value of best alternative for MAX along path to state 'g'
	// beta  - value of best alternative for MIN along path to state 'g'
	//
	//	int alphaBetaMax( int alpha, int beta, int depthleft ) {
	//		   if ( depthleft == 0 ) return evaluate();
	//		   for ( all moves) {
	//		      score = alphaBetaMin( alpha, beta, depthleft - 1 );
	//		      if( score >= beta )
	//		         return beta;   // fail hard beta-cutoff
	//		      if( score > alpha )
	//		         alpha = score; // alpha acts like max in MiniMax
	//		   }
	//		   return alpha;
	//		}
	private int max(GameState g, int alpha, int beta, int side, int depth){
	    int[] moves = generateMoves(g, side, depth);
	    if(isMaxDepthOrHasNoMoves(depth, moves.length)){
	    	return evaluate(g, side, depth);
	    }
	    int numMoves = g.numberOfLegalMoves[depth];
	    if(isTrace)
	    	log.trace("num moves at depth " + depth + ": "+numMoves);
		for(int i=0; i<numMoves; i++){
	        nodeCount++;
	        int move = moves[i];
			if(depth == 0)
			{
				String moveStr = Util.displayMoveStr(move, false, false);
				if(isTrace) log.trace("checking " + moveStr + " ("+(i+1)+" of "+(numMoves)+")");
			}
	        currentMove[depth] = move;
	        g.makeMove(move, side==WHITE);
	        int val = min(g,alpha,beta,Util.opposing(side),depth+1); //recurse!
	        g.undoMove(move, side==WHITE);
	        if(depth==0){
	        	g.moves[i] = move;
	        	g.movesValue[i] = val;
	        }
			if(isTrace) 
				logMove(g, depth, move, val, alpha, beta);
	        if (val >= beta){
	        	if(isTrace){
	        		log.trace(indent(depth) + "@" + depth + " " + val + ">=" + beta + 
	        				" prunes sub-graph (fail hard beta-cutoff) at move " + Util.displayMoveStr(move, false, false));
	        	}
	        	return beta;
	        }
	        if(val > alpha)
	        {    
	        	alpha = val; // alpha acts like max in MiniMax  (shrinks the window up from -Infinity)
	        	updatePrincipalVariationLine(g, depth, val, move);
	        }
	    }
	    return alpha;
	}

	protected void updatePrincipalVariationLine(GameState g, int depth, int score, int move) {
		ScoredMove existingNode = pvLine[depth];
		for(int i=0; i<=depth; i++)
		{
			int currentLineMove = currentMove[i]; //next move to copy to PV
			ScoredMove newNode = new ScoredMove(currentLineMove, score);
			if(showPvUpdates)
			{
				if(existingNode != null)
				{
					log.debug("@"+i+" replacing PV node "+existingNode+" with "+newNode);
				} else {
					log.debug("@"+i+" adding PV node "+newNode);
				}
			}
			pvLine[i] = newNode;
		}
	}

	// min   - returns minimum value from state 'g'
	//
	// alpha - value of best alternative for MAX along path to state 'g'
	// beta  - value of best alternative for MIN along path to state 'g'
	//
	//	int alphaBetaMin( int alpha, int beta, int depthleft ) {
	//		   if ( depthleft == 0 ) return -evaluate();
	//		   for ( all moves) {
	//		      score = alphaBetaMax( alpha, beta, depthleft - 1 );
	//		      if( score <= alpha )
	//		         return alpha; // fail hard alpha-cutoff
	//		      if( score < beta )
	//		         beta = score; // beta acts like min in MiniMax
	//		   }
	//		   return beta;
	//		}
	private int min(GameState g, int alpha, int beta, int side, int depth){
		int[] moves = generateMoves(g, side, depth);
		if(isMaxDepthOrHasNoMoves(depth, moves.length)){
			return evaluate(g, side, depth);
		}
	    int numMoves = g.numberOfLegalMoves[depth];
	    if(isTrace)
	    	log.trace("num moves at depth " + depth + ": "+numMoves);
		for(int i=0; i<numMoves; i++){
			nodeCount++;
			int move = moves[i];
			if(depth == 0)
			{
				String moveStr = Util.displayMoveStr(move, false, false);
				if(isTrace) log.trace("checking " + moveStr + " ("+(i+1)+" of "+(numMoves)+")");
			}
			currentMove[depth] = move;
			g.makeMove(move, side==WHITE);
			int val = max(g,alpha,beta,Util.opposing(side), depth+1);  //recurse!
			g.undoMove(move, side==WHITE);
			if(depth==0){
				g.moves[i] = move;
				g.movesValue[i] = val;
			}
			if(isTrace) 
				logMove(g, depth, move, val, alpha, beta);
			if (val <= alpha){   //Found a cutoff
	        	if(isTrace){
	        		log.trace(indent(depth) + "@" + depth + " " + val + "<=" + alpha + 
        				" prunes sub-graph (fail hard alpha-cutoff) at move " + Util.displayMoveStr(move, false, false));
	        	}
				return alpha;
			}
	        if(val < beta)
	        {   
	        	beta = val; // beta acts like min in MiniMax (shrinks the window down from +Infinity)
	        	updatePrincipalVariationLine(g, depth, val, move);
	        }
		}
		return beta;
	}

	protected boolean isMaxDepthOrHasNoMoves(int depth, int numMoves) {
		boolean isMaxDepth = (depth == depthLimit);
//		if(isMaxDepth) log.debug("max depth reached: " + depth);
		return isMaxDepth || numMoves == 0;
	}

	protected int[] generateMoves(GameState g, int side, int depth) {
		// Generate legal moves from this position
		int[] moves = new int[MAX_NUM_GENERATED_MOVES]; //how many moves are there actually? fails with 50
	    if (!moveGenerator.isAttacked(g, side, g.getPosition().getKingSquare(side))){
	        moveGenerator.GenerateCaptures(g, moves, side, depth);
	        moveGenerator.GenerateNonCaptures(g, moves, side, depth);
	    } else {
	        moveGenerator.GenerateKingEscapes(g, moves, side, depth);
	    }
	    return moves;
	}

	//
	// Returns an evaluation score for the side to move 'side'
	// A more positive number is better for white.
	// A more negative number is better for black.
	int evaluate(GameState g, int side, int depth){
		return evaluator.evaluate(g, side, depth, currentMove, SEARCH_DEBUG, EVAL);
	}

	protected void logMove(GameState g, int depth, int move, int moveScore, int alpha, int beta) {
			String line = "";
			for(int j=0; j<depth; j++){
				line += Util.displayMoveStr(currentMove[j], false, false)+ " ";
			}

		        log.trace(indent(depth) + "@" + depth + " " + line + " " + 
		        		Util.displayMoveStr(move, false, false) + 
		        		String.format("  score=%d  alpha=%d  beta=%d", moveScore, alpha, beta));
	}

	private static String indent(int d){
	  String indent = "";
	  for (int i=0;i<d;i++)
		indent += "  ";
	  return indent;
	}
	
}
