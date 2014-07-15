package com.jeremybrooks.chess.search;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.base.Bitmap.*;

import org.junit.Test;

public class SearchParamsTest {

    @Test
    public void givenGameInOneHour() {
        SearchParams c = new SearchParams(SearchParams.ONE_HOUR);
        assertEquals(SearchParams.ONE_HOUR, c.getTime(WHITE));
        assertEquals(0, c.getIncrement(WHITE));
        assertEquals(c.getTime(WHITE), c.getTime(BLACK));
        assertEquals(c.getIncrement(WHITE), c.getIncrement(BLACK));
        assertEquals(0, c.getMovesToGo());
        assertTrue(c.isSuddenDeath());
    }

    @Test
    public void givenSuddenDeathBlitzGame() {
        final int twoMinutesForGame = 2*SearchParams.ONE_MINUTE;
        final int withSixSecondIncrementPerMove = 6*SearchParams.ONE_SECOND;
        SearchParams c = new SearchParams(twoMinutesForGame, withSixSecondIncrementPerMove);
        assertEquals(twoMinutesForGame, c.getTime(WHITE));
        assertEquals(withSixSecondIncrementPerMove, c.getIncrement(WHITE));
        assertEquals(c.getTime(WHITE), c.getTime(BLACK));
        assertEquals(c.getIncrement(WHITE), c.getIncrement(BLACK));
        assertEquals(0, c.getMovesToGo());
        assertTrue(c.isSuddenDeath());
    }

    @Test
    public void givenSuddenDeathGameInProgressWithTimeOdds() {
        SearchParams c = new SearchParams();
        c.setTime(WHITE, 54321);
        c.setIncrement(WHITE, 2000);
        c.setTime(BLACK, 98765);
        c.setIncrement(BLACK, 1000);
        assertEquals(54321, c.getTime(WHITE));
        assertEquals(2000, c.getIncrement(WHITE));
        assertEquals(98765, c.getTime(BLACK));
        assertEquals(1000, c.getIncrement(BLACK));
        assertTrue(c.isSuddenDeath());
    }

    @Test
    public void givenFortyMovesInTwoHours() {
        int fortyMoves = 40;
        int inTwoHours = SearchParams.TWO_HOURS;
        int withNoIncrementPerMove = 0;
        SearchParams c = new SearchParams(fortyMoves, inTwoHours, withNoIncrementPerMove);
        assertEquals(inTwoHours, c.getTime(WHITE));
        assertEquals(0, c.getIncrement(WHITE));
        assertEquals(c.getTime(WHITE), c.getTime(BLACK));
        assertEquals(c.getIncrement(WHITE), c.getIncrement(BLACK));
        assertEquals(fortyMoves, c.getMovesToGo());
        assertFalse(c.isSuddenDeath());
    }
}
