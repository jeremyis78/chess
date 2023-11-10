package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Piece.Color.*;

public class Bishop extends SlidingPiece {

    public static int SQUARE_CENTIPAWN_VALUE[] = new int[] {  
            // From white's point of view:
            //              a1   b1   c1   d1   e1   f1   g1   h1
            /* 1st rank */ -20, -10, -10, -10, -10, -10, -10, -20,
            /*   2nd    */ -10,   5,   0,   0,   0,   0,   5, -10,  
            /*   3rd    */ -10,  10,  10,  10,  10,  10,  10, -10, 
            /*   4th    */ -10,   0,  10,  10,  10,  10,   0, -10,
            /*   5th    */ -10,   5,   5,  10,  10,   5,   5, -10,
            /*   6th    */ -10,   0,   5,  10,  10,   5,   0, -10,
            /*   7th    */ -10,   0,   0,   0,   0,   0,   0, -10,
            /* 8th rank */ -20, -10, -10, -10, -10, -10, -10, -20,
            //              a8   b8   c8   d8   e8   f8   g8   h8
        };

    public Bishop(Color color) { 
        super(color, Piece.BISHOP, 'B');
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