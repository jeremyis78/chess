package com.jeremybrooks.base;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.base.Bitmap.*;

import com.jeremybrooks.chess.base.Knight;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;
import org.junit.Test;

import com.jeremybrooks.chess.base.Piece.Color;
import com.jeremybrooks.chess.util.Util;

public class KnightTest {

    private Piece knight;
    
    @Test
    public void givenWhiteKnight() {
        knight = new Knight(Color.W);
        assertKnight();
        assertEquals('N', knight.toChar());
        assertEquals(Piece.ENCODED[Piece.KNIGHT], knight.encodedByColor());
    }

    @Test
    public void givenBlackKnight() {
        knight = new Knight(Color.B);
        assertKnight();
        assertEquals('n', knight.toChar());
        assertEquals(-1 * Piece.ENCODED[Piece.KNIGHT], knight.encodedByColor());
    }

    private void assertKnight() {
        assertTrue(knight.exists());
        assertEquals(Piece.KNIGHT, knight.index());
        assertEquals(Piece.ENCODED[Piece.KNIGHT], knight.encoded());
    }

    @Test
    public void givenEmptyBoard() {
        knight = new Knight(Color.W);
        Position p = new Position();
        long advances = knight.advances(F3, p);
        assertEquals("e1 g1 d2 h2 d4 h4 e5 g5 ", Util.displaySquaresStr(advances));
    }

    @Test
    public void givenPiecesInTheWay() {
        knight = new Knight(Color.W);
        int knightSquare = F3;
        Position p = occupiedPositionWithKnightOn(knightSquare);
        long emptySquares = ~p.getOccupied(0);
        long noncaptures = knight.advances(knightSquare, p) & emptySquares;
        assertEquals("d2 h4 e5 ", Util.displaySquaresStr(noncaptures));
    }

    @Test
    public void givenPiecesThatCanBeCaptured() {
        knight = new Knight(Color.W);
        int knightSquare = F3;
        Position p = occupiedPositionWithKnightOn(knightSquare);
        long opponentPieces = p.getOpponentPiecesExceptKing(Piece.WHITE);
        long captures = knight.advances(knightSquare, p) & opponentPieces;
        assertEquals("d4 g5 ", Util.displaySquaresStr(captures));
    }

    private Position occupiedPositionWithKnightOn(int knightSquare) {
        Position p = new Position();
        p.placePiece(Piece.WHITE, Piece.KNIGHT, knightSquare);
        p.placePiece(Piece.WHITE, Piece.KING, E1);
        p.placePiece(Piece.WHITE, Piece.ROOK, G1);
        p.placePiece(Piece.WHITE, Piece.PAWN, H2);
        p.placePiece(Piece.BLACK, Piece.QUEEN, D4);
        p.placePiece(Piece.BLACK, Piece.BISHOP, G5);
        return p;
    }

}
