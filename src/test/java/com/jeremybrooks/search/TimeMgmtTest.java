package com.jeremybrooks.search;

import static org.junit.Assert.*;

import com.jeremybrooks.chess.search.SearchParams;
import com.jeremybrooks.chess.search.TimeMgmt;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.util.Util;

public class TimeMgmtTest {

    //fixtures
    private SearchParams params;
    
    @Before
    public void setUp()
    {
        params = new SearchParams(5000, 5000);
        params.setIncrement(Piece.WHITE, 0);
        params.setIncrement(Piece.BLACK, 0);
    }
    
    @Test
    public void testTime() {
        params.setMovesToGo(0);
        Assert.assertTrue(params.isSuddenDeath());
        final int bufferTimeMillis = 10;
        TimeMgmt tm = new TimeMgmt();
        tm.setParams(params);
        int timePerMove = params.getTime(Piece.WHITE)/50;
        System.out.println("Time/move: " + timePerMove);
        for(int usedTime=0; usedTime<=110; usedTime++)
        {
            int startTime = Util.milliTime() - usedTime;
//            System.out.println(usedTime + " timeLeft? " + tm.hasTimeLeft(WHITE, startTime));
            if ((usedTime + bufferTimeMillis) < timePerMove) assertFalse( tm.hasExpired(Piece.WHITE, startTime));
            else                                             assertTrue(tm.hasExpired(Piece.WHITE, startTime));
        }
        
        
    }
}
