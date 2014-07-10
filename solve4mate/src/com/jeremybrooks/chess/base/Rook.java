package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.ROOK;

public class Rook extends SlidingPiece {
    
    public Rook(Color color) { 
        super(color, ROOK, 'R');
    }

    @Override
    public boolean exists() {
        return true;
    }
}