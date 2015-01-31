package com.jeremybrooks.chess.search;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.base.Bitmap.*;

import org.junit.Test;

import com.jeremybrooks.chess.base.Piece;

public class SearchParamsTest {

    @Test
    public void givenGameInOneHour() {
        SearchParams c = new SearchParams(SearchParams.ONE_HOUR);

        assertEquals(SearchParams.ONE_HOUR, c.getTime(Piece.WHITE));
        assertEquals(0,                     c.getIncrement(Piece.WHITE));
        assertEquals(c.getTime(Piece.WHITE),      c.getTime(Piece.BLACK));
        assertEquals(c.getIncrement(Piece.WHITE), c.getIncrement(Piece.BLACK));
        assertEquals(0,                     c.getMovesToGo());
        assertTrue(c.isSuddenDeath());
    }
    
    @Test
    public void givenLastMoveInTimeControl() {
        SearchParams p = new SearchParams(1, SearchParams.ONE_MINUTE);
        
        assertEquals(false,                   p.isSuddenDeath());
        assertEquals(1,                       p.getMovesToGo());
        assertEquals(SearchParams.ONE_MINUTE, p.getTime(Piece.WHITE));
        assertEquals(SearchParams.ONE_MINUTE, p.getTime(Piece.BLACK));
        assertEquals(0,                       p.getIncrement(Piece.WHITE));
        assertEquals(0,                       p.getIncrement(Piece.BLACK));
    }

    @Test
    public void givenSuddenDeathBlitzGame() {
        final int twoMinutesForGame = 2*SearchParams.ONE_MINUTE;
        final int withSixSecondIncrementPerMove = 6*SearchParams.ONE_SECOND;
        SearchParams c = new SearchParams(0, twoMinutesForGame, withSixSecondIncrementPerMove);
        
        assertEquals(twoMinutesForGame,             c.getTime(Piece.WHITE));
        assertEquals(withSixSecondIncrementPerMove, c.getIncrement(Piece.WHITE));
        assertEquals(c.getTime(Piece.WHITE),              c.getTime(Piece.BLACK));
        assertEquals(c.getIncrement(Piece.WHITE),         c.getIncrement(Piece.BLACK));
        assertEquals(0,                             c.getMovesToGo());
        assertTrue(c.isSuddenDeath());
    }

    @Test
    public void givenSuddenDeathGameInProgressWithTimeOdds() {
        SearchParams c = new SearchParams();
        c.setTime(Piece.WHITE, 54321);
        c.setIncrement(Piece.WHITE, 2000);
        c.setTime(Piece.BLACK, 98765);
        c.setIncrement(Piece.BLACK, 1000);
        
        assertEquals(54321, c.getTime(Piece.WHITE));
        assertEquals(2000,  c.getIncrement(Piece.WHITE));
        assertEquals(98765, c.getTime(Piece.BLACK));
        assertEquals(1000,  c.getIncrement(Piece.BLACK));
        assertTrue(c.isSuddenDeath());
    }

    @Test
    public void givenFortyMovesInTwoHours() {
        int fortyMoves = 40;
        int inTwoHours = SearchParams.TWO_HOURS;
        int withNoIncrementPerMove = 0;
        SearchParams c = new SearchParams(fortyMoves, inTwoHours, withNoIncrementPerMove);
        
        assertEquals(inTwoHours,            c.getTime(Piece.WHITE));
        assertEquals(0,                     c.getIncrement(Piece.WHITE));
        assertEquals(c.getTime(Piece.WHITE),      c.getTime(Piece.BLACK));
        assertEquals(c.getIncrement(Piece.WHITE), c.getIncrement(Piece.BLACK));
        assertEquals(fortyMoves,            c.getMovesToGo());
        assertFalse(c.isSuddenDeath());
    }
}
