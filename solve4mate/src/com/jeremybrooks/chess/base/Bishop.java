package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.BISHOP;

public class Bishop extends SlidingPiece {

    public Bishop(Color color) { 
        super(color, BISHOP, 'B');
    }

    @Override
    public boolean exists() {
        return true;
    }
}