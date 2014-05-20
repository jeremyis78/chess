package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.WHITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
		int materialAdvantage = 1475; //white is up a queen and a rook
		int whiteMatesBlackScore = Evaluator.CHECKMATE - searchDepth + materialAdvantage;
		int actualScore = evaluate(g, isWhiteToMove, searchDepth);
		assertEquals(whiteMatesBlackScore, actualScore);
		assertTrue("white mates black should be a negative score", actualScore > 0);
	}
	
	@Test
	public void givenBlackMatesWhite()
	{
		String backRankMate = "k7/8/8/8/8/8/5PPP/r5K1 w - - 0 1";
		GameState g = new GameState(2);
		boolean isWhiteToMove = setupState(g, backRankMate);
		int searchDepth = 0;
		int materialAdvantage = 200; //black: rook(500),bishop(325),knight(325) minus white: 3 pawns (300)
		int blackMatesWhiteScore = -1 * (Evaluator.CHECKMATE - searchDepth + materialAdvantage);
		int actualScore = evaluate(g, isWhiteToMove, searchDepth);
		assertEquals(blackMatesWhiteScore, actualScore);
		assertFalse("black mates white should be a negative score", actualScore >= 0);
	}

	@Test
	public void givenBishopPair()
	{
		String whiteHasBishopPair = "bn5k/8/8/8/8/8/8/BB5K w - - 0 1";
		GameState g = new GameState(2);
		boolean isWhiteToMove = setupState(g, whiteHasBishopPair);
		int searchDepth = 0;
		int materialAdvantage = 50; //black has bn = 6.5, white has bb = 7 (6.5 + 0.5)
		int expectedScore = materialAdvantage;
		int actualScore = evaluate(g, isWhiteToMove, searchDepth);
		assertEquals("white has advantage with bishop pair", expectedScore, actualScore);
	}
	
	@Test @Ignore
	public void givenManyPositionsTimeMe()
	{
//		int timesToRun[] = new int[]{1,10,50,100,200,300,400,500};
//		for(int TIMES_TO_RUN_EVAL_PER_POSITION: timesToRun)
//		{
		int numTimesExecuted=0;
		long elapsedNanos = 0L;
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
		
		for(String position: positions)
		{
			for(int times=0; times<1 /*TIMES_TO_RUN_EVAL_PER_POSITION*/; times++)
			{
				GameState g = new GameState(2);
				boolean isWhiteToMove = setupState(g, position);
				int searchDepth = 0;
				long start = System.nanoTime();
				int actualScore = evaluate(g, isWhiteToMove, searchDepth);
				elapsedNanos += System.nanoTime() - start;
//				System.out.println(actualScore + ": " + g.get());
				numTimesExecuted++;
			}
		}
		System.out.println("ran evaluate() " + numTimesExecuted + " times in " + elapsedNanos + " nanoseconds = " + (elapsedNanos / numTimesExecuted) + " nanos/run");
//		}
	}

	
	@Test @Ignore
	public void testDrawScore()
	{
		fail("need to write this one; requires new code in evaluate() which is more complex"
				+ "as it needs to look for stalemate, insufficient material, fifty move rule, repetition, etc");
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
