package com.jeremybrooks.base;

import com.jeremybrooks.chess.base.King;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Piece.Color;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.util.Util;
import org.junit.Test;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KingTest {

    private Piece king;
    
    @Test
    public void givenWhiteKing() {
        king = new King(Color.W);
        assertKing();
        assertEquals('K', king.toChar());
        assertEquals(Piece.ENCODED[Piece.KING], king.encodedByColor());
    }

    @Test
    public void givenBlackKing() {
        king = new King(Color.B);
        assertKing();
        assertEquals('k', king.toChar());
        assertEquals(-1 * Piece.ENCODED[Piece.KING], king.encodedByColor());
    }

    private void assertKing() {
        assertTrue(king.exists());
        assertEquals(Piece.KING, king.index());
        assertEquals(Piece.ENCODED[Piece.KING], king.encoded());
    }

    @Test
    public void givenEmptyBoardNoOpposingKing() {
        king = new King(Color.W);
        Position p = new Position();
        long emptySquares = ~p.getOccupied(0);
        long advances = king.advances(E1, p) & emptySquares;
        assertEquals("d1 f1 d2 e2 f2 ", Util.displaySquaresStr(advances));
    }

    @Test
    public void givenOpposingKing() {
        king = new King(Color.W);
        Position p = new Position();
        p.placePiece(Piece.BLACK, Piece.KING, C3);
        long emptySquares = ~p.getOccupied(0);
        long advances = king.advances(E1, p) & emptySquares;
        assertEquals("d1 f1 e2 f2 ", Util.displaySquaresStr(advances));
    }

    @Test
    public void givenPiecesInTheWay() {
        king = new King(Color.B);
        int kingSquare = F4;
        Position p = occupiedPositionGivenKingOn(kingSquare);
        long emptySquares = ~p.getOccupied(0);
        long advances = king.advances(kingSquare, p) & emptySquares;
        long attacks = king.attacks(kingSquare, p);
        assertEquals("e3 f3 g3 g4 e5 f5 ", Util.displaySquaresStr(advances));
        assertEquals("e3 f3 g3 e4 g4 e5 f5 g5 ", Util.displaySquaresStr(attacks));
    }
    
    @Test
    public void givenPiecesThatCanBeCaptured() {
        king = new King(Color.B);
        int kingSquare = F4;
        Position p = occupiedPositionGivenKingOn(kingSquare);
        long opponentPieces = p.getOpponentPiecesExceptKing(Piece.BLACK);
        long captures = king.advances(kingSquare, p) & opponentPieces;
        assertEquals("e4 ", Util.displaySquaresStr(captures));
    }

    private Position occupiedPositionGivenKingOn(int kingSquare) {
        Position p = new Position();
        p.placePiece(Piece.BLACK, Piece.KING, kingSquare);
        p.placePiece(Piece.WHITE, Piece.PAWN, E4);
        p.placePiece(Piece.BLACK, Piece.BISHOP, G5);
        return p;
    }

}
