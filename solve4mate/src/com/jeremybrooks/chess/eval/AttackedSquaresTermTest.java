package com.jeremybrooks.chess.eval;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AttackedSquaresTermTest extends EvalTermTestBase {

	@Before
	public void setUp() throws Exception {
		term = new AttackedSquaresTerm();
	}

	@Test
	public void givenInitialPosition() {
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		int expectedScore = 0;
		int score = evaluate(fen);
		assertEquals(expectedScore, score);
	}

	@Test
	public void givenPositionIncludingEnPassantOption() {
		String fen = "r1r5/1b1nq1k1/p2b2p1/4ppP1/1p1P2Q1/1B2PN2/1P1B1P2/1K1R3R w - f6 0 1";
		int expectedScore = -1; // = 40 squares attacked by white - 41 by black
		int score = evaluate(fen);
		assertEquals(expectedScore, score);
	}

}
