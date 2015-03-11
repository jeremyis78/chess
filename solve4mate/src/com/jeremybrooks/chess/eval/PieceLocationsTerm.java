package com.jeremybrooks.chess.eval;

import static com.jeremybrooks.chess.base.Bitmap.hasMore;
import static com.jeremybrooks.chess.base.Piece.BLACK;
import static com.jeremybrooks.chess.base.Piece.KING;
import static com.jeremybrooks.chess.base.Piece.PAWN;
import static com.jeremybrooks.chess.base.Piece.WHITE;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Piece.Color;
import com.jeremybrooks.chess.base.PieceFactory;
import com.jeremybrooks.chess.base.Position;

public class PieceLocationsTerm extends EvalTerm {

	@Override
	public int evaluate(GameState state) {
        // Count pieces
        Position position = state.getPosition();
        int[][] pieceLocationValue = new int[2][6];
        for(int color = 0; color<2; color++){
            for (int piece = PAWN; piece <= KING; piece++){

            	long pieces = position.getPieces(color,piece);
            	while(hasMore(pieces)) {
            		int square = Bitmap.lowestBitNumber(pieces);
            		pieceLocationValue[color][piece] += pieceLocationScore(color, piece, square);
					pieces = Bitmap.clearBit(pieces, square);
            	}

            }
        }
        int score = 0;
        for (int pieceIndex = PAWN; pieceIndex <= KING; pieceIndex++){
        	score += (pieceLocationValue[WHITE][pieceIndex] - pieceLocationValue[BLACK][pieceIndex]);
        }
        return score;
	}
	
	private int pieceLocationScore(int color, int pieceIndex, int square) {
		Piece p = PieceFactory.fromIndex(color==0?Color.W:Color.B, pieceIndex);
		int centipawnValueOnSquare = p.centipawnValueOnSquare(square);
		return centipawnValueOnSquare;
	}

}
