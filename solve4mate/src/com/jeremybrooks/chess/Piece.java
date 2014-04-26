package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

/**
 * Represents a piece on the chess board.
 * 
 * TODO: Work to eventually replace Position's "int board[64]" to be an array of these
 * instead (which would eliminate the static {@link #fromBoardPiece(int)} method.
 * 
 * Eventually (and ideally) we should be able massage this class into something that
 * can be used in the move generators (for even more complexity reduction)
 * 
 * @author jeremy
 *
 */
/*
 * Of the 37 classes as of this commit, these are the only classes remaining
 * with a complexity of 4 or greater.
 * 
 * class                  Line coverage  Branch coverage   Complexity
 * ---------------------- -------------  ---------------   ----------
	EscapeGenerator	        85% 79/92      66% 42/63        33
	NonCaptureGenerator	   100% 81/81      93% 41/44        27
	CaptureGenerator	   100% 54/54      96% 28/29        19
	Search++                 0% 0/83        0% 0/38         6.5
	SolveForMate++	         0% 0/46        0% 0/22         5.5
	MoveGenerator	        85% 206/242    72% 112/154      4.483
	Evaluator	            79%	54/68      70% 21/30        4.4
	 ++ = currently has NO unit tests
 *
 */
public class Piece {
	private static enum Color{W, B}
	
	/* initialize these only once, we only need one copy */
	private static final Piece factory = new Piece();
	private static final Pawn whitePawn = factory.new Pawn(Color.W);
	private static final Pawn blackPawn = factory.new Pawn(Color.B);
	private static final Knight whiteKnight = factory.new Knight(Color.W);
	private static final Knight blackKnight = factory.new Knight(Color.B);
	private static final Bishop whiteBishop = factory.new Bishop(Color.W);
	private static final Bishop blackBishop = factory.new Bishop(Color.B);
	private static final Rook whiteRook = factory.new Rook(Color.W);
	private static final Rook blackRook = factory.new Rook(Color.B);
	private static final Queen whiteQueen = factory.new Queen(Color.W);
	private static final Queen blackQueen = factory.new Queen(Color.B);
	private static final King whiteKing = factory.new King(Color.W);
	private static final King blackKing = factory.new King(Color.B);
	private static final Absent absent = factory.new Absent();

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
			return Piece.fromConstant(pieceColor, NONE);
		}
		return Piece.fromConstant(pieceColor, TO_PIECE[Math.abs(boardPiece)]);
	}

	public static Piece fromConstant(Color color, int pieceConstant)
	{
		switch(pieceConstant)
		{
		case PAWN: return color==Color.W ? whitePawn : blackPawn;
		case KNIGHT: return color==Color.W ? whiteKnight : blackKnight;
		case BISHOP: return color==Color.W ? whiteBishop : blackBishop;
		case ROOK: return color==Color.W ? whiteRook : blackRook;
		case QUEEN: return color==Color.W ? whiteQueen : blackQueen;
		case KING: return color==Color.W ? whiteKing : blackKing;
		case NONE: return absent;
		default: throw new IllegalArgumentException("invalid piece: " + pieceConstant);
		}
	}

	protected Color color;
	public String toString() { return ""+toChar(); }
	public char toChar() { return '?'; }
	
	/**
	 * @return the piece as it is or will be encoded into a 'move' int
	 */
	public int moveEncoded() { return PIECE[constant()]; }
	public int constant() { return NONE; }
	public boolean exists() { return (NONE != constant()); }
	public Piece getPiece(){ return new Pawn(Color.W); }
	
	private class Absent extends Piece {
		public Absent() { }
		@Override public char toChar() { return BOARD_EMPTY_SQUARE; }
		@Override public int constant() { return NONE; }
	}
	private class Pawn extends Piece {
		public Pawn(Color color) { this.color = color; }
		@Override public char toChar() { return color==Color.W ? 'P' : 'p'; }
		@Override public int constant() { return PAWN; }
	}
	private class Knight extends Piece {
		public Knight(Color color) { this.color = color; }
		@Override public char toChar() { return color==Color.W ? 'N' : 'n'; }
		@Override public int constant() { return KNIGHT; }
	}
	private class Bishop extends Piece {
		public Bishop(Color color) { this.color = color; }
		@Override public char toChar() { return color==Color.W ? 'B' : 'b'; };
		@Override public int constant() { return BISHOP; }
	}
	private class Rook extends Piece {
		public Rook(Color color) { this.color = color; }
		@Override public char toChar() { return color==Color.W ? 'R' : 'r'; };
		@Override public int constant() { return ROOK; }
	}
	private class Queen extends Piece {
		public Queen(Color color) { this.color = color; }
		@Override public char toChar() { return color==Color.W ? 'Q' : 'q'; };
		@Override public int constant() { return QUEEN; }
	}
	private class King extends Piece {
		public King(Color color) { this.color = color; }
		@Override public char toChar() { return color==Color.W ? 'K' : 'k'; };
		@Override public int constant() { return KING; }
	}

}