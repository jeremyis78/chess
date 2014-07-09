package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static com.jeremybrooks.chess.base.FenBuilder.*;

/**
 * <pre>
 *  FenParser is used for extracting the fields found
 *  in a Forsythe-Edwards Notation string which can represent
 *  the state of the chess game at any point.
 *  
 *  For example, the following FEN represents the beginning
 *  of a chess game:
 *  
 *  rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
 *  
 *  It has six fields
 *  
 *  1) board               Represents pieces on the chessboard
 *           Upper case letters are white pieces.
 *           
 *           The board is read from top left to lower
 *           right A8, B8, C8, ..., G1, H1.  Encountering
 *           a / indicates the end of the rank; the next
 *           character read will be on the rank below it.
 *           
 *           A digit denotes a consecutive number of empty squares
 *                               
 *  2) onMove              Whose turn is it?  
 *             w = white
 *             b = black
 *                               
 *  3) castlingOptions     What castling options are still available?
 *         
 *         The presence of one of these letters indicates
 *         that option is available. 
 *           K = white short castle
 *           Q = white long castle
 *           k = black short castle
 *           q = black queen castle
 *           - = no castling available for white or black
 *         Examples:  KQk, Kq, kq, q
 *                                                            
 *  4) enPassantSquare     the square where an en passant capture could occur
 *         
 *         Examples:  d3, h6 or - if en passant is not valid
 *  
 *  5) halfMoveNumber      the number of moves since a capture or pawn was moved
 *      
 *  6) currentMoveNumber   the current move number in the game
 *  
 *         in the example the 1 corresponds to what the next
 *         move played would be recorded as (ie, 1. e4)
 *  
 *  NOTE: there can be FENs that are invalid such as having the white short castle 
 *        option available but the king is not on his home square or the rook has
 *        moved.  The parser does no validation between the state of the pieces
 *        on the board (first field) and the castlingOptions (third field).
 *  
 *  The parser attempts to parse as many of the six FEN fields as it finds.
 *  You can call it like so,
 *  
 *      FenParser p = new FenParser();
 *      p.init(fenString);
 *      p.parse();
 *      Position pos = p.getPosition();
 *      boolean whiteToMove = p.isWhiteToMove();
 *  
 *  After calling parse(), retrieve the values of the parsed
 *  fields by calling the getters: isWhiteToMove(), getCurrentMoveNumber(), etc.
 *  For fields that are not found during parsing the default will be used.
 *  
 *  If you were to provide only the first field (the board), then
 *  the parser assumes 
 *      white is to move ('w')
 *      no castling options are available (ie '-'),
 *      no en passant option (ie '-')
 *      number of half moves since last capture or pawn move is 0
 *      current move number is 1
 *  </pre>  
 */
public class FenParser {
    private static final int PIECE_BOARD_FIELD         = 0;
	private static final int ON_MOVE_FIELD             = 1;
	private static final int CASTLING_OPTIONS_FIELD    = 2;
	private static final int EN_PASSANT_SQUARE_FIELD   = 3;
	private static final int HALF_MOVE_NUMBER_FIELD    = 4;
	private static final int CURRENT_MOVE_NUMBER_FIELD = 5;

	private boolean nothingToParse = true;
	private int numberOfFieldsFound = 0;
	private String[] field;
	/* provide reasonable defaults, empty board, white to move, no castling, first move */
	private Position position = new Position();
	private int playerOnMove = WHITE;
	private boolean whiteShortCastle = false;
	private boolean whiteLongCastle = false;
	private boolean blackShortCastle = false;
	private boolean blackLongCastle = false;
	private int enPassantSquare = NOSQUARE;
	private int halfMovesSinceCaptureOrPawnAdvance = 0;
	private int currentMoveNumber = 1;
	
	public static FenParser INSTANCE = new FenParser();

	public FenParser(){}

	public FenParser(String fen)
	{
		init(fen);
	}

	public void init(String fen)
	{
		if(!fen.trim().isEmpty())
		{
			field = fen.split(""+FIELD_DELIMITER);
			numberOfFieldsFound = field.length;
		}
        if(numberOfFieldsFound > 0)
        	nothingToParse = false;
	}
	
	
	public void parse()
	{
		if(PIECE_BOARD_FIELD < numberOfFieldsFound) parsePieceBoard();
		if(ON_MOVE_FIELD < numberOfFieldsFound)	parseOnMove();
		if(CASTLING_OPTIONS_FIELD < numberOfFieldsFound) parseCastlingOptions();
		if(EN_PASSANT_SQUARE_FIELD < numberOfFieldsFound) parseEnPassantSquare();
		if(HALF_MOVE_NUMBER_FIELD < numberOfFieldsFound) parseHalfMoveNumber();
		if(CURRENT_MOVE_NUMBER_FIELD < numberOfFieldsFound) parseCurrentMoveNumber();
	}

	/**
	 * Parses pieceBoard and returns a Position.
	 * 
	 * Throws IllegalArgumentException if 
	 *    1) the board does not have eight ranks or
	 *    2) the board has too few or too many pieces or empty squares on a rank
	 *    3) there is more than one white or black king on the board
	 * @return the Position representing the parsed pieceBoard
	 */
	public static Position parsePieceBoard(String pieceBoard)
	{
		FenParser parser = new FenParser(pieceBoard);
		return parser.parsePieceBoard();
	}

	private Position parsePieceBoard()
	{
		/*
		 * Algorithm parses the board as the characters are read from left to
		 * right as found in the FENs board representation.
		 *     
		 *  For example,
		 *  
		 *      k6K/8/5P2/8/8/8/8/8
		 *      
		 *  will be read in starting from the upper left corner of the chess board (at a8).
		 *  From left to right the algorithm parses the string as follows:
		 *  
		 *  	k = blackKing put on a8
		 *      6 = 6 empty squares (b8 to g8)
		 *      K = whiteKing put on h8
		 *      / = move to 7th rank
		 *      8 = eight empty squares (a7 to h7)
		 *      / = move to 6th rank
		 *      5 = five empty squares (a6 to e6)
		 *      P = whitePawn put on f6
		 *      2 = two empty squares (g6 to h6)
		 *      / = move to 5th rank
		 *      and so on.  
		 */
		if(nothingToParse)
			throw new IllegalStateException("need something to parse; "
					+ "call init(String) or use constructor before calling parse()");

		String toParse = field[PIECE_BOARD_FIELD];
		String[] fenRow = toParse.split(""+RANK_DELIMITER);
		if (fenRow.length != 8)
		{
			throw new IllegalArgumentException("board must contain eight ranks");
		}
		position = new Position();
		int numWhiteKingsPlaced = 0;
		int numBlackKingsPlaced = 0;
		
		int currentSquare = Bitmap.A8;
		for(int index = 0; index < 8; index++)
		{
			String charsOnRow = fenRow[index]; 
			int rankNumber = Math.abs(index - 8); //actual rank on chess board
			if(doNotFitOnRank(charsOnRow))
			{
				throw new IllegalArgumentException("board pieces and empty squares on rank #"+
						rankNumber+" do not fit on eight files: "+charsOnRow);
			}
			for (int charIndex = 0; charIndex < charsOnRow.length(); charIndex++)
			{
				char boardCharacter = charsOnRow.charAt(charIndex);
				if (Character.isDigit(boardCharacter))
				{
					//Skip those empty squares by adding the digit's value
					currentSquare += (boardCharacter - '0');
					//System.out.println("  " + boardCharacter + " empty squares");
				} else {
					int color = (Character.isUpperCase(boardCharacter)) ? WHITE : BLACK;
					int piece = PieceFactory.create(boardCharacter).index();
					if(piece == KING && color == WHITE) numWhiteKingsPlaced++;
					if(piece == KING && color == BLACK) numBlackKingsPlaced++;
					validateOneKingPerColorOrThrow(numWhiteKingsPlaced, numBlackKingsPlaced);
					position.placePiece(color, piece, currentSquare);
					//System.out.println("  " + boardCharacter + " add at " + Util.SqToStr(currentSquare));
		            currentSquare++;
				}
			}	
			//System.out.println("put "+charsOnRow+" on rank #" + rankNumber);
			//move down the board to first square of the rank beneath
			//the current one (a7, a6, a5 ...)
			currentSquare = ((rankNumber - 1) * 8) - 8;
		}
		return position;
	}

	private boolean doNotFitOnRank(String rankFen)
	{
		int len = rankFen.length();
		int filesRead = 0;
		for(int i=0; i < len; i++)
		{
			char c = rankFen.charAt(i);
			if (Character.isDigit(c))
			{
				filesRead += c - '0'; //found empty squares (represents multiple files)
				continue;
			}
			filesRead++; //found a piece (represents a single file)
		}
		if(filesRead != 8)
		{
			return true; //error!
		}
		return false;
	}

	private void validateOneKingPerColorOrThrow(int numWhiteKingsPlaced, 
			int numBlackKingsPlaced) {
		if(numWhiteKingsPlaced > 1)
			throw new IllegalArgumentException("board has too many white kings; wking='K'");
		if(numBlackKingsPlaced > 1)
			throw new IllegalArgumentException("board has too many black kings; bking='k'");
	}

	private void parseOnMove() {
		String toParse = field[ON_MOVE_FIELD].trim();
		char onMove = toParse.charAt(0);
	    if (WHITE_ON_MOVE == onMove) playerOnMove = WHITE;
	    else if (BLACK_ON_MOVE == onMove) playerOnMove = BLACK;
	    else 
	    	throw new IllegalArgumentException("onMove '"+toParse+"' is invalid; "
	    		+ "use 'w' for white or 'b' for black");
	    
	}

	private void parseCastlingOptions() {
		String toParse = field[CASTLING_OPTIONS_FIELD];
		if(toParse.length() > 4 || toParse.trim().isEmpty())
		{
			throw new IllegalArgumentException("castling options '" + toParse + "' "
					+ "must not be empty or exceed four characters; use only characters from KQkq");
		}
        for (char option: toParse.toCharArray())
        {
        	switch(option)
        	{
        	case UNSET:
        		whiteShortCastle = false;
        		whiteLongCastle = false;
        		blackShortCastle = false;
        		blackLongCastle = false;
        		break;
        	case WHITE_SHORT_CASTLE_OPTION:
        		whiteShortCastle = true;
        		break;
        	case WHITE_LONG_CASTLE_OPTION:
        		whiteLongCastle = true;
        		break;
        	case BLACK_SHORT_CASTLE_OPTION:
        		blackShortCastle = true;
        		break;
        	case BLACK_LONG_CASTLE_OPTION:
        		blackLongCastle = true;
        		break;
        	default:
    			throw new IllegalArgumentException("castling options '" + toParse + "' "
    					+ "are invalid; use only characters from KQkq or - for none");
        	}
        }
	}

	/*
	 * Parses and returns the square (number) where an en passant capture can occur.
	 * If no square is specified {@code NOSQUARE} is returned.
	 * @return an int representing the parsed en passant square
	 */
	private void parseEnPassantSquare() {
		String toParse = field[EN_PASSANT_SQUARE_FIELD];
	    if (UNSET == toParse.charAt(0)){
	        enPassantSquare = NOSQUARE;
	    } else {
	    	int unvalidatedSquare = Square.squareOf(toParse);
	    	enPassantSquare = validEnPassantSquare(isWhiteToMove(), unvalidatedSquare);
	    }
	}

	/*
	 * Parses the number of half moves since the last irreversible
	 * move--a capture or pawn advance.
	 * If the parsed value is negative the default value is used.
	 */
	private void parseHalfMoveNumber() {
		String toParse = field[HALF_MOVE_NUMBER_FIELD];
		int n = Integer.parseInt(toParse);
		halfMovesSinceCaptureOrPawnAdvance = 0;
	    if (n > 0){
	    	halfMovesSinceCaptureOrPawnAdvance = n;
	    }
	}

	/*
	 * Parses the current move number. If the parsed value
	 * is less than or equal to zero the default value is used.
	 */
	private void parseCurrentMoveNumber() {
		String toParse = field[CURRENT_MOVE_NUMBER_FIELD];
	    int n = Integer.parseInt(toParse);
	    if (n > 0){
	    	currentMoveNumber = n;
	    }  
	}
	
	public Position getPosition()
	{
		return position;
	}

	public boolean isWhiteToMove() {
		return playerOnMove == WHITE;
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
		return halfMovesSinceCaptureOrPawnAdvance;
	}

	public int getCurrentMoveNumber() {
		return currentMoveNumber;
	}
}
