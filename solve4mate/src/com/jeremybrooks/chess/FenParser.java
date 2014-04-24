package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import static com.jeremybrooks.chess.Util.StrToSq;

public class FenParser {
	private static final char WHITE_ON_MOVE = 'w';
	private static final char BLACK_ON_MOVE = 'b';
	private static final String UNSET_FIELD = "-";
	private static final int NUMBER_OF_FIELDS = 6;
	private String originalFen;
	private char[] board;
	private boolean whiteToMove;
	private boolean whiteShortCastle;
	private boolean whiteLongCastle;
	private boolean blackShortCastle;
	private boolean blackLongCastle;
	private int enPassantSquare;
	private int halfMoveNumber;
	private int moveNumber;
	

	public FenParser(){
		board = new char[64];
	}
		
	public void parse(String fen)
	{
		originalFen = fen;
		String[] fields = originalFen.split(" ");
        if (fields.length < NUMBER_OF_FIELDS){
        	throw new IllegalArgumentException("FEN string '"+originalFen+"' "
        			+ "needs six space-delimited fields: "
        			+ "board onMove castlingOptions enPassantSquare halfMoveNumber moveNumber");
        }
        parseBoard(fields[0]);
		parseOnMove(fields[1]);
		parseCastleOptions(fields[2]);
		parseEnPassantSquare(fields[3]);
		parseHalfMoveNumber(fields[4]);
		parseMoveNumber(fields[5]);
	}

	public void parseBoard(String field) {
		String[] ranks = field.split("/");
		if (ranks.length != 8)
		{
			throw new IllegalArgumentException("board must contain eight ranks");
		}
		boolean isKingPlaced[] = new boolean[2];
		
		//Start with the first rank
		int currentSquare = Bitmap.A1; //keep track of the square we're on
		for(int m = ranks.length - 1; m >= 0; m--)
		{
			String rank = ranks[m];
			validateFiles(rank, m-8);
			for (int n = 0; n < rank.length(); n++)
			{
				char boardCharacter = rank.charAt(n);
				if (Character.isDigit(boardCharacter))
				{
					currentSquare = fillWithEmptySquares(currentSquare, boardCharacter);
				}
				else
				{
		            placePiece(isKingPlaced, currentSquare, boardCharacter);
		            currentSquare++;
		            //files++;
				}
			}
		}
	}

	public static void validateFiles(String rankFen, int rankNumber)
	{
		int len = rankFen.length();
		if (len == 0 || len > 8)
		{
			throw new IllegalArgumentException("board must contain eight squares on a rank, found: "+ rankFen);
		}
		
		int filesRead = 0;
		for(int i=0; i < len; i++)
		{
			char c = rankFen.charAt(i);
			if (Character.isDigit(c))
			{
				filesRead += c - '0'; //represents multiple files
				continue;
			}
			filesRead++; //represents a single file
		}
		if(filesRead != 8)
		{
			throw new IllegalArgumentException("board pieces and empty squares on rank #"+
					Math.abs(rankNumber)+" do not fit on eight files: "+rankFen);
		}
	}

	private void placePiece(boolean[] isKingPlaced, int sq, char c) {
		switch(c){
		case 'P':
		case 'N':
		case 'B':
		case 'R':
		case 'Q':
		    board[sq] = c;
		    break;
		case 'K':
		    if(isKingPlaced[Bitmap.WHITE])
		    {
		        throw new IllegalArgumentException("board has too many white kings");
		    }
		    board[sq] = c;
		    isKingPlaced[Bitmap.WHITE] = true;
		    break;
		case 'p':
		case 'n':
		case 'b':
		case 'r':
		case 'q':
			board[sq] = c;
			break;
		case 'k':
		    if(isKingPlaced[Bitmap.BLACK])
		    {
		        throw new IllegalArgumentException("board has too many black kings");
		    }
		    board[sq] = c;
		    isKingPlaced[Bitmap.BLACK] = true;
		    break;
		default: //illegal character
		    throw new IllegalArgumentException("board contains invalid "
		    		+ "piece '" + c + "'; allowed piece characters are: KkQqRrBbNnPp"); 
		}
	}

	private int fillWithEmptySquares(int currentSquare, char numberOfEmptySquares) {
		int emptysq = currentSquare;
		//add digit's value to sq
		currentSquare += numberOfEmptySquares - '0'; 
         
		//initialize those squares to empty
		while (emptysq < currentSquare)
		    board[emptysq++] = BOARD_EMPTY_SQUARE;
		return currentSquare;
	}

	private void parseOnMove(String field) {
		char onMove = field.trim().charAt(0);
	    if (WHITE_ON_MOVE == onMove) {
	        whiteToMove = true;
	    } else if (BLACK_ON_MOVE == onMove) {
	        whiteToMove = false;
	    } else {
	    	throw new IllegalArgumentException("onMove '"+field+"' is invalid; use 'w' for white or 'b' for black");
	    }
	}

	private void parseCastleOptions(String field) {
		if(field.length() > 4 || field.trim().isEmpty())
		{
			throw new IllegalArgumentException("castling options '" + field + "' "
					+ "must not be empty or exceed four characters; use only characters from KQkq");
		}
        for (char option: field.toCharArray())
        {
        	switch(option)
        	{
        	case '-':
        		whiteShortCastle = false;
        		whiteLongCastle = false;
        		blackShortCastle = false;
        		blackLongCastle = false;
        		break;
        	case 'K':
        		whiteShortCastle = true;
        		break;
        	case 'Q':
        		whiteLongCastle = true;
        		break;
        	case 'k':
        		blackShortCastle = true;
        		break;
        	case 'q':
        		blackLongCastle = true;
        		break;
        	default:
    			throw new IllegalArgumentException("castling options '" + field + "' "
    					+ "are invalid; use only characters from KQkq");
        	}
        }
	}

	private void parseEnPassantSquare(String field) {
	    if (UNSET_FIELD.equals(field)){
	        enPassantSquare = NOSQUARE;
	        return;
	    }
	    int square = StrToSq(field);
	    String onMove = whiteToMove?"w":"b";
	    if(whiteToMove && Util.notOnSixthRank(square))
	    {
			throw new IllegalArgumentException("given '"+onMove+"' to move, the "
	        		+ "en passant square '"+field+"' ought to be on the 6th rank");	    	
	    }
	    if(!whiteToMove && Util.notOnThirdRank(square))
	    {
	        throw new IllegalArgumentException("given '"+onMove+"' to move, the "
	        		+ "en passant square '"+field+"' ought to be on the 3rd rank");
	    }
        enPassantSquare = square;
	}

	private void parseHalfMoveNumber(String field) {
	    int n = Integer.parseInt(field);
	    if (n < 0){
	    	throw new IllegalArgumentException("halfMoveNumber '"+field+"' "
	    			+ "must be zero or greater");
	    }
	    halfMoveNumber = n;		
	}

	private void parseMoveNumber(String field) {
	    int n = Integer.parseInt(field);
	    if (n <= 0){
	    	throw new IllegalArgumentException("moveNumber '"+field+"' must be greater than zero");
	    }  
	    moveNumber = n;
	}

	public char getBoardCharacter(int onSquare) {
		return board[onSquare];
	}

	public boolean isWhiteToMove() {
		return whiteToMove;
	}

	public boolean hasWhiteShortCastleOption() {
		return whiteShortCastle;
	}

	public boolean hasWhiteLongCastleOption() {
		return whiteLongCastle;
	}

	public boolean hasBlackShortCastleOption() {
		return blackShortCastle;
	}

	public boolean hasBlackLongCastleOption() {
		return blackLongCastle;
	}

	public int getEnPassantSquare() {
		return enPassantSquare;
	}

	public int getHalfMoveNumber() {
		return halfMoveNumber;
	}

	public int getMoveNumber() {
		return moveNumber;
	}
}
