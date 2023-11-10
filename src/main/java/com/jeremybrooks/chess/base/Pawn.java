package com.jeremybrooks.chess.base;

import com.jeremybrooks.chess.movegen.Attacks;

public class Pawn extends Piece {

    public static int SQUARE_CENTIPAWN_VALUE[] = new int[] { 
            // From white's point of view:
            //              a1   b1   c1   d1   e1   f1   g1   h1
            /* 1st rank */   0,   0,   0,   0,   0,   0,   0,   0,
            /*   2nd    */   5,  10,  10, -20, -20,  10,  10,   5,  
            /*   3rd    */   5,  -5, -10,  0,    0, -10,  -5,   5, 
            /*   4th    */   0,   0,   0,  20,  20,   0,   0,   0,
            /*   5th    */   5,   5,  10,  25,  25,  10,   5,   5,
            /*   6th    */  10,  10,  20,  30,  30,  20,  10,  10,
            /*   7th    */  50,  50,  50,  50,  50,  50,  50,  50,
            /* 8th rank */   0,   0,   0,   0,   0,   0,   0,   0,
            //              a8   b8   c8   d8   e8   f8   g8   h8
        };

    public Pawn(Color color){ 
        super(color, Piece.PAWN, 'P');
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
    public long advances(int fromSquare, Position position) {
        int side = (color==Color.W?Piece.WHITE:Piece.BLACK);
        long occupiedSquares = position.getOccupied(0);
        long emptySquares = ~occupiedSquares;
        long pawnsMoveOne = 0;
        if (side == Piece.WHITE) {
            long whitePawns = position.getPawns(Piece.WHITE);
            pawnsMoveOne = (whitePawns << 8) & emptySquares;
        } else {
            long blackPawns = position.getPawns(Piece.BLACK);
            pawnsMoveOne = (blackPawns >> 8) & emptySquares;
        }
        return pawnsMoveOne;
    }

    @Override
    public long attacks(int fromSquare, Position position) {
        long attacks = 0L;
        attacks = Attacks.forPiece(this, fromSquare, position);
        return attacks;
    }

}