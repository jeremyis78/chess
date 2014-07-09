package com.jeremybrooks.chess.search;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.base.Bitmap.*;

import org.junit.Test;

public class SearchParamsTest {

	@Test
	public void givenGameInOneHour() {
		SearchParams c = new SearchParams(SearchParams.ONE_HOUR);
		assertEquals(SearchParams.ONE_HOUR, c.getRemainingMillisFor(WHITE));
		assertEquals(0, c.getIncrementMillisFor(WHITE));
		assertEquals(c.getRemainingMillisFor(WHITE), c.getRemainingMillisFor(BLACK));
		assertEquals(c.getIncrementMillisFor(WHITE), c.getIncrementMillisFor(BLACK));
		assertEquals(0, c.getMovesToGo());
		assertTrue(c.isSuddenDeath());
	}

	@Test
	public void givenSuddenDeathBlitzGame() {
		final int twoMinutesForGame = 2*SearchParams.ONE_MINUTE;
		final int withSixSecondIncrementPerMove = 6*SearchParams.ONE_SECOND;
		SearchParams c = new SearchParams(twoMinutesForGame, withSixSecondIncrementPerMove);
		assertEquals(twoMinutesForGame, c.getRemainingMillisFor(WHITE));
		assertEquals(withSixSecondIncrementPerMove, c.getIncrementMillisFor(WHITE));
		assertEquals(c.getRemainingMillisFor(WHITE), c.getRemainingMillisFor(BLACK));
		assertEquals(c.getIncrementMillisFor(WHITE), c.getIncrementMillisFor(BLACK));
		assertEquals(0, c.getMovesToGo());
		assertTrue(c.isSuddenDeath());
	}

	@Test
	public void givenSuddenDeathGameInProgressWithTimeOdds() {
		SearchParams c = new SearchParams();
		c.setRemainingMillisFor(WHITE, 54321);
		c.setIncrementMillisFor(WHITE, 2000);
		c.setRemainingMillisFor(BLACK, 98765);
		c.setIncrementMillisFor(BLACK, 1000);
		assertEquals(54321, c.getRemainingMillisFor(WHITE));
		assertEquals(2000, c.getIncrementMillisFor(WHITE));
		assertEquals(98765, c.getRemainingMillisFor(BLACK));
		assertEquals(1000, c.getIncrementMillisFor(BLACK));
		assertTrue(c.isSuddenDeath());
	}

	@Test
	public void givenFortyMovesInTwoHours() {
		int fortyMoves = 40;
		int inTwoHours = SearchParams.TWO_HOURS;
		int withNoIncrementPerMove = 0;
		SearchParams c = new SearchParams(fortyMoves, inTwoHours, withNoIncrementPerMove);
		assertEquals(inTwoHours, c.getRemainingMillisFor(WHITE));
		assertEquals(0, c.getIncrementMillisFor(WHITE));
		assertEquals(c.getRemainingMillisFor(WHITE), c.getRemainingMillisFor(BLACK));
		assertEquals(c.getIncrementMillisFor(WHITE), c.getIncrementMillisFor(BLACK));
		assertEquals(fortyMoves, c.getMovesToGo());
		assertFalse(c.isSuddenDeath());
	}
}
