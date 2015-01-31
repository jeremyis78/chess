package com.jeremybrooks.chess.base;

public class Queen extends SlidingPiece {
    
    public Queen(Color color) { 
        super(color, Piece.QUEEN, 'Q');
    }

    @Override
    public boolean exists() {
        return true;
    }
}