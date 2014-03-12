package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import org.junit.Test;

public class EvaluatorTest {

	@Test
	public void testStartPositionEvaluationHasNoAdvantageForEitherSide() {
		GameState g = new GameState();
		Evaluator evaluator = new Evaluator();
		MoveGenerator moveGenerator = new MoveGenerator();
		evaluator.setMoveGenerator(moveGenerator);
		
		String initialPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		g.set(initialPosition);
		int scoreAtStartOfGame = evaluator.evaluate(g, Color.WHITE, 0, false, true);
		assertFalse("white should have no advantage", scoreAtStartOfGame > 0);
		assertFalse("black should have no advantage", scoreAtStartOfGame < 0);
		assertEquals(0, scoreAtStartOfGame);
	}

}
