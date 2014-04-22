/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author jeremy
 *
 */
public class SolveForMate {

	private static final PrintStream out = System.out;
	private static final PrintStream err = System.err;
	private static final Search search = new Search();
	
	
	// So given a position in which white mates in 2,
	// depth = getDepthToMate(2) = 2 + (2-1) = 3
	// 
	private static int getDepthToMate(int mateInN){
	  return (mateInN + (mateInN -1));
	}



	public static void main(String[] args) throws IOException{
	    GameState g = new GameState();
	    if (args.length < 1){
	    	err.println("Give me a file name (e.g. problems.fen) that " +
	    			    "has some problems to solve for mate\n");
	        System.exit(1);
	    }
	    int mateInN = 0;
	    try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) 
	    {
	    	while (br.ready()){
	    		String line = br.readLine();
	    		if (line.trim().charAt(0) == '#') continue;
	    		g = new GameState();
	    		g.set(line);
	    		
	    		//
	    		// Set the depth for the engine to search for mate 
	    		// Display the board to solve for mate
	    		//
	    		g.setSearchDepth(getDepthToMate(mateInN));
	    		if(g.sideToMove == Bitmap.WHITE){
	    			out.print("White ");
	    		} else {
	    			out.print("Black ");
	    		}
	    		out.println("to move and force mate in " + mateInN);
	    		g.display();
	    		
	    		//
	    		// Search for a line to mate
	    		//
	    		out.println("\nThinking . . .");
	    		long start;
	    		long stop;
	    		float elapsedMs;
	    		start = System.currentTimeMillis();
	    		// NOTE: getBestMove's goal is to find the best move
	    		//       however since it doesn't work for non-mate
	    		//      situations i'm using it to just solve for mate
	    		//      since it puts the mating line in g.bestLine if it
	    		//      finds mate.
	    		int move = search.getBestMove(g, g.sideToMove);
	    		stop = System.currentTimeMillis();
	    		elapsedMs = stop - start;
	    		
	    		
	    		//
	    		// Display results
	    		//
	    		out.println("nodes     : " + g.nodes);
	    		out.println("elapsed   : " + elapsedMs); 
	    		out.print("nodes/sec : ");
	    		if (elapsedMs > 0) {
	    			out.println(g.nodes / elapsedMs);
	    		} else {
	    			out.println("--");
	    		}
	    		
	    		
	    		//
	    		// Display the root moves and the best value seen so far in the tree
	    		//
	    		out.println("Root-Move  Best-Value");
	    		for(int i=0; i<g.numberOfLegalMoves[0]; i++){
	    			Util.displayMove(g.moves[i], false, false);
	    			out.println("       " + g.movesValue[i]);
	    		}
	    		out.println();
	    		
	    		// Display the line to mate (if any)
	    		if (g.numberOfLinesToMate > 0){
	    			out.println("Mate found");
	    			for(int i=0; i<g.searchDepth; i++){
	    				Util.displayMove(g.bestLine[i], false, false);
	    				out.print("  ");
	    			}
	    			out.println();
	    		} else {
	    			out.println("No mate found");      
	    		}
	    		out.println("*************************************");
	    	}
	    	
	    }
	}
}
