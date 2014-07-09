package com.jeremybrooks.chess.eval;

import com.jeremybrooks.chess.base.GameState;

public abstract class EvalTermTestBase {

	private static final int GAMESTATE_MAX_MOVES = 2;
	protected EvalTerm term;

	
	protected int evaluate(String fen) {
		GameState g = new GameState(GAMESTATE_MAX_MOVES);
		g.set(fen);
		int score = term.evaluate(g);
		return score;
	}

	protected static int whiteScore(int score) { return score; }
	protected static int blackScore(int score) { return -1 * score; }

	protected static int pieceCount(char pieceChar, String fen) {
		int count = 0;
		for(char c: fen.toCharArray())
		{
			if(pieceChar == c) count++;
		}
		return count;
	}


}