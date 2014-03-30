package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

public class AttacksTest {

	private static final String BASE_DIR = "data/";
	private static final int PLUS1_MOVES_COLUMN = 2;
	private static final int MINUS1_MOVES_COLUMN = 3;
	private static final int PLUS7_MOVES_COLUMN = 4;
	private static final int MINUS7_MOVES_COLUMN = 5;
	private static final int PLUS8_MOVES_COLUMN = 6;
	private static final int MINUS8_MOVES_COLUMN = 7;
	private static final int PLUS9_MOVES_COLUMN = 8;
	private static final int MINUS9_MOVES_COLUMN = 9;
	private static final int RANK_MOVES_COLUMN = 3;
	private static final int FILE_MOVES_COLUMN = 4;
	private static final int RIGHT45_MOVES_COLUMN = 3;
	private static final int LEFT45_MOVES_COLUMN = 3;
	private static final int WHITE_PAWN_MOVES_COLUMN = 2;
	private static final int BLACK_PAWN_MOVES_COLUMN = 3;

	Attacks attacks = Attacks.getInstance();
	
	@Test
	public void testGenBaseAttacks(){
		assertFileEqualsString(BASE_DIR + "correct-base-attacks.txt",
				attacks.baseAttacksAsHumanReadableString().trim());
	}

	@Test
	public void testKingAttacks()
	{
		assertAttacksEqualMovesFromFile("king", "king-attacks-on-empty-board.txt", attacks.king);
	}

	@Test
	public void testKnightAttacks()
	{
		assertAttacksEqualMovesFromFile("knight", "knight-attacks-on-empty-board.txt", attacks.knight);
	}

	@Test
	public void testPlus1Attacks()
	{
		assertAttacksEqualMovesFromFile("plus1", "queen-attacks-on-empty-board.txt", PLUS1_MOVES_COLUMN, attacks.plus1);
	}

	@Test
	public void testMinus1Attacks()
	{
		assertAttacksEqualMovesFromFile("minus1", "queen-attacks-on-empty-board.txt", MINUS1_MOVES_COLUMN, attacks.minus1);
	}

	@Test
	public void testPlus7Attacks()
	{
		assertAttacksEqualMovesFromFile("plus7", "queen-attacks-on-empty-board.txt", PLUS7_MOVES_COLUMN, attacks.plus7);
	}

	@Test
	public void testMinus7Attacks()
	{
		assertAttacksEqualMovesFromFile("minus7", "queen-attacks-on-empty-board.txt", MINUS7_MOVES_COLUMN, attacks.minus7);
	}

	@Test
	public void testPlus8Attacks()
	{
		assertAttacksEqualMovesFromFile("plus8", "queen-attacks-on-empty-board.txt", PLUS8_MOVES_COLUMN, attacks.plus8);
	}

	@Test
	public void testMinus8Attacks()
	{
		assertAttacksEqualMovesFromFile("minus8", "queen-attacks-on-empty-board.txt", MINUS8_MOVES_COLUMN, attacks.minus8);
	}

	@Test
	public void testPlus9Attacks()
	{
		assertAttacksEqualMovesFromFile("plus9", "queen-attacks-on-empty-board.txt", PLUS9_MOVES_COLUMN, attacks.plus9);
	}

	@Test
	public void testMinus9Attacks()
	{
		assertAttacksEqualMovesFromFile("minus9", "queen-attacks-on-empty-board.txt", MINUS9_MOVES_COLUMN, attacks.minus9);
	}

	@Test
	public void testRankAttacks()
	{
		assertBlockedAttacksEqualMovesFromFile("rook's rank", "rook-attacks.txt", RANK_MOVES_COLUMN, attacks.rank);
	}

	@Test
	public void testFileAttacks()
	{
		assertBlockedAttacksEqualMovesFromFile("rook's file", "rook-attacks.txt", FILE_MOVES_COLUMN, attacks.file);
	}

	@Test
	public void testRightDiagonalAttacks()
	{
		assertBlockedAttacksEqualMovesFromFile("right diagonal", "bishop-attacks-right45.txt", RIGHT45_MOVES_COLUMN, attacks.R45);
	}

	@Test
	public void testLeftDiagonalAttacks()
	{
		assertBlockedAttacksEqualMovesFromFile("left diagonal", "bishop-attacks-left45.txt", LEFT45_MOVES_COLUMN, attacks.L45);
	}

	@Test
	public void testWhitePawnAttacks()
	{
		assertAttacksEqualMovesFromFile("white pawn", "pawn-attacks-on-empty-board.txt", WHITE_PAWN_MOVES_COLUMN, attacks.whitepawn);
	}

	@Test
	public void testBlackPawnAttacks()
	{
		assertAttacksEqualMovesFromFile("black pawn", "pawn-attacks-on-empty-board.txt", BLACK_PAWN_MOVES_COLUMN, attacks.blackpawn);
	}

	private void assertBlockedAttacksEqualMovesFromFile(String attackDescription,
			String dataFile, int movesColumn, long[][] blockedAttacksFromPiece) {

		String filename = BASE_DIR + dataFile;
		File correctFile = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(correctFile)))
		{
			while (br.ready())
			{
				String[] fields = br.readLine().split("\t");
				int squareIndex = Util.StrToSq(fields[0]);
				int occupationIndex = occupationIndex(fields[1]);
				String associatedFEN = fields[2];
				String expectedMoves = fields[movesColumn];
				assertMovesGivenFEN(attackDescription, associatedFEN, 
						expectedMoves, Util.formatSquares(blockedAttacksFromPiece[squareIndex][occupationIndex]));
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	private static void assertAttacksEqualMovesFromFile(String pieceName, String dataFile, long[] attacksFromPiece)
	{
		int defaultColumnContainingMoves = 2;
		assertAttacksEqualMovesFromFile(pieceName, dataFile, defaultColumnContainingMoves, attacksFromPiece);
	}

	private static void assertAttacksEqualMovesFromFile(String pieceName, String dataFile, 
			int columnOfMoves, long[] attacksFromPiece)
	{
		String filename = BASE_DIR + dataFile;
		File correctFile = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(correctFile)))
		{
			while (br.ready())
			{
				String[] fields = br.readLine().split("\t");
				int pieceSquareIndex = Util.StrToSq(fields[0]);
				String givenFEN = fields[1];
				String expectedMoves = fields[columnOfMoves];
				assertMovesGivenFEN(pieceName, givenFEN, 
						expectedMoves, Util.formatSquares(attacksFromPiece[pieceSquareIndex]));
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	private static int occupationIndex(String occupationBitmap) {
		return Integer.parseInt(occupationBitmap)>>1;
	}

	private static void assertMovesGivenFEN(String message, String associatedFEN,
			String expectedMoves, String actualMoves) 
	{
		if(!expectedMoves.equals(actualMoves))
		{
			Displayer displayer = new Displayer();
			Position position = new Position(associatedFEN);
			StringBuilder msgBuilder = new StringBuilder();
			msgBuilder.append("Given this position:\n");
			msgBuilder.append(displayer.formatBoard(position));
			msgBuilder.append("\nExpected "+message+" moves are: ").append(expectedMoves);
			msgBuilder.append("\nActual "+message+" moves are  : ").append(actualMoves);
			fail(msgBuilder.toString());
		}
	}

	@Test
	public void testGetA1H8diag(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<15; i++)
    	{
    		String board = Util.formatLongBitmapAsBoard(attacks.getA1H8diag(i,(byte)0xff));
    		sb.append(board + "\n");
    	}
		assertFileEqualsString(BASE_DIR + "diagonals-a1h8.txt", sb.toString());
	}

	@Test
	public void testGetH1A8diag(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<15; i++)
    	{
    		String board = Util.formatLongBitmapAsBoard(attacks.getH1A8diag(i,(byte)0xff));
    		sb.append(board + "\n");
    	}
		assertFileEqualsString(BASE_DIR + "diagonals-h1a8.txt", sb.toString());
	}

	/**
	 * Asserts the trimmed version of the file contents of filename
	 *  as a string equals the trimmed version of string
	 * @param filename the filename of the correct output
	 * @param string the string to compare to the correct output
	 * @throws IOException
	 */
	private void assertFileEqualsString(String filename, String string)
	{
		File correctFile = new File(filename);
		StringBuffer correct = new StringBuffer();
		try (BufferedReader br = new BufferedReader(new FileReader(correctFile))) {
			while (br.ready()){
				correct.append(br.readLine() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(correct.toString().trim(), string.trim());
		
	}
	
}
