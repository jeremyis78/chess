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
//        long attacks = 0;
//        if (slidesOnDiagonals())
//        {
//            long allPieces45Left = position.getAllPieces(-45);
//            long allPieces45Right = position.getAllPieces(45);
//            attacks |= AbstractGenerator.bishopAttacks(fromSquare, allPieces45Left, allPieces45Right);
//        }
//        if (slidesLaterally())
//        {
//            long allPiecesByRank = position.getAllPieces(0);
//            long allPiecesByFile = position.getAllPieces(90);
//            attacks |= AbstractGenerator.rookAttacks(fromSquare, allPiecesByRank, allPiecesByFile);
//        }
//        return attacks;
    }

    public boolean slidesLaterally() {
        return Util.bool(encoded() & AbstractGenerator.ROOK_OR_QUEEN);
    }

    public boolean slidesOnDiagonals() {
        return Util.bool(encoded() & AbstractGenerator.BISHOP_OR_QUEEN);
    }
}