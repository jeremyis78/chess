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
 * @author jeremy
 *
 */
public class GameState {

	//*******************************************************************
	//*                                                                 *
	//* +--------+--------+--------+--------+-------------+             *
	//* |  bit 0 |  bit 1 |  bit 2 |  bit 3 | bits 4 - 7  |             *
	//* +--------+--------+--------+--------+-------------+             *
	//* |      WHITE      |      BLACK      |             |             *
	//* | short  | long   | short  | long   |   unused    |             *
	//* | castle | castle | castle | castle |             |             *
	//* +--------+--------+--------+--------+-------------+             *
	//*                                                                 *
	//*******************************************************************
	public static final int W_SHORT_CASTLE = 1;
	public static final int W_LONG_CASTLE  = 2;
	public static final int B_SHORT_CASTLE = 4;
	public static final int B_LONG_CASTLE  = 8;

	private static final String BLACK_TO_MOVE_FLAG = "b";
	private static final String WHITE_TO_MOVE_FLAG = "w";
	private static final String UNSET_FLAG = "-";
	public static final String FEN_START = new String(
	    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

	public static final int MAX_DEPTH = 150;
	private static final int NO_CASTLING_ALLOWED = 0;

	final byte CASTLE_START = 
		W_SHORT_CASTLE | W_LONG_CASTLE | B_SHORT_CASTLE | B_LONG_CASTLE; 
			
    private Position pos;
    private boolean whiteToMove = true;
    int depth;			//current depth of search
    long attacked[] = new long[MAX_DEPTH];
    private int castle[] = new int[MAX_DEPTH + 1];
    private byte enPassantSq[] = new byte[MAX_DEPTH + 1];
    private byte halfMoveClock[] = new byte[MAX_DEPTH + 1];
    private byte fullMoveClock[] = new byte[MAX_DEPTH];//dont' think this needs to be an array
    
    /*
     * TODO: The following member vars are not related to the state of the game (board or flags)
     * So they should be refactored into Search or a search context object
     */
    int currentLine[] = new int[MAX_DEPTH]; //line we are searching
    int bestLine[] = new int[MAX_DEPTH];    //the best line of play
    int numberOfLinesToMate;    //# of lines of play that lead to mate
    int numberOfLegalMoves[] = new int[MAX_DEPTH];  //no. of moves at this depth
    int numberOfLegalMovesToDepth[] = new int[MAX_DEPTH]; //no of moves UP to current depth
    int moves[] = new int[100];             //legal moves from this state
    int movesValue[] = new int[100];        //minimax value of the moves from this state 
    int nodes = 0;                  //nodes searched
    int best = 0;                   //best value seen in search
    int movesIndex; 
    
    //TODO: need a variable or something to keep track of 
    //      the number of repeated positions 
    //      (for claiming/declaring a draw)
    // I think the only way to do this is to have a transposition table
    // whose key is the current position of the board.
    //
    
	public GameState()
	{
		pos = new Position();
	    //TODO: fix this so it gets all the array indices 
	    //filled (MAXDEPTH +1) see definitions.h for gamestate
	    numberOfLinesToMate = 0;
	    for (int i = 0; i < MAX_DEPTH; i++){
	        castle[i] = 0;
	        enPassantSq[i] = NOSQUARE;	//No En Passant Target Square at start
	        halfMoveClock[i] = 0;
	        attacked[i] = 0;		
	        numberOfLegalMoves[i] = 0;
	        numberOfLegalMovesToDepth[i] = 0;
	        currentLine[i] = 0;
	        bestLine[i] = 0;
	    }             
	    castle[0] = CASTLE_START;
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
        setBoard(fields[0]);
        setSide(fields[1]);
        setCastle(fields[2]);
        setEnPassant(fields[3]);
        setHalfMoveClock(fields[4]);
        setFullMoveClock(fields[5]);
	}
	
	public String get()
	{
		StringBuilder fen = new StringBuilder();
		fen.append(getBoard());
		fen.append(" ");
		fen.append(getSide());
		fen.append(" ");
		fen.append(getCastle());
		fen.append(" ");
		fen.append(getEnPassant());
		fen.append(" ");
		fen.append(getHalfMoveClock());
		fen.append(" ");
		fen.append(getFullMoveClock());
		return fen.toString();
	}

	void setBoard(final String s)
	{
	    pos.set(s);  //throws error if any
	}

	/**
	 * Gets the pieces on the board at the current depth.
	 * 
	 * @return
	 */
	private String getBoard()
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
	
	void setSide(final String s)
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

	private String getSide()
	{
		return (whiteToMove) ? WHITE_TO_MOVE_FLAG : BLACK_TO_MOVE_FLAG;
	}

	void setCastle(final String castlingFlags)
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
				appendCastlingFlag(W_SHORT_CASTLE);
			}
			if('Q' == flagChar)
			{
				appendCastlingFlag(W_LONG_CASTLE);
			}
			if('k' == flagChar)
			{
				appendCastlingFlag(B_SHORT_CASTLE);
			}
			if('q' == flagChar)
			{
				appendCastlingFlag(B_LONG_CASTLE);
			}
        }
	}
	
	private void appendCastlingFlag(int castlingFlag)
	{
		castle[0] |= castlingFlag; 
	}
	
	private void overwriteCastlingFlags(int castlingFlags)
	{
		castle[0] = castlingFlags;
	}
	
	/**
	 * Get the castling FEN field at the current state (ie, at current depth)
	 */
	private String getCastle()
	{
		StringBuilder castleFen = new StringBuilder();
		int castleBitmap = castle[depth];
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

	public boolean hasShortCastleOption(boolean isWhitesMove, int depth)
	{
		if(isWhitesMove) 
			return bool(castle[depth] & W_SHORT_CASTLE);
		else
			return bool(castle[depth] & B_SHORT_CASTLE);
	}

	public boolean hasLongCastleOption(boolean isWhitesMove, int depth)
	{
		if(isWhitesMove) 
			return bool(castle[depth] & W_LONG_CASTLE);
		else
			return bool(castle[depth] & B_LONG_CASTLE);
	}

	void setEnPassant(final String s)
	{
	    if (UNSET_FLAG.equals(s)){
	        enPassantSq[0] = NOSQUARE;
	        return;
	    }
	    int sq = StrToSq(s);
	    if(whiteToMove && Util.notOnSixthRank(sq))
	    {
	        throw new IllegalArgumentException("Given '"+getSide()+"' to move, the "
	        		+ "en passant square '"+s+"' ought to be on the 6th rank");	    	
	    }
	    if(!whiteToMove && Util.notOnThirdRank(sq))
	    {
	        throw new IllegalArgumentException("Given '"+getSide()+"' to move, the "
	        		+ "en passant square '"+s+"' ought to be on the 3rd rank");
	    }
        enPassantSq[0] = (byte) sq;
	}

	/**
	 * Get the en-passant FEN field at the current state (ie, at current depth)
	 * The returned value should be on the 3rd or 6th rank (the only valid values)
	 * @return square where capture via en passant can occur
	 */
	String getEnPassant()
	{
		byte epSquare = enPassantSq[depth];
		if(epSquare == NOSQUARE)
			return UNSET_FLAG;
		return SqToStr(epSquare);
	}
	
	public int getEnPassantSquare(int depth)
	{
		return enPassantSq[depth];
	}

	void setHalfMoveClock(final String s)
	{
	    byte n = Byte.parseByte(s);
	    if (n < 0){
	    	throw new IllegalArgumentException("Half move clock '"+s+"' "
	    			+ "must be zero or greater");
	    }
	    halfMoveClock[depth] = n;
	}

	/**
	 * Gets the number of halfMoves since the last irreversible 
	 * move (any capture or pawn move) at the current depth.
	 * @return the halfMoveClock
	 */
	private byte getHalfMoveClock() {
		return halfMoveClock[depth];
	}

	void setFullMoveClock(final String s)
	{
	    byte n = Byte.parseByte(s);
	    if (n <= 0){
	    	throw new IllegalArgumentException("Move number '"+s+"' must be greater than zero");
	    }  
	    fullMoveClock[0] = n;
	}

	/**
	 * Gets the current move number at the current depth
	 * @return the fullMoveClock
	 */
	private byte getFullMoveClock() {
		return fullMoveClock[depth];
	}

	boolean movesLeft(){
	  if (movesIndex < (numberOfLegalMovesToDepth[depth] + numberOfLegalMoves[depth]))
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
	    int from, to;           //squares involved
	    int moving, captured, promotion;  //pieces involved

	    from = move & 0x3F;                         //first 6 bits
	    to = (move >> 6) & 0x3F;                    //next 6
	    moving = TO_PIECE[(move >> 12) & 0x7];      //next 3
	    captured = TO_PIECE[(move >> 15) & 0x7];    //next 3
	    promotion = TO_PIECE[(move >> 18) & 0x7];   //next 3

	    // If it's a king moving, check for castling
	    // If it's a rook moving, remove castling for that side if still available
	    // If it's a pawn capture check for en passant capture 
	    // or advancing two squares

	    if (moving == KING){
		    updateCastlingFlagsWhenKingMoves(isWhitesMove);
		    int rookFrom = correspondingRookIfKingCastled(from, to, isWhitesMove);
			if(rookFrom != NOSQUARE)
			{
				if(isOnGFile(to)){
					moveRook(rookFrom, squareLeftOf(to), isWhitesMove);
				} else if (isOnCFile(to)) {
					moveRook(rookFrom, squareRightOf(to), isWhitesMove);
				}
			}
	    }
	    else if (moving == ROOK)
	    {
	        updateCastlingFlagsWhenRookMoves(from, isWhitesMove);             
	    } else {
	        //EnPassant
	        if( isEnPassantCapture(moving, to, captured))
	        {
	        	pos.erasePiece(squareBehind(to, isWhitesMove?0:1));
	        	captured = NONE;
	        } else if (moving == PAWN && twoSquaresBehind(to, isWhitesMove?0:1) == from) {
	            //Pawn advances two squares
	        	updateEnPassantSquare(from, isWhitesMove);
	        }
	        duplicateCastlingFlags();
	    }

	    //Move the piece
	    if(captured != NONE){
	        pos.erasePiece(to);
	    }
	    pos.erasePiece(from);
	    if(promotion != NONE) {
	    	placePiece(promotion, to, isWhitesMove);
	    } else {
	    	placePiece(moving, to, isWhitesMove);
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
	    depth++;
	    
	    //Compute the number of moves that come before
	    //this depth in the moves[] array
	    for(int i = 0; i < depth; i++)
	        numberOfLegalMovesToDepth[depth] += numberOfLegalMoves[i];
	    movesIndex = 0;
	    
	    return false;
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
	    int from, to;           //squares involved
	    int moving, captured;

	    from = move & 0x3F;                         //first 6 bits
	    to = (move >> 6) & 0x3F;                    //next 6
	    moving = TO_PIECE[(move >> 12) & 0x7];      //next 3
	    captured = TO_PIECE[(move >> 15) & 0x7];    //next 3
	    
	    // Undo any moves made here at this depth
	    // by setting legalMoves to zero.
	    numberOfLegalMoves[depth] = 0;
	    halfMoveClock[depth] = 0;
	    
	    //Undo the depth
	    depth--;
	    
	    //Undo the moving piece
	    pos.erasePiece(to);  //NOTE: also erases any promotion piece that was placed there
	    placePiece(moving, from, isWhitesMove);

	    //Undo a castling move (that is, undo the rook move)
	    if (moving == KING){
		    int rookFrom = correspondingRookIfKingCastled(from, to, isWhitesMove);
			if(rookFrom != NOSQUARE)
			{
				if(isOnGFile(to)){
					moveRook(squareLeftOf(to), rookFrom, isWhitesMove);
				} else if (isOnCFile(to)) {
					moveRook(squareRightOf(to), rookFrom, isWhitesMove);
				}
			}
	    } 
	    //NOTE: Castling flags are stored on the castle stack (array) so simply
	    //undoing the depth (i.e. depth--) undoes any castling flag changes.
	    
	    //Place captured piece back on the board
	    if(captured != NONE){
	        if(isEnPassantCapture(moving, to, captured))
	        {
	        	placeOpposingPiece(PAWN, squareBehind(to, isWhitesMove?0:1), isWhitesMove);
	        } else { //Normal capture
	        	placeOpposingPiece(captured, to, isWhitesMove);
	        }
	    }
	    whiteToMove = isWhitesMove; //we're undoing the move for side so it will still be side's turn to move
	    return false;
	}

	private void placePiece(int piece, int to, boolean isWhitesMove)
	{
		int side = (isWhitesMove ? 0 : 1);
    	pos.placePiece(side, piece, to);
	}

	private void placeOpposingPiece(int piece, int to, boolean isWhitesMove)
	{
		int side = (isWhitesMove ? 0 : 1);
    	pos.placePiece(opposing(side), piece, to);
	}
	
	private int correspondingRookIfKingCastled(int from, int to, boolean isWhitesMove) {
		int rookSquare = NOSQUARE;
		if (isWhitesMove && isWhiteShortCastle(from, to)){
			rookSquare = H1;
		} else if (isWhitesMove && isWhiteLongCastle(from, to)){
			rookSquare =  A1;
		} else if (!isWhitesMove && isBlackShortCastle(from, to)){
			rookSquare =  H8;
		} else if (!isWhitesMove && isBlackLongCastle(from, to)){
			rookSquare =  A8;
		}
		return rookSquare;
	}

	private void duplicateCastlingFlags() {
		// Castle status remains constant since king and
		// rook did not move
		castle[depth + 1] = castle[depth];
	}

	private void incrementFullMoveClock() {
		fullMoveClock[depth + 1] = (byte)(fullMoveClock[depth] + 1);
	}

	private void duplicateFullMoveClock() {
		fullMoveClock[depth + 1] = fullMoveClock[depth];
	}

	private void incrementHalfMoveClock() {
		halfMoveClock[depth + 1] = (byte)(halfMoveClock[depth] + 1);
	}

	private void resetHalfMoveClock() {
		halfMoveClock[depth + 1] = 0;
	}

	private void moveRook(int rookFrom, int rookTo, boolean isWhitesMove) {
		int side = (isWhitesMove ? 0 : 1);
		pos.erasePiece(rookFrom);
		pos.placePiece(side, ROOK, rookTo);
	}

	private void updateCastlingFlagsWhenKingMoves(boolean isWhitesMove) {
		if(isWhitesMove){
			//white king moved; castling is no longer legal for him
			castle[depth + 1] = castle[depth] & (B_SHORT_CASTLE | B_LONG_CASTLE);
		} else {
			//black king moved; castling is no longer legal for him
			castle[depth + 1] = castle[depth] & (W_SHORT_CASTLE | W_LONG_CASTLE);
		}
	}

	private void updateCastlingFlagsWhenRookMoves(int rookFromSquare, boolean isWhitesMove) {
		if (isWhitesMove){
			if (bool(castle[depth] & W_LONG_CASTLE) && rookFromSquare == A1)
				castle[depth + 1] = castle[depth] & ~W_LONG_CASTLE;
			else if (bool(castle[depth] & W_SHORT_CASTLE) && rookFromSquare == H1)
				castle[depth + 1] = castle[depth] & ~W_SHORT_CASTLE;
		} else {
			if (bool(castle[depth] & B_LONG_CASTLE) && rookFromSquare == A8)
				castle[depth + 1] = castle[depth] & ~B_LONG_CASTLE;
			else if (bool(castle[depth] & B_SHORT_CASTLE) && rookFromSquare == H8)
				castle[depth + 1] = castle[depth] & ~B_SHORT_CASTLE;
		}
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

	private void updateEnPassantSquare(int from, boolean isWhitesMove) {
		enPassantSq[depth + 1] = (byte)squareAhead(from, isWhitesMove?0:1);
	}

	private boolean isEnPassantCapture(int moving, int to, int captured) {
		return moving == PAWN && captured == PAWN && to == enPassantSq[depth];
	}

	void display(){
	    AbstractDisplayer displayer = new Displayer();
	    System.out.println(displayer.formatBoard(pos));
	}
}
