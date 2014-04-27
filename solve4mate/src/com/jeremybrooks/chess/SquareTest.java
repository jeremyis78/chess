package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jeremybrooks.chess.Piece.Color;

public class SquareTest {

	private Square square;
	
	@Before
	public void setUp()
	{
		square = new Square();
	}
	
	@Test
	public void testIsOccupied() {
		assertFalse(square.isOccupied());
		square = new Square(new Empty());
		assertFalse("absent piece for piece is still an unoccupied square",
				square.isOccupied());
	}

//	@Test
//	public void testClear() {
//		Pawn whitePawn = new Pawn(Color.W);
//		square = new Square(whitePawn);
//		assertSquareIsOccupiedBy(whitePawn);
//		
//		square.clear();
//		assertSquareIsUnoccupied();
//		
//		square.setPiece(Piece.absence());
//		square.clear();
//		assertSquareIsUnoccupied();
//	}

//	@Test
//	public void testSetAndGetPiece() {
//		Bishop blackBishop = new Bishop(Color.B);
//		square = new Square();
//
//		square.setPiece(blackBishop);
//		assertSquareIsOccupiedBy(blackBishop);
//		
//		square.setPiece(null);
//		assertSquareIsUnoccupied();
//	}

	@Test @Ignore
	public void testWith() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testUnoccupied() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testFromBoard() {
		fail("Not yet implemented");
	}

	private void assertSquareIsUnoccupied() {
		assertFalse(square.isOccupied());
		assertNotNull(square.get());
		assertSame(new Empty(), square.get());
	}

	private void assertSquareIsOccupiedBy(Piece piece) {
		assertTrue(square.isOccupied());
		assertSame(piece, square.get());
	}
}
