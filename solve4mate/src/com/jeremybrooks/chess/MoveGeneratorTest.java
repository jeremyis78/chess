package com.jeremybrooks.chess;

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
	public void testGenerateCapturesFromStartingPositionWhiteToMove() {
		g.set(POSITIONS[0]);
		mg.GenerateCaptures(g, g.moves, Color.WHITE, 0);
		Set<String> generatedCaptures = createHumanReadableMoves(g.numberOfLegalMoves[0]);
		Set<String> expectedCapturesFromStartingPosition = Collections.emptySet();
		assertMovesAreEqual(expectedCapturesFromStartingPosition, generatedCaptures);
	}

	public void testGenerateCapturesFromStartingPositionBlackToMove() {
		g.set(POSITIONS[0]);
		mg.GenerateCaptures(g, g.moves, Color.BLACK, 0);
		Set<String> generatedCaptures = createHumanReadableMoves(g.numberOfLegalMoves[0]);
		Set<String> expectedCapturesFromStartingPosition = Collections.emptySet();
		assertMovesAreEqual(expectedCapturesFromStartingPosition, generatedCaptures);
	}

	public void testGenerateNonCapturesFromStartingPositionWhiteToMove() {
		g.set(POSITIONS[0]);
		mg.GenerateNonCaptures(g, g.moves, Color.WHITE, 0);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[0]);
		assertMovesAreEqual(expectedWhiteMovesFromStartingPosition(), generatedMoves);
	}

	public void testGenerateNonCapturesFromStartingPositionBlackToMove() {
		g.set(POSITIONS[0]);
		mg.GenerateNonCaptures(g, g.moves, Color.BLACK, 0);
		Set<String> generatedMoves = createHumanReadableMoves(g.numberOfLegalMoves[0]);
		assertMovesAreEqual(expectedBlackMovesFromStartingPosition(), generatedMoves);
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
