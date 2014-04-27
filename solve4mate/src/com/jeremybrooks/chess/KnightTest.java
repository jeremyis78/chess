package com.jeremybrooks.chess;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.Bitmap.*;

import org.junit.Test;

import com.jeremybrooks.chess.Piece.Color;

public class KnightTest {

	private Knight knight;
	
	@Test
	public void testConstruction() {
		knight = new Knight(Color.W);
		assertEquals('N', knight.toChar());
		assertEquals(KNIGHT, knight.index());
		assertEquals(PIECE[KNIGHT], knight.encoded());
		assertEquals(PIECE[KNIGHT], knight.encodedByColor());
		
		knight = new Knight(Color.B);
		assertEquals('n', knight.toChar());
		assertEquals(KNIGHT, knight.index());
		assertEquals(PIECE[KNIGHT], knight.encoded());
		assertEquals(-1 * PIECE[KNIGHT], knight.encodedByColor());
	}

	@Test
	public void testNonCaptures() {
		knight = new Knight(Color.W);
		Position p = new Position();
		long nonCaptures = knight.nonCaptures(F3, p);
		assertEquals("e1 g1 d2 h2 d4 h4 e5 g5 ", Util.displaySquaresStr(nonCaptures));
		
		p.placePiece(WHITE, KING, E1);
		p.placePiece(WHITE, ROOK, G1);
		p.placePiece(BLACK, QUEEN, D4);
		nonCaptures = knight.nonCaptures(F3, p);
		assertEquals("d2 h2 h4 e5 g5 ", Util.displaySquaresStr(nonCaptures));
	}
	
	@Test
	public void testNonCapturesWithBlockingPieces() {
		knight = new Knight(Color.W);
		Position p = new Position();
		p.placePiece(WHITE, KING, E1);
		p.placePiece(WHITE, ROOK, G1);
		p.placePiece(BLACK, QUEEN, D4);
		long nonCaptures = knight.nonCaptures(F3, p);
		assertEquals("d2 h2 h4 e5 g5 ", Util.displaySquaresStr(nonCaptures));
	}

}
