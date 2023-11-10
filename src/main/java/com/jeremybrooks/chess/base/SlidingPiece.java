package com.jeremybrooks.chess.base;

import com.jeremybrooks.chess.movegen.AbstractGenerator;
import com.jeremybrooks.chess.movegen.Attacks;
import com.jeremybrooks.chess.util.Util;

public abstract class SlidingPiece extends Piece {

    public SlidingPiece(Color color, int pieceIndex, char displayCharacter)
    {
        super(color, pieceIndex, displayCharacter);
    }
    
    @Override 
    public long advances(int fromSquare, Position position)
    {
        return attacks(fromSquare, position);
    }

    @Override 
    public long attacks(int fromSquare, Position position)
    {
        return Attacks.forPiece(this, fromSquare, position);
    }

    public boolean slidesLaterally() {
        return Util.bool(encoded() & AbstractGenerator.ROOK_OR_QUEEN);
    }

    public boolean slidesOnDiagonals() {
        return Util.bool(encoded() & AbstractGenerator.BISHOP_OR_QUEEN);
    }
}