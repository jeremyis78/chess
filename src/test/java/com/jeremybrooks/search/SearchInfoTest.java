package com.jeremybrooks.search;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jeremybrooks.chess.search.RootMove;
import com.jeremybrooks.chess.search.Search;
import com.jeremybrooks.chess.search.SearchInfo;
import org.junit.Test;

import com.jeremybrooks.chess.util.Util;

public class SearchInfoTest {

    @Test
    public void givenNewInstance()
    {   
        int noMove          = 0;
        int score           = 123;
        int nodeCount       = 60000;
        int elapsedTime     = 2000;
        final List<Integer> noMoveList = new ArrayList<>(1);
        noMoveList.add(noMove);
        
        RootMove rm = new RootMove(noMove, score);
        SearchInfo i = new SearchInfo(rm, nodeCount, elapsedTime);
        assertEquals(123,           i.getScore());
        assertEquals(elapsedTime,   i.getElapsedTime());
        assertEquals(nodeCount,     i.getNodeCount());
        assertEquals(0,             i.getPliesInBestLine());
        assertEquals(1,             i.getBestLine().size());
        assertEquals(noMoveList,    i.getBestLine());
        assertEquals("<none>",      Util.toFan(i.getBestLine()));
    }
    
    @Test
    public void testGetNodesPerSecond() {
        int nodeCount = 10000;
        int roughHalfSecondInMillis = 501;       // roughly half second
        double expectedNodesPerSecond = 19960.1; // 10,000 / 0.501 = 19960.079840319362
        double withinSecondsAccurate = 0.1;
        
        SearchInfo i = new SearchInfo();
        i.setNodeCount(nodeCount);
        i.setElapsedTime(roughHalfSecondInMillis);
        double nps = i.getNodesPerSecond();
        assertEquals(expectedNodesPerSecond, nps, withinSecondsAccurate );
    }
    
    @Test 
    public void givenSearchConstants()
    {
        assertTrue("max/infinite score must fall outside of mate range",  Search.MAXWINDOW >  Search.MAX_MATE);
        assertTrue(Search.MAX_MATE > Search.MIN_MATE);
        assertTrue(Search.MIN_MATE > Search.DRAW);
        assertTrue(Search.MATES >  Search.MIN_MATE);
        assertTrue(Search.MATED < -Search.MIN_MATE);
    }
    
    @Test
    public void givenLowerBound()
    {
        RootMove rm = new RootMove(0, Search.LOWER_BOUND);
        SearchInfo info = new SearchInfo(rm, 0, 0);
        assertEquals(Search.LOWER_BOUND,    info.getScore());
        assertEquals(true,                  info.isLowerBound());
        assertEquals(false,                 info.isUpperBound());
        assertEquals(false,                 info.isMateOrMated());
    }

    @Test
    public void givenUpperBound()
    {
        RootMove rm = new RootMove(0, Search.UPPER_BOUND);
        SearchInfo info = new SearchInfo(rm, 0, 0);
        assertEquals(Search.UPPER_BOUND,    info.getScore());
        assertEquals(false,                  info.isLowerBound());
        assertEquals(true,                 info.isUpperBound());
        assertEquals(false,                 info.isMateOrMated());
    }

    @Test
    public void givenSideToMoveGivesMateScore()
    {
        RootMove rm = new RootMove(0, Search.MATES);
        SearchInfo info = new SearchInfo(rm, 0, 0);
        assertEquals(Search.MATES,          info.getScore());
        assertEquals(false,                 info.isLowerBound());
        assertEquals(false,                 info.isUpperBound());
        assertEquals(true,                  info.isMateOrMated());
    }

    @Test
    public void givenSideToMoveIsMatedScore()
    {
        RootMove rm = new RootMove(0, Search.MATED);
        SearchInfo info = new SearchInfo(rm, 0, 0);
        assertEquals(Search.MATED,          info.getScore());
        assertEquals(false,                 info.isLowerBound());
        assertEquals(false,                 info.isUpperBound());
        assertEquals(true,                  info.isMateOrMated());
    }

    @Test
    public void givenSideToMoveGivesMateWithMinimumScore()
    {
        int pliesToMate = (Search.MAX_MATE - Search.MIN_MATE) - 1;
        RootMove rm = new RootMove(0, Search.MATES - pliesToMate);
        SearchInfo info = new SearchInfo(rm, 0, 0);
        assertEquals(Search.MIN_MATE+1,  info.getScore());
        assertEquals(false,              info.isLowerBound());
        assertEquals(false,              info.isUpperBound());
        assertEquals(true,               info.isMateOrMated());
    }

    @Test
    public void givenSideToMoveIsMatedWithMinimumScore()
    {
        int pliesToMate = (Search.MAX_MATE - Search.MIN_MATE);
        RootMove rm = new RootMove(0, Search.MATES - pliesToMate);
        SearchInfo info = new SearchInfo(rm, 0, 0);
        assertEquals(Search.MIN_MATE,    info.getScore());
        assertEquals(false,              info.isLowerBound());
        assertEquals(false,              info.isUpperBound());
        assertEquals(true,               info.isMateOrMated());
    }
    
}
