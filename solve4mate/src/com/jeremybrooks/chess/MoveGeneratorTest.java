package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class MoveGeneratorTest {

	private static final String[] POSITIONS = {
		"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
		"rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1"
	};
	
	private MoveGenerator mg = new MoveGenerator();
	private GameState g; 
	
	@Before
	public void setUp() throws Exception {
		g = new GameState();
	}

	@Test
	public void testEncodeMove()
	{
		int fromSquare = Bitmap.B7;
		int toSquare = Bitmap.C8;
		int movingPiece = Bitmap.PIECE[Bitmap.TO_PIECE[Pieces.PAWN]];
		int capturedPiece = Bitmap.PIECE[Bitmap.TO_PIECE[Pieces.BISHOP]];
		int promotedPiece = Bitmap.PIECE[Bitmap.TO_PIECE[Pieces.QUEEN]];
		int encodedMove = MoveGenerator.EncodeMove(fromSquare, toSquare, movingPiece, capturedPiece, promotedPiece);
		
		assertEquals(fromSquare, encodedMove & 0x3F);
		assertEquals(toSquare, (encodedMove >> 6) & 0x3F);
		assertEquals(movingPiece, (encodedMove >> 12) & 0x3);
		assertEquals(capturedPiece, (encodedMove >> 15) & 0x3);
		assertEquals(promotedPiece, (encodedMove >> 18) & 0x3);
	}
	
	@Test
	public void testGenerateCapturesFromStartingPositionWhiteToMove() {
		g.set(POSITIONS[0]);
		mg.GenerateCaptures(g, g.moves, Color.WHITE, 0);
		Set<String> generatedCaptures = createHumanReadableMoves(g.numberOfLegalMoves[0]);
		Set<String> expectedCapturesFromStartingPosition = Collections.emptySet();
		assertMovesAreEqual(expectedCapturesFromStartingPosition, generatedCaptures);
	}

	@Test
	public void testGenerateCapturesFromStartingPositionBlackToMove() {
		g.set(POSITIONS[0]);
		mg.GenerateCaptures(g, g.moves, Color.BLACK, 0);
		Set<String> generatedCaptures = createHumanReadableMoves(g.numberOfLegalMoves[0]);
		Set<String> expectedCapturesFromStartingPosition = Collections.emptySet();
		assertMovesAreEqual(expectedCapturesFromStartingPosition, generatedCaptures);
	}

	@Test
	public void testGenerateNonCapturesFromStartingPositionWhiteToMove() {
		g.set(POSITIONS[0]);
		mg.GenerateNonCaptures(g, g.moves, Color.WHITE, 0);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[0]);
		assertMovesAreEqual(expectedWhiteMovesFromStartingPosition(), generatedMoves);
	}

	@Test
	public void testGenerateNonCapturesFromStartingPositionBlackToMove() {
		g.set(POSITIONS[0]);
		mg.GenerateNonCaptures(g, g.moves, Color.BLACK, 0);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[0]);
		assertMovesAreEqual(expectedBlackMovesFromStartingPosition(), generatedMoves);
	}
	
	@Test
	public void testGenerateNonCapturesPosition1() {
		g.set("R1Q5/1p3p2/1k1qpb2/8/P2p4/P2P2P1/4rPK1/8 w - - 0 1");
		System.out.println(new Displayer().formatBoard(g.pos));
		System.out.println("To Move: " + (g.sideToMove == 0 ? "White" : "Black"));
		int depth = 0;
		mg.GenerateNonCaptures(g, g.moves, g.sideToMove, depth);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[depth]);
		String kingMoves = "Kg2-f1,Kg2-g1,Kg2-h1,Kg2-h2,Kg2-f3,Kg2-h3";
		String pawnMoves = "Pf2-f3,Pf2-f4,Pg3-g4,Pa4-a5";
		String rookMoves = "Ra8-a5,Ra8-a6,Ra8-a7,Ra8-b8";
		String queenMoves = "Qc8-c1,Qc8-c2,Qc8-c3,Qc8-c4,Qc8-c5,Qc8-c6,Qc8-c7," +
				"Qc8-d7,Qc8-b8,Qc8-d8,Qc8-e8,Qc8-f8,Qc8-g8,Qc8-h8";
		assertMovesAreEqual(toSetOfMoves(kingMoves+","+pawnMoves+","+rookMoves+","+queenMoves), generatedMoves);
	}

	@Test
	public void testGenerateCapturesPosition1() {
		g.set("R1Q5/1p3p2/1k1qpb2/8/P2p4/P2P2P1/4rPK1/8 w - - 0 1");
		System.out.println(new Displayer().formatBoard(g.pos));
		System.out.println("To Move: " + (g.sideToMove == 0 ? "White" : "Black"));
		int depth = 0;
		mg.GenerateCaptures(g, g.moves, g.sideToMove, depth);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[depth]);
		String queenMoves = "Qc8xb7,Qc8xe6";
		assertMovesAreEqual(toSetOfMoves(queenMoves), generatedMoves);
	}
	
	@Test
	public void testPrintingMoves() {
		
		String[] positions = new String[]{
				"r1b2k1r/1pQn2pp/pP2p3/2p1Pp1N/B2p2q1/B2P4/P1P2PPP/R3K2R w KQ f6 0 1"
				/*
				"r4r1k/1R1R2p1/7p/8/8/3Q1Ppq/P7/6K1 w - - 0 1",
				"6k1/ppp2pp1/1q4b1/5rQp/8/1P6/PBP2PPP/3R2K1 w - - 0 1",
				"8/6k1/8/3b3Q/pP4P1/1P6/KP3r2/N4r2 b - - 0 1",
				"3rr1k1/pp3ppp/3b4/2p5/2Q5/6qP/PPP1B1P1/R1B2K1R b - - 0 1",
				"5r1k/p4rpp/1p1b1pQ1/q2p4/P2N1PPN/1P2P2R/7P/6RK w - - 0 1"
				*/
		};
		for(String position: positions)
		{
			g.set("r1b2k1r/1pQn2pp/pP2p3/2p1Pp1N/B2p2q1/B2P4/P1P2PPP/R3K2R w KQ f6 0 1");
			System.out.println(new Displayer().formatBoard(g.pos));
			System.out.println("To Move: " + (g.sideToMove == 0 ? "White" : "Black") + "\n");
			int depth = 0;
			mg.GenerateCaptures(g, g.moves, g.sideToMove, depth);
			mg.GenerateNonCaptures(g, g.moves, g.sideToMove, depth);
			Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[depth]);
			
			String allMoves = "blah"; 
			assertMovesAreEqual(toSetOfMoves(allMoves), generatedMoves);
		}
	}
	
	@Test
	public void testWhitePawnPromotions()
	{
		g.set("1r1n2k1/2PP4/8/8/8/8/2q5/K7 w - - 0 1");
		System.out.println(new Displayer().formatBoard(g.pos));
		System.out.println("To Move: " + (g.sideToMove == 0 ? "White" : "Black") + "\n");
		int depth = 0;
		mg.GenerateCaptures(g, g.moves, g.sideToMove, depth);
		mg.GenerateNonCaptures(g, g.moves, g.sideToMove, depth);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[depth]);
		
		String expectedMoves = "Pc7-c8B,Pc7-c8N,Pc7-c8Q,Pc7-c8R,Pc7xb8B,Pc7xb8N,"
				+ "Pc7xb8Q,Pc7xb8R,Pc7xd8B,Pc7xd8N,Pc7xd8Q,Pc7xd8R"; 
		assertMovesAreEqual(toSetOfMoves(expectedMoves), generatedMoves);
	}

	@Test
	public void testBlackPawnPromotions()
	{
		g.set("k7/2Q5/8/8/8/8/7p/K5NR b - - 0 1");
		System.out.println(new Displayer().formatBoard(g.pos));
		System.out.println("To Move: " + (g.sideToMove == 0 ? "White" : "Black") + "\n");
		int depth = 0;
		mg.GenerateCaptures(g, g.moves, g.sideToMove, depth);
		mg.GenerateNonCaptures(g, g.moves, g.sideToMove, depth);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[depth]);
		
		String expectedMoves = "Ph2xg1Q,Ph2xg1R,Ph2xg1B,Ph2xg1N"; 
		assertMovesAreEqual(toSetOfMoves(expectedMoves), generatedMoves);
	}

	@Test
	public void testWhiteCastling()
	{
		g.set("k7/8/8/8/8/p6p/P6P/R3K2R w KQ - 0 1");
		System.out.println(new Displayer().formatBoard(g.pos));
		System.out.println("To Move: " + (g.sideToMove == 0 ? "White" : "Black") + "\n");
		int depth = 0;
		mg.GenerateCaptures(g, g.moves, g.sideToMove, depth);
		mg.GenerateNonCaptures(g, g.moves, g.sideToMove, depth);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[depth]);
		
		String expectedMoves = "Ke1-c1 0-0-0,Ke1-d1,Ke1-d2,Ke1-e2,Ke1-f1,Ke1-f2,Ke1-g1 0-0,"
				+ "Ra1-b1,Ra1-c1,Ra1-d1,Rh1-f1,Rh1-g1"; 
		assertMovesAreEqual(toSetOfMoves(expectedMoves), generatedMoves);
		generatedMoves.contains("Ke1-c1 0-0-0");
		generatedMoves.contains("Ke1-g1 0-0");
		
	}

	@Test
	public void testWhiteCannotCastle()
	{
		g.set("k7/8/8/8/8/p6p/P6P/R3K2R w - - 0 1 ; king already moved");
		System.out.println(new Displayer().formatBoard(g.pos));
		System.out.println("To Move: " + (g.sideToMove == 0 ? "White" : "Black") + "\n");
		int depth = 0;
		mg.GenerateCaptures(g, g.moves, g.sideToMove, depth);
		mg.GenerateNonCaptures(g, g.moves, g.sideToMove, depth);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[depth]);
		
		assertFalse(generatedMoves.contains("Ke1-c1 0-0-0"));
		assertFalse(generatedMoves.contains("Ke1-g1 0-0"));
	}

	
	@Test
	public void testMixOfEverythingPosition()
	{
		g.set("r1b2k1r/1pQn2pp/pP2p3/2p1Pp1N/B2p2q1/B2P4/P1P2PPP/R3K2R w KQ f6 0 1");
		System.out.println(new Displayer().formatBoard(g.pos));
		System.out.println("To Move: " + (g.sideToMove == 0 ? "White" : "Black") + "\n");
		int depth = 0;
		mg.GenerateCaptures(g, g.moves, g.sideToMove, depth);
		mg.GenerateNonCaptures(g, g.moves, g.sideToMove, depth);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[depth]);
		
		String allMoves = "Pc2-c3,Pc2-c4,Pe5xf6,Pf2-f3,Pf2-f4,Pg2-g3,Ph2-h3,Ph2-h4," +
				"Ba3-b2,Ba3-b4,Ba3-c1,Ba3xc5,Ba4-b3,Ba4-b5,Ba4-c6,Ba4xd7," +
				"Nh5-f4,Nh5-f6,Nh5-g3,Nh5xg7," +
				"Ra1-b1,Ra1-c1,Ra1-d1,Rh1-f1,Rh1-g1," +
				"Qc7-b8,Qc7-c6,Qc7-d6,Qc7-d8,Qc7xb7,Qc7xc5,Qc7xc8,Qc7xd7," +
				"Ke1-d2,Ke1-f1,Ke1-g1 0-0"; 
		assertMovesAreEqual(toSetOfMoves(allMoves), generatedMoves);
	}
	
	private Set<String> createHumanReadableMoves(int numberOfLegalMoves) {
		Set<String> moves = new HashSet<String>();
		for(int i=0; i < numberOfLegalMoves; i++)
		{
			String s = Util.displayMoveStr(g.moves[i], false, false); 
			moves.add(s);
		}
		return moves;
	}
	
	private static Set<String> expectedWhiteMovesFromStartingPosition()
	{
		Set<String> moves = new HashSet<String>();
		moves.add("Pa2-a4");
		moves.add("Pb2-b4");
		moves.add("Pc2-c4");
		moves.add("Pd2-d4");
		moves.add("Pe2-e4");
		moves.add("Pf2-f4");
		moves.add("Pg2-g4");
		moves.add("Ph2-h4");
		moves.add("Pa2-a3");
		moves.add("Pb2-b3");
		moves.add("Pc2-c3");
		moves.add("Pd2-d3");
		moves.add("Pe2-e3");
		moves.add("Pf2-f3");
		moves.add("Pg2-g3");
		moves.add("Ph2-h3");
		moves.add("Nb1-a3");
		moves.add("Nb1-c3");
		moves.add("Ng1-f3");
		moves.add("Ng1-h3");
		return moves;
	}

	private static Set<String> expectedBlackMovesFromStartingPosition()
	{
		Set<String> moves = new HashSet<String>();
		moves.add("Pa7-a5");
		moves.add("Pb7-b5");
		moves.add("Pc7-c5");
		moves.add("Pd7-d5");
		moves.add("Pe7-e5");
		moves.add("Pf7-f5");
		moves.add("Pg7-g5");
		moves.add("Ph7-h5");
		moves.add("Pa7-a6");
		moves.add("Pb7-b6");
		moves.add("Pc7-c6");
		moves.add("Pd7-d6");
		moves.add("Pe7-e6");
		moves.add("Pf7-f6");
		moves.add("Pg7-g6");
		moves.add("Ph7-h6");
		moves.add("Nb8-a6");
		moves.add("Nb8-c6");
		moves.add("Ng8-f6");
		moves.add("Ng8-h6");
		return moves;
	}

	private static Set<String> toSetOfMoves(String movesInCSV)
	{
		String[] moveArray = movesInCSV != null ? movesInCSV.split(",") : new String[]{};
		Set<String> moveSet = new HashSet<>();
		for(String move: moveArray)
		{
			moveSet.add(move);
		}
		return moveSet;
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
	
	
	private static void assertMovesAreEqual(Set<String> expectedMoves, Set<String> actualMoves)
	{
		if(setsAreDifferent(expectedMoves, actualMoves));
		{
			List<String> expectedSorted = new ArrayList<>();
			List<String> actualSorted = new ArrayList<>();
			expectedSorted.addAll(expectedMoves);
			actualSorted.addAll(actualMoves);
			Collections.sort(expectedSorted);
			Collections.sort(actualSorted);
			assertEquals(oneMovePerLine(expectedSorted), oneMovePerLine(actualSorted));
		}
	}

	private static String oneMovePerLine(List<String> moves) {
		StringBuilder sb = new StringBuilder();
		for(String move: moves)
		{
			sb.append(move);
			sb.append("\n");
		}
		return sb.toString();
	}

	private static boolean setsAreDifferent(Set<String> expected,
			Set<String> actual) {
		return !expected.toString().equals(actual.toString());
	}
	
}
