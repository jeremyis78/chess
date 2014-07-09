package com.jeremybrooks.chess.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.FenParser;
import com.jeremybrooks.chess.base.Position;

public class BitboardDisplayerTest {

	private BitboardDisplayer bitboardDisplayer;

	@Before
	public void init() {
		bitboardDisplayer = new BitboardDisplayer();
	}

	@Test
	public void testStartingPosition()
	{
		
		String initialBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
		Position p = FenParser.parsePieceBoard(initialBoard);
		String expectedPieceBitboards =
				"   -----------------\n" +
						"8 | r n b q - b n r |\n" +
						"7 | p p p p p p p p |\n" +
						"6 | - - - - - - - - |\n" +
						"5 | - - - - - - - - |\n" +
						"4 | - - - - - - - - |\n" +
						"3 | - - - - - - - - |\n" +
						"2 | P P P P P P P P |\n" +
						"1 | R N B Q - B N R |\n" +
						"   -----------------\n" +
						"    a b c d e f g h\n";
		assertEquals(expectedPieceBitboards, bitboardDisplayer.formatBoard(p));
	}

	@Test
	public void testQueensGambitAccepted()
	{
		String board = "rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR";
		Position p = FenParser.parsePieceBoard(board);
		String expectedPieceBitboards =
				"   -----------------\n" +
						"8 | r n b q - b n r |\n" +
						"7 | p p p - p p p p |\n" +
						"6 | - - - - - - - - |\n" +
						"5 | - - - P - - - - |\n" +
						"4 | - - - - - - - - |\n" +
						"3 | - - - - - - - - |\n" +
						"2 | P P P P - P P P |\n" +
						"1 | R N B Q - B N R |\n" +
						"   -----------------\n" +
						"    a b c d e f g h\n";
		Assert.assertEquals(expectedPieceBitboards, bitboardDisplayer.formatBoard(p));
	}

	@Test
	public void testEndgamePosition()
	{
		String board = "q1n5/1P3p2/2P5/8/1K6/5b2/k6P/7R";
		Position p = FenParser.parsePieceBoard(board);
		String expectedPieceBitboards =
				"   -----------------\n" +
						"8 | q - n - - - - - |\n" +
						"7 | - P - - - p - - |\n" +
						"6 | - - P - - - - - |\n" +
						"5 | - - - - - - - - |\n" +
						"4 | - - - - - - - - |\n" +
						"3 | - - - - - b - - |\n" +
						"2 | - - - - - - - P |\n" +
						"1 | - - - - - - - R |\n" +
						"   -----------------\n" +
						"    a b c d e f g h\n";
		Assert.assertEquals(expectedPieceBitboards, bitboardDisplayer.formatBoard(p));
	}
}
