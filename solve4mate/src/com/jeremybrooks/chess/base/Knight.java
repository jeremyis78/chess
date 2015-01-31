package com.jeremybrooks.chess.base;

import com.jeremybrooks.chess.movegen.Attacks;

public class Knight extends Piece {
    
    public Knight(Color color) {
        super(color, Piece.KNIGHT, 'N');
    }
    
    @Override
    public boolean exists() {
        return true;
    }
    
    @Override
    public long advances(int fromSquare, Position position)
    {
        int mySide = (color==Color.W?0:1);
        long notMyPieces = ~position.getPieces(mySide);
        return attacks(fromSquare, position) & notMyPieces;
    }
    
    @Override
    public long attacks(int fromSquare, Position position) {
        long attacks = Attacks.forPiece(this, fromSquare, position);
        return attacks;
    }

}