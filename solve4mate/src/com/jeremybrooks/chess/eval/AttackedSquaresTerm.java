package com.jeremybrooks.chess.eval;

import static com.jeremybrooks.chess.base.Bitmap.*;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.util.Displayer;

public class AttackedSquaresTerm extends EvalTerm {
    private static final Logger log = Logger.getLogger(AttackedSquaresTerm.class);
    private static final String DESC = "number of squares attacked by white minus those attacked by black";

    public AttackedSquaresTerm() {
        setDescription(DESC);
    }

    @Override
    public int evaluate(GameState gameState) {
        //computing both in the same loop is .37 times faster
        //than computing them in two loops. For terms
        //that require iterating all squares, using a visitor pattern
        //for each type of term (instead of the current design)
        //might be the most efficient.
        Position position = gameState.getPosition();
        long attackedBy[] = new long[2];
        for(int square = A1; square <= H8; square++)
        {
            Piece piece = position.get(square);
            if(!piece.exists()) continue;
            int pieceColor = piece.encodedByColor()>0?WHITE:BLACK;
            attackedBy[pieceColor] |= piece.attacks(square, position);
        }
        int wAttacks = Long.bitCount(attackedBy[WHITE]);
        int bAttacks = Long.bitCount(attackedBy[BLACK]);
        log.trace(new Displayer().formatBoard(gameState.getPosition()));
        log.trace("white attacks: " + wAttacks + "\n" + Bitmap.format(attackedBy[WHITE]));
        log.trace("black attacks: " + bAttacks + "\n" + Bitmap.format(attackedBy[BLACK]));
        return wAttacks - bAttacks;
    }

}
