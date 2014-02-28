package com.jeremybrooks.chess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class MoveGeneratorTest extends TestCase {

	private static final String DIR = "data";
	
	private static final String[] POSITIONS = {
		"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
		"rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1"
	};
	
	private MoveGenerator mg = new MoveGenerator();
	private GameState g; 
	
	protected void setUp() throws Exception {
		super.setUp();
		g = new GameState();
	}

	public void testClearPiece() {
		fail("Not yet implemented");
	}

	public void testFirstPiece() {
		fail("Not yet implemented");
	}

	public void testGenerateCaptures() {
		fail("Not yet implemented");
		/*
		 * pawn,knight,bishop,rook,queen,king capturing pieces
		 * pawn captures and promotes
		 * 
		 */
		
		
	}

//	public void testGenerateNonCaptures() {
//		//assertEquals() //TODO: I must have been working on this method when I last stopped because
//		//obviously there's a missing ';' here and this didn't compile when trying to resurrect this project on Feb 21, 2014
//		
//		g.set(POSITIONS[0]);
//		
//		mg.GenerateNonCaptures(g, g.moves, Color.WHITE, 0);
//		
//		Set<String> moves = new HashSet<String>();
//		for(int i=0; i < g.legalMoves[0]; i++)
//		{
//			String s = Util.displayMoveStr(g.moves[i], false, false); 
//			moves.add(s);
//			System.out.println(s);
//			//System.out.println("expected.add(\"" + s + "\");");
//		}
//		assertSame(expected, moves);
//	}

	public void testGenerateKingEscapes() {
		fail("Not yet implemented");
	}

	public void testIsAttacked() {
		fail("Not yet implemented");
	}

	
	/**
	 * Returns a map consisting of two keys:
	 * 		description -> a String describing the test
	 * 		position -> a String (fen) of the state of the game
	 *  	moves -> a Set of moves from that position
	 *  
	 * @param filename
	 * @return
	 * @throws IOException 
	 */
	private static Map<String,Object> loadFromFile(String filename) throws IOException
	{
		Map<String, Object> map = new HashMap<String, Object>(2);
		BufferedReader br = new BufferedReader(new FileReader(filename));

		//skip starting comments
		while(br.ready() && br.readLine().trim().startsWith("#")) ;
		
		String position = br.readLine();
		Set<String> moves = new HashSet<String>();
		while(br.ready()){
			moves.add(br.readLine());
		}
		map.put("position", position);
		map.put("moves", moves);
		return map;
	}
	
	
	private static void assertSetsAreEqual(Set<String> expected, Set<String> actual)
	{
		if (expected.size() != actual.size())
		{
			fail("sets are not the same size, expected size is " + expected.size() + 
					" actual size is " + actual.size());
		}
		//TODO: again the following didn't compile when resurrecting this project on Feb 21, 2014
//		expected.
//		Collections.sort(expected.to);
//		Collections.sort
		
	}
	
}
