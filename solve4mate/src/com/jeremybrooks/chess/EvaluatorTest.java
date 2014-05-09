package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.WHITE;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EvaluatorTest {

	private Evaluator eval;
	
	@Before
	public void setUp()
	{
		eval = new Evaluator();
		MoveGenerator moveGenerator = new MoveGenerator();
		eval.setMoveGenerator(moveGenerator);
	}

	@Test
	public void testStartPositionEvaluationHasNoAdvantageForEitherSide() {
		GameState g = new GameState(GameState.MAX_NUM_MOVES_MADE);
		String initialPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		g.set(initialPosition);
		int scoreAtStartOfGame = eval.evaluate(g, WHITE, 0, false, true);
		assertFalse("white should have no advantage", scoreAtStartOfGame > 0);
		assertFalse("black should have no advantage", scoreAtStartOfGame < 0);
		assertEquals(0, scoreAtStartOfGame);
	}
	
	@Test
	public void testWhiteMatesBlack()
	{
		String queenRookMate = "k6Q/5R2/8/8/8/8/8/6K1 b - - 0 1";
		GameState g = new GameState(2);
		boolean isWhiteToMove = setupState(g, queenRookMate);
		int searchDepth = 0;
		int materialAdvantage = 1400; //white is up a queen and a rook
		int whiteMatesBlackScore = Evaluator.CHECKMATE - searchDepth + materialAdvantage;
		int actualScore = evaluate(g, isWhiteToMove, searchDepth);
		assertEquals(whiteMatesBlackScore, actualScore);
		assertTrue("white mates black should be a negative score", actualScore > 0);
	}
	
	@Test
	public void testBlackMatesWhite()
	{
		String backRankMate = "k7/8/8/8/8/8/5PPP/r5K1 w - - 0 1";
		GameState g = new GameState(2);
		boolean isWhiteToMove = setupState(g, backRankMate);
		int searchDepth = 0;
		int materialAdvantage = 200; //black rook (500) minus 3 white pawns (300)
		int blackMatesWhiteScore = -1 * (Evaluator.CHECKMATE - searchDepth + materialAdvantage);
		int actualScore = evaluate(g, isWhiteToMove, searchDepth);
		assertEquals(blackMatesWhiteScore, actualScore);
		assertFalse("black mates white should be a negative score", actualScore >= 0);
	}
	
	@Test @Ignore
	public void testDrawScore()
	{
		fail("need to write this one; requires new code in evaluate() which is more complex"
				+ "as it needs to look for stalemate, insufficient material, fifty move rule, repitition, etc");
	}


	
	private boolean setupState(GameState gameState, String startState) {
		String position = startState;
		gameState.set(startState);
		String initialState = gameState.get();
		assertEquals(position, initialState);
		return gameState.isWhiteToMove();
	}

	private int evaluate(GameState g, boolean isWhiteToMove, int searchDepth) {
		return eval.evaluate(g, isWhiteToMove?0:1, searchDepth, false, false);
	}

}
