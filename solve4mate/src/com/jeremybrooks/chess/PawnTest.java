package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.jeremybrooks.chess.Piece.Color;

public class PawnTest {

	private Piece pawn;
	
	@Test
	public void givenWhitePawn() {
		pawn = new Pawn(Color.W);
		assertPawn();
		assertEquals('P', pawn.toChar());
		assertEquals(PIECE[PAWN], pawn.encodedByColor());
	}

	@Test
	public void givenBlackPawn() {
		pawn = new Pawn(Color.B);
		assertPawn();
		assertEquals('p', pawn.toChar());
		assertEquals(-1 * PIECE[PAWN], pawn.encodedByColor());
	}

	private void assertPawn() {
		assertTrue(pawn.exists());
		assertEquals(PAWN, pawn.index());
		assertEquals(PIECE[PAWN], pawn.encoded());
	}

	@Test
	public void givenWhitePawnOnStartingRank() {
		Pawn myPawn = new Pawn(Color.W);
		pawn = myPawn;
		Position p = new Position();
		p.placePiece(WHITE, PAWN, E2);
		long attacks = pawn.attacks(E2, p);
		long advances = pawn.advances(E2, p);
		long pushes = myPawn.pushes(p);
		long pushesTwo = myPawn.pushesTwo(p);
		assertEquals("d3 f3 ", Util.displaySquaresStr(attacks));
		assertEquals("e3 e4 ", Util.displaySquaresStr(advances));
		assertEquals("e3 ", Util.displaySquaresStr(pushes));
		assertEquals("e4 ", Util.displaySquaresStr(pushesTwo));
	}

	@Test
	public void givenBlackPawnOnSecondRank() {
		Pawn myPawn = new Pawn(Color.B); 
		pawn = myPawn;
		Position p = new Position();
		p.placePiece(BLACK, PAWN, H2);
		long attacks = pawn.attacks(H2, p);
		long advances = pawn.advances(H2, p);
		long pushes = myPawn.pushes(p);
		long pushesTwo = myPawn.pushes(p);
		assertEquals("g1 ", Util.displaySquaresStr(attacks));
		assertEquals("h1 ", Util.displaySquaresStr(advances));
		assertEquals("", Util.displaySquaresStr(pushes));
		assertEquals("", Util.displaySquaresStr(pushesTwo));
	}

}
