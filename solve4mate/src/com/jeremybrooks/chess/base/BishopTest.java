package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.jeremybrooks.chess.base.Piece.Color;
import com.jeremybrooks.chess.util.Util;

public class BishopTest {

    private Piece bishop;
    
    @Test
    public void givenWhiteBishop() {
        bishop = new Bishop(Color.W);
        assertBishop();
        assertEquals('B', bishop.toChar());
        assertEquals(Piece.ENCODED[Piece.BISHOP], bishop.encodedByColor());
    }

    @Test
    public void givenBlackBishop() {
        bishop = new Bishop(Color.B);
        assertBishop();
        assertEquals('b', bishop.toChar());
        assertEquals(-1 * Piece.ENCODED[Piece.BISHOP], bishop.encodedByColor());
    }

    private void assertBishop() {
        assertTrue(bishop.exists());
        assertEquals(Piece.BISHOP, bishop.index());
        assertEquals(Piece.ENCODED[Piece.BISHOP], bishop.encoded());
    }
    
    @Test
    public void givenAnEmptyBoard() {
        bishop = new Bishop(Color.W);
        int bishopSquare = E5;
        Position p = new Position();
        p.placePiece(Piece.WHITE, Piece.BISHOP, bishopSquare);
        long advances = bishop.advances(bishopSquare, p);
        assertEquals("a1 b2 h2 c3 g3 d4 f4 d6 f6 c7 g7 b8 h8 ", Util.displaySquaresStr(advances));
    }

    @Test
    public void givenPiecesInTheWay() {
        bishop = new Bishop(Color.W);
        int bishopSquare = E5;
        Position p = new Position();
        p.placePiece(Piece.WHITE, Piece.BISHOP, bishopSquare);
        p.placePiece(Piece.WHITE, Piece.ROOK, A1);
        p.placePiece(Piece.WHITE, Piece.PAWN, B2);
        p.placePiece(Piece.BLACK, Piece.PAWN, C7);
        long emptySquares = ~p.getAllPieces(0);
        long noncaptures = bishop.advances(bishopSquare, p) & emptySquares;
        assertEquals("h2 c3 g3 d4 f4 d6 f6 g7 h8 ", Util.displaySquaresStr(noncaptures));
    }

    @Test
    public void givenPiecesThatCanBeCaptured() {
        bishop = new Bishop(Color.W);
        int bishopSquare = E5;
        Position p = new Position();
        p.placePiece(Piece.WHITE, Piece.BISHOP, bishopSquare);
        p.placePiece(Piece.WHITE, Piece.ROOK, A1);
        p.placePiece(Piece.WHITE, Piece.PAWN, B2);
        p.placePiece(Piece.BLACK, Piece.PAWN, C7);
        p.placePiece(Piece.BLACK, Piece.KING, G7);
        long opponentPieces = p.getOpponentPiecesExceptKing(Piece.WHITE);
        long captures = bishop.advances(bishopSquare, p) & opponentPieces;
        assertEquals("c7 ", Util.displaySquaresStr(captures));
    }
}
