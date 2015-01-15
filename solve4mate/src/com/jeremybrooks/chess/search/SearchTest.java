package com.jeremybrooks.chess.search;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.eval.Evaluator;
import com.jeremybrooks.chess.movegen.DefaultGenerator;
import com.jeremybrooks.chess.util.Util;

public class SearchTest {

    protected static final int THREE_SECONDS_REMAINING = 3;
    protected static final int MAX_DEPTH_THREE = 3;
    protected Search search;
    protected SearchParams params;
    protected GameState gameState;
    
    @Before
    public void setUp() throws Exception {
        search = new Search(MAX_DEPTH_THREE);
        gameState = new GameState();
        //Should be able to run all these test searches in under three seconds
        params = new SearchParams(THREE_SECONDS_REMAINING);
        DefaultGenerator generator = new DefaultGenerator();
        Evaluator evaluator = new Evaluator();
        generator.setGameState(gameState);
        search.setEvaluator(evaluator);
        search.setMoveGenerator(generator);
        search.setTimer(new TimeMgmt());
    }

    @After
    public void tearDown() throws Exception {
    }
    
    private void setupPosition(String fen) {
        gameState.set(fen);
        search.setGameState(gameState);
    }

    @Test
    public void givenWhiteHasBeenMated() {
        setupPosition("6k1/5ppp/8/8/8/8/5PPP/3r2K1 w - - 0 1");
        search.search(params);
        SearchInfo info = search.getInfo();
        assertEquals(Search.MATED,          info.getScore());
        assertEquals("<none>",              Util.toFan(info.getBestLine()));
        assertEquals(true,                  info.isMateOrMated());
    }

    @Test
    public void givenBlackHasBeenMated() {
        setupPosition("4R1k1/5ppp/8/8/8/8/5PPP/3R2K1 b - - 0 1");
        search.search(params);
        SearchInfo info = search.getInfo(); 
        assertEquals(Search.MATED,          info.getScore());
        assertEquals("<none>",              Util.toFan(info.getBestLine()));
        assertEquals(true,                  info.isMateOrMated());
    }

    @Test
    public void givenWhiteCanMateInOne() {
        setupPosition("3q1rk1/5pbp/5Qp1/8/8/2B5/5PPP/6K1 w - - 0 1  #Chess, Lazlo Polgar #1");
        String expectedBestLine = "Qf6xg7";
        int expectedScore       = Search.MATES - 1;
        search.search(params);
        SearchInfo info = search.getInfo(); 
        assertEquals(expectedScore,         info.getScore());
        assertEquals(expectedBestLine,      Util.toFan(info.getBestLine()));
        assertEquals(true,                  info.isMateOrMated());
    }

    @Test
    public void givenBlackCanMateInOne() {
        setupPosition("r5k1/5ppp/8/8/8/8/5PPP/6K1 b - - 0 1");
        String expectedBestLine = "Ra8-a1";
        int expectedScore       = Search.MATES - 1;
        search.search(params);
        SearchInfo info = search.getInfo(); 
        assertEquals(expectedScore,         info.getScore());
        assertEquals(expectedBestLine,      Util.toFan(info.getBestLine()));
        assertEquals(true,                  info.isMateOrMated());
    }
    
    @Test
    public void givenWhiteCanMateInTwo()
    {
        setupPosition("4K1k1/6pp/2NN4/8/8/8/8/8 w - - 0 1 #Mammoth Book of Chess  Graham Burgess #4");
        String expectedBestLine = "Nc6-e7 Kg8-h8 Nd6-f7";
        int expectedScore       = Search.MATES - 3;
        search.search(params);
        SearchInfo info = search.getInfo(); 
        assertEquals(expectedScore,         info.getScore());
        assertEquals(expectedBestLine,      Util.toFan(info.getBestLine()));
        assertEquals(true,                  info.isMateOrMated());
    }

    @Test
    public void givenBlackCanMateInTwo()
    {
        setupPosition("4k1K1/6PP/2nn4/8/8/8/8/8 b - - 0 1 #Mammoth Book of Chess  Graham Burgess #4 colors reversed");
        String expectedBestLine = "Nc6-e7 Kg8-h8 Nd6-f7";
        int expectedScore       = Search.MATES - 3;
        search.search(params);
        SearchInfo info = search.getInfo(); 
        assertEquals(expectedScore,         info.getScore());
        assertEquals(expectedBestLine,      Util.toFan(info.getBestLine()));
        assertEquals(true,                  info.isMateOrMated());
    }

    @Test
    public void givenDrawByStalemateWhiteToMove() {
        setupPosition("8/8/8/8/8/6k1/5q2/7K w - - 0 1");
        String expectedBestLine = "<none>";
        int expectedScore       = 0;
        search.search(params);
        SearchInfo info = search.getInfo(); 
        assertEquals(expectedScore,         info.getScore());
        assertEquals(expectedBestLine,      Util.toFan(info.getBestLine()));
        assertEquals(false,                 info.isMateOrMated());
    }

    @Test
    public void givenDrawByStalemateBlackToMove() {
        setupPosition("7k/5Q2/6K1/8/8/8/8/8 b - - 0 1");
        String expectedBestLine = "<none>";
        int expectedScore       = 0;
        search.search(params);
        SearchInfo info = search.getInfo(); 
        assertEquals(expectedScore,         info.getScore());
        assertEquals(expectedBestLine,      Util.toFan(info.getBestLine()));
        assertEquals(false,                 info.isMateOrMated());
    }
//
//    @Test
//    public void givenDrawBy3FoldRepetitionWhiteToMove() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void givenDrawBy3FoldRepetitionBlackToMove() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void givenDrawByInsufficientMaterialWhiteToMove() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void givenDrawByInsufficientMaterialBlackToMove() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void givenDrawBy50MoveRuleWhiteToMove() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void givenDrawBy50MoveRuleBlackToMove() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void givenMateInOneIsMoreValuableThanMateInTwo() {
//        fail("Not yet implemented");
//    }

}
