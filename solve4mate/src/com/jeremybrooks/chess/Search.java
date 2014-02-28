/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess;

import java.io.PrintStream;

/**
 * @author jeremy
 *
 */
public class Search {

	private static final PrintStream out = System.out;
	
	/*******************************************************************/
	/*	                       alphabeta.cpp                       */
	/*******************************************************************/
	/*                                                                 */
	/*  This file contains the search algorithm (alphabeta).           */
	/*******************************************************************/

	public static final boolean SEARCH_DEBUG = true; //SEARCH_DB 
	public static final boolean DEBUG = false; //DB
	public static final boolean EVAL = false;
	public static final int MAXWINDOW = 1000000;   //To pass to alpha beta for initial window (+inf, -inf)
	public static final int CHECKMATE = 100000;    //value for checkmate
	public static final int DRAW = 0;              //value for draw

	public static final int whitePieceValue[] = 
	{
	  100, // White Pawn
	  300, // White Knight
	  300, // White Bishop
	  500, // White Rook
	  900  // White Queen
	};

	public static final int blackPieceValue[] = 
	{
	  100, // Black Pawn
	  300, // Black Knight
	  300, // Black Bishop
	  500, // Black Rook
	  900  // Black Queen
	};


	MoveGenerator mg = new MoveGenerator();

	//These boards represent the Positional Value (in centipawns)
	//on the board for the named piece (a1=0, b1=1,...,h8=63).
	//These are from white's perspective
	int knightPV[][] = new int[][]{
	    //White
	    {   5,  5, 12, 12, 12, 12,  5,  5, // first rank (a1-h1)
	       10, 15, 20, 20, 20, 20, 15, 10, // second rank  
	       10, 20, 30, 20, 20, 30, 20, 10, // 
	       10, 20, 30, 20, 20, 30, 20, 10, //    .
	       10, 40, 45, 20, 20, 45, 40, 10, //    .
	       10, 20, 30, 20, 20, 30, 20, 10, //    .
	       10, 15, 20, 20, 20, 20, 15, 10, //
	        5, 10, 12, 12, 12, 12, 10,  5  // eighth rank
	    },
	    //Black
	    {   
	        5, 10, 12, 12, 12, 12, 10,  5, // first rank
	       10, 15, 20, 20, 20, 20, 15, 10, //
	       10, 20, 30, 20, 20, 30, 20, 10, //    .
	       10, 40, 45, 20, 20, 45, 40, 10, //    .
	       10, 20, 30, 20, 20, 30, 20, 10, //    .
	       10, 20, 30, 20, 20, 30, 20, 10, // 
	       10, 15, 20, 20, 20, 20, 15, 10, // seventh rank  
	        5,  5, 12, 12, 12, 12,  5,  5  // eighth rank
	    }
	};

	int bishopPV[][] = new int[][]{
	    //White
	    {    10, 10,  8, 15, 15,  8, 10, 10, // first rank (a1-h1)
	         10, 50, 10, 10, 10, 10, 50, 10, // second rank
	         10, 20, 20, 25, 25, 20, 20, 10, //
	         10, 20, 45, 20, 20, 45, 20, 10, //    .
	         15, 45, 40, 30, 30, 40, 45, 15, //    .
	         15, 20, 20, 20, 20, 20, 20, 15, //    .
	         15, 20, 20, 20, 20, 20, 20, 15, //
	         10, 15, 20, 20, 20, 20, 15, 10  // eighth rank
	    },
	    //Black
	    {
	        10, 15, 20, 20, 20, 20, 15, 10, // first rank
	        15, 20, 20, 20, 20, 20, 20, 15, //
	        15, 20, 20, 20, 20, 20, 20, 15, //    .
	        15, 45, 40, 30, 30, 40, 45, 15, //    .
	        10, 20, 45, 20, 20, 45, 20, 10, //    .
	        10, 20, 20, 25, 25, 20, 20, 10, //        
	        10, 50, 10, 10, 10, 10, 50, 10, // seventh rank
	        10, 10,  8, 15, 15,  8, 10, 10  // eighth rank
	    }
	};
	    
	int rookPV[][] = new int[][]{
	    //White
	    {    20,  5,  5, 45, 45, 45,  5, 20, // first rank (a1-h1)
	          5,  5,  5, 18, 20, 10,  5,  5, // second rank
	         10, 10, 10, 13, 15, 10, 10, 10, //
	         10, 10, 10, 10, 12, 10, 10, 10, //    .
	         10, 10, 10, 10, 10, 10, 10, 10, //    .
	         10, 10, 10, 10, 10, 10, 10, 10, //    .
	         10, 10, 10, 10, 10, 10, 10, 10, //
	         10, 10, 10, 10, 10, 10, 10, 10 // eighth rank
	    },
	    //Black
	    {    10, 10, 10, 10, 10, 10, 10, 10, // first rank
	         10, 10, 10, 10, 10, 10, 10, 10, //
	         10, 10, 10, 10, 10, 10, 10, 10, //    .
	         10, 10, 10, 10, 10, 10, 10, 10, //    .
	         10, 10, 10, 10, 12, 10, 10, 10, //    .
	         10, 10, 10, 13, 15, 10, 10, 10, //
	          5,  5,  5, 18, 20, 10,  5,  5, // seventh rank
	         20,  5,  5, 45, 45, 45,  5, 20  // eighth rank        
	    }
	};



	int getBestMove(GameState g, int side){
	    int best = 0;

	    //TODO
	    //Run alphabeta with Iterative Deepening
	    //by calling it with larger and larger values for g.searchDepth
	    // Do Iterative Deepening up to depth 5 
	    // That means solve4mate will be able to find mates in 3 at most.
	    // W moves, B moves, W moves, B moves, W moves and mates
	    //
	    //
	    //
	    for(g.searchDepth = 1; g.searchDepth<5; g.searchDepth++){
	      best = alphabeta(g, side);
	      if (g.numberOfLinesToMate > 0)
	    	  break;
	    }

	    //Find the best move by its minimax value
	    int bestMove = 0;
	    for(int i=0; i<g.legalMoves[0]; i++){
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
	    if (!mg.isAttacked(g, side, g.pos.kingSq[side])){
	        mg.GenerateCaptures(g, g.moves, side, depth);
	        mg.GenerateNonCaptures(g, g.moves, side, depth);
	    } else {
	        mg.GenerateKingEscapes(g, g.moves, side, depth);
	    }
	    //Now reset the legal moves to zero so everything
	    //works correctly for the search.
	    g.legalMoves[depth] = 0;


	    if(side == Color.WHITE){
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
	    int best, numMoves;
	    int moves[] = new int[100];
	    int val;

	    if(depth == g.searchDepth){
	        return evaluate(g, side, depth);
	    }

	    // Generate legal moves from this position
	    if (!mg.isAttacked(g, side, g.pos.kingSq[side])){
	        mg.GenerateCaptures(g, moves, side, depth);
	        mg.GenerateNonCaptures(g, moves, side, depth);
	    } else {
	        mg.GenerateKingEscapes(g, moves, side, depth);
	        if (g.legalMoves[depth] == 0){ //checkmate
	            return evaluate(g, side, depth);
	        }
	    }
	    numMoves = g.legalMoves[depth];

	    // Try each move
	    best = -MAXWINDOW;
	    //cerr << numMoves << endl;
	    for(int i=0; i<numMoves; i++){
	        g.nodes++;

	if(SEARCH_DEBUG){
	        out.println();
	        indent(depth);
	        Util.displayMove(moves[i], false, false);
	}


	if(DEBUG){
	        indent(depth);
	        Util.displayMove(moves[i], false, false);
	        out.printf("  best=%d  alpha=%d  beta=%d\n", best, alpha, beta);
	}



	        //*****************************************
	        //*                                       *
	        //* Make move, recurse, undo move         *
	        //*                                       *
	        //*****************************************
	        g.makeMove(moves[i], side);
	        g.currentLine[depth] = moves[i];
		val = min(g,alpha,beta,Util.opp(side),depth+1);
	        best = Math.max(best, val);
		if(depth==0){
		  g.movesValue[i] = val;
		}
	        g.undoMove(moves[i], side);

	if(DEBUG){
	        indent(depth);
	        Util.displayMove(moves[i], false, false);
	        out.printf("  best=%d  alpha=%d  beta=%d\n", best, alpha, beta);
	}


	          if (best >= beta){
	if(DEBUG){
		    out.println();
		    indent(depth);
		    out.print("ret. max's best (cutoff) = " + best + " with move ");
		    Util.displayMove(moves[i], false, false);
		    out.println();
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
	    int best, numMoves;
	    int moves[] = new int[100];
	    int val;
	    
	    if(depth == g.searchDepth){
	        return evaluate(g, side, depth);
	    }
	    
	    // Generate legal moves from this position
	    if (!mg.isAttacked(g, side, g.pos.kingSq[side])){
	        mg.GenerateCaptures(g, moves, side, depth);
	        mg.GenerateNonCaptures(g, moves, side, depth);
	    } else {
	        mg.GenerateKingEscapes(g, moves, side, depth);
	        if (g.legalMoves[depth] == 0){ //checkmate
	            return evaluate(g, side, depth);
	        }
	    }
	    numMoves = g.legalMoves[depth];

	    // Try each move
	    best = +MAXWINDOW;
	    //cerr << numMoves << endl;
	    for(int i=0; i<numMoves; i++){
	        g.nodes++;

	if(SEARCH_DEBUG){
	        out.println();
	        indent(depth);
	        Util.displayMove(moves[i], false, false); //cout << endl;
	}

	if(DEBUG){
	        indent(depth);
	        Util.displayMove(moves[i], false, false);
	        out.printf("  best=%d  alpha=%d  beta=%d\n", best, alpha, beta);
	}

	        //*****************************************
	        //*                                       *
	        //* Make move, recurse, undo move         *
	        //*                                       *
	        //*****************************************
	        g.makeMove(moves[i], side);
	        g.currentLine[depth] = moves[i];
		val = max(g,alpha,beta,Util.opp(side), depth+1);
	        best = Math.min(best, val);
		if(depth==0){
		  g.movesValue[i] = val;
		}
	        g.undoMove(moves[i], side);




	if(DEBUG){
	        indent(depth);
	        Util.displayMove(moves[i], false, false);
	        out.printf("  best=%d  alpha=%d  beta=%d\n", best, alpha, beta);
	}



	        if (best <= alpha){   //Found a cutoff
	if(DEBUG){
		  out.println();
		  indent(depth);
		  out.print("ret. min's best (cutoff) = " + best + " with move ");
		  Util.displayMove(moves[i], false, false);
		  out.println();
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




	//
	// Returns an evaluation score for the side to move 'side'
	// A more positive number is better for white.
	// A more negative number is better for black.
	int evaluate(GameState g, int side, int depth){
	    int wScore = 0, bScore = 0;  //score for white and black
	    int numMoves = 0;
	    int moves[] = new int[70]; //to pass to GenKingEscapes..not used otherwise
	    int score = 0;
	    int pieceSq = -1;
	    long pieces = 0;
	    boolean draw;
	    boolean mate;
	    
	    draw = false;
	    mate = false;
	    
	    //Does king have legal moves?
	    numMoves = mg.GenerateKingEscapes(g, moves, side, depth);
	    if (numMoves == 0){
	        if (mg.isAttacked(g, side, g.pos.kingSq[side])){
	            // Mate
	            mate = true;
	            
	            //Mate-in-1 > Mate-in-2 > Mate-in-3 > ... etc
	            score = CHECKMATE - depth;  
	            
	            //Copy the line of play to checkmate to bestLine
	            if (g.numberOfLinesToMate == 0){
//	                memcpy(g.bestLine, g.currentLine, depth*sizeof(move_t));
	            System.arraycopy(g.currentLine, 0, g.bestLine, 0, g.currentLine.length);
			//for(int i=0; i<g.searchDepth; i++){
			//  displayMove(g.bestLine
	                g.numberOfLinesToMate++;
	            } else {
	                g.numberOfLinesToMate++;
	            }
	            
	        } else {
	            // Draw
	            //draw = true;
	            //score = DRAW;
	        }
	    }
	    
	    // Compute material value
	    for (int i = Pieces.PAWNS; i <= Pieces.QUEENS; i++){
	        wScore += whitePieceValue[i] * 
	            Util.PieceCount(g.pos.pieces[Color.WHITE][i]);
	        
	        bScore += blackPieceValue[i] * 
	            Util.PieceCount(g.pos.pieces[Color.BLACK][i]);
	    }
	    
	    // Estimate positional value
	    for(int i = 0; i<2; i++){
	        // For knights
	        pieces = g.pos.pieces[i][Pieces.KNIGHTS];
	        while(pieces > 0){
	            pieceSq = mg.FirstPiece(pieces);
	            if (i == Color.WHITE){
	                wScore += knightPV[i][pieceSq];
	            } else {
	                bScore += knightPV[i][pieceSq];
	            } 
	            pieces = mg.ClearPiece(pieces, pieceSq);
	        }
	        // For bishops
	        pieces = g.pos.pieces[i][Pieces.BISHOPS];
	        while(pieces > 0){
	            pieceSq = mg.FirstPiece(pieces);
	            if (i == Color.WHITE){
	                wScore += bishopPV[i][pieceSq];
	            } else {
	                bScore += bishopPV[i][pieceSq];
	            } 
	            pieces = mg.ClearPiece(pieces, pieceSq);
	        }
	        // For rooks
	        pieces = g.pos.pieces[i][Pieces.ROOKS];
	        while(pieces > 0){
	            pieceSq = mg.FirstPiece(pieces);
	            if (i == Color.WHITE){
	                wScore += rookPV[i][pieceSq];
	            } else {
	                bScore += rookPV[i][pieceSq];
	            } 
	            pieces = mg.ClearPiece(pieces, pieceSq);
	        }
	    }
	  

	    if(mate){

if(SEARCH_DEBUG){
        out.print("   " + score + " mate!");
}
	        //cout << "\nmate\n";
		//g.display();
	        return score;
	    } else if (draw) {
			out.println("draw:");
			g.display();
			out.println("finds draw");
	        return 0;
	    } else {


if(EVAL){
      out.println("white score: "+ wScore);
      out.println("black score: "+ bScore);
        //if (check){
        //    cout << " check";
        //}
}


	        return wScore - bScore;
	    }
	}


	private static void indent(int d){
	  for (int i=0;i<d;i++)
		System.out.print("  ");
	}

	
}
