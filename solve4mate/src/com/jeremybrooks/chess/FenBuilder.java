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
	    char fen[] = new char[100];
	    int fenIndex = 0;
	    int contEmptySquares = 0;
	    for (int i = A8; i >= A1; i-=8){
	        if (i < A8){
	            fen[fenIndex++] = RANK_DELIMITER.charAt(0); //Rank separator only on first 7..not the last one
	        }
	        for (int j = i; j < i+8; j++){
	            switch(position.getBoard(j)){
	            //white pieces: pawns, knights, bishops, rooks, queens, king
	            case 1:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[0];
	                break;
	            case 2:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares);
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[1];
	                break;
	            case 5:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[2];
	                break;
	            case 6:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
					fen[fenIndex++] = BOARD_PIECE[3];
					break;
	            case 7:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[4];
	                break;
	            case 3:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[5];
	                break;
	                
	            //black pieces: pawns, knights, bishops, rooks, queens, king
	            case -1:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
					fen[fenIndex++] = BOARD_PIECE[6];
					break;
	            case -2:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[7];
	                break;
	            case -5:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[8];
	                break;
	            case -6:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[9];
	                break;
	            case -7:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[10];
	                break;
	            case -3:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[11];
	                break;
	            default:
	                //increment a counter for contiguous empty squares
	                contEmptySquares++;
	                break;
	            }
	        }
	        //tack on any empty squares at the end
	        if (contEmptySquares > 0 && i >= A1){
	            fen[fenIndex++] = toChar(contEmptySquares);
	            contEmptySquares = 0;
	        }	
	    }
	    pieceBoard = new String(fen).trim();
		return this;
	}
	
	private char toChar(int i) {
		if (i < 0 || i > 9)
		{
			throw new IllegalArgumentException("i should be in range [0..9], found " + i);
		}
		return new Integer(i).toString().charAt(0);
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
