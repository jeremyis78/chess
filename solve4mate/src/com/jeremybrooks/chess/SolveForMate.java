/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * @author jeremy
 *
 */
public class SolveForMate {

	private static final Logger log = Logger.getLogger(SolveForMate.class);
	private static final PrintStream out = System.out;
	private static final PrintStream err = System.err;
	private static final Search search = new Search();
	private static final String EOL = System.getProperty("line.separator");
	
	
	// So given a position in which white mates in 2,
	// depth = getDepthToMate(2) = 2 + (2-1) = 3
	// 
	private static int getDepthToMate(int mateInN){
		/*
		 * mateIn	depth
		 * 1		1			1+1-1 = 1
		 * 2		3           2+2-1 = 3
		 * 3		5           3+3-1 = 5
		 * 4		7
		 * 5		9
		 */
		int depth=0;
		switch(mateInN)
		{
		case 1: 
			depth = 2;
			break;
		case 2: 
			depth = 4;
			break;
		case 3: 
			depth = 5;
			break;
		case 4: 
			depth = 7;
			break;
		case 5: 
			depth = 9;
			break;
		default: 
			depth = (2 * mateInN) - 1;
			break;
		}
		out.println("mate in "+mateInN+" requires depth of "+depth);
		return depth;
	}



	public static void main(String[] args) throws IOException{
		BasicConfigurator.configure();
		log.info("starting solve4mate");
	    if (args.length < 1){
	    	err.println("Give me a file name (e.g. problems.fen) that " +
	    			    "has some problems to solve for mate\n");
	        System.exit(1);
	    }
	    int mateInN = 4;
	    try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) 
	    {
	    	while (br.ready()){
	    		String line = br.readLine();
	    		if (line.trim().charAt(0) == '#') continue;
	    		int firstSpaceIndex = line.indexOf(" ");
				mateInN = Integer.parseInt(line.substring(0, firstSpaceIndex));
	    		String fen = line.substring(firstSpaceIndex +1);
	    		GameState g = new GameState(getDepthToMate(mateInN));
	    		g.set(fen);
	    		search.setMaxSearchDepth(getDepthToMate(mateInN)-1);
    			displayHeader(mateInN, g.isWhiteToMove(), g.getPosition());
	    		out.println("Searching for mate in "+mateInN+"...");
	    		long start;
	    		long stop;
	    		float elapsedMs;
	    		start = System.currentTimeMillis();
	    		// NOTE: getBestMove's goal is to find the best move
	    		//       however since it doesn't work for non-mate
	    		//      situations i'm using it to just solve for mate
	    		//      since it puts the mating line in g.bestLine if it
	    		//      finds mate.
	    		int move = search.getBestMove(g, g.isWhiteToMove()?0:1);
	    		stop = System.currentTimeMillis();
	    		elapsedMs = stop - start;
	    		displayResults(g, move, elapsedMs);
	    		search.reset();
	    	}
	    	
	    }
	}

	private static void displayHeader(int mateInN, boolean isWhiteToMove, Position position) {
		StringBuilder header = new StringBuilder();
		header.append(EOL);
		header.append((isWhiteToMove?"White":"Black") + " to move and force mate in " + mateInN);
		header.append(" (search depth "+search.getMaxSearchDepth()+")");
		header.append(EOL);
	    AbstractDisplayer displayer = new Displayer();
	    header.append(displayer.formatBoard(position));
		out.println(header.toString());
	}
	
	private static void displayResults(GameState g, int bestMove, float elapsedMs) {
		StringBuilder result = new StringBuilder();
		if (g.numberOfLinesToMate > 0){
			result.append("Mate found: ");
			for(int i=0; i<search.getMaxSearchDepth(); i++){
				result.append(Util.displayMoveStr(g.bestLine[i], false, false)+ " ");
			}
		} else {
			result.append("No mate found (best line: ");
			for(int i=0; i<search.getMaxSearchDepth(); i++){
				result.append(Util.displayMoveStr(g.bestLine[i], false, false)+ " ");
			}
			result.append(")");
		}
		result.append(EOL);
		result.append("PV: ").append(search.getPVMoveLine());
		result.append(EOL);
		result.append("nodes     : " + g.nodes + EOL);
//		result.append("elapsed   : " + elapsedMs + EOL); 
//		result.append("nodes/sec : " + ((elapsedMs > 0) ? g.nodes/elapsedMs : "--") + EOL);
		result.append("best move : " + Util.displayMoveStr(bestMove, false, false));
		result.append(EOL);
		
		// Display the root moves and the best value seen so far in the tree
		result.append("Root-Move  Best-Value"+EOL);
		for(int i=0; i<g.numberOfLegalMoves[0]; i++){
			result.append(Util.displayMoveStr(g.moves[i], false, false));
			result.append("       " + g.movesValue[i] + EOL);
		}
		result.append(EOL);
		result.append("*************************************");
		System.out.println(result.toString());
	}
}
