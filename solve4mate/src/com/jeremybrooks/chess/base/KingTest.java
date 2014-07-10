package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.jeremybrooks.chess.base.Piece.Color;
import com.jeremybrooks.chess.util.Util;

public class KingTest {

    private Piece king;
    
    @Test
    public void givenWhiteKing() {
        king = new King(Color.W);
        assertKing();
        assertEquals('K', king.toChar());
        assertEquals(PIECE[KING], king.encodedByColor());
    }

    @Test
    public void givenBlackKing() {
        king = new King(Color.B);
        assertKing();
        assertEquals('k', king.toChar());
        assertEquals(-1 * PIECE[KING], king.encodedByColor());
    }

    private void assertKing() {
        assertTrue(king.exists());
        assertEquals(KING, king.index());
        assertEquals(PIECE[KING], king.encoded());
    }

    @Test
    public void givenEmptyBoardNoOpposingKing() {
        king = new King(Color.W);
        Position p = new Position();
        long emptySquares = ~p.getAllPieces(0);
        long advances = king.advances(E1, p) & emptySquares;
        assertEquals("d1 f1 d2 e2 f2 ", Util.displaySquaresStr(advances));
    }

    @Test
    public void givenOpposingKing() {
        king = new King(Color.W);
        Position p = new Position();
        p.placePiece(BLACK, KING, C3);
        long emptySquares = ~p.getAllPieces(0);
        long advances = king.advances(E1, p) & emptySquares;
        assertEquals("d1 f1 e2 f2 ", Util.displaySquaresStr(advances));
    }

    @Test
    public void givenPiecesInTheWay() {
        king = new King(Color.B);
        int kingSquare = F4;
        Position p = occupiedPositionGivenKingOn(kingSquare);
        long emptySquares = ~p.getAllPieces(0);
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
        long opponentPieces = p.getOpponentPiecesExceptKing(BLACK);
        long captures = king.advances(kingSquare, p) & opponentPieces;
        assertEquals("e4 ", Util.displaySquaresStr(captures));
    }

    private Position occupiedPositionGivenKingOn(int kingSquare) {
        Position p = new Position();
        p.placePiece(BLACK, KING, kingSquare);
        p.placePiece(WHITE, PAWN, E4);
        p.placePiece(BLACK, BISHOP, G5);
        return p;
    }

}
