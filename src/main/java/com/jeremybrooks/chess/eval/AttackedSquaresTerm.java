package com.jeremybrooks.chess.eval;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.util.Displayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.jeremybrooks.chess.base.Bitmap.A1;
import static com.jeremybrooks.chess.base.Bitmap.H8;

public class AttackedSquaresTerm extends EvalTerm {
    private static final Logger log = LogManager.getLogger(AttackedSquaresTerm.class);
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
        long[] attackedBy = new long[2];
        for(int square = A1; square <= H8; square++)
        {
            Piece piece = position.get(square);
            if(!piece.exists()) continue;
            int pieceColor = piece.encodedByColor()>0?Piece.WHITE:Piece.BLACK;
            attackedBy[pieceColor] |= piece.attacks(square, position);
        }
        int wAttacks = Long.bitCount(attackedBy[Piece.WHITE]);
        int bAttacks = Long.bitCount(attackedBy[Piece.BLACK]);
        log.trace(new Displayer(gameState.getPosition()).formatBoard());
        log.trace("white attacks: " + wAttacks + "\n" + Bitmap.format(attackedBy[Piece.WHITE]));
        log.trace("black attacks: " + bAttacks + "\n" + Bitmap.format(attackedBy[Piece.BLACK]));
        return wAttacks - bAttacks;
    }

}
