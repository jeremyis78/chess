package com.jeremybrooks.chess.base;

public class Rook extends SlidingPiece {
    
    public Rook(Color color) { 
        super(color, Piece.ROOK, 'R');
    }

    @Override
    public boolean exists() {
        return true;
    }
}