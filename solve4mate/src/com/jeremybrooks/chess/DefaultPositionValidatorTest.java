package com.jeremybrooks.chess;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.Bitmap.*;

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
		nonAdjacentKings.placePiece(Bitmap.WHITE, KING, H8);
		nonAdjacentKings.placePiece(Bitmap.BLACK, KING, G6);
		validator.validateOrThrow(nonAdjacentKings);
	}

	@Test
	public void testBothKingsAreMissingIsInvalid() {
		Position empty = new Position();
		assertValidationThrows("board is missing one or both kings", empty);
	}

	@Test
	public void testOnlyOneKingMissingIsInvalid() {
		Position whiteKingOnly = parsePosition("8/8/8/8/8/8/6K1/8");
		assertValidationThrows("board is missing one or both kings", whiteKingOnly);
		
		Position blackKingOnly = parsePosition("8/8/8/8/8/8/6k1/8");
		assertValidationThrows("board is missing one or both kings", blackKingOnly);
	}

	@Test
	public void testAdjacentKingsIsInvalid() {
		Position adjacentKings = parsePosition("8/8/8/8/8/8/6K1/7k");
		assertValidationThrows("board cannot have adjacent kings", adjacentKings);
	}

	private static Position parsePosition(String pieceBoard)
	{
		return FenParser.parsePieceBoard(pieceBoard);
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
