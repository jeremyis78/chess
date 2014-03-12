package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import java.io.PrintStream;

public class Evaluator {

	private static final PrintStream out = System.out; 
	private static final int CHECKMATE = 100000;    //value for checkmate
	private static final int whitePieceValue[] = 
		{
		100, // White Pawn
		300, // White Knight
		300, // White Bishop
		500, // White Rook
		900  // White Queen
		};
	private static final int blackPieceValue[] = 
		{
		100, // Black Pawn
		300, // Black Knight
		300, // Black Bishop
		500, // Black Rook
		900  // Black Queen
		};


	//MoveGenerator mg = new MoveGenerator();

	//These boards represent the Positional Value (in centipawns)
	//on the board for the named piece (a1=0, b1=1,...,h8=63).
	//These are from white's perspective
	private static int knightPV[][] = new int[][]{
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

	private static int bishopPV[][] = new int[][]{
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

	private static int rookPV[][] = new int[][]{
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


	private MoveGenerator mg;


	public MoveGenerator getMoveGenerator() {
		return mg;
	}

	public void setMoveGenerator(MoveGenerator mg) {
		this.mg = mg;
	}

	/**
	 * 
	 * Returns an evaluation score for the side to move 'side'
	 * A more positive number is better for white.
	 * A more negative number is better for black.
	 */
	public int evaluate(GameState g, int side, int depth, boolean isSearchDebug, boolean isEval){
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
			if (mg.isAttacked(g, side, g.pos.getKingSquare(side))){
				// Mate
				mate = true;

				//Mate-in-1 > Mate-in-2 > Mate-in-3 > ... etc
				score = CHECKMATE - depth;  

				//Copy the line of play to checkmate to bestLine
				if (g.numberOfLinesToMate == 0){
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
		for (int i = PAWN; i <= QUEEN; i++){
			wScore += whitePieceValue[i] * 
					Util.PieceCount(g.pos.getPieces(Bitmap.WHITE,i));

			bScore += blackPieceValue[i] * 
					Util.PieceCount(g.pos.getPieces(Bitmap.BLACK,i));
		}

		// Estimate positional value
		for(int i = 0; i<2; i++){
			// For knights
			pieces = g.pos.getPieces(i, KNIGHT);
			while(pieces != 0){
				pieceSq = squareOfFirstPiece(pieces);
				if (i == Bitmap.WHITE){
					wScore += knightPV[i][pieceSq];
				} else {
					bScore += knightPV[i][pieceSq];
				} 
				pieces = clearPieceOnSquare(pieces, pieceSq);
			}
			// For bishops
			pieces = g.pos.getPieces(i, BISHOP);
			while(pieces != 0){
				pieceSq = squareOfFirstPiece(pieces);
				if (i == Bitmap.WHITE){
					wScore += bishopPV[i][pieceSq];
				} else {
					bScore += bishopPV[i][pieceSq];
				} 
				pieces = clearPieceOnSquare(pieces, pieceSq);
			}
			// For rooks
			pieces = g.pos.getPieces(i, ROOK);
			while(pieces != 0){
				pieceSq = squareOfFirstPiece(pieces);
				if (i == Bitmap.WHITE){
					wScore += rookPV[i][pieceSq];
				} else {
					bScore += rookPV[i][pieceSq];
				} 
				pieces = clearPieceOnSquare(pieces, pieceSq);
			}
		}
		if(mate){

			if(isSearchDebug){
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

			if(isEval){
				out.println("white score: "+ wScore);
				out.println("black score: "+ bScore);
			}

			return wScore - bScore;
		}
	}

	private long clearPieceOnSquare(long pieces, int pieceSq) {
		return Bitmap.clearBit(pieces, pieceSq);
	}

	private int squareOfFirstPiece(long pieces) {
		return Bitmap.lowestBitNumber(pieces);
	}

}
