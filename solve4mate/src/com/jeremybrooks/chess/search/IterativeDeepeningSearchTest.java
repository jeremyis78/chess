package com.jeremybrooks.chess.search;

import org.junit.Before;

import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.eval.Evaluator;
import com.jeremybrooks.chess.movegen.DefaultGenerator;

/**
 * Runs the same tests in SearchTest but searches using ID-Search instead.
 * 
 * @author jeremy
 *
 */
public class IterativeDeepeningSearchTest extends SearchTest {

    @Before
    public void setUp() throws Exception {
        search = new IterativeDeepeningSearch(MAX_DEPTH_THREE);
        gameState = new GameState();
        //Should be able to run all these test searches in under three seconds
        params = SearchParams.forOnePosition(3000); //THREE_SECONDS_REMAINING);
        DefaultGenerator generator = new DefaultGenerator();
        Evaluator evaluator = new Evaluator();
        generator.setGameState(gameState);
        search.setEvaluator(evaluator);
        search.setMoveGenerator(generator);
        search.setTimer(new TimeMgmt());

    }
    
}
