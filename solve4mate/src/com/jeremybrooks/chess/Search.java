/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import static com.jeremybrooks.chess.Bitmap.*;

/**
 * @author jeremy
 *
 */
public class Search {

	private static final Logger log = Logger.getLogger(Search.class);
	private static final PrintStream out = System.out;
	
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
	int maxSearchDepth = 0;

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
	    for(maxSearchDepth = 1; maxSearchDepth<5; maxSearchDepth++){
	      best = alphabeta(g, side);
	      if (g.numberOfLinesToMate > 0)
	    	  break;
	    }

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
	    if (!mg.isAttacked(g, side, g.getPosition().getKingSquare(side))){
	        mg.GenerateCaptures(g, g.moves, side, depth);
	        mg.GenerateNonCaptures(g, g.moves, side, depth);
	    } else {
	        mg.GenerateKingEscapes(g, g.moves, side, depth);
	    }
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
	        logMove(depth, moves[i], i, best, alpha, beta);
	        g.makeMove(moves[i], side==WHITE);
	        g.currentLine[depth] = moves[i];
	        val = min(g,alpha,beta,Util.opposing(side),depth+1); //recurse!
	        best = Math.max(best, val);
	        if(depth==0){
	        	g.movesValue[i] = val;
	        }
	        g.undoMove(moves[i], side==WHITE);
			logMove(depth, moves[i], i, best, alpha, beta);
	        if (best >= beta){
	        	if(DEBUG){
	        		log.debug(indent(depth) + "return max's best (cutoff) = " + best + " with move " + Util.displayMoveStr(moves[i], false, false));
	        	}
	        	return best;
	        }
	        alpha = Math.max(alpha, best);
	        //Store the minimax (alpha) value for the moves that branch off from 
	        //the initial root position we started searching from.
	        //if(depth == 0){
	        //    g.movesValue[i] = alpha;
	        //}
	    }
	    return best;
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
			logMove(depth, moves[i], i, best, alpha, beta);
			g.makeMove(moves[i], side==WHITE);
			g.currentLine[depth] = moves[i];
			val = max(g,alpha,beta,Util.opposing(side), depth+1);  //recurse!
			best = Math.min(best, val);
			if(depth==0){
				g.movesValue[i] = val;
			}
			g.undoMove(moves[i], side==WHITE);
			logMove(depth, moves[i], i, best, alpha, beta);
			if (best <= alpha){   //Found a cutoff
				if(DEBUG){
					log.debug(indent(depth) + "return min's best (cutoff) = " + best + " with move " + Util.displayMoveStr(moves[i], false, false));
				}
				return best;
			}
			beta = Math.min(beta, best);
			//Store the minimax (beta) value for the moves that branch off from 
			//the initial root position we started searching from.
			//if (depth == 0){
			//    g.movesValue[i] = beta;
			//}
		}
		return best;
	}

	private boolean isMaxDepthOrHasNoMoves(int depth, int numMoves) {
		return depth == maxSearchDepth || numMoves == 0;
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

	private void logMove(int depth, int move, int i, int best, int alpha, int beta) {
		if(SEARCH_DEBUG){
		        out.println();
		        indent(depth);
		        log.debug(indent(depth) + Util.displayMoveStr(move, false, false));
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
