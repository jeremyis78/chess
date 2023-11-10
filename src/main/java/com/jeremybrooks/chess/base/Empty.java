package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;

public class Empty extends Piece {

    public Empty()
    {
        super(Piece.Color.W, Piece.NONE, BOARD_EMPTY_SQUARE);
    }
    
    @Override
    public boolean exists() {
        return false;
    }
    
    @Override
    public int centipawnValueOnSquare(int square) {
        return 0;
    }

    @Override
    public long advances(int fromSquare, Position position) {
        return 0;
    }

    @Override
    public long attacks(int fromSquare, Position position) {
        return 0;
    }

}
