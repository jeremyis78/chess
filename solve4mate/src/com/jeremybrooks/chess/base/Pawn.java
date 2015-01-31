package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
import com.jeremybrooks.chess.movegen.Attacks;

public class Pawn extends Piece {
    
    public Pawn(Color color){ 
        super(color, Piece.PAWN, 'P');
    }

    @Override
    public boolean exists() {
        return true;
    }
    
    @Override
    public long advances(int fromSquare, Position position) {
        int side = (color==Color.W?Piece.WHITE:Piece.BLACK);
        long occupiedSquares = position.getAllPieces(0);
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
        int side = (color==Color.W?0:1);
        attacks = Attacks.forPiece(this, fromSquare, position);
        return attacks;
    }

}