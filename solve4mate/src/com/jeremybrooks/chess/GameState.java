/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 9, 2010
 */
package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import static com.jeremybrooks.chess.Util.*;

/**
 * GameState contains a board representation and flags or metadata.
 * 
 * The flags are castling status, en passant target squares,
 * the halfmove clock and full move number.
 *
 *  TODO: Board!!!!! (that's the best name for this class which would
 *  contain a Pieces (formerly Position) class
 *
 * @author jeremy
 *
 */
public class GameState {

	public static final int W_SHORT_CASTLE = 1;
	public static final int W_LONG_CASTLE  = 2;
	public static final int B_SHORT_CASTLE = 4;
	public static final int B_LONG_CASTLE  = 8;

	private static final String BLACK_TO_MOVE_FLAG = "b";
	private static final String WHITE_TO_MOVE_FLAG = "w";
	private static final String UNSET_FLAG = "-";
	public static final String FEN_START = new String(
	    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

	public static final int MAX_NUM_MOVES_MADE = 150; //N2: far better than 'max depth' in this context
	private static final int NO_CASTLING_ALLOWED = 0;

	final byte CASTLE_START = 
		W_SHORT_CASTLE | W_LONG_CASTLE | B_SHORT_CASTLE | B_LONG_CASTLE; 
			
    private Position pos;
    
    /**
     *  Represents the number of moves (i.e. black moves a pawn) applied to
     *  this game state.  For example, after
     *     MoveNumber    Move   numberOfMovesMade	halfMoveNumber  
     *     1.			e4			1					0
     *     1. 			... e5		2					0
     *     2. 			Nc3			3					1
     *     2. 			... Nf6		4					2
     */
    private int numberOfMovesMade = 0; // N2 !! choose names at appropriate abstraction level
    private int maxNumberOfMovesMade;
    private boolean whiteToMove = true;
    private int castle[];
    private byte enPassantSq[];
    private byte halfMoveClock[];
    private byte fullMoveClock[];
    long attacked[];
    
    /*
     * TODO: The following member vars are not related to the state of the game (board or flags)
     * So they should be refactored into Search or a search context object
     */
    int currentLine[]; //line we are searching
    int bestLine[]; //the best line of play
    int numberOfLinesToMate;    //# of lines of play that lead to mate
    int numberOfLegalMoves[];  //no. of moves at this depth
    int numberOfLegalMovesToDepth[]; //no of moves UP to current depth
    int moves[];             //legal moves from this state
    int movesValue[];        //minimax value of the moves from this state 
    int nodes = 0;                  //nodes searched
    int best = 0;                   //best value seen in search
    int movesIndex; 
    
    //TODO: need a variable or something to keep track of 
    //      the number of repeated positions 
    //      (for claiming/declaring a draw)
    // I think the only way to do this is to have a transposition table
    // whose key is the current position of the board.
    //
    
	public GameState(int maxNumberOfMovesToSupport)
	{
		pos = new Position();
		maxNumberOfMovesMade = maxNumberOfMovesToSupport;
		int maxMoves = maxNumberOfMovesToSupport + 1;
		castle = new int[maxMoves];
		enPassantSq = new byte[maxMoves];
		halfMoveClock = new byte[maxMoves];
		fullMoveClock = new byte[maxMoves];

		attacked = new long[maxMoves];
	    currentLine = new int[maxMoves];
	    bestLine = new int[maxMoves];
	    numberOfLegalMoves = new int[maxMoves];
	    numberOfLegalMovesToDepth = new int[maxMoves];
	    moves = new int[100];
	    movesValue = new int[100]; 
	    nodes = 0;
	    best = 0;

	    //TODO: fix this so it gets all the array indices 
	    //filled (MAXDEPTH +1) see definitions.h for gamestate
	    numberOfLinesToMate = 0;
	    for (int moveNumber = 0;
	    		moveNumber < maxMoves;
	    		moveNumber++){
	        castle[moveNumber] = 0;
	        enPassantSq[moveNumber] = NOSQUARE;
	        halfMoveClock[moveNumber] = 0;
	        fullMoveClock[moveNumber] = 0;
	        attacked[moveNumber] = 0;		
	        numberOfLegalMoves[moveNumber] = 0;
	        numberOfLegalMovesToDepth[moveNumber] = 0;
	        currentLine[moveNumber] = 0;
	        bestLine[moveNumber] = 0;
	    }             
	    castle[numberOfMovesMade] = CASTLE_START;
	    movesIndex = 0;
	    fullMoveClock[0] = 1;
	    whiteToMove = true;
	}

	public void set(final String fen){
	    final int numFields = 6;
    
	    // Read the 'fen' array into variables
	    // Anything after the sixth field and the '#' 
	    // character is a comment so ignore it.
	    String[] fields = fen.split(" ");
        if (fields.length < numFields){
        	throw new IllegalArgumentException("The FEN string '"+fen+"' "
        			+ "needs six space-delimited fields: "
        			+ "board onMove castlingFlags enPassantSquare halfMoveClock moveNumber");
        }
              
        //Initialize board, side, castling, en pas, hmc, and fmc.
        //zeroeth index is moves to mate
        parseBoardFEN(fields[0]);
        parseSideFEN(fields[1]);
        parseCastleFEN(fields[2]);
        parseEnPassantFEN(fields[3]);
        parseHalfMoveNumber(fields[4]);
        parseMoveNumber(fields[5]);
	}
	
	public String get()
	{
		StringBuilder fen = new StringBuilder();
		fen.append(getBoardFEN());
		fen.append(" ");
		fen.append(getSideFEN());
		fen.append(" ");
		fen.append(getCastleFEN());
		fen.append(" ");
		fen.append(getEnPassantFEN());
		fen.append(" ");
		fen.append(getHalfMoveNumber());
		fen.append(" ");
		fen.append(getMoveNumber());
		return fen.toString();
	}

	private void parseBoardFEN(final String s)
	{
	    pos.set(s);  //throws error if any
	}

	/**
	 * Gets the pieces on the board at the current depth.
	 * 
	 * @return
	 */
	private String getBoardFEN()
	{	
		//NOTE: the position is always kept in sync with the depth
		return pos.getFen();
	}
	
	public Position getPosition()
	{
		return pos;
	}

	public boolean isWhiteToMove()
	{
		return whiteToMove;
	}

	public void setWhiteToMove(boolean isWhitesMove)
	{
		whiteToMove = isWhitesMove;
	}
	
	private void parseSideFEN(final String s)
	{
	    if (WHITE_TO_MOVE_FLAG.equals(s)) {
	        setWhiteToMove(true);
	        return;
	    } else if (BLACK_TO_MOVE_FLAG.equals(s)) {
	        setWhiteToMove(false);
	        return;
	    }
        throw new IllegalArgumentException("Side to move '"+s+"' is invalid; use 'w' for white or 'b' for black");
	}

	private String getSideFEN()
	{
		return (whiteToMove) ? WHITE_TO_MOVE_FLAG : BLACK_TO_MOVE_FLAG;
	}

	private void parseCastleFEN(final String castlingFlags)
	{
		if(castlingFlags.length() > 4)
		{
			throw new IllegalArgumentException("Castling flags '" + castlingFlags + "' "
					+ "are invalid; use only characters from KQkq");
		}
		if(UNSET_FLAG.equals(castlingFlags))
		{
			overwriteCastlingFlags(NO_CASTLING_ALLOWED);
			return;
		}
        for (char flagChar: castlingFlags.toCharArray())  //int i = 0; i < castlingFlags.length(); i++){
        {
			if('K' == flagChar)
			{
				addCastlingOption(W_SHORT_CASTLE);
			}
			if('Q' == flagChar)
			{
				addCastlingOption(W_LONG_CASTLE);
			}
			if('k' == flagChar)
			{
				addCastlingOption(B_SHORT_CASTLE);
			}
			if('q' == flagChar)
			{
				addCastlingOption(B_LONG_CASTLE);
			}
        }
	}
	
	/**
	 * Adds the castling option at the current depth
	 * 
	 * @param castlingFlag
	 */
	private void addCastlingOption(int castlingFlag)
	{
		castle[numberOfMovesMade] |= castlingFlag; 
	}

	/**
	 * Overwrites the castling flags with the given flags
	 * 
	 * @param castlingFlags
	 */
	private void overwriteCastlingFlags(int castlingFlags)
	{
		castle[numberOfMovesMade] = castlingFlags;
	}

	/**
	 * Get the castling FEN field at the current state (ie, at current depth)
	 */
	private String getCastleFEN()
	{
		StringBuilder castleFen = new StringBuilder();
		int castleBitmap = castle[numberOfMovesMade];
		if(bool(castleBitmap & W_SHORT_CASTLE))
		{
			castleFen.append("K");
		}
		if(bool(castleBitmap & W_LONG_CASTLE))
		{
			castleFen.append("Q");
		}
		if(bool(castleBitmap & B_SHORT_CASTLE))
		{
			castleFen.append("k");
		}
		if(bool(castleBitmap & B_LONG_CASTLE))
		{
			castleFen.append("q");
		}
		
		if(castleFen.length() == 0)
			return UNSET_FLAG;
		else
			return castleFen.toString();
	}

	public boolean hasShortCastleOption()
	{
		if(whiteToMove) 
			return bool(castle[numberOfMovesMade] & W_SHORT_CASTLE);
		else
			return bool(castle[numberOfMovesMade] & B_SHORT_CASTLE);
	}

	public boolean hasLongCastleOption()
	{
		if(whiteToMove) 
			return bool(castle[numberOfMovesMade] & W_LONG_CASTLE);
		else
			return bool(castle[numberOfMovesMade] & B_LONG_CASTLE);
	}

	private void parseEnPassantFEN(final String s)
	{
	    if (UNSET_FLAG.equals(s)){
	        enPassantSq[numberOfMovesMade] = NOSQUARE;
	        return;
	    }
	    int sq = StrToSq(s);
	    if(whiteToMove && Util.notOnSixthRank(sq))
	    {
	        throw new IllegalArgumentException("Given '"+getSideFEN()+"' to move, the "
	        		+ "en passant square '"+s+"' ought to be on the 6th rank");	    	
	    }
	    if(!whiteToMove && Util.notOnThirdRank(sq))
	    {
	        throw new IllegalArgumentException("Given '"+getSideFEN()+"' to move, the "
	        		+ "en passant square '"+s+"' ought to be on the 3rd rank");
	    }
        enPassantSq[numberOfMovesMade] = (byte) sq;
	}

	/**
	 * Get the en-passant FEN field at the current state (ie, at current depth)
	 * The returned value should be on the 3rd or 6th rank (the only valid values)
	 * @return square where capture via en passant can occur
	 */
	private String getEnPassantFEN()
	{
		if(hasEnPassantOption())
			return SqToStr(getEnPassantSquare());
		return UNSET_FLAG;
	}
	
	public boolean hasEnPassantOption()
	{
		return getEnPassantSquare() != NOSQUARE;
	}

	public int getEnPassantSquare()
	{
		return enPassantSq[numberOfMovesMade];
	}

	private void parseHalfMoveNumber(final String s)
	{
	    byte n = Byte.parseByte(s);
	    if (n < 0){
	    	throw new IllegalArgumentException("Half move clock '"+s+"' "
	    			+ "must be zero or greater");
	    }
	    halfMoveClock[numberOfMovesMade] = n;
	}

	/**
	 * Gets the number of halfMoves since the last irreversible 
	 * move (any capture or pawn move) at the current depth.
	 * @return the halfMoveClock
	 */
	public byte getHalfMoveNumber() {
		return halfMoveClock[numberOfMovesMade];
	}

	void parseMoveNumber(final String s)
	{
	    byte n = Byte.parseByte(s);
	    if (n <= 0){
	    	throw new IllegalArgumentException("Move number '"+s+"' must be greater than zero");
	    }  
	    fullMoveClock[numberOfMovesMade] = n;
	}

	/**
	 * Gets the current move number at the current depth
	 * @return the fullMoveClock
	 */
	public byte getMoveNumber() {
		return fullMoveClock[numberOfMovesMade];
	}

	public int getNumberOfMovesMade()
	{
		return numberOfMovesMade;
	}
	
	boolean movesLeft(){
	  if (movesIndex < (numberOfLegalMovesToDepth[numberOfMovesMade] + numberOfLegalMoves[numberOfMovesMade]))
		return true;
	  else 
		return false;
	}

	@Deprecated
	public boolean makeMove(int move, int side){
		boolean isWhitesMove = (WHITE == side);
		return makeMove(move, isWhitesMove);
	}

	/**
	 * Makes the given move and updates the board's state accordingly.
	 * 
	 * After calling this method you can expect the current depth will be
	 * incremented by 1 and {@code g.isWhiteToMove() == !isWhitesMove}.  The move number, 
	 * castling flags, enPassant square, and half move clock are also updated.
	 * 
	 * @param move the encoded move to make
	 * @param isWhitesMove flag indicating which side is making the move
	 * @return
	 */
	public boolean makeMove(int move, boolean isWhitesMove){
		if(numberOfMovesMade == maxNumberOfMovesMade)
		{
			throw new IllegalStateException("max number of moves have been made: " + maxNumberOfMovesMade);
		}
		if(isWhitesMove != whiteToMove)
		{
			throw new IllegalStateException("isWhiteToMove conflicts with isWhiteToMove()");
		}
		whiteToMove = isWhitesMove;						//cache whose move it is
	    int from = move & 0x3F;                         //first 6 bits
	    int to = (move >> 6) & 0x3F;                    //next 6
	    int moving = TO_PIECE[(move >> 12) & 0x7];      //next 3
	    int captured = TO_PIECE[(move >> 15) & 0x7];    //next 3
	    int promotion = TO_PIECE[(move >> 18) & 0x7];   //next 3
	    if (moving == KING){
		    updateCastlingOptionsWhenKingMoves();
		    int rookFrom = correspondingRookIfKingCastled(from, to);
			if(rookFrom != NOSQUARE)
			{
				if(isOnGFile(to)){
					moveRook(rookFrom, squareLeftOf(to));
				} else if (isOnCFile(to)) {
					moveRook(rookFrom, squareRightOf(to));
				}
			}
	    }
	    else if (moving == ROOK)
	    {
	        updateCastlingOptionsWhenRookMoves(from);             
	    } else {
	        if( isEnPassantCapture(moving, to, captured))
	        {
	        	pos.erasePiece(squareBehind(to, isWhitesMove?0:1));
	        	captured = NONE;
	        } else if (isPawnAdvancingTwoSquares(moving, from, to)) {
	        	updateEnPassantSquareForNextMove(from);
	        }
	        duplicateCastlingFlags();
	    }

	    //Move the piece
	    if(captured != NONE){
	        pos.erasePiece(to);
	    }
	    pos.erasePiece(from);
	    if(promotion != NONE) {
	    	placePiece(promotion, to);
	    } else {
	    	placePiece(moving, to);
	    }

	    if (isIrreversibleMove(moving, captured)){
	        resetHalfMoveClock();
	    } else {
	    	incrementHalfMoveClock();
	    }

	    if(isWhitesMove)
	    	duplicateFullMoveClock();
	    else
	    	incrementFullMoveClock();

	    whiteToMove = !isWhitesMove;
	    numberOfMovesMade++;
	    
	    //Compute the number of moves that come before
	    //this depth in the moves[] array
	    for(int i = 0; i < numberOfMovesMade; i++)
	        numberOfLegalMovesToDepth[numberOfMovesMade] += numberOfLegalMoves[i];
	    movesIndex = 0;
	    return false;
	}

	private boolean isPawnAdvancingTwoSquares(int moving, int from, int to) {
		return moving == PAWN && from == twoSquaresBehind(to, whiteToMove?0:1);
	}

	private boolean isIrreversibleMove(int moving, int captured) {
		return captured != NONE || moving == PAWN;
	}

	@Deprecated
	public boolean undoMove(int move, int side){
		boolean isWhitesMove = (WHITE == side);
		return undoMove(move, isWhitesMove);
	}

	/**
	 * Undoes the given move by the given side on move.
	 * 
	 * After calling this method you can expect the current depth will be
	 * decremented by 1 and {@code g.isWhiteToMove() == isWhitesMove}.  
     *
	 * All of the flags should (I think) remain untouched because undoing a move
	 * is simply decrementing the depth (ie, popping the stack)
	 * 
	 * @param move the encoded move to undo
	 * @param isWhitesMove flag indicating which side is having their move undone
	 * @return
	 */
	public boolean undoMove(int move, boolean isWhitesMove){
		if(numberOfMovesMade == 0)
		{
			throw new IllegalStateException("no moves to undo; call makeMove() first");
		}
		whiteToMove = isWhitesMove;
	    int from = move & 0x3F;                         //first 6 bits
	    int to = (move >> 6) & 0x3F;                    //next 6
	    int moving = TO_PIECE[(move >> 12) & 0x7];      //next 3
	    int captured = TO_PIECE[(move >> 15) & 0x7];    //next 3
	    
	    // Undo any moves made here at this depth
	    // by setting legalMoves to zero.
	    numberOfLegalMoves[numberOfMovesMade] = 0;
	    halfMoveClock[numberOfMovesMade] = 0;
	    
	    //Undo the depth
	    numberOfMovesMade--;
	    
	    //Undo the moving piece
	    pos.erasePiece(to);  //NOTE: also erases any promotion piece that was placed there
	    placePiece(moving, from);

	    //Undo a castling move (that is, undo the rook move)
	    if (moving == KING){
		    int rookFrom = correspondingRookIfKingCastled(from, to);
			if(rookFrom != NOSQUARE)
			{
				if(isOnGFile(to)){
					moveRook(squareLeftOf(to), rookFrom);
				} else if (isOnCFile(to)) {
					moveRook(squareRightOf(to), rookFrom);
				}
			}
	    } 
	    //NOTE: Castling flags are stored on the castle stack (array) so simply
	    //decrementing the numberOfMovesMade undoes any castling flag changes.
	    
	    //Place captured piece back on the board
	    if(isEnPassantCapture(moving, to, captured))
	    {
	    	placeOpposingPiece(PAWN, squareBehind(to, whiteToMove?0:1));
	    } else if (captured != NONE) { //Normal capture
	    	placeOpposingPiece(captured, to);
	    }
	    return false;
	}

	private void placePiece(int piece, int to)
	{
		int side = (whiteToMove?0:1);
    	pos.placePiece(side, piece, to);
	}

	private void placeOpposingPiece(int piece, int to)
	{
		int side = (whiteToMove?0:1);
    	pos.placePiece(opposing(side), piece, to);
	}
	
	private int correspondingRookIfKingCastled(int from, int to) {
		int rookSquare = NOSQUARE;
		if (whiteToMove)
		{
			if(isWhiteShortCastle(from, to)) rookSquare = H1;
			else
			if (isWhiteLongCastle(from, to)) rookSquare =  A1;
		} else {
			if(isBlackShortCastle(from, to)) rookSquare =  H8;
			else
			if (isBlackLongCastle(from, to)) rookSquare =  A8;
		}
		return rookSquare;
	}

	private void duplicateCastlingFlags() {
		// Castle status remains constant since king and
		// rook did not move
		castle[numberOfMovesMade + 1] = castle[numberOfMovesMade];
	}

	private void incrementFullMoveClock() {
		fullMoveClock[numberOfMovesMade + 1] = (byte)(fullMoveClock[numberOfMovesMade] + 1);
	}

	private void duplicateFullMoveClock() {
		fullMoveClock[numberOfMovesMade + 1] = fullMoveClock[numberOfMovesMade];
	}

	private void incrementHalfMoveClock() {
		halfMoveClock[numberOfMovesMade + 1] = (byte)(halfMoveClock[numberOfMovesMade] + 1);
	}

	private void resetHalfMoveClock() {
		halfMoveClock[numberOfMovesMade + 1] = 0;
	}

	private void moveRook(int rookFrom, int rookTo) {
		int side = (whiteToMove?0:1);
		pos.erasePiece(rookFrom);
		pos.placePiece(side, ROOK, rookTo);
	}

	private void updateCastlingOptionsWhenKingMoves() {
		if(whiteToMove){
			removeCastlingOptionForNextMove(W_SHORT_CASTLE | W_LONG_CASTLE);
		} else {
			removeCastlingOptionForNextMove(B_SHORT_CASTLE | B_LONG_CASTLE);
		}
	}

	private void updateCastlingOptionsWhenRookMoves(int rookFromSquare) {
		if (whiteToMove){
			if (hasShortCastleOption() && rookFromSquare == H1)
				removeCastlingOptionForNextMove(W_SHORT_CASTLE);
			else if (hasLongCastleOption() && rookFromSquare == A1)
				removeCastlingOptionForNextMove(W_LONG_CASTLE);
		} else {
			if (hasShortCastleOption() && rookFromSquare == H8)
				removeCastlingOptionForNextMove(B_SHORT_CASTLE);
			else if (hasLongCastleOption() && rookFromSquare == A8)
				removeCastlingOptionForNextMove(B_LONG_CASTLE);
		}
	}

	private void removeCastlingOptionForNextMove(int castlingOption)
	{
		castle[numberOfMovesMade + 1] = castle[numberOfMovesMade] & ~castlingOption;
	}

	private boolean isBlackLongCastle(int from, int to) {
		return from == E8 && to == C8;
	}

	private boolean isBlackShortCastle(int from, int to) {
		return from == E8 && to == G8;
	}

	private boolean isWhiteLongCastle(int from, int to) {
		return from == E1 && to == C1;
	}

	private boolean isWhiteShortCastle(int from, int to) {
		return from == E1 && to == G1;
	}

	private void updateEnPassantSquareForNextMove(int from) {
		enPassantSq[numberOfMovesMade + 1] = (byte)squareAhead(from, whiteToMove?0:1);
	}

	private boolean isEnPassantCapture(int moving, int to, int captured) {
		return moving == PAWN && captured == PAWN && to == getEnPassantSquare();
	}

	void display(){
	    AbstractDisplayer displayer = new Displayer();
	    System.out.println(displayer.formatBoard(pos));
	}
}
