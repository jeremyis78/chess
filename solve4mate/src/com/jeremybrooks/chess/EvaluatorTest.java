package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EvaluatorTest {

	private Evaluator eval;
	
	@Before
	public void setUp()
	{
		eval = new Evaluator();
		DefaultGenerator moveGenerator = new DefaultGenerator();
		eval.setMoveGenerator(moveGenerator);
	}

	@Test
	public void givenStartPositionEvaluation() {
		GameState g = new GameState(GameState.MAX_NUM_MOVES_MADE);
		String initialPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		g.set(initialPosition);
		int scoreAtStartOfGame = eval.evaluate(g, WHITE, 0, false, true);
		assertFalse("white should have no advantage", scoreAtStartOfGame > 0);
		assertFalse("black should have no advantage", scoreAtStartOfGame < 0);
		assertEquals(0, scoreAtStartOfGame);
	}
	
	@Test
	public void givenWhiteMatesBlack()
	{
		String queenRookMate = "k6Q/5R2/8/8/8/8/8/6K1 b - - 0 1";
		GameState g = new GameState(2);
		boolean isWhiteToMove = setupState(g, queenRookMate);
		int searchDepth = 0;
		int materialAdvantage = 1475 + (13 * 5); //white is up a queen and a rook (plus rook bonus for no pawns)
		int whiteMatesBlackScore = Evaluator.CHECKMATE - searchDepth + materialAdvantage;
		int actualScore = evaluate(g, isWhiteToMove, searchDepth);
		assertEquals(whiteMatesBlackScore, actualScore);
		assertTrue("white mates black should be a negative score", actualScore > 0);
	}
	
	@Test
	public void givenBlackMatesWhite()
	{
		String backRankMate = "k7/8/8/8/8/8/5PPP/q5K1 w - - 0 1";
		GameState g = new GameState(2);
		boolean isWhiteToMove = setupState(g, backRankMate);
		int searchDepth = 0;
		int materialAdvantage = 675; //black: queen(1 * 975) minus white: pawn(3 * 100)   
		int blackMatesWhiteScore = -1 * (Evaluator.CHECKMATE - searchDepth + materialAdvantage);
		int actualScore = evaluate(g, isWhiteToMove, searchDepth);
		assertEquals(blackMatesWhiteScore, actualScore);
		assertFalse("black mates white should be a negative score", actualScore >= 0);
	}

	@Test
	public void givenBishopPair()
	{
		String whiteHasBishopPair = "7k/8/8/8/8/8/8/BB5K w - - 0 1";
		GameState g = new GameState(2);
		boolean isWhiteToMove = setupState(g, whiteHasBishopPair);
		int searchDepth = 0;
		int materialAdvantage = 700; //white has bb = 7 (6.5 + 0.5)
		int expectedScore = materialAdvantage;
		int actualScore = evaluate(g, isWhiteToMove, searchDepth);
		assertEquals("white has advantage with bishop pair", expectedScore, actualScore);
	}

	@Test
	public void givenTwoBlackKnightsEightPawns() {
		GameState g = new GameState(GameState.MAX_NUM_MOVES_MADE);
		String initialPosition = "1n2k1n1/pppppppp/8/8/8/8/8/4K3 b kq - 0 1";
		g.set(initialPosition);
		int score = eval.evaluate(g, BLACK, 0, false, false);
		int startingScore = (325 * 2) + (100 * 8);
		int knightAdjustmentGivenEightPawns = (6 * 3) * 2;
		int startingBlackScore = -1 * (startingScore + knightAdjustmentGivenEightPawns);
		assertEquals(startingBlackScore, score);
	}

	@Test
	public void givenOneBlackKnightFourPawns() {
		GameState g = new GameState(GameState.MAX_NUM_MOVES_MADE);
		String initialPosition = "1n2k3/pppp4/8/8/8/8/8/4K3 b - - 0 1";
		g.set(initialPosition);
		int score = eval.evaluate(g, BLACK, 0, false, false);
		int knightFourPawnsScore = 325 + (100 * 4);
		int knightAdjustmentGivenFourPawns = -6;
		int startingBlackScore = -1 * (knightFourPawnsScore + knightAdjustmentGivenFourPawns);
		assertEquals(startingBlackScore, score);
	}

	@Test
	public void givenTwoWhiteKnightEightPawns() {
		GameState g = new GameState(GameState.MAX_NUM_MOVES_MADE);
		String initialPosition = "4k3/8/8/8/8/8/PPPPPPPP/1N2K1N1 w - - 0 1";
		g.set(initialPosition);
		int score = eval.evaluate(g, WHITE, 0, false, false);
		int twoKnightsEightPawnsScore = (325 * 2) + (100 * 8);
		int knightAdjustmentGivenAllPawns = 6 * 3 * 2; //bonus * 3 pawns * 2 knights
		int startingBlackScore = twoKnightsEightPawnsScore + knightAdjustmentGivenAllPawns;
		assertEquals(startingBlackScore, score);
	}
	
	@Test
	public void givenOneBlackKnightFivePawns() {
		//Boundary condition for knight adjustment is at 5 pawns
		GameState g = new GameState(GameState.MAX_NUM_MOVES_MADE);
		String initialPosition = "1n2k3/ppppp3/8/8/8/8/8/4K3 b - - 0 1";
		g.set(initialPosition);
		int score = eval.evaluate(g, BLACK, 0, false, false);
		int knightFivePawnsScore = 325 + (100 * 5);
		int knightAdjustmentGivenFivePawns = 0;
		int expectedScore = -1 * (knightFivePawnsScore + knightAdjustmentGivenFivePawns);
		assertEquals(expectedScore, score);
	}
	
	@Test
	public void givenOneBlackRookFourPawns() {
		GameState g = new GameState(GameState.MAX_NUM_MOVES_MADE);
		String initialPosition = "r3k3/pppp4/8/8/8/8/8/4K3 b - - 0 1";
		g.set(initialPosition);
		int score = eval.evaluate(g, BLACK, 0, false, false);
		int rookFourPawnsScore = 500 + (100 * 4);
		int rookAdjustmentGivenFourPawns = 13;
		int startingBlackScore = -1 * (rookFourPawnsScore + rookAdjustmentGivenFourPawns);
		assertEquals(startingBlackScore, score);
	}

	@Test
	public void givenTwoWhiteRooksSevenPawns() {
		GameState g = new GameState(GameState.MAX_NUM_MOVES_MADE);
		String initialPosition = "4k3/8/8/8/8/8/1PPPPPPP/R3K2R w - - 0 1";
		g.set(initialPosition);
		int score = eval.evaluate(g, WHITE, 0, false, false);
		int twoRooksSevenPawnsScore = (500 * 2) + (100 * 7);
		int rookAdjustmentGivenSevenPawns = 2 * (-13 * 2); //2 rooks * adjustment * pawns over 5
		int startingBlackScore = twoRooksSevenPawnsScore + rookAdjustmentGivenSevenPawns;
		assertEquals(startingBlackScore, score);
	}

	@Test
	public void givenWhiteRookFivePawns() {
		GameState g = new GameState(GameState.MAX_NUM_MOVES_MADE);
		String initialPosition = "4k3/8/8/8/8/8/3PPPPP/R3K3 w - - 0 1";
		g.set(initialPosition);
		int score = eval.evaluate(g, WHITE, 0, false, false);
		int twoRooksSevenPawnsScore = (500 * 1) + (100 * 5);
		int rookAdjustmentGivenSevenPawns = 0;
		int startingBlackScore = twoRooksSevenPawnsScore + rookAdjustmentGivenSevenPawns;
		assertEquals(startingBlackScore, score);
	}

	@Test @Ignore
	public void testEvaluateScoring()
	{
		List<String> positions = new ArrayList<>(14000);
		try (BufferedReader br = new BufferedReader(new FileReader("test-positions.txt")))
		{
			while(br.ready())
			{
				String line = br.readLine();
				if(line.startsWith("#")) continue;
				positions.add(line.split("\t")[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		positions.add("k7/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		positions.add("rnbqkbnr/pppppppp/8/8/8/8/8/4K3 w KQkq - 0 1");
		positions.add("k7/8/8/8/8/8/4PPPP/RNBQKBNR w KQkq - 0 1");
		positions.add("r1bk3r/3p1ppp/2p1p3/p2n4/8/BRPB4/P1P2PPP/1R4K1 w - - 2 17");

		for(String position: positions)
		{
			GameState g = new GameState(2);
			boolean isWhiteToMove = setupState(g, position);
			int searchDepth = 0;
			int actualScore = evaluate(g, isWhiteToMove, searchDepth);
			System.out.println(actualScore + ": " + g.get());
		}
	}


	@Test @Ignore
	public void givenManyPositionsTimeMe()
	{
		/*
		 * Run speed tests on the eval functions
		 */
		List<String> positions = new ArrayList<>(14000);
		try (BufferedReader br = new BufferedReader(new FileReader("test-positions.txt")))
		{
			while(br.ready())
			{
				String line = br.readLine();
				if(line.startsWith("#")) continue;
				positions.add(line.split("\t")[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		int timesToRun[] = new int[]{300};
		for(int numTimesToRun: timesToRun)
		{
			int numTimesExecuted=0;
			long elapsedNanos = 0L;
			int numPositions = positions.size();
			for(int index=0; index < numPositions; index++)
			{
				int positionIndex = (int) (numPositions * Math.random());
				String position = positions.get(positionIndex);
				for(int times=0; times<numTimesToRun; times++)
				{
					GameState g = new GameState(2);
					//System.out.println("index: " + positionIndex);
					boolean isWhiteToMove = setupState(g, position);
					int searchDepth = 0;
					long start = System.nanoTime();
					int actualScore = evaluate(g, isWhiteToMove, searchDepth);
					elapsedNanos += System.nanoTime() - start;
					//System.out.println(actualScore + ": " + g.get());
					numTimesExecuted++;
				}
				index++;
			}
			assertTrue("needs to run at least 1.5 million times for good average results", 
					numTimesExecuted > 1500000);
			double nanosPerSecond = 1e9;
			System.out.println("ran evaluate() " +
					numTimesExecuted + " times in " + 
					elapsedNanos/nanosPerSecond + " seconds = " +
					(elapsedNanos / numTimesExecuted) + " nanos/run");
		}
	}
	
	private boolean setupState(GameState gameState, String startState) {
		String position = startState;
		gameState.set(startState);
		String initialState = gameState.get();
		assertEquals(position.substring(0, position.length()-2), initialState.substring(0, position.length()-2));
		return gameState.isWhiteToMove();
	}

	private int evaluate(GameState g, boolean isWhiteToMove, int searchDepth) {
		return eval.evaluate(g, isWhiteToMove?0:1, searchDepth, false, false);
	}

}
