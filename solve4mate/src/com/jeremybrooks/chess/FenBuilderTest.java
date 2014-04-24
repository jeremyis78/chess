package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FenBuilderTest {
	private static final String[] TEST_PIECE_BOARD;
	static
	{
		TEST_PIECE_BOARD = new String[]{
			"rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
			"k1K1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p",
			"8/8/8/8/8/8/8/k1K5",
			"1k1K1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1",
			"q1n5/1P3P2/2P5/8/K7/8/k6P/8",
            "1K1Q1R1B/1N4P1/8/8/8/8/1k1q1r1b/1n4p1"
			};
	}
	private FenBuilder builder;
	
	@Before
	public void setUp()
	{
		builder = new FenBuilder();
	}
	
	@Test
	public void testNullaryConstructor() {
		String expectedWithNoFieldsAppended = "- w - - 0 1";
		assertEquals(expectedWithNoFieldsAppended, builder.toString());
	}

	@Test
	public void testToString() {
		Position position = new Position();
		builder.appendPieceBoard(position);
		builder.appendOnMove(false);
		builder.appendCastlingOptions(GameState.W_LONG_CASTLE | GameState.B_SHORT_CASTLE);
		builder.appendEnPassantSquare(Bitmap.H3);
		builder.appendHalfMoveNumber(0);
		builder.appendCurrentMoveNumber(1);
		String expectedToString = "8/8/8/8/8/8/8/8 b Qk h3 0 1";
		assertEquals(expectedToString, builder.toString());
	}

	@Test
	public void givenTestPieceBoards() {
		for(String pieceBoard: TEST_PIECE_BOARD)
		{
			Position position = new Position();
			position.set(pieceBoard);
			builder.appendPieceBoard(position);
			String expected = pieceBoard+" w - - 0 1";
			assertEquals(expected, builder.toString());
			builder.reset();
		}
	}

	@Test
	public void givenBlackToMove() {
		builder.appendOnMove(false);
		String expected = "- b - - 0 1";
		assertEquals(expected, builder.toString());
	}
	
	@Test
	public void givenWhiteToMove() {
		builder.appendOnMove(true);
		String expected = "- w - - 0 1";
		assertEquals(expected, builder.toString());
	}

	@Test
	public void givenEveryCombinationOfCastlingOptions() {
		String[] options = new String[]{"-",   "K",   "Q",   "KQ",  
										"k",  "Kk",  "Qk",  "KQk",
										"q",  "Kq",  "Qq",  "KQq",
										"kq", "Kkq", "Qkq", "KQkq"};
		int castlingBitmap = 0;
		for(String option: options)
		{
			builder.appendCastlingOptions(castlingBitmap++);
			String expected = "- w "+option+" - 0 1";
			assertEquals(expected, builder.toString());
			builder.reset();
		}
	}

	@Test
	public void givenEnPassantSquareOnSixthRank() {
		builder.appendEnPassantSquare(Bitmap.D6);
		String expected = "- w - d6 0 1";
		assertEquals(expected, builder.toString());
	}

	@Test
	public void givenEnPassantSquareOnThirdRank() {
		builder.appendOnMove(false);
		builder.appendEnPassantSquare(Bitmap.H3);
		String expected = "- b - h3 0 1";
		assertEquals(expected, builder.toString());
	}

	@Test
	public void givenWhiteToMoveEnPassantSquareNotOnSixthRank() {
		builder.appendEnPassantSquare(Bitmap.D7);
		String expected = "- w - - 0 1";
		assertEquals(expected, builder.toString());
	}

	@Test
	public void givenBlackToMoveEnPassantSquareNotOnThirdRank() {
		builder.appendOnMove(false);
		builder.appendEnPassantSquare(Bitmap.H2);
		String expected = "- b - - 0 1";
		assertEquals(expected, builder.toString());
	}

	@Test
	public void givenPositiveHalfMoveNumber() {
		builder.appendHalfMoveNumber(3);
		String expected = "- w - - 3 1";
		assertEquals(expected, builder.toString());
	}

	@Test
	public void givenZeroHalfMoveNumber() {
		builder.appendHalfMoveNumber(0);
		String expected = "- w - - 0 1";
		assertEquals(expected, builder.toString());
	}

	@Test
	public void givenNegativeHalfMoveNumberKeepDefaultValue() {
		builder.appendHalfMoveNumber(-1);
		String expected = "- w - - 0 1";
		assertEquals(expected, builder.toString());
	}

	@Test
	public void givenPositiveCurrentMoveNumber() {
		builder.appendCurrentMoveNumber(72);
		String expected = "- w - - 0 72";
		assertEquals(expected, builder.toString());
	}

	@Test
	public void givenCurrentMoveNumberIsZeroOrLessKeepDefaultValue() {
		builder.appendCurrentMoveNumber(0);
		String expected = "- w - - 0 1";
		assertEquals(expected, builder.toString());
	}

}
