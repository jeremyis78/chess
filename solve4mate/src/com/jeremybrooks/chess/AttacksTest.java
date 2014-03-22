package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

public class AttacksTest {

	private static final String BASE_DIR = "data/";
	Attacks attacks = Attacks.getInstance();
	
	@Test
	public void testGenMasksPlusMinus(){
		assertFileEqualsString(BASE_DIR + "correct-plus-minus-attacks.txt",
				attacks.plusMinusAttacksAsHumanReadableString().trim());
	}

	@Test
	public void testGenBaseAttacks(){
		assertFileEqualsString(BASE_DIR + "correct-base-attacks.txt",
				attacks.baseAttacksAsHumanReadableString().trim());
	}
	
	@Test
	public void testRookAttacks()
	{
		String filename = BASE_DIR + "rook-attacks.txt";
		File correctFile = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(correctFile)))
		{
			while (br.ready())
			{
				String[] fields = br.readLine().split("\t");
				int squareIndex = Util.StrToSq(fields[0]);
				int occupationIndex = occupationIndex(fields[1]);
				String associatedFEN = fields[2];
				String expectedMovesOnRank = fields[3];
				String expectedMovesOnFile = fields[4];
				assertEquals(expectedMovesOnRank, Util.formatSquares(attacks.rank[squareIndex][occupationIndex]));
				assertEquals(expectedMovesOnFile, Util.formatSquares(attacks.file[squareIndex][occupationIndex]));
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testBishopAttacks45DegreesRight()
	{
		String filename = BASE_DIR + "bishop-attacks-right45.txt";
		File correctFile = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(correctFile)))
		{
			while (br.ready())
			{
				String[] fields = br.readLine().split("\t");
				int squareIndex = Util.StrToSq(fields[0]);
				int occupationIndex = occupationIndex(fields[1]);
				String associatedFEN = fields[2];
				String expectedMovesOn45DegreesRight = fields[3];
				assertEquals(expectedMovesOn45DegreesRight, Util.formatSquares(attacks.R45[squareIndex][occupationIndex]));
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testBishopAttacks45DegreesLeft()
	{
		String filename = BASE_DIR + "bishop-attacks-left45.txt";
		File correctFile = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(correctFile)))
		{
			while (br.ready())
			{
				String[] fields = br.readLine().split("\t");
				int squareIndex = Util.StrToSq(fields[0]);
				int occupationIndex = occupationIndex(fields[1]);
				String associatedFEN = fields[2];
				String expectedMovesOn45DegreesLeft = fields[3];
				assertEquals(expectedMovesOn45DegreesLeft, Util.formatSquares(attacks.L45[squareIndex][occupationIndex]));
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	private static int occupationIndex(String occupationBitmap) {
		return Integer.parseInt(occupationBitmap)>>1;
	}

	@Test
	public void testGetA1H8diag(){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<15; i++)
    	{
    		String board = Util.formatLongBitmapAsBoard(attacks.getA1H8diag(i,(byte)0xff));
    		sb.append(board + "\n");
    		//System.out.println(board);
    	}
		assertFileEqualsString(BASE_DIR + "set-all-bits-in-a1-h8-diagonals.txt", sb.toString());
	}

	@Test
	public void testGetH1A8diag(){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<15; i++)
    	{
    		String board = Util.formatLongBitmapAsBoard(attacks.getH1A8diag(i,(byte)0xff));
    		sb.append(board + "\n");
    		//System.out.println(board);
    	}
		assertFileEqualsString(BASE_DIR + "set-all-bits-in-h1-a8-diagonals.txt", sb.toString());
	}

//	public void testGenPawnAttacks() {
//		fail("Not yet implemented");
//	}
//
//	public void testGenKingKnightAttacks() {
//		fail("Not yet implemented");
//	}

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
