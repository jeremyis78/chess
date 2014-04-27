package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import static com.jeremybrooks.chess.Util.bool;

public class FenBuilder {


	public static final String RANK_DELIMITER = "/";
	public static final char WHITE_ON_MOVE = 'w';
	public static final char BLACK_ON_MOVE = 'b';
	public static final char WHITE_SHORT_CASTLE_OPTION = 'K';
	public static final char WHITE_LONG_CASTLE_OPTION = 'Q';
	public static final char BLACK_SHORT_CASTLE_OPTION = 'k';
	public static final char BLACK_LONG_CASTLE_OPTION = 'q';
	public static final char FIELD_DELIMITER = ' ';
	public static final char UNSET = '-';
	
	private StringBuilder builder;
	private String pieceBoard;
	private boolean whiteToMove;
	private String castlingOptions;
	private int enPassantSquare;
	private int halfMovesSinceCaptureOrPawnAdvance;
	private int currentMoveNumber;
	
	/* temporary fields to assist when building the piece board */
	private char fen[];
	private int fenIndex;
	private int contiguousEmptySquares;
	
	public FenBuilder(){
		reset();
	}

	public void reset() {
		builder = new StringBuilder();
		pieceBoard = "";
		whiteToMove = true;
		castlingOptions = "";
		enPassantSquare = Bitmap.NOSQUARE;
		halfMovesSinceCaptureOrPawnAdvance = 0;
		currentMoveNumber = 1;
	}
	
	@Override
	public String toString()
	{
		appendField(pieceBoard);
		appendField(""+(whiteToMove?WHITE_ON_MOVE:BLACK_ON_MOVE));
		appendField(castlingOptions);
		appendField(Util.SqToStr(enPassantSquare));
		appendField(""+halfMovesSinceCaptureOrPawnAdvance);
		appendField(""+currentMoveNumber);
		removeLastDelimiter();
		return builder.toString();
	}

	private void appendField(String field) {
		if(field.isEmpty())	builder.append(UNSET);
		else builder.append(field);
		builder.append(FIELD_DELIMITER);
	}

	private void removeLastDelimiter() {
		builder.deleteCharAt(builder.length()-1);
	}
	
	public FenBuilder appendPieceBoard(Position position)
	{
	    fen = new char[100];
	    fenIndex = 0;
	    contiguousEmptySquares = 0;
	    for (int i = A8; i >= A1; i-=8){
	        if (i < A8){
	            addCharacter(RANK_DELIMITER.charAt(0)); //Rank separator only on first 7..not the last one
	        }
	        for (int j = i; j < i+8; j++){
	            int boardPiece = position.getBoard(j);
	    		Piece piece = PieceFactory.fromBoardPiece(boardPiece);
	    		if(piece.exists())
	    		{
	    			addDigitForAnyEmptySquares();
	    			addCharacter(piece.toChar());
	    		} else {
	    			//increment a counter for contiguous empty squares
	    			contiguousEmptySquares++;
	    		}
	        }
	        //tack on any empty squares at the end
	        addDigitForAnyEmptySquares();
	    }
	    pieceBoard = new String(fen).trim();
		return this;
	}
	
	private void addDigitForAnyEmptySquares()
	{
		if(contiguousEmptySquares > 0)
		{
			fen[fenIndex++] = charForDigit(contiguousEmptySquares);
			contiguousEmptySquares = 0;
		}
	}

	private void addCharacter(char c)
	{
		fen[fenIndex++] = c;
	}

	private char charForDigit(int i) {
		//assert (i > 0 && i <= 9);
		return (char)('0' + i); //Character.forDigit(i, 10);
	}

	public FenBuilder appendOnMove(boolean isWhiteToMove)
	{
		whiteToMove = isWhiteToMove;
		return this;
	}
	
	public FenBuilder appendCastlingOptions(int castlingOptions)
	{
		String options = "";
		if(bool(castlingOptions & GameState.W_SHORT_CASTLE)) options += WHITE_SHORT_CASTLE_OPTION;
		if(bool(castlingOptions & GameState.W_LONG_CASTLE)) options += WHITE_LONG_CASTLE_OPTION;
		if(bool(castlingOptions & GameState.B_SHORT_CASTLE)) options += BLACK_SHORT_CASTLE_OPTION;
		if(bool(castlingOptions & GameState.B_LONG_CASTLE)) options += BLACK_LONG_CASTLE_OPTION;
		if(options.isEmpty()) options += UNSET;
		this.castlingOptions = options;
		return this;
	}

	public FenBuilder appendEnPassantSquare(int square)
	{
		enPassantSquare = validEnPassantSquare(whiteToMove, square);
		return this;
	}

	/**
	 * Gets the validated en passant square by ensuring compatibility with who's
	 * on move.
	 * 
	 * Will return {@code Bitmap.NOSQUARE} if either of the following are true:
	 *     white's turn and the square is not on the sixth rank
	 *     black's turn and the square is not on the third rank
	 * @param isWhitesMove is white on the move
	 * @param enPassantSquare the en passant square
	 * @return
	 */
	public static int validEnPassantSquare(boolean isWhitesMove, int enPassantSquare)
	{
	    if(isWhitesMove)
	    {
	    	if(Util.notOnSixthRank(enPassantSquare))
	    	{
	    		// with 'w' to move, enPassant on a square other than the 6th rank is invalid
	    		enPassantSquare = Bitmap.NOSQUARE;
	    	}
	    } else {
	    	if(Util.notOnThirdRank(enPassantSquare))
	    	{
	    		// with 'b' to move, enPassant on a square other than 3rd rank is invalid
	    		enPassantSquare = Bitmap.NOSQUARE;
	    	}
	    }
		return enPassantSquare;

	}
	
	public FenBuilder appendHalfMoveNumber(int halfMoveNumber)
	{
		if(halfMoveNumber >= 0) 
			halfMovesSinceCaptureOrPawnAdvance = halfMoveNumber;
		//Keep the default value if it's negative
		return this;
	}

	public FenBuilder appendCurrentMoveNumber(int moveNumber)
	{
		if(moveNumber > 0)
			currentMoveNumber = moveNumber;
		//Keep the default value if it's not greater than zero
		return this;
	}

}
