package com.jeremybrooks.chess.search;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.util.Util;

public class IterativeDeepeningSearch extends Search {
    private static final Logger log = Logger.getLogger(IterativeDeepeningSearch.class);
    
    public IterativeDeepeningSearch(int maxDepth)
    {
        super(maxDepth);
    }
    
    @Override
    public void search(SearchParams params)
    {
        int minimax = 0;
        timer.setParams(params);
        log.debug("whiteTime " + params.getTime(Bitmap.WHITE));
        log.debug("blackTime " + params.getTime(Bitmap.BLACK));
        log.debug("movesToGo " + params.getMovesToGo());

        RootMove bestRootMove = new RootMove(0, Search.LOWER_BOUND);
        boolean mate = false;
        int elapsedTimeMillis = 0;
        startTime = Util.milliTime();
        for(int maxDepth=1;  //TODO: why are we starting at 1?....in super.search(params) we start at 0!!
                maxDepth<=maxDepthLimit;
                maxDepth++)
        {
            //UciDriver.sendResponse("info depth %d time %d", depth, (Util.milliTime() - startTime));
            int side = g.isWhiteToMove()?0:1;
            minimax = super.search(side, maxDepth);
            int iterationTimeMillis = Util.milliTime() - startTime;
            sortRootMoves();
            bestRootMove = getBestRootMove();
            if(log.isDebugEnabled()) {
                int nodesPerSecond = (int)(nodeCount / (iterationTimeMillis/1000.0));
                log.debug(maxDepth + "/" + effectiveDepth + " ply in " + iterationTimeMillis + "ms and " + nodeCount + " nodes " + 
                        "(" + nodesPerSecond + " nps) yielded (" + minimax + ") " + Util.toFan(bestRootMove.getPvMoves()));
            }
            mate = Math.abs(minimax) >= Search.MIN_MATE;
            if(mate)
            {
                log.debug("mate found! " + minimax );
                break;
            }
            if(!timer.hasTimeLeft(side, startTime)){
                log.debug("time's up; stopping search");
                break;
            }
        }
        log.info("best " + Util.displayMoveStr(bestRootMove.getMove(),false,false) + " " + bestRootMove.getScore() 
              +" pv " + Util.toFan(bestRootMove.getPvMoves()));
        
        elapsedTimeMillis = Util.milliTime() - startTime;
        info = new SearchInfo(bestRootMove, getNodeCount(), elapsedTimeMillis);
    }
    
}
