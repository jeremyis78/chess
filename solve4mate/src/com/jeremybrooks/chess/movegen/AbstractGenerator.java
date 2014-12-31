/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 9, 2010
 */
package com.jeremybrooks.chess.movegen;

import static com.jeremybrooks.chess.base.Bitmap.*;

import java.util.List;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Piece.Color;
import com.jeremybrooks.chess.base.PieceFactory;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.base.Square;
import com.jeremybrooks.chess.util.Util;

/**
 * 
 * @author jeremy
 *
 */
public abstract class AbstractGenerator implements Generator {

    public static final int MAX_NUM_GENERATED_MOVES = 100;
    public static final int BISHOP_OR_QUEEN = 0x01;  //mask to determine Bishop/Queen            
    public static final int ROOK_OR_QUEEN   = 0x02;  //mask to determine Rook/Queen
    protected static final Attacks att = Attacks.INSTANCE;
    
    protected GameState g;

    public AbstractGenerator(){
        
    }

    public GameState getGameState() {
        return g;
    }
    
    public void setGameState(GameState gameState) {
        this.g = gameState;
    }

    public static boolean morePieces(long pieceBoard)
    {
        return pieceBoard != 0;
    }

    // Generate moves to any squares that are set in 'targets'
    protected void generateInterpositions (GameState g, List<Integer> moves, int side, int depth,
                                 long targets)
    {
        //TODO: finished this function...now just call it from
        //      GenerateInCheckMoves() where appropriate.
        if(!Util.bool(targets))
        {
            return;
        }
        long pieces;
        long pMoves;
        long advanceTwo;
        long promoters;
        long empty;
        int to, from;

        //***************************************************************************
        //*                                                                         *
        //* Add all pawn moves (promotions, advance-two, advance-one)               *
        //*                                                                         *
        //***************************************************************************

        Position position = g.getPosition();
        long allPiecesByRank = position.getAllPieces(0);
        empty = ~allPiecesByRank;

        //getPawnMoves(g, side, pMoves, promoters, advanceTwo);
        pMoves = getPawnAdvanceOne(g, side);
        advanceTwo = getPawnAdvanceTwo(g, side);
        promoters = getPawnPromotions(g, side);


        //TODO:
        // AND 'promoters' and 'targets' to limit even more
        // AND 'advanceTwo' and 'targets' "   "    "    "
        // AND 'pMoves' and 'targets'     "   "    "    "
        //Do this before passing them on to the while loops
        //below (then remove the 'if((1L << to) & targets){' checks

        //Pawn promotions
        while (morePieces(promoters)) {
            to = lowestBitNumber(promoters);
            //Add move ONLY if the move is to 'targets'
            if (Util.bool((1L << to) & targets))
            {
                from = Square.squareBehind(to, side);

                //Only add an interposer if it's not pinned to the King
                //if (!isPinned(g, from, to, PIECE[PAWN], 0)){
                if (isLegal(g, Util.EncodeMove(from, to, PIECE[PAWN], 0, 0), side))
                {    
                    for (int i = QUEEN; i >= KNIGHT; i--)
                    {
                        moves.add(Util.EncodeMove (from, to, PIECE[PAWN], 0, PIECE[i]));
                    }
                }
            }
            promoters = clearBit(promoters, to);
        }
        // Pawns advance two squares
        while (morePieces(advanceTwo))
        {
            to = lowestBitNumber(advanceTwo);
            //Add move ONLY if the move is to 'targets'
            if(Util.bool((1L << to) & targets)){ 
                from = Square.twoSquaresBehind(to, side);

                //Only add an interposer if it's not pinned to the King
                //if (!isPinned(g, from, to, PIECE[PAWN], 0)){
                if(isLegal(g, Util.EncodeMove(from, to, PIECE[PAWN], 0, 0), side)){
                    moves.add(Util.EncodeMove (from, to, PIECE[PAWN], 0, 0));
                }
            }
            advanceTwo = clearBit(advanceTwo, to);
        }
        // Pawns advance one square
        while (morePieces(pMoves)) {
            to = lowestBitNumber(pMoves);
            //Add move ONLY if the move is to 'targets'
            if(Util.bool((1L << to) & targets))
            {
                from = Square.squareBehind(to, side);

                //Only add an interposer if it's not pinned to the King
                //if (!isPinned(g, from, to, PIECE[PAWN], 0)){
                int encodedMove = Util.EncodeMove(from, to, PIECE[PAWN], 0, 0);
                if(isLegal(g, encodedMove, side)){
                    moves.add(encodedMove);
                }
            }
            pMoves = clearBit(pMoves, to);
        }

        //***************************************************************************
        //*                                                                         *
        //* Add all knight, bishop, rook, queen moves (no king moves since he's in  *
        //* check)                                                                  *
        //***************************************************************************

        for (int p = KNIGHT; p <= QUEEN; p++) {
            pieces = position.getPieces (side, p);
            while (morePieces(pieces)) {
                from = lowestBitNumber(pieces);
                //now make pMoves only those moves which will interpose
                //between the king and the checker (by ANDing with targets).
                Piece thePiece = PieceFactory.fromIndex(side==WHITE?Color.W:Color.B, p);
                pMoves = Attacks.forPiece(thePiece, from, position) & empty & targets;
                while (morePieces(pMoves)) {
                    to = lowestBitNumber(pMoves);
                    //Add move ONLY if it is to 'targets'
                    //if ((1L << to) & targets) {

                    //Only add an interposer if it's not pinned to the King
                    //if (!isPinned(g, from, to, PIECE[p], 0)){
                    int encodedMove = Util.EncodeMove(from, to, PIECE[p], 0, 0);
                    if(isLegal(g, encodedMove, side)){
                       moves.add(encodedMove);
                    }
                    pMoves = clearBit(pMoves, to);
                }
                pieces = clearBit(pieces, from);
            }
        }
    }

    public boolean isAttacked(GameState g, int side, int sq)
    {
        return Util.bool(attackers(g, side, sq));
    }

    public long attackers(GameState g, int sideUnderAttack, int squareUnderAttack)
    {
        return Attacks.attackers(g, sideUnderAttack, squareUnderAttack);
    }

    static int isPawnPromotion(int side, int from){
        switch(side){
        case Bitmap.WHITE:
            if(from + 8 >= A8){
                return 1;
            }
            break;
        case Bitmap.BLACK:
            if(from - 8 <= H1){
                return 1;
            }
            break;
        }
        return 0;
    }

    @Override
    public boolean canWhiteShortCastle(GameState g, int side){
        Position position = g.getPosition();
        if (g.hasShortCastleOption()
            && position.isEmpty(F1)
            && position.isEmpty(G1) 
            && !isAttacked (g, side, E1)
            && !isAttacked (g, side, F1)
            && !isAttacked (g, side, G1)
            && !isAttacked (g, side, H1)) {
            return true;
        }
        return false;
    }

    public boolean canWhiteLongCastle(GameState g, int side){
        Position position = g.getPosition();
        if (g.hasLongCastleOption() &&
            position.isEmpty(D1)
            && position.isEmpty(C1)
            && position.isEmpty(B1) 
            && !isAttacked (g, side, E1)
            && !isAttacked (g, side, D1)
            && !isAttacked (g, side, C1)
            && !isAttacked (g, side, B1)
            && !isAttacked (g, side, A1)) {
            return true;
        }
        return false;
    }

    public boolean canBlackShortCastle(GameState g, int side){
        Position position = g.getPosition();
        if (g.hasShortCastleOption()
            && position.isEmpty(F8)
            && position.isEmpty(G8) 
            && !isAttacked (g, side, E8)
            && !isAttacked (g, side, F8)
            && !isAttacked (g, side, G8)
            && !isAttacked (g, side, H8)) {
            return true;
        }
        return false;
    }
    
    public boolean canBlackLongCastle(GameState g, int side){
        Position position = g.getPosition();
        if (g.hasShortCastleOption()
            && position.isEmpty(D8)
            && position.isEmpty(C8)
            && position.isEmpty(B8) 
            && !isAttacked (g, side, E8)
            && !isAttacked (g, side, D8)
            && !isAttacked (g, side, C8)
            && !isAttacked (g, side, A8)) {
            return true;
        }
        return false;  
    }


    /*

    // isPinned()
    // 
    // Returns true if the move by 'mover' from square 'from' to 'to'
    // exposes king to check.  Returns false otherwise.
    bool isPinned(gamestate &g, int from, int to, int mover, int cap){
        int move;
        bool pinned;
        //int savedNumMoves;

        //It doesn't matter what the piece promotes to...hence
        //a zero for the promotion piece below.
        move = EncodeMove(from, to, mover, cap, 0);
        //cout << "Before make move:\n";
        //g.display();
        //printf("rooks : %0llx\n", g.pos.pieces[Color.WHITE][Pieces.ROOKS]);
        //printf("queens: %0llx\n", g.pos.pieces[Color.WHITE][Pieces.QUEENS]);

        g.makeMove(move);
        //makeMove changes the side...so change it back
        //g.sideToMove = Toggle(g.sideToMove);

        pinned = isAttacked(g, g.sideToMove, g.pos.kingSq[g.sideToMove]);

        g.undoMove(move);
        //cout << "After undo move:\n";
        //g.display();
        //printf("rooks : %0llx\n", g.pos.pieces[Color.WHITE][Pieces.ROOKS]);
        //printf("queens: %0llx\n", g.pos.pieces[Color.WHITE][Pieces.QUEENS]);
       
        return pinned;
    }
    */



    // isLegal()
    // 
    // Returns true if the move 'move' is legal (doesn't exposes/leaves the
    // king in check). This is for use when the king is moving.  We have to
    // save the king square upfront...then make the king move with the saved
    // value.
    // Returns false if the king is in check after 'move' is made.
    boolean isLegal(GameState g, int move, int side){
        boolean legal;

        //Save the king square in case the king is the moving piece
        //int kingSq = g.pos.kingSq[side];
        g.makeMove(move, WHITE==side);
        legal = !isAttacked(g, side, g.getPosition().getKingSquare(side));  //use the saved king square
        g.undoMove(move, WHITE==side);
        return legal;
    }
    
    private long getPawnAdvanceOne(GameState g, int side)
    {
        long advOne = 0;
        long empty;

        Position position = g.getPosition();
        empty = ~position.getAllPieces(0);

        switch (side) {
        case Bitmap.WHITE:
            advOne = (position.getPawns(side) << 8) & empty & ~EIGHTHRANK;
            // 'advOne' is all moves except those to the eighth rank
            break;
        case Bitmap.BLACK:
            advOne = (position.getPawns(side) >> 8) & empty & ~FIRSTRANK;
            // 'advOne' is all moves except those to the first rank
            break;
        }
        return advOne;
    }

    private long getPawnAdvanceTwo(GameState g, int side)
    {
        long advTwo = 0;
        long empty;

        Position position = g.getPosition();
        empty = ~position.getAllPieces(0);// all[ALL];

        switch (side) {
        case Bitmap.WHITE:
            advTwo = position.getPawns(side) & SECONDRANK;
            advTwo = (advTwo << 8) & empty;
            advTwo = (advTwo << 8) & empty;
            break;
        case Bitmap.BLACK:
            advTwo = position.getPawns(side) & SEVENTHRANK;
            advTwo = (advTwo >> 8) & empty;
            advTwo = (advTwo >> 8) & empty;
            break;
        }
        return advTwo;
    }
    
    private long getPawnPromotions(GameState g, int side)
    {
        long prom = 0;
        long empty;

        Position position = g.getPosition();
        empty = ~position.getAllPieces(0);

        switch (side) {
        case Bitmap.WHITE:
            prom = (position.getPawns(side) << 8) & empty & EIGHTHRANK;
            // 'prom' is only the moves to the eighth rank
            break;
        case Bitmap.BLACK:
            prom = (position.getPawns(side) >> 8) & empty & FIRSTRANK;
            // 'prom' is only the moves to the first rank
            break;
        }
        return prom;
    }

}
