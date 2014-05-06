package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.jeremybrooks.chess.Piece.Color;

public class RookTest {

	private Piece rook;
	
	@Test
	public void givenWhiteRook() {
		rook = new Rook(Color.W);
		assertRook();
		assertEquals('R', rook.toChar());
		assertEquals(PIECE[ROOK], rook.encodedByColor());
	}

	@Test
	public void givenBlackRook() {
		rook = new Rook(Color.B);
		assertRook();
		assertEquals('r', rook.toChar());
		assertEquals(-1 * PIECE[ROOK], rook.encodedByColor());
	}

	private void assertRook() {
		assertTrue(rook.exists());
		assertEquals(ROOK, rook.index());
		assertEquals(PIECE[ROOK], rook.encoded());
	}

	@Test
	public void givenAnEmptyBoard() {
		rook = new Rook(Color.W);
		int rookSquare = B3;
		Position p = new Position();
		p.placePiece(BLACK, ROOK, rookSquare);

		long emptySquares = ~p.getAllPieces(0);
		long advances = rook.advances(rookSquare, p) & emptySquares;
		assertEquals("b1 b2 a3 c3 d3 e3 f3 g3 h3 b4 b5 b6 b7 b8 ", Util.displaySquaresStr(advances));
	}

	@Test
	public void givenPiecesInTheWay() {
		rook = new Rook(Color.B);
		int rookSquare = B3;
		Position p = occupiedPositionWithRookOn(rookSquare);
		long emptySquares = ~p.getAllPieces(0);
		long nonCaptures = rook.advances(rookSquare, p) & emptySquares;
		assertEquals("c3 d3 e3 f3 g3 b4 b5 b6 ", Util.displaySquaresStr(nonCaptures));
	}
	
	@Test
	public void givenPiecesThatCanBeCaptured() {
		rook = new Rook(Color.B);
		int rookSquare = B3;
		Position p = occupiedPositionWithRookOn(rookSquare);
		long opponentPieces = p.getOpponentPiecesExceptKing(BLACK);
		long captures = rook.advances(rookSquare, p) & opponentPieces;
		assertEquals("b2 a3 b7 ", Util.displaySquaresStr(captures));
	}

	private Position occupiedPositionWithRookOn(int rookSquare) {
		Position p = new Position();
		p.placePiece(BLACK, ROOK, rookSquare);
		p.placePiece(WHITE, PAWN, A3);
		p.placePiece(WHITE, BISHOP, B7);
		p.placePiece(BLACK, ROOK, H3);
		p.placePiece(WHITE, QUEEN, B2);
		return p;
	}

}
