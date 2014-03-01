package com.jeremybrooks.chess;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DisplayerTest {

	Displayer displayer;
	
	@Before
	public void init() {
		displayer = new Displayer();
	}

	@Test
	public void testStartingPosition()
	{
		Position p = createStartingPosition();
		String expectedBoard = 
                        "   -----------------\n" +
						"8 | r n b q k b n r |\n" +
						"7 | p p p p p p p p |\n" +
						"6 | - - - - - - - - |\n" +
						"5 | - - - - - - - - |\n" +
						"4 | - - - - - - - - |\n" +
						"3 | - - - - - - - - |\n" +
						"2 | P P P P P P P P |\n" +
						"1 | R N B Q K B N R |\n" +
						"   -----------------\n" +
						"    a b c d e f g h\n";
		assertEquals(expectedBoard, displayer.formatBoard(p));

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
		assertEquals(expectedPieceBitboards, displayer.formatAllBitboards(p));
	}

	private Position createStartingPosition() {
		return new Position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
	}

	@Test
	public void testQueensGambitAccepted()
	{
		Position p = new Position();
		p.set("rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR");

		String expectedBoard = 
                "   -----------------\n" +
				"8 | r n b q k b n r |\n" +
				"7 | p p p - p p p p |\n" +
				"6 | - - - - - - - - |\n" +
				"5 | - - - P - - - - |\n" +
				"4 | - - - - - - - - |\n" +
				"3 | - - - - - - - - |\n" +
				"2 | P P P P - P P P |\n" +
				"1 | R N B Q K B N R |\n" +
				"   -----------------\n" +
				"    a b c d e f g h\n";
		Assert.assertEquals(expectedBoard, displayer.formatBoard(p));

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
		Assert.assertEquals(expectedPieceBitboards, displayer.formatAllBitboards(p));
	}
	
	@Test
	public void testEndgamePosition()
	{
		Position position = new Position();
		position.set("q1n5/1P3p2/2P5/8/1K6/5b2/k6P/7R");
		String expectedBoard = 
                "   -----------------\n" +
				"8 | q - n - - - - - |\n" +
				"7 | - P - - - p - - |\n" +
				"6 | - - P - - - - - |\n" +
				"5 | - - - - - - - - |\n" +
				"4 | - K - - - - - - |\n" +
				"3 | - - - - - b - - |\n" +
				"2 | k - - - - - - P |\n" +
				"1 | - - - - - - - R |\n" +
				"   -----------------\n" +
				"    a b c d e f g h\n";
		Assert.assertEquals(expectedBoard, displayer.formatBoard(position));

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
		Assert.assertEquals(expectedPieceBitboards, displayer.formatAllBitboards(position));
	}

}
