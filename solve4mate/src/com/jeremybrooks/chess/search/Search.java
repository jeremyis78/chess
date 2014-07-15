/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess.search;

import static com.jeremybrooks.chess.base.Bitmap.WHITE;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.UciDriver;
import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.eval.Evaluator;
import com.jeremybrooks.chess.movegen.AbstractGenerator;
import com.jeremybrooks.chess.movegen.DefaultGenerator;
import com.jeremybrooks.chess.search.ScoredMove.Precision;
import com.jeremybrooks.chess.util.Util;

/**
 * @author jeremy
 *
 */
public class Search {

    private static final Logger log = Logger.getLogger(Search.class);
    private static transient final boolean isTrace = log.isTraceEnabled();
    private static transient final boolean showPvUpdates = false; //logs principle variation changes

    public static final boolean SEARCH_DEBUG = false; //SEARCH_DB 
    public static final boolean DEBUG = false; //DB
    public static final boolean EVAL = false;
    public static final int MAXWINDOW = 1000000;   //To pass to alpha beta for initial window (+inf, -inf)
    public static final int CHECKMATE = 100000;    //value for checkmate
    public static final int DRAW = 0;              //value for draw

    private DefaultGenerator moveGenerator = new DefaultGenerator();
    private Evaluator evaluator = new Evaluator();
    protected SearchParams params;
    protected TimeMgmt timer;
        
    /*
     * Current state of the chess game 
     */
    protected GameState g;
    
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
    protected ScoredMove[] pvLine;
    
    /*
     * Holds the current moves at this depth in the search.
     * If depth is 0, it holds all moves from that position
     * If depth is 1, it holds all moves available after an initial first move
     * and so on.
     */
    private ScoredMove[] rootMove;    
    
    /*
     * Holds results of the search
     */
    private SearchInfo info;
    
    /*
     * Time we started searching
     */
    protected int startTime;


    public Search() {
        super();
    }
    
    public Search(TimeMgmt timer)
    {
        super();
        this.timer = timer;
    }
    
    public Search(int stackSize) {
        super();
        setStackSize(stackSize);
    }

    public long getNodeCount() {
        return nodeCount;
    }

    public GameState getGameState() {
        return g;
    }

    public void setGameState(GameState gameState) {
        this.g = gameState;
    }

    public int getStackSize()
    {
        return stackSize;
    }
    
    public void setStackSize(int size) {
        this.stackSize = size;
        pvLine = new ScoredMove[this.stackSize];
        currentMove = new int[this.stackSize];
        rootMove = new ScoredMove[AbstractGenerator.MAX_NUM_GENERATED_MOVES];
    }
    
    public int getDepthLimit() {
        return depthLimit;
    }

    public void setDepthLimit(int depthLimit)
    {
        this.depthLimit = depthLimit;
    }
    
    public void setMoveGenerator(DefaultGenerator defaultGenerator) {
        this.moveGenerator = defaultGenerator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }
    
    public void setParams(SearchParams params) {
        this.params = params;
    }

    public void setTimer(TimeMgmt timer) {
        this.timer = timer;
    }

    public ScoredMove[] getRootMove() {
        return rootMove;
    }

    public void setRootMove(ScoredMove[] rootMove) {
        this.rootMove = rootMove;
    }
    
    private String getScoredRootMoves() {
        StringBuilder sb = new StringBuilder("\n");
        for(ScoredMove sm: rootMove)
        {
            if(sm == null) break;
            sb.append(Util.displayMoveStr(sm.getMove(),false,false));
            sb.append("("+sm.getScore()+")\n");
        }
        return sb.toString();
    }

    public String getPVMoveLine()
    {
        StringBuilder line = new StringBuilder();
        for(int nodeIndex = 0;
                nodeIndex < depthLimit;
                nodeIndex++)
        {
            ScoredMove node = pvLine[nodeIndex];
            line.append(Util.displayMoveStr(node.getMove(), false, false));
            line.append(" ");
        }
        return line.toString();
    }

    public ScoredMove[] getPVLine()
    {
        return pvLine;
    }
    

    /**
     * @return the results of the search
     */
    public SearchInfo getInfo() {
        return info;
    }

    /**
     * Search for the minimax value of the given GameState.
     * @param side  side to move
     * 
     * @return the minimax value of the search
     */
    public int search(int side){
        return search(side, stackSize);
    }

    /**
     * Search, up to the depth limit, for the minimax value of the given GameState
     * @param side  side to move
     * @param depthLimit  stop the search when this depth is reached
     *
     * @return the minimax value of the search
     */
    public int search(int side, int depthLimit){
        setDepthLimit(depthLimit);
        timer.setParams(params);
        startTime = Util.milliTime();
        int minimax = alphabetaMaxWindow(side);
        int elapsedTimeMillis = (Util.milliTime() - startTime);
        boolean mate = Math.abs(minimax) > Evaluator.CHECKMATE / 2;
        String solutionMoves = getPVMoveLine();
        String scoredRootMoves = getScoredRootMoves();
        info = new SearchInfo();
        info.setScore(minimax);
        info.setSolutionMoves(solutionMoves);
        info.setBestLine(getPVLine());
        info.setMate(mate);
        info.setNodeCount(getNodeCount());
        info.setElapsedTime(elapsedTimeMillis);
        info.setScoredRootMoves(scoredRootMoves);
        return minimax;
    }

    /**
     * Performs a full-width alpha-beta search.
     * 
     * A full window search means:
     * alpha = -Infinity
     * beta = +Infinity
     * @param side  side to move
     * 
     * @return the minimax value of the search
     */
    protected int alphabetaMaxWindow(int side) {
        return alphabeta(side, -MAXWINDOW, +MAXWINDOW);
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
     * @param side  side to move
     * @param alpha  represents the lower bound of the target window
     * @param beta  represents the upper bound of the target window
     * 
     * @return the minimax value of the search
     */
    protected int alphabeta(int side, int alpha, int beta){
        int minimaxValue;
        int depth = 0;
        
        //Now reset the legal moves to zero so everything
        //works correctly for the search.
        g.numberOfLegalMoves[depth] = 0;
        moveGenerator.setGameState(g); //FIXME: works for now but needs fixing (F1): gross!
        if(side == Bitmap.WHITE){
          minimaxValue = max(alpha, beta, side, depth);
        } else {
          minimaxValue = min(alpha, beta, side, depth);
        }
        return minimaxValue;
    }


    // max  - returns max value from state 'g' 
    //         
    //
    // alpha - value of best alternative for MAX along path to state 'g'
    // beta  - value of best alternative for MIN along path to state 'g'
    //
    //    int alphaBetaMax( int alpha, int beta, int depthleft ) {
    //           if ( depthleft == 0 ) return evaluate();
    //           for ( all moves) {
    //              score = alphaBetaMin( alpha, beta, depthleft - 1 );
    //              if( score >= beta )
    //                 return beta;   // fail hard beta-cutoff
    //              if( score > alpha )
    //                 alpha = score; // alpha acts like max in MiniMax
    //           }
    //           return alpha;
    //        }
    protected int max(int alpha, int beta, int side, int depth){
        long nodeKey = g.fullZobristKey();
        Precision nodePrecision = Precision.LOWER_BOUND;
        ScoredMove node = lookup(nodeKey);
        if(isCacheHit(node, alpha, beta, depth))
        {
            int score = cachedScore(node, alpha, beta, depth);
            if(log.isDebugEnabled()) log.debug("cache hit (score="+score+") on "+g.get());
            return score;
        }

        int[] moves = generateMoves(side, depth);
        if(isMaxDepthOrHasNoMoves(depth, moves.length)){
            return evaluate(side, depth);
        }
        int numMoves = g.numberOfLegalMoves[depth];
        if(isTrace)
            log.trace("num moves at depth " + depth + ": "+numMoves);
        for(int i=0; i<numMoves /* && timer.hasTimeLeft(side, startTime, params)*/; i++){
            nodeCount++;
            int move = moves[i];
            if(depth == 0)
            {
                String moveStr = Util.displayMoveStr(move, false, false);
                if(isTrace) log.trace("checking " + moveStr + " ("+(i+1)+" of "+(numMoves)+")");
            }
            currentMove[depth] = move;
            g.makeMove(move, side==WHITE);
            int val = min(alpha,beta,Util.opposing(side),depth+1); //recurse!
            g.undoMove(move, side==WHITE);
            if(depth==0){
                g.moves[i] = move;
                g.movesValue[i] = val;
            }
            if(isTrace) 
                logMove(depth, move, val, alpha, beta);
            if (val >= beta){
                if(isTrace){
                    log.trace(indent(depth) + "@" + depth + " " + val + ">=" + beta + 
                            " prunes sub-graph (fail hard beta-cutoff) at move " + Util.displayMoveStr(move, false, false));
                }
                store(nodeKey, beta, Precision.UPPER_BOUND, depth);
                return beta;
            }
            if(val > alpha)
            {    
                alpha = val; // alpha acts like max in MiniMax  (shrinks the window up from -Infinity)
                nodePrecision = Precision.EXACT;
                updatePrincipalVariationLine(g, depth, val, move);
            }
        }
        store(nodeKey, alpha, nodePrecision, depth);
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
    //    int alphaBetaMin( int alpha, int beta, int depthleft ) {
    //           if ( depthleft == 0 ) return -evaluate();
    //           for ( all moves) {
    //              score = alphaBetaMax( alpha, beta, depthleft - 1 );
    //              if( score <= alpha )
    //                 return alpha; // fail hard alpha-cutoff
    //              if( score < beta )
    //                 beta = score; // beta acts like min in MiniMax
    //           }
    //           return beta;
    //        }
    protected int min(int alpha, int beta, int side, int depth){
        long nodeKey = g.fullZobristKey();
        Precision nodePrecision = Precision.UPPER_BOUND;
        ScoredMove node = lookup(nodeKey);
        if(isCacheHit(node, alpha, beta, depth))
        {
            int score = cachedScore(node, alpha, beta, depth);
            if(log.isDebugEnabled()) log.debug("cache hit (score="+score+") on "+g.get());
            return score;
        }

        int[] moves = generateMoves(side, depth);
        if(isMaxDepthOrHasNoMoves(depth, moves.length)){
            return evaluate(side, depth);
        }
        int numMoves = g.numberOfLegalMoves[depth];
        if(isTrace)
            log.trace("num moves at depth " + depth + ": "+numMoves);
        for(int i=0; i<numMoves /*&& timer.hasTimeLeft(side, startTime, params)*/; i++){
            nodeCount++;
            int move = moves[i];
            if(depth == 0)
            {
                String moveStr = Util.displayMoveStr(move, false, false);
                if(isTrace) log.trace("checking " + moveStr + " ("+(i+1)+" of "+(numMoves)+")");
            }
            currentMove[depth] = move;
            g.makeMove(move, side==WHITE);
            int val = max(alpha,beta,Util.opposing(side),depth+1);  //recurse!
            g.undoMove(move, side==WHITE);
            if(depth==0){
                g.moves[i] = move;
                g.movesValue[i] = val;
            }
            if(isTrace) 
                logMove(depth, move, val, alpha, beta);
            if (val <= alpha){   //Found a cutoff
                if(isTrace){
                    log.trace(indent(depth) + "@" + depth + " " + val + "<=" + alpha + 
                        " prunes sub-graph (fail hard alpha-cutoff) at move " + Util.displayMoveStr(move, false, false));
                }
                store(nodeKey, alpha, Precision.LOWER_BOUND, depth);
                return alpha;
            }
            if(val < beta)
            {   
                beta = val; // beta acts like min in MiniMax (shrinks the window down from +Infinity)
                nodePrecision = Precision.EXACT;
                updatePrincipalVariationLine(g, depth, val, move);
            }
        }
        store(nodeKey, beta, nodePrecision, depth);
        return beta;
    }

    protected boolean isMaxDepthOrHasNoMoves(int depth, int numMoves) {
        boolean isMaxDepth = (depth == depthLimit);
//        if(isMaxDepth) log.debug("max depth reached: " + depth);
        return isMaxDepth || numMoves == 0;
    }

    protected int[] generateMoves(int side, int depth) {
        // Generate legal moves from this position
        int[] moves = new int[AbstractGenerator.MAX_NUM_GENERATED_MOVES]; //how many moves are there actually? fails with 50
        if (!moveGenerator.isAttacked(g, side, g.getPosition().getKingSquare(side))){
            moveGenerator.generateCaptures(moves, side, depth);
            moveGenerator.generateNonCaptures(moves, side, depth);
        } else {
            moveGenerator.generateKingEscapes(moves, side, depth);
        }
        return moves;
    }

    //
    // Returns an evaluation score for the side to move 'side'
    // A more positive number is better for white.
    // A more negative number is better for black.
    int evaluate(int side, int depth){
        return evaluator.evaluate(g, side, depth, currentMove, SEARCH_DEBUG, EVAL);
    }
    
    protected ScoredMove lookup(long key) {
        //no cache by default;
        return null; 
    }

    protected void store(Long key, int score, ScoredMove.Precision metadata, int depth) {
        //no cache by default
    }

    protected boolean isCacheHit(ScoredMove node, int alpha, int beta, int depth) {
        //no cache by default;
        return false;
    }

    protected Integer cachedScore(ScoredMove node, int alpha, int beta, int depth)
    {
        //no cache by default;
        return null;
    }

    protected void logMove(int depth, int move, int moveScore, int alpha, int beta) {
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
