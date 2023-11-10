package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Piece.Color.*;
import com.jeremybrooks.chess.movegen.Attacks;
import com.jeremybrooks.chess.util.Util;

public class King extends Piece {

    public static int SQUARE_CENTIPAWN_VALUE[] = new int[] { 
        // From white's point of view:
        //               a1   b1   c1   d1   e1   f1   g1   h1
        /* 1st rank */   20,  30,  10,   0,   0,  10,  30,  20,
        /*   2nd    */   20,  20,   0,   0,   0,   0,  20,  20,  
        /*   3rd    */  -10, -20, -20, -20, -20, -20, -20, -10, 
        /*   4th    */  -20, -30, -30, -40, -40, -30, -30, -20,
        /*   5th    */  -30, -40, -40, -50, -50, -40, -40, -30,
        /*   6th    */  -30, -40, -40, -50, -50, -40, -40, -30,
        /*   7th    */  -30, -40, -40, -50, -50, -40, -40, -30,
        /* 8th rank */  -30, -40, -40, -50, -50, -40, -40, -30,
        //               a8   b8   c8   d8   e8   f8   g8   h8   
    };

    public King(Color color) 
    { 
        super(color, Piece.KING, 'K');
    }

    @Override
    public boolean exists() {
        return true;
    }
    
    @Override
    public int centipawnValueOnSquare(int square) {
        int onSquare = (color == Color.W) ? square : square ^ 56;
        return SQUARE_CENTIPAWN_VALUE[onSquare];
    }

    /* 
     * Assumes that we have a "colored" piece so we can grab the squares the 
     * opposing king is attacking to be sure we don't move there
     */
    @Override
    public long advances(int fromSquare, Position position) {
        int mySide = (color==W?0:1);
        long notMyPieces = ~position.getAllPiecesAndKing(mySide);
        long advances = attacks(fromSquare, position) & notMyPieces;

        //exclude moves that are attacked by opponent's king
        if(position.isKingPlaced(Util.opposing(mySide)))
        {
            int opposingKingSquare = position.getKingSquare(Util.opposing(mySide));
            long notAttackedByOpposingKing = ~Attacks.forPiece(this, opposingKingSquare, position);
            advances &= notAttackedByOpposingKing;
        }
        return advances;
    }

    @Override
    public long attacks(int fromSquare, Position position) {
        long attacks = Attacks.forPiece(this, fromSquare, position);
        return attacks;
    }

}