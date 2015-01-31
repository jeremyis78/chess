package com.jeremybrooks.chess.base;

public class Bishop extends SlidingPiece {

    public Bishop(Color color) { 
        super(color, Piece.BISHOP, 'B');
    }

    @Override
    public boolean exists() {
        return true;
    }
}