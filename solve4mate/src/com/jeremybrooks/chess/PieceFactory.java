package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

import com.jeremybrooks.chess.Piece.Color;

public class PieceFactory {
	
	private static final Piece WHITE_PAWN = new Pawn(Color.W);
	private static final Piece BLACK_PAWN = new Pawn(Color.B);
	private static final Piece WHITE_KNIGHT = new Knight(Color.W);
	private static final Piece BLACK_KNIGHT = new Knight(Color.B);
	private static final Piece WHITE_BISHOP = new Bishop(Color.W);
	private static final Piece BLACK_BISHOP = new Bishop(Color.B);
	private static final Piece WHITE_ROOK = new Rook(Color.W);
	private static final Piece BLACK_ROOK = new Rook(Color.B);
	private static final Piece WHITE_QUEEN = new Queen(Color.W);
	private static final Piece BLACK_QUEEN = new Queen(Color.B);
	private static final Piece WHITE_KING = new King(Color.W);
	private static final Piece BLACK_KING = new King(Color.B);
	private static final Piece EMPTY = new Empty();
	
	/**
	 * Given a piece returned from Position.getBoard(int) 
	 * construct and return the appropriate Piece object
	 * 
	 * @param boardPiece value returned from Position.getBoard(int)
	 * @return a corresponding Piece object
	 */
	public static Piece fromBoardPiece(int boardPiece)
	{
		//positive boardPiece = white
		//negative boardPiece = black
		Color pieceColor = boardPiece > 0 ? Color.W : Color.B;
		if(BOARD_EMPTY_SQUARE == boardPiece)
		{
			return PieceFactory.fromIndex(pieceColor, NONE);
		}
		return PieceFactory.fromIndex(pieceColor, TO_PIECE[Math.abs(boardPiece)]);
	}

	public static Piece fromIndex(Piece.Color color, int pieceIndex)
	{
		switch(pieceIndex)
		{
		case PAWN: return color==Color.W ? WHITE_PAWN : BLACK_PAWN;
		case KNIGHT: return color==Color.W ? WHITE_KNIGHT : BLACK_KNIGHT;
		case BISHOP: return color==Color.W ? WHITE_BISHOP : BLACK_BISHOP;
		case ROOK: return color==Color.W ? WHITE_ROOK : BLACK_ROOK;
		case QUEEN: return color==Color.W ? WHITE_QUEEN : BLACK_QUEEN;
		case KING: return color==Color.W ? WHITE_KING : BLACK_KING;
		case NONE: return EMPTY;   
		default: throw new IllegalArgumentException("invalid pieceIndex: " + pieceIndex);
		}
	}

}
