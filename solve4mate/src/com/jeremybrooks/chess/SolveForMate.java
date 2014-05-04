/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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
	private static final String EOL = System.getProperty("line.separator");
	
	private static List<Puzzle> readPuzzleFile(String filename)
	{
		List<Puzzle> puzzles = new ArrayList<>();
	    try (BufferedReader br = new BufferedReader(new FileReader(filename))) 
	    {
	    	while (br.ready()){
	    		String line = br.readLine();
	    		if (line.trim().charAt(0) == '#') continue;
	    		int firstSpaceIndex = line.indexOf(" ");
				int mateInN = Integer.parseInt(line.substring(0, firstSpaceIndex));
	    		String fen = line.substring(firstSpaceIndex+1);
	    		Puzzle puzzle = new Puzzle(fen, mateInN);
	    		puzzles.add(puzzle);
	    	}
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return puzzles;
	}

	public static void main(String[] args) throws IOException{
		BasicConfigurator.configure();
		log.info("starting solve4mate");
	    if (args.length < 1){
	    	err.println("Give me a file name (e.g. problems.fen) that " +
	    			    "has some problems to solve for mate\n");
	        System.exit(1);
	    }
	    String filename = args[0];
	    List<Puzzle> puzzles = readPuzzleFile(filename);
	    Solver solver = new Solver();
	    
	    for(Puzzle puzzle: puzzles){
	    	displayHeader(puzzle);
	    	Solver.Info solveInfo = solver.solve(puzzle);
	    	displayResults(solveInfo);
	    }
	}

	private static void displayHeader(Puzzle puzzle) {
		FenParser parser = new FenParser();
		parser.init(puzzle.getFen());
		parser.parse();
		Position position = parser.getPosition();
		boolean isWhiteToMove = parser.isWhiteToMove();
		
		StringBuilder header = new StringBuilder();
		header.append(EOL);
		header.append((isWhiteToMove?"White":"Black") + " to move and force mate in " + puzzle.getMovesToMate());
		header.append(EOL);
	    AbstractDisplayer displayer = new Displayer();
	    header.append(displayer.formatBoard(position));
		out.println(header.toString());
	}
	
	private static void displayResults(Solver.Info solveInfo) {
		StringBuilder result = new StringBuilder();
		if (solveInfo.isMate()){
			result.append("Mate found: " + solveInfo.getSolutionMoves());
		} else {
			result.append("No mate found (best line: empty for now");
//			for(int i=0; i<search.getMaxSearchDepth(); i++){
//				result.append(Util.displayMoveStr(g.bestLine[i], false, false)+ " ");
//			}
//			result.append(")");
		}
		int totalNodes = solveInfo.getTotalNodes();
		double solveTimeMillis = solveInfo.getSolveTimeMillis();
		result.append(EOL);
		result.append("Is mate?  : ").append(solveInfo.isMate() + EOL);
		result.append("Best line : ").append(solveInfo.getSolutionMoves() + EOL);
		result.append("Nodes     : ").append(totalNodes + EOL);
		result.append("Time(ms)  : ").append(solveTimeMillis + EOL); 
		result.append("Nodes/sec : ").append(((solveTimeMillis > 0) ? totalNodes/solveTimeMillis : "--") + EOL);
		result.append(EOL);
		
//		// Display the root moves and the best value seen so far in the tree
//		result.append("Root-Move  Best-Value"+EOL);
//		for(int i=0; i<g.numberOfLegalMoves[0]; i++){
//			result.append(Util.displayMoveStr(g.moves[i], false, false));
//			result.append("       " + g.movesValue[i] + EOL);
//		}
//		result.append(EOL);
		result.append("*************************************");
		out.println(result.toString());
	}
}
