package com.jeremybrooks.chess;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.eval.Evaluator;
import com.jeremybrooks.chess.movegen.DefaultGenerator;
import com.jeremybrooks.chess.search.IterativeDeepeningSearch;
import com.jeremybrooks.chess.search.Search;
import com.jeremybrooks.chess.search.SearchInfo;
import com.jeremybrooks.chess.search.SearchParams;
import com.jeremybrooks.chess.search.TimeMgmt;

public class Solver {
    private static final Logger log = Logger.getLogger(Solver.class);
    private DefaultGenerator moveGenerator;
    private Evaluator evaluator;
    private Search search;
    private SearchParams searchParams;

    
    public Solver() {
        DefaultGenerator mg = new DefaultGenerator();
        Evaluator eval = new Evaluator();
        setMoveGenerator(mg);
        setEvaluator(eval);
        search = new IterativeDeepeningSearch(8);
        search.setEvaluator(evaluator);
        search.setMoveGenerator(moveGenerator);
        search.setTimer(new TimeMgmt());
    }

    public SearchInfo search(GameState g, int maxDepth)
    {
        assert(maxDepth <= GameState.MAX_NUM_MOVES_MADE);
        
        search.setGameState(g);
        log.debug("Searching...");
        search.search(searchParams);
        SearchInfo info = search.getInfo();
        return info;
    }

    public SearchInfo solve(Puzzle puzzle)
    {
        int movesToMate = puzzle.getMovesToMate();
        GameState g = new GameState();
        search.setGameState(g);
        search.setParams(searchParams);
        g.set(puzzle.getFen());

        log.debug("Searching for mate in "+movesToMate+"...");
        search.search(searchParams);
        SearchInfo solveInfo = search.getInfo();
        return solveInfo;
    }

    public void setMoveGenerator(DefaultGenerator moveGenerator) {
        this.moveGenerator = moveGenerator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public void setSearchParams(SearchParams params) {
        this.searchParams = params;
    }
    

}