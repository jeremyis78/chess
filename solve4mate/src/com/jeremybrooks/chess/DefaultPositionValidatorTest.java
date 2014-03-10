package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DefaultPositionValidatorTest {

	private PositionValidator validator;
	
	@Before
	public void setup()
	{
		validator = new DefaultPositionValidator();
	}

	@Test
	public void testTwoNonAdjacentKingsIsValid() {
		Position nonAdjacentKings = new Position();
		nonAdjacentKings.placePiece(Color.WHITE, Pieces.KING, Bitmap.H8);
		nonAdjacentKings.placePiece(Color.BLACK, Pieces.KING, Bitmap.G6);
		validator.validateOrThrow(nonAdjacentKings);
	}

	@Test
	public void testBothKingsAreMissingIsInvalid() {
		Position empty = new Position();
		assertValidationThrows("board is missing one or both kings", empty);
	}

	@Test
	public void testOnlyOneKingMissingIsInvalid() {
		Position whiteKingOnly = new Position("8/8/8/8/8/8/6K1/8");
		assertValidationThrows("board is missing one or both kings", whiteKingOnly);
		
		Position blackKingOnly = new Position("8/8/8/8/8/8/6k1/8");
		assertValidationThrows("board is missing one or both kings", blackKingOnly);
	}

	@Test
	public void testAdjacentKingsIsInvalid() {
		Position adjacentKings = new Position("8/8/8/8/8/8/6K1/7k");
		assertValidationThrows("board cannot have adjacent kings", adjacentKings);
	}
	
	private void assertValidationThrows(String expectedErrorMessage, Position empty) {
		
		try
		{
			validator.validateOrThrow(empty);
			fail("The position [" + empty.getFen() + "] should have thrown exception" );
		} catch (IllegalArgumentException e)
		{
			assertEquals(expectedErrorMessage, e.getMessage());
		}
		
	}
}
