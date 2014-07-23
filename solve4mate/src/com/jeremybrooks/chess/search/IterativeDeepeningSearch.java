package com.jeremybrooks.chess.search;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.UciDriver;
import com.jeremybrooks.chess.eval.Evaluator;
import com.jeremybrooks.chess.util.Util;

public class IterativeDeepeningSearch extends Search {
    private static final Logger log = Logger.getLogger(IterativeDeepeningSearch.class);
    
    @Override
    public int search(int side)
    {
        int minimax = 0;
        timer.setParams(params);
        int elapsedTimeMillis = 0;
        startTime = Util.milliTime();
        for(int depth=1;
                depth<=getStackSize();
                depth++)
        {
            UciDriver.sendResponse("info depth %d time %d", depth, (Util.milliTime() - startTime));
            minimax = super.search(side, depth);
            String pvLine = getPVMoveLine();
            if(log.isDebugEnabled())
                log.debug(depth + "-ply in " + (Util.milliTime() - startTime) + "ms" + 
                        " yielded (" + minimax + ") " + pvLine);
            if(!timer.hasTimeLeft(side, startTime)){
                System.out.println("time's up; stopping search");
                break;
            }
            
        }
        elapsedTimeMillis = Util.milliTime() - startTime;
        boolean mate = Math.abs(minimax) > Evaluator.CHECKMATE / 2;
        String solutionMoves = getPVMoveLine();
        String scoredRootMoves = getScoredRootMoves();
        info = new SearchInfo();
        info.setScore(minimax);
        info.setSolutionMoves(solutionMoves);
        info.setBestLine(getPVLine());
        info.setMate(mate);
        info.setNodeCount(getNodeCount());
        info.setElapsedTime(elapsedTimeMillis);
        info.setScoredRootMoves(scoredRootMoves);
        return minimax;
    }
    
}
