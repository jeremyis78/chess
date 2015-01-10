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

        boolean mate = false;
        int elapsedTimeMillis = 0;
        startTime = Util.milliTime();
        for(int depth=1;  //TODO: why are we starting at 1?....in super.search(params) we start at 0!!
                depth<=maxDepthLimit;
                depth++)
        {
            //UciDriver.sendResponse("info depth %d time %d", depth, (Util.milliTime() - startTime));
            int side = g.isWhiteToMove()?0:1;
            minimax = super.search(side, depth);
            String pvLine = getPVMoveLine();
            if(log.isDebugEnabled()) {
                int searchTimeMillis = Util.milliTime() - startTime;
                int nodesPerSecond = (int)(nodeCount / (searchTimeMillis/1000.0));
                log.debug(depth + "/" + effectiveDepth + " ply in " + searchTimeMillis + "ms and " + nodeCount + " nodes " + 
                        "(" + nodesPerSecond + " nps) yielded (" + minimax + ") " + pvLine);
            }
            mate = Math.abs(minimax) > Search.CHECKMATE / 2;
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
        sortRootMoves();
        RootMove bestRootMove = getBestRootMove();
        log.info("best " + Util.displayMoveStr(bestRootMove.getMove(),false,false) + " " + bestRootMove.getScore() 
              +" pv " + bestRootMove.toFormattedPvLine());
        
        elapsedTimeMillis = Util.milliTime() - startTime;
        String solutionMoves = getPVMoveLine();
        String scoredRootMoves = getScoredRootMoves();
        info = new SearchInfo(bestRootMove, mate, getNodeCount(), elapsedTimeMillis);
        info.setScore(minimax);
        info.setOldSolutionMoves(solutionMoves);
        info.setOldBestLine(getPVLine());
        info.setScoredRootMoves(scoredRootMoves);
    }
    
}
