package com.jeremybrooks.chess.search;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.UciDriver;

public class IterativeDeepeningSearch extends Search {
    private static final Logger log = Logger.getLogger(IterativeDeepeningSearch.class);
    
    @Override
    public int search(int side)
    {
        int minimax = 0;
        for(int depth=1;
                depth<=getStackSize();
                depth++)
        {
            UciDriver.sendResponse("info depth %d", depth);
            minimax = super.search(side, depth);
            String pvLine = getPVMoveLine();
            if(log.isDebugEnabled())
                log.debug("Searching to depth " + depth + 
                        " yielded (" + minimax + ") " + pvLine);
        }
        return minimax;
    }
    
}
