package com.jeremybrooks.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class FenParserTest {

	private FenParser parser;
	
	@Before
	public void setUp()
	{
		parser = new FenParser();
	}
	
	@Test
	public void testParseWhiteToMove() {
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 5 30";
		parser.parse(fen);
		assertEquals('r', parser.getBoardCharacter(Bitmap.A8));
		assertEquals('n', parser.getBoardCharacter(Bitmap.B8));
		assertEquals('b', parser.getBoardCharacter(Bitmap.C8));
		assertEquals('q', parser.getBoardCharacter(Bitmap.D8));
		assertEquals('k', parser.getBoardCharacter(Bitmap.E8));
		assertEquals('b', parser.getBoardCharacter(Bitmap.F8));
		assertEquals('n', parser.getBoardCharacter(Bitmap.G8));
		assertEquals('r', parser.getBoardCharacter(Bitmap.H8));
		for(int square = Bitmap.A7; square >= Bitmap.H7; square--)
		{
			assertEquals('p', parser.getBoardCharacter(square));
		}
		for(int square = Bitmap.A6; square >= Bitmap.H3; square--)
		{
			assertEquals(Bitmap.BOARD_EMPTY_SQUARE, parser.getBoardCharacter(square));
		}
		for(int square = Bitmap.A2; square >= Bitmap.H2; square--)
		{
			assertEquals('P', parser.getBoardCharacter(square));
		}
		assertEquals('R', parser.getBoardCharacter(Bitmap.A1));
		assertEquals('N', parser.getBoardCharacter(Bitmap.B1));
		assertEquals('B', parser.getBoardCharacter(Bitmap.C1));
		assertEquals('Q', parser.getBoardCharacter(Bitmap.D1));
		assertEquals('K', parser.getBoardCharacter(Bitmap.E1));
		assertEquals('B', parser.getBoardCharacter(Bitmap.F1));
		assertEquals('N', parser.getBoardCharacter(Bitmap.G1));
		assertEquals('R', parser.getBoardCharacter(Bitmap.H1));

		assertTrue(parser.isWhiteToMove());
		assertFalse(parser.hasWhiteShortCastleOption());
		assertFalse(parser.hasWhiteLongCastleOption());
		assertFalse(parser.hasBlackShortCastleOption());
		assertFalse(parser.hasBlackLongCastleOption());
		assertEquals(Bitmap.NOSQUARE, parser.getEnPassantSquare());
		assertEquals(5, parser.getHalfMoveNumber());
		assertEquals(30, parser.getMoveNumber());
	}

	@Test
	public void testParseWithBlackCastlingEnPassant() {
		String fen = "8/4P3/8/8/8/8/4P3/k6K b KQkq d3 3 22";
		parser.parse(fen);
		assertEquals('P', parser.getBoardCharacter(Bitmap.E7));
		assertEquals('P', parser.getBoardCharacter(Bitmap.E2));
		assertEquals('k', parser.getBoardCharacter(Bitmap.A1));
		assertEquals('K', parser.getBoardCharacter(Bitmap.H1));
		assertFalse(parser.isWhiteToMove());
		assertTrue(parser.hasWhiteShortCastleOption());
		assertTrue(parser.hasWhiteLongCastleOption());
		assertTrue(parser.hasBlackShortCastleOption());
		assertTrue(parser.hasBlackLongCastleOption());
		assertEquals(Bitmap.D3, parser.getEnPassantSquare());
		assertEquals(3, parser.getHalfMoveNumber());
		assertEquals(22, parser.getMoveNumber());
	}

	@Test
	public void testSetNotEnoughFields()
	{
		String notEnoughFields = "k6K/8/8/8/8/8/8/8 w KQkq";
		String expectedError = "FEN string 'k6K/8/8/8/8/8/8/8 w KQkq' needs six space-delimited fields: board onMove castlingOptions enPassantSquare halfMoveNumber moveNumber";
		assertInvalid(notEnoughFields, expectedError);
	}
	
	@Test
	public void testSetTooManyWhiteKings()
	{
		String tooManyWhiteKingsOnDifferentRanks = "2K5/8/8/8/8/8/8/7K w - - 0 1";
		assertInvalid(tooManyWhiteKingsOnDifferentRanks, "board has too many white kings");
	}

	@Test
	public void testSetTooManyBlackKings()
	{
		String tooManyBlackKingsOnSameRank = "8/k6k/8/8/8/8/8/8 w - - 0 1";
		assertInvalid(tooManyBlackKingsOnSameRank, "board has too many black kings");
	}

	@Test
	public void testSetTooManyRanksOnBoard()
	{
		String tooManyRanks = "8/8/8/8/8/8/8/8/8 w - - 0 1";
		assertInvalid(tooManyRanks, "board must contain eight ranks");
	}

	@Test
	public void testSetTooManyFilesOnBoard()
	{
		String tooManyFiles = "k8/8/8/8/8/8/8/8 w - - 0 1";
		assertInvalid(tooManyFiles, "board pieces and empty squares on rank #8 do not fit on eight files: k8");
	}
	
	@Test
	public void testSetUnknownPiece()
	{
		String invalidPiece = "k6K/8/8/8/8/8/8/7z w - - 0 1";
		assertInvalid(invalidPiece, "board contains invalid piece 'z'; allowed piece characters are: KkQqRrBbNnPp");
	}
	
	@Test
	public void testInvalidOnMove()
	{
		String badFen = "k6K/8/8/8/8/8/8/8 xx - - 0 1";
		String expectedError = "onMove 'xx' is invalid; use 'w' for white or 'b' for black";
		assertInvalid(badFen, expectedError);
	}

	@Test
	public void testInvalidCastlingCharacters()
	{
		String badFen = "k6K/8/8/8/8/8/8/8 b KQxx - 0 2";
		String expectedError = "castling options 'KQxx' are invalid; use only characters from KQkq";
		assertInvalid(badFen, expectedError);
	}

	@Test
	public void testTooManyCastlingCharacters()
	{
		String badFen = "k6K/8/8/8/8/8/8/8 b KQkqK - 0 2";
		String expectedError = "castling options 'KQkqK' must not be empty or exceed four characters; use only characters from KQkq";
		assertInvalid(badFen, expectedError);
	}

	@Test
	public void testBlankCastlingOptions()
	{
		String badFen = "k6K/8/8/8/8/8/8/8 b  - 0 2";
		String expectedError = "castling options '' must not be empty or exceed four characters; use only characters from KQkq";
		assertInvalid(badFen, expectedError);
	}

	@Test
	public void testInvalidEnPassant()
	{
		String badFen = "k6K/8/8/8/8/8/8/8 w - e3 0 1";
		String expectedError = "given 'w' to move, the en passant square 'e3' ought to be on the 6th rank";
		assertInvalid(badFen, expectedError);
		
		badFen = "k6K/8/8/8/8/8/8/8 b - h6 0 1";
		expectedError = "given 'b' to move, the en passant square 'h6' ought to be on the 3rd rank";
		assertInvalid(badFen, expectedError);
	}

	@Test
	public void testHalfMoveNumberIsNotNegative()
	{
		String badFen = "k6K/8/8/8/8/8/8/8 b KQkq - -1 2";
		String expectedError = "halfMoveNumber '-1' must be zero or greater";
		assertInvalid(badFen, expectedError);
	}

	@Test
	public void testMoveNumberIsAboveZero()
	{
		String badFen = "k6K/8/8/8/8/8/8/8 b KQkq - 1 0";
		String expectedError = "moveNumber '0' must be greater than zero";
		assertInvalid(badFen, expectedError);
	}

	public void testIsValidRankFen(){
		String[] good = 
			new String[]
			           {
						//"8",
						"1p1p1p1p",
						"p1p1p1p1",
						"RNBQKBNR",
						"2P5",
			           };
		for(int i=0; i<good.length; i++)
		{
			try
			{
				FenParser.validateFiles(good[i], 1);
			} catch (IllegalArgumentException e){
				fail("should not throw, valid rank fen " + good[i] + " " + e.getMessage());
			}
		}

	
		String[] bad = 
			new String[]
			           {
						"7",
						"p1p1p1p1p1p",
						"1p1p1p1",
						"RNB3BNR",
						"2P4",
			           };
		for(int i=0; i<good.length; i++)
		{
			try
			{
				FenParser.validateFiles(bad[i], 1);
				fail("should throw, invalid rank fen " + bad[i]);
			} catch (IllegalArgumentException e){
			}
		}

	}

	private void assertInvalid(String position, String expectedError) {
		try {
			parser.parse(position);
			fail(position+" did not throw '"+expectedError+"'");
		} catch (IllegalArgumentException e) {
			assertEquals(expectedError, e.getMessage());
		}	
	}

}
