package com.jeremybrooks.chess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

public class AttacksTest extends TestCase {

	private static final String BASE_DIR = "data/";
	
	Attacks attacks = Attacks.getInstance();
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	

	public void testGenMasksPlusMinus(){
		assertFileEqualsString(BASE_DIR + "correct-plus-minus-attacks.txt",
				attacks.plusMinusAttacksAsHumanReadableString().trim());
	}

	public void testGenBaseAttacks(){
		assertFileEqualsString(BASE_DIR + "correct-base-attacks.txt",
				attacks.baseAttacksAsHumanReadableString().trim());
	}
	
	public void testGenRankAttacks(){
		assertFileEqualsString(BASE_DIR + "correct-rank-attacks.txt", attacks.rankAttacksAsHumanReadableString());
	}

	public void testGenFileAttacks() {
		//fail("Not yet implemented");
		//System.out.println(attacks.fileAttacksAsHumanReadableString());
		assertFileEqualsString(BASE_DIR + "correct-file-attacks.txt", attacks.fileAttacksAsHumanReadableString());
	}

	public void testGenDiagonal45DegreesRightAttacks() {
		//fail("It appears this output is incorrect....first few are okay but not farther on");
		assertFileEqualsString(BASE_DIR + "correct-rotated-45-right-attacks.txt", attacks.diagonal45DegreesRightAttacksAsHumanReadableString());
	}

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

	public void testGenDiagonal45DegreesLeftAttacks() {
		assertFileEqualsString(BASE_DIR + "correct-rotated-45-left-attacks.txt", attacks.diagonal45DegreesLeftAttacksAsHumanReadableString());
	}

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
