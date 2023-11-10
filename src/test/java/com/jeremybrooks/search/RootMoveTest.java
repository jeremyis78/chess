package com.jeremybrooks.search;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jeremybrooks.chess.search.RootMove;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RootMoveTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void givenNewInstance() {
        int move = 123; //doesn't matter, just an int
        int plies = 0;
        RootMove rm = new RootMove(move, 1);
        rm.setPvMove(move, plies);

        assertEquals(move,  rm.getMove());
        assertEquals(1,     rm.getScore());
        assertEquals(move,  rm.getPvMove(plies));
        assertEquals(1,     rm.getPvLength());    
    }

    @Test
    public void givenSingleMoveAtEndOfPv() {
        int move = 123; //doesn't matter, just an int
        int noMove = 0;
        int plies = 3;
        RootMove rm = new RootMove(move, 1);
        rm.setPvMove(move, plies);

        assertEquals(noMove,    rm.getPvMove(plies-3));
        assertEquals(noMove,    rm.getPvMove(plies-2));
        assertEquals(noMove,    rm.getPvMove(plies-1));
        assertEquals(move,      rm.getPvMove(plies));
        assertEquals(plies+1,   rm.getPvLength());    
    }

    @Test
    public void givenAnOverwrittenPvMove() {
        int move = 123; //doesn't matter, just an int
        int move2 = 456;
        int move3 = 789;
        int noMove = 0;
        int plies = 3;
        RootMove rm = new RootMove(0, 1);
        rm.setPvMove(move, plies);
        rm.setPvMove(move2, plies);
        rm.setPvMove(move3, plies-1);

        assertEquals(0,         rm.getMove());
        assertEquals(noMove,    rm.getPvMove(plies-3));
        assertEquals(noMove,    rm.getPvMove(plies-2));
        assertEquals(move3,     rm.getPvMove(plies-1));
        assertEquals(move2,     rm.getPvMove(plies));
        assertEquals(plies+1,   rm.getPvLength());
    }

    @Test
    public void givenCompletePv() {
        int move1 = 123; //doesn't matter, just an int
        int move2 = 456;
        int move3 = 789;
        int score = 189;
        RootMove rm = new RootMove(move1, score);
        //in search we start populating the pv in reverse (from the end to the beginning)
        //so we do the same here for testing
        rm.setPvMove(move3, 2);
        rm.setPvMove(move2, 1);
        rm.setPvMove(move1, 0);

        assertEquals(move1,     rm.getMove());
        assertEquals(move1,     rm.getPvMove(0));
        assertEquals(move2,     rm.getPvMove(1));
        assertEquals(move3,     rm.getPvMove(2));
        assertEquals(3,         rm.getPvLength());
    }

    @Test
    public void givenTwoMovesToSort() {
        RootMove smallScoreMove = new RootMove(0, 22);
        RootMove bigScoreMove   = new RootMove(0, 99);
        List<RootMove> list = new ArrayList<>();
        list.add(smallScoreMove);
        list.add(bigScoreMove);
        assertSame("precondition smaller score is first", smallScoreMove, list.get(0));
        assertSame("precondition bigger score is last",   bigScoreMove,   list.get(1));
        Collections.sort(list);
        assertEquals(bigScoreMove,   list.get(0));
        assertEquals(smallScoreMove, list.get(1));
    }

//    @Test
//    public void testEqualsObject() {
//        fail("Not yet implemented");
//    }

}
