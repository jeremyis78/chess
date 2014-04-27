package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.jeremybrooks.chess.Piece.Color;

public class KingTest {

	private Piece king;
	
	@Test
	public void testConstruction() {
		king = new King(Color.B);
		assertEquals('k', king.toChar());
		assertEquals(KING, king.index());
		assertEquals(PIECE[KING], king.encoded());
		assertEquals(-1 * PIECE[KING], king.encodedByColor());
	}
	
	@Test @Ignore
	public void testNonCaptures() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testConstant() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testKing() {
		fail("Not yet implemented");
	}

}
