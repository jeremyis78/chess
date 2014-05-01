/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.WHITE;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author jeremy
 *
 */
public class Search {

	private static final Logger log = Logger.getLogger(Search.class);
	/*******************************************************************/
	/*	                       alphabeta.cpp                       */
	/*******************************************************************/
	/*                                                                 */
	/*  This file contains the search algorithm (alphabeta).           */
	/*******************************************************************/

	public static final boolean SEARCH_DEBUG = false; //SEARCH_DB 
	public static final boolean DEBUG = false; //DB
	public static final boolean EVAL = false;
	public static final int MAXWINDOW = 1000000;   //To pass to alpha beta for initial window (+inf, -inf)
	public static final int CHECKMATE = 100000;    //value for checkmate
	public static final int DRAW = 0;              //value for draw

	MoveGenerator mg = new MoveGenerator();
	Evaluator eval = new Evaluator();
	private int maxSearchDepth = 0;
	
	/**
	 * Maps (depth -> a move on the principle variation)
	 * Iterating over this in increasing order of key 0-maxDepth will yield the best line found.
	 */
	private Map<Integer,PVNode> pvNodeMap = new HashMap<>();
	
	/**
	 * Holds a move and score (hopefully) on the principal variation (best line found)
	 * @author jeremy
	 *
	 */
	public class PVNode {
		private int move;
		private int score;

		public PVNode(int move, int score) {
			super();
			this.move = move;
			this.score = score;
		}
		public int getMove() { return move; }
		public void setMove(int move) { this.move = move; }
		public int getScore() {	return score; }
		public void setScore(int score) { this.score = score; }
		public String toString(){
			return Util.displayMoveStr(move, false, false) + "("+score+")";
		}
	}

	public void reset(){
		pvNodeMap.clear();
	}
	
	public int getMaxSearchDepth() {
		return maxSearchDepth;
	}

	public void setMaxSearchDepth(int maxSearchDepth) {
		this.maxSearchDepth = maxSearchDepth;
	}
	
	public String getPVMoveLine()
	{
		StringBuilder line = new StringBuilder();
		for(int nodeIndex = 0;
				nodeIndex < maxSearchDepth;
				nodeIndex++)
		{
			PVNode node = pvNodeMap.get(nodeIndex);
			line.append(node).append(" ");
		}
		return line.toString();
	}

	int getBestMove(GameState g, int side){
	    int best = 0;
	    eval.setMoveGenerator(mg);
	    //TODO
	    //Run alphabeta with Iterative Deepening
	    //by calling it with larger and larger values for g.searchDepth
	    // Do Iterative Deepening up to depth 5 
	    // That means solve4mate will be able to find mates in 3 at most.
	    // W moves, B moves, W moves, B moves, W moves and mates
	    //
	    //
	    //
//	    for(maxSearchDepth = 1; maxSearchDepth<5; maxSearchDepth++){
	      best = alphabeta(g, side);
//	      if (g.numberOfLinesToMate > 0)
//	    	  break;
//	    }

	    //Find the best move by its minimax value
	    int bestMove = 0;
	    for(int i=0; i<g.numberOfLegalMoves[0]; i++){
	        if(g.movesValue[i] == best){
	            bestMove = g.moves[i];
	        }
	    } 
	    return bestMove;
	}


	// alphabeta  - returns minimax value for state 'g'
	//
	// g     - state of the chess game
	// side  - side to move (0 or 1)
	//
	int alphabeta(GameState g, int side){
	    int best;

	    // Generate the initial moves from state 'g'
	    // Note: g.moves is being filled with moves here
	    int depth = 0;
	    //Now reset the legal moves to zero so everything
	    //works correctly for the search.
	    g.numberOfLegalMoves[depth] = 0;


	    if(side == Bitmap.WHITE){
	      best = max(g, -MAXWINDOW, +MAXWINDOW, side, 0);
	    } else {
	      best = min(g, -MAXWINDOW, +MAXWINDOW, side, 0);
	    }
	    g.best = best;
	    //displayMove(g.currentLine[0], 0, 0); cout << endl;
	    return best;
	}


	// max  - returns max value from state 'g' 
//	     
	//
	// alpha - value of best alternative for MAX along path to state 'g'
	// beta  - value of best alternative for MIN along path to state 'g'
	int max(GameState g, int alpha, int beta, int side, int depth){
	    int best;
	    int val;
	    int[] moves = generateMoves(g, side, depth);
	    if(isMaxDepthOrHasNoMoves(depth, moves.length)){
	    	return evaluate(g, side, depth);
	    }
	    best = -MAXWINDOW;
//	    log.debug("moves: " + g.numberOfLegalMoves[depth]);
	    for(int i=0; i<g.numberOfLegalMoves[depth]; i++){
	        g.nodes++;
	        int move = moves[i];
	        g.currentLine[depth] = move;
//	        logMove(g, depth, move, i, best, alpha, beta);
	        g.makeMove(move, side==WHITE);
	        val = min(g,-beta,-alpha,Util.opposing(side),depth+1); //recurse!
	        g.undoMove(move, side==WHITE);
	        best = Math.max(best, val);
	        if(depth==0){
	        	g.moves[i] = move;
	        	g.movesValue[i] = val;
	        }
			logMove(g, depth, move, i, best, alpha, beta);
	        if (val >= beta){
	        	if(DEBUG){
	        		log.debug(indent(depth) + "@" + depth + "  return max's best (cutoff) = " + val + " with move " + Util.displayMoveStr(move, false, false));
	        	}
	        	break;
	        }
	        if(val > alpha)
	        {    
	        	alpha = val;
	        	storeOrReplacePVNode(depth, best, move);
	        }
	        alpha = Math.max(alpha, val);
	    }
	    return best;
	}

	private void storeOrReplacePVNode(int depth, int best, int move) {
		PVNode existingNode = pvNodeMap.get(depth);
		PVNode newNode = new PVNode(move, best);
		if(existingNode != null)
		{
			log.debug("@"+depth+" replacing PV node "+existingNode+" with "+newNode);
		} else {
			log.debug("@"+depth+" adding PV node "+newNode);
		}
		pvNodeMap.put(depth, newNode);
	}

	// min   - returns minimum value from state 'g'
	//
	// alpha - value of best alternative for MAX along path to state 'g'
	// beta  - value of best alternative for MIN along path to state 'g'
	//
	int min(GameState g, int alpha, int beta, int side, int depth){
		int best;
		int val;
		int[] moves = generateMoves(g, side, depth);
		if(isMaxDepthOrHasNoMoves(depth, moves.length)){
			return evaluate(g, side, depth);
		}
		best = +MAXWINDOW;
//		log.debug("moves: " + g.numberOfLegalMoves[depth]);
		for(int i=0; i<g.numberOfLegalMoves[depth]; i++){
			g.nodes++;
			int move = moves[i];
			g.currentLine[depth] = move;
//			logMove(g, depth, move, i, best, alpha, beta);
			g.makeMove(move, side==WHITE);
			val = max(g,-beta,-alpha,Util.opposing(side), depth+1);  //recurse!
			g.undoMove(move, side==WHITE);
			best = Math.min(best, val);
			if(depth==0){
				g.moves[i] = move;
				g.movesValue[i] = val;
			}
			logMove(g, depth, move, i, best, alpha, beta);
			if (val >= beta){   //Found a cutoff
	        	if(DEBUG){
	        		log.debug(indent(depth) + "@" + depth + "  return min's best (cutoff) = " + val + " with move " + Util.displayMoveStr(move, false, false));
	        	}
				break;
			}
			beta = Math.min(beta, val);
	        if(val > alpha)
	        {   
	        	alpha = val;
	        	storeOrReplacePVNode(depth, best, move);
	        }
		}
		return best;
	}

	private boolean isMaxDepthOrHasNoMoves(int depth, int numMoves) {
		boolean isMaxDepth = depth == maxSearchDepth;
//		if(isMaxDepth) log.debug("max depth reached: " + depth);
		return isMaxDepth || numMoves == 0;
	}

	private int[] generateMoves(GameState g, int side, int depth) {
		// Generate legal moves from this position
		int[] moves = new int[70]; //how many moves are there actually?
	    if (!mg.isAttacked(g, side, g.getPosition().getKingSquare(side))){
	        mg.GenerateCaptures(g, moves, side, depth);
	        mg.GenerateNonCaptures(g, moves, side, depth);
	    } else {
	        mg.GenerateKingEscapes(g, moves, side, depth);
	    }
	    return moves;
	}

	//
	// Returns an evaluation score for the side to move 'side'
	// A more positive number is better for white.
	// A more negative number is better for black.
	int evaluate(GameState g, int side, int depth){
		return eval.evaluate(g, side, depth, SEARCH_DEBUG, EVAL);
	}

	private void logMove(GameState g, int depth, int move, int i, int best, int alpha, int beta) {
		if(SEARCH_DEBUG){
			String line = "";
			for(int j=0; j<depth; j++){
				line += Util.displayMoveStr(g.currentLine[j], false, false)+ " ";
			}

		        log.debug(indent(depth) + "@" + depth + "  " + line + " " + 
		        		Util.displayMoveStr(move, false, false) + 
		        		String.format("  best=%d  alpha=%d  beta=%d", best, alpha, beta));
		}
		if(DEBUG) log.debug(String.format("  best=%d  alpha=%d  beta=%d\n", best, alpha, beta));
	}

	private static String indent(int d){
	  String indent = "";
	  for (int i=0;i<d;i++)
		indent += "  ";
	  return indent;
	}
	
}
