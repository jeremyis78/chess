package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Piece.Color.*;
import com.jeremybrooks.chess.movegen.Attacks;

public class Knight extends Piece {

    public static int SQUARE_CENTIPAWN_VALUE[] = new int[] { 
            // From white's point of view:
            //              a1   b1   c1   d1   e1   f1   g1   h1
            /* 1st rank */ -50, -40, -30, -30, -30, -30, -40, -50,
            /*   2nd    */ -40, -20,   0,   0,   0,   0, -20, -40,  
            /*   3rd    */ -30,   5,  10,  15,  15,  10,   5, -30, 
            /*   4th    */ -30,   0,  15,  20,  20,  15,   0, -30,
            /*   5th    */ -30,   5,  15,  20,  20,  15,   5, -30,
            /*   6th    */ -30,   0,  10,  15,  15,  10,   0, -30,
            /*   7th    */ -40, -30,   0,   5,   5,   0, -20, -40,
            /* 8th rank */ -50, -40, -30, -30, -30, -30, -40, -50
            //              a8   b8   c8   d8   e8   f8   g8   h8
        };

    public Knight(Color color) {
        super(color, Piece.KNIGHT, 'N');
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
    
    @Override
    public long advances(int fromSquare, Position position)
    {
        int mySide = (color==W?0:1);
        long notMyPieces = ~position.getAllPiecesAndKing(mySide);
        return attacks(fromSquare, position) & notMyPieces;
    }
    
    @Override
    public long attacks(int fromSquare, Position position) {
        long attacks = Attacks.forPiece(this, fromSquare, position);
        return attacks;
    }

}