package com.jeremybrooks.chess.search;

import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IterativeDeepeningSearch extends Search {
    private static final Logger log = LogManager.getLogger(IterativeDeepeningSearch.class);
    
    public IterativeDeepeningSearch(int maxDepth)
    {
        super(maxDepth);
    }
    
    @Override
    public void search(SearchParams params)
    {
        int minimax = 0;
        timer.setParams(params);
        log.info("id-search whiteTime " + params.getTime(Piece.WHITE) + " blackTime " + params.getTime(Piece.BLACK) + " movesToGo " + params.getMovesToGo());

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
            if(timer.hasExpired(side, startTime)){
                log.debug("time's up; stopping search");
                break;
            }
        }
        elapsedTimeMillis = Util.milliTime() - startTime;
        log.info("best " + Util.displayMoveStr(bestRootMove.getMove(),false,false) + " score " + bestRootMove.getScore() 
                +" millis " + elapsedTimeMillis + " pv " + Util.toFan(bestRootMove.getPvMoves()));
        info = new SearchInfo(bestRootMove, getNodeCount(), elapsedTimeMillis);
    }
    
}
