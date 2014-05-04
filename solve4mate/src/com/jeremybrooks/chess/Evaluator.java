package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

import org.apache.log4j.Logger;

public class Evaluator {
	private static final Logger log = Logger.getLogger(Evaluator.class);
	static final int CHECKMATE = 100000;    //value for checkmate
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
	 * Scores advantageous for white are positive.
	 * Scores advantageous for black are negative.
	 * Zero indicates no advantage for either side
	 * TODO: there's no code or score for a draw yet.
	 * 
	 * A more positive number is better for white.
	 * A more negative number is better for black.
	 */
	public int evaluate(GameState g, int side, int depth, boolean isSearchDebug, boolean isEval){
		int wMaterialScore = 0, bMaterialScore = 0;  //score for white and black
		int wPositionalScore = 0, bPositionalScore = 0;
		int score = 0;
		int pieceSq = -1;
		long pieces = 0;
		boolean draw = false;
		boolean mate = false;

		Position position = g.getPosition();
		if (isCheckMated(g, side, depth))
		{
				mate = true;
				//Mate-in-1 > Mate-in-2 > Mate-in-3 > ... etc  (white is mated is negative, black is 
				score = (CHECKMATE - depth) * (side==WHITE?-1:+1);  

				//Copy the line of play to checkmate to bestLine
				if (g.numberOfLinesToMate == 0){
					System.arraycopy(g.currentLine, 0, g.bestLine, 0, g.currentLine.length);
//					String line = "";
//					for(int i=0; i<g.currentLine.length; i++)
//					{
//						line += Util.displayMoveStr(g.bestLine[i], false, false) + " ";
//					}
//					log.debug("mate line: " + line);
					g.numberOfLinesToMate++;
				} else {
					g.numberOfLinesToMate++;
				}
		}

		// Compute material value
		for (int i = PAWN; i <= QUEEN; i++){
			wMaterialScore += whitePieceValue[i] * 
					Util.bitCount(position.getPieces(Bitmap.WHITE,i));

			bMaterialScore += blackPieceValue[i] * 
					Util.bitCount(position.getPieces(Bitmap.BLACK,i));
		}

		// Estimate positional value
		for(int i = 0; i<2; i++){
			// For knights
			pieces = position.getPieces(i, KNIGHT);
			while(pieces != 0){
				pieceSq = squareOfFirstPiece(pieces);
				if (i == Bitmap.WHITE){
					wPositionalScore += knightPV[i][pieceSq];
				} else {
					bPositionalScore += knightPV[i][pieceSq];
				} 
				pieces = clearPieceOnSquare(pieces, pieceSq);
			}
			// For bishops
			pieces = position.getPieces(i, BISHOP);
			while(pieces != 0){
				pieceSq = squareOfFirstPiece(pieces);
				if (i == Bitmap.WHITE){
					wPositionalScore += bishopPV[i][pieceSq];
				} else {
					bPositionalScore += bishopPV[i][pieceSq];
				} 
				pieces = clearPieceOnSquare(pieces, pieceSq);
			}
			// For rooks
			pieces = position.getPieces(i, ROOK);
			while(pieces != 0){
				pieceSq = squareOfFirstPiece(pieces);
				if (i == Bitmap.WHITE){
					wPositionalScore += rookPV[i][pieceSq];
				} else {
					bPositionalScore += rookPV[i][pieceSq];
				} 
				pieces = clearPieceOnSquare(pieces, pieceSq);
			}
		}
		int wTotalScore = wMaterialScore + wPositionalScore;
		int bTotalScore = bMaterialScore + wPositionalScore;
		if(mate){

//			if(isSearchDebug)
				String mateLine = "";
				for(int i=0;i<depth;i++){mateLine += Util.displayMoveStr(g.currentLine[i], false, false)+" ";}
				FenBuilder fb = new FenBuilder();
				fb.appendPieceBoard(g.getPosition());
				log.debug("mate("+score+"): " + mateLine + "  " +fb.toString());
//			}
			return score;
		} else if (draw) {
			log.debug("draw:");
			g.display();
			log.debug("finds draw");
			return 0;
		} else {
			if(isEval){
				log.debug("white score: "+ wTotalScore + " = " + wMaterialScore + " + " + wPositionalScore);
				log.debug("black score: "+ bTotalScore + " = " + bMaterialScore + " + " + bPositionalScore);
			}
			return wTotalScore - bTotalScore;
		}
	}

	boolean isCheckMated(GameState g, int side, int depth)
	{
		//Does king have legal moves?
		int moves[] = new int[70];
		int numMoves = mg.GenerateKingEscapes(g, moves, side, depth);
		//number of moves may be helpful for evaluation tuning because 1 or 2 moves
		//limits the branching factor so that could be use to feed into the overall evaluation score
		Position position = g.getPosition();
		if (numMoves == 0 &&   mg.isAttacked(g, side, position.getKingSquare(side)))
		{
			return true;
		}
		return false;
	}
	
	private long clearPieceOnSquare(long pieces, int pieceSq) {
		return Bitmap.clearBit(pieces, pieceSq);
	}

	private int squareOfFirstPiece(long pieces) {
		return Bitmap.lowestBitNumber(pieces);
	}

}
