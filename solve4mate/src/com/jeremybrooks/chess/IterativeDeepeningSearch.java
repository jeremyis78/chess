package com.jeremybrooks.chess;

import org.apache.log4j.Logger;

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
			minimax = super.search(side, depth);
			String pvLine = getPVMoveLine();
			if(log.isDebugEnabled())
				log.debug("Searching to depth " + depth + 
						" yielded (" + minimax + ") " + pvLine);
		}
		return minimax;
	}
	
}
