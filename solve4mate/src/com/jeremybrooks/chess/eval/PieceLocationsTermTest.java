package com.jeremybrooks.chess.eval;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.GameState;

public class PieceLocationsTermTest extends EvalTermTestBase {

    @Before
    public void setUp()
    {
        term = new PieceLocationsTerm();
    }

	@Test
	public void spotCheckStartingPosition() {
        String fen = GameState.FEN_START;
        int expectedScore = 2 * (5 + 10 + 10 + -20)  //white pawns
        		          + 2 * 0    //white rooks
        		          + 2 * -40  //white knights
        		          + 2 * -10  //white bishops
        		          + -5       //white queen
        		          + 0;       //white king
        expectedScore = expectedScore - expectedScore;
        int score = evaluate(fen);
        assertEquals(expectedScore, score);
	}

	@Test
	public void spotCheckStartingPositionAllWhiteOnlyBlackKingAndRookOnA7() {
        String fen = "4k3/r7/8/8/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1";
        int expectedScore = 2 * (5 + 10 + 10 + -20)  //white pawns
        		          + 2 * 0    //white rooks
        		          + 2 * -40  //white knights
        		          + 2 * -10  //white bishops
        		          + -5       //white queen
        		          + 0;       //white king
        int blackKingScore = 0;
        int blackRookScore = -5;
        expectedScore -= (blackKingScore + blackRookScore);
        int score = evaluate(fen);
        assertEquals(expectedScore, score);
	}

	@Test
	public void spotCheckStartingPositionAllBlackOnlyWhiteKingAndKnightE5() {
        String fen = "rnbqkbnr/pppppppp/8/8/4N3/8/8/4K3 w kq - 0 1";
        int expectedScore = 2 * (5 + 10 + 10 + -20)  //black pawns
        		          + 2 * 0    //black rooks
        		          + 2 * -40  //black knights
        		          + 2 * -10  //black bishops
        		          + -5       //black queen
        		          + 0;       //black king
        expectedScore *= -1; //for black
        int whiteKingScore = 0;
        int whiteKnightOnE5Score = 20;
        expectedScore += (whiteKingScore + whiteKnightOnE5Score);
        int score = evaluate(fen);
        assertEquals(expectedScore, score);
	}

}
