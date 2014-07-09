package com.jeremybrooks.chess.base;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.base.Bitmap.*;

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
		assertEquals(PIECE[KNIGHT], knight.encodedByColor());
	}

	@Test
	public void givenBlackKnight() {
		knight = new Knight(Color.B);
		assertKnight();
		assertEquals('n', knight.toChar());
		assertEquals(-1 * PIECE[KNIGHT], knight.encodedByColor());
	}

	private void assertKnight() {
		assertTrue(knight.exists());
		assertEquals(KNIGHT, knight.index());
		assertEquals(PIECE[KNIGHT], knight.encoded());
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
		long emptySquares = ~p.getAllPieces(0);
		long noncaptures = knight.advances(knightSquare, p) & emptySquares;
		assertEquals("d2 h4 e5 ", Util.displaySquaresStr(noncaptures));
	}

	@Test
	public void givenPiecesThatCanBeCaptured() {
		knight = new Knight(Color.W);
		int knightSquare = F3;
		Position p = occupiedPositionWithKnightOn(knightSquare);
		long opponentPieces = p.getOpponentPiecesExceptKing(WHITE);
		long captures = knight.advances(knightSquare, p) & opponentPieces;
		assertEquals("d4 g5 ", Util.displaySquaresStr(captures));
	}

	private Position occupiedPositionWithKnightOn(int knightSquare) {
		Position p = new Position();
		p.placePiece(WHITE, KNIGHT, knightSquare);
		p.placePiece(WHITE, KING, E1);
		p.placePiece(WHITE, ROOK, G1);
		p.placePiece(WHITE, PAWN, H2);
		p.placePiece(BLACK, QUEEN, D4);
		p.placePiece(BLACK, BISHOP, G5);
		return p;
	}

}
