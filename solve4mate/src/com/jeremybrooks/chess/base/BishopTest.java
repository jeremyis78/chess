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
        assertEquals(PIECE[BISHOP], bishop.encodedByColor());
    }

    @Test
    public void givenBlackBishop() {
        bishop = new Bishop(Color.B);
        assertBishop();
        assertEquals('b', bishop.toChar());
        assertEquals(-1 * PIECE[BISHOP], bishop.encodedByColor());
    }

    private void assertBishop() {
        assertTrue(bishop.exists());
        assertEquals(BISHOP, bishop.index());
        assertEquals(PIECE[BISHOP], bishop.encoded());
    }
    
    @Test
    public void givenAnEmptyBoard() {
        bishop = new Bishop(Color.W);
        int bishopSquare = E5;
        Position p = new Position();
        p.placePiece(WHITE, BISHOP, bishopSquare);
        long advances = bishop.advances(bishopSquare, p);
        assertEquals("a1 b2 h2 c3 g3 d4 f4 d6 f6 c7 g7 b8 h8 ", Util.displaySquaresStr(advances));
    }

    @Test
    public void givenPiecesInTheWay() {
        bishop = new Bishop(Color.W);
        int bishopSquare = E5;
        Position p = new Position();
        p.placePiece(WHITE, BISHOP, bishopSquare);
        p.placePiece(WHITE, ROOK, A1);
        p.placePiece(WHITE, PAWN, B2);
        p.placePiece(BLACK, PAWN, C7);
        long emptySquares = ~p.getAllPieces(0);
        long noncaptures = bishop.advances(bishopSquare, p) & emptySquares;
        assertEquals("h2 c3 g3 d4 f4 d6 f6 g7 h8 ", Util.displaySquaresStr(noncaptures));
    }

    @Test
    public void givenPiecesThatCanBeCaptured() {
        bishop = new Bishop(Color.W);
        int bishopSquare = E5;
        Position p = new Position();
        p.placePiece(WHITE, BISHOP, bishopSquare);
        p.placePiece(WHITE, ROOK, A1);
        p.placePiece(WHITE, PAWN, B2);
        p.placePiece(BLACK, PAWN, C7);
        p.placePiece(BLACK, KING, G7);
        long opponentPieces = p.getOpponentPiecesExceptKing(WHITE);
        long captures = bishop.advances(bishopSquare, p) & opponentPieces;
        assertEquals("c7 ", Util.displaySquaresStr(captures));
    }
}
