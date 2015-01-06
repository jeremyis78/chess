/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 9, 2010
 */
package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static com.jeremybrooks.chess.base.Square.*;
import static com.jeremybrooks.chess.util.Util.opposing;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.movegen.Attacks;
import com.jeremybrooks.chess.util.FenBuilder;
import com.jeremybrooks.chess.util.FenParser;
import com.jeremybrooks.chess.util.Util;
import com.jeremybrooks.chess.util.ZobristKey;

/**
 * GameState contains a board representation and flags for each move that was made.
 * 
 * The flags are castling status, the en passant target square,
 * the halfmove clock and full move number.
 *
 *  makeMove() updates the board to make the given move on the internal
 *  representation, updates the side to move, and increments the 
 *  numberOfMovesMade (getNumberOfMovesMade()). 
 *  
 *  undoMove() reverses the changes made by the given move, reverts side
 *  to move and decrements numberOfMovesMade.
 *  
 *  numberOfMovesMade is akin to ply or depth in other engines; it does NOT represent 
 *  the full move clock or move number.  For example, after
 *  <pre>
 *  -----------------------------------------------------------------------
 *  | Chess Notation |              GameState members                     |
 *  |---------------------------------------------------------------------|
 *  |                |     numberOfMovesMade  halfMoveNumber   moveNumber | 
 *  |    1. e4       |            1                  0             1      |
 *  |    1...e5      |            2                  0             1      |
 *  |    2. Nc3      |            3                  1             2      |
 *  |    2...Nf6     |            4                  2             2      |
 *  -----------------------------------------------------------------------
 *  </pre>
 *  TODO: Board!!!!! (that's the best name for this class which would
 *  contain a Pieces (formerly Position) class (BoardStack might
 *  be more accurate, given it's a stack of states/boards)
 *
 * @author jeremy
 *
 */
public class GameState {
    private static final Logger log = Logger.getLogger(GameState.class); 

    public static final int W_SHORT_CASTLE = 1;
    public static final int W_LONG_CASTLE  = 2;
    public static final int B_SHORT_CASTLE = 4;
    public static final int B_LONG_CASTLE  = 8;

    public static final String FEN_START = new String(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    public static final int MAX_NUM_MOVES_MADE = 150; //N2: far better than 'max depth' in this context

    private Position pos;
    private PositionInfo[] posInfo;
    private int numberOfMovesMade = 0; // N2 !! choose names at appropriate abstraction level
    private int maxNumberOfMovesMade;
    private boolean whiteToMove = true;

    public GameState()
    {
        this(MAX_NUM_MOVES_MADE);
    }

    /**
     * Constructor useful for an instance when we don't need the entire stack (ie, for testing).
     * 
     * @param maxNumberOfMovesToSupport
     */
    public GameState(int maxNumberOfMovesToSupport)
    {
        pos = new Position();
        log.trace("initializing stack to depth " + maxNumberOfMovesToSupport);
        maxNumberOfMovesMade = maxNumberOfMovesToSupport;
        int maxMoves = maxNumberOfMovesToSupport + 1;
        posInfo = new PositionInfo[maxMoves];
        for (int moveNumber = 0;
                moveNumber < maxMoves;
                moveNumber++){
            posInfo[moveNumber] = new PositionInfo();
        }             
        posInfo[0].setMoveNumber(1);
        posInfo[0].setCastleOptionsFromFen("KQkq");
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

        FenParser parser = new FenParser(fen);
        parser.parse();
        pos = parser.getPosition();
        setWhiteToMove(parser.isWhiteToMove());
        setCastlingOptions(parser.getCastlingOptions());
        setEnPassantSquare(parser.getEnPassantSquare());
        setHalfMoveNumber(parser.getOperandInt(FenParser.OPCODE_HMVC));
        setMoveNumber(parser.getOperandInt(FenParser.OPCODE_FMVN));
    }
    
    public String get()
    {
        FenBuilder fb = new FenBuilder();
        fb.appendPieceBoard(pos);
        fb.appendOnMove(isWhiteToMove());
        fb.appendCastlingOptions(posInfo[numberOfMovesMade].getCastleOptionsAsFen());
        fb.appendEnPassantSquare(posInfo[numberOfMovesMade].getEnPassantSquare());
        fb.appendHalfMoveNumber(posInfo[numberOfMovesMade].getReversiblePlies());
        fb.appendCurrentMoveNumber(posInfo[numberOfMovesMade].getMoveNumber());
        return fb.toString();
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

    /**
     * Sets the castling options to the given flags (overwrites)
     * 
     * @param castlingFlags
     */
    private void setCastlingOptions(String castlingOptions)
    {
        posInfo[numberOfMovesMade].setCastleOptionsFromFen(castlingOptions);
    }

    public boolean hasShortCastleOption()
    {
        if(whiteToMove)
            return posInfo[numberOfMovesMade].hasShortCastleOption(WHITE);
        else
            return posInfo[numberOfMovesMade].hasShortCastleOption(BLACK);
    }

    public boolean hasLongCastleOption()
    {
        if(whiteToMove) 
            return posInfo[numberOfMovesMade].hasLongCastleOption(WHITE);
        else
            return posInfo[numberOfMovesMade].hasLongCastleOption(BLACK);
    }

    public boolean hasEnPassantOption()
    {
        return getEnPassantSquare() != NOSQUARE;
    }

    public int getEnPassantSquare()
    {
        return posInfo[numberOfMovesMade].getEnPassantSquare();
    }

    public void setEnPassantSquare(int enPassantSquare)
    {
        posInfo[numberOfMovesMade].setEnPassantSquare((byte) enPassantSquare);
    }

    /**
     * Gets the number of halfMoves since the last irreversible 
     * move (any capture or pawn move) at the current depth.
     * @return the halfMoveClock
     */
    public byte getHalfMoveNumber() {
        return (byte) posInfo[numberOfMovesMade].getReversiblePlies();
    }

    public void setHalfMoveNumber(int halfMoves)
    {
        posInfo[numberOfMovesMade].setReversiblePlies((byte) halfMoves);
    }
    
    /**
     * Gets the current move number at the current depth
     * @return the fullMoveClock
     */
    public byte getMoveNumber() {
        return (byte) posInfo[numberOfMovesMade].getMoveNumber();
    }
    
    public void setMoveNumber(int moveNumber)
    {
        posInfo[numberOfMovesMade].setMoveNumber(moveNumber);
    }

    public int getNumberOfMovesMade()
    {
        return numberOfMovesMade;
    }
    
    @Deprecated
    public boolean makeMove(int move, int side){
        boolean isWhitesMove = (WHITE == side);
        return makeMove(move, isWhitesMove);
    }

    /**
     * Makes the given move and updates the board's state accordingly.
     * 
     * After calling this method you can expect the numberOfMovesMade will be
     * incremented by 1 and {@code g.isWhiteToMove() == !isWhitesMove}.  The move number, 
     * castling flags, enPassant square, and half move clock are also updated.
     * 
     * @param move the encoded move to make
     * @param isWhitesMove flag indicating which side is making the move
     * @return
     */
    public boolean makeMove(int move, boolean isWhitesMove){
        if(log.isTraceEnabled()) log.trace(indent() + formatCurrentLine());
        if(numberOfMovesMade == maxNumberOfMovesMade)
        {
            throw new IllegalStateException("max number of moves have been made: " + maxNumberOfMovesMade);
        }
        if(isWhitesMove != whiteToMove)
        {
            throw new IllegalStateException("isWhiteToMove conflicts with isWhiteToMove()");
        }
        whiteToMove = isWhitesMove;                        //cache whose move it is
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
                erasePiece(squareBehind(to, isWhitesMove?0:1));
                captured = NONE;
            } else if (isPawnAdvancingTwoSquares(moving, from, to)) {
                updateEnPassantSquareForNextMove(squareAhead(from, whiteToMove?0:1));
            }
            duplicateCastlingFlags();
        }

        //Move the piece
        if(captured != NONE){
            erasePiece(to);
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
        
        if(log.isTraceEnabled()) log.trace(indent()+"after make " +  Util.displayMoveStr(move, false, false) + "                   EP is "+named(getEnPassantSquare()));
        return false;
    }

    private String formatCurrentLine() {
//        StringBuilder current = new StringBuilder();
//        for(int i=0; i<numberOfMovesMade; i++){
//            String moveNo = (i%2==0) ? ((i+2)/2) + ". " : "";
//            current.append(moveNo+Util.displayMoveStr(currentLine[i], false, false)+ " ");
//        }
        return "<empty>";// current.toString();
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
        
        posInfo[numberOfMovesMade].setReversiblePlies(0); //Is this needed???
        
        //Undo the depth
        numberOfMovesMade--;
        
        //Undo the moving piece
        erasePiece(to);  //NOTE: also erases any promotion piece that was placed there
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
        
        if(isPawnAdvancingTwoSquares(moving, from, to)){
            updateEnPassantSquareForNextMove(NOSQUARE);
        }
        //Place captured piece back on the board
        if(isEnPassantCapture(moving, to, captured))
        {
            placeOpposingPiece(PAWN, squareBehind(to, whiteToMove?0:1));
        } else if (captured != NONE) { //Normal capture
            placeOpposingPiece(captured, to);
        }
        if(log.isTraceEnabled()) log.debug(indent()+"after undo " +  Util.displayMoveStr(move, false, false) + "                   EP is "+named(getEnPassantSquare()));
        return false;
    }

    public long fullZobristKey()
    {
        long hash = 0L;
        for(int square = A1; square <= H8; square++)
        {
            Piece piece = pos.get(square);
            if(piece.exists())
            {
                hash ^= ZobristKey.get(piece, square);
            }
        }
        return hash;
    }
    
    
    private String indent() {
        String indent = "";
        int depth = numberOfMovesMade;
        while(depth-- > 0) indent += " ";
        return indent;
    }

    private void placePiece(int piece, int square)
    {
        int side = (whiteToMove?WHITE:BLACK);
        pos.placePiece(side, piece, square);
    }

    private void placeOpposingPiece(int piece, int square)
    {
        int side = (whiteToMove?WHITE:BLACK);
        pos.placePiece(opposing(side), piece, square);
    }
    
    private void erasePiece(int square)
    {
        pos.erasePiece(square);
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
        int existingCastleOptions = posInfo[numberOfMovesMade].getCastleOptions();
        posInfo[numberOfMovesMade + 1].setCastleOptions(existingCastleOptions);
    }

    private void incrementFullMoveClock() {
        int existingMoveNumber = posInfo[numberOfMovesMade].getMoveNumber();
        posInfo[numberOfMovesMade + 1].setMoveNumber(existingMoveNumber + 1);
    }

    private void duplicateFullMoveClock() {
        int existingMoveNumber = posInfo[numberOfMovesMade].getMoveNumber();
        posInfo[numberOfMovesMade + 1].setMoveNumber(existingMoveNumber);
    }

    private void incrementHalfMoveClock() {
        int existingRevPlies = posInfo[numberOfMovesMade].getReversiblePlies();
        posInfo[numberOfMovesMade + 1].setReversiblePlies(existingRevPlies  + 1);
    }

    private void resetHalfMoveClock() {
        posInfo[numberOfMovesMade + 1].setReversiblePlies(0);
    }

    private void moveRook(int rookFrom, int rookTo) {
        erasePiece(rookFrom);
        placePiece(ROOK, rookTo);
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
        int newOptions = posInfo[numberOfMovesMade].getCastleOptions();
        newOptions &= ~castlingOption;
        posInfo[numberOfMovesMade + 1].setCastleOptions(newOptions);
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
        posInfo[numberOfMovesMade + 1].setEnPassantSquare(from);
    }

    private boolean isEnPassantCapture(int moving, int to, int captured) {
        return moving == PAWN && captured == PAWN && to == getEnPassantSquare();
    }

    public boolean inCheck() {
        int side = isWhiteToMove()?0:1;
        return Util.bool(Attacks.attackers(this, side, pos.getKingSquare(side)));
    }
}
