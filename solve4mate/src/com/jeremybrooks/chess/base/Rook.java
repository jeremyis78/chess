package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Piece.Color.*;


public class Rook extends SlidingPiece {

    public static int SQUARE_CENTIPAWN_VALUE[] = new int[] { 
            // From white's point of view:
            //              a1   b1   c1   d1   e1   f1   g1   h1
            /* 1st rank */   0,   0,   0,   5,   5,   0,   0,   0,
            /*   2nd    */  -5,   0,   0,   0,   0,   0,   0,  -5,  
            /*   3rd    */  -5,   0,   0,   0,   0,   0,   0,  -5, 
            /*   4th    */  -5,   0,   0,   0,   0,   0,   0,  -5,
            /*   5th    */  -5,   0,   0,   0,   0,   0,   0,  -5,
            /*   6th    */  -5,   0,   0,   0,   0,   0,   0,  -5,
            /*   7th    */   5,  10,  10,  10,  10,  10,  10,   5,
            /* 8th rank */   0,   0,   0,   0,   0,   0,   0,   0,
            //              a8   b8   c8   d8   e8   f8   g8   h8   
        };

    public Rook(Color color) { 
        super(color, Piece.ROOK, 'R');
    }

    @Override
    public boolean exists() {
        return true;
    }
    
    @Override
    public int centipawnValueOnSquare(int square) {
        int onSquare = (color == W) ? square : square ^ 56;
        return SQUARE_CENTIPAWN_VALUE[onSquare];
    }
}