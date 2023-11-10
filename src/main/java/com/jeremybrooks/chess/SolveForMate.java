/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess;

import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.search.SearchInfo;
import com.jeremybrooks.chess.search.SearchParams;
import com.jeremybrooks.chess.util.AbstractDisplayer;
import com.jeremybrooks.chess.util.Displayer;
import com.jeremybrooks.chess.util.FenParser;
import com.jeremybrooks.chess.util.Util;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jeremy
 *
 */
public class SolveForMate {
    private static final Logger log = LogManager.getLogger();
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
                Puzzle puzzle = Puzzle.parse(line);
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
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.INFO);
        log.info("starting solve4mate");
        if (args.length < 1){
            err.println("Give me a file name (e.g. problems.fen) that " +
                        "has some problems to solve for mate\n");
            System.exit(1);
        }
        String filename = args[0];
        List<Puzzle> puzzles = readPuzzleFile(filename);
        Solver solver = new Solver();
        int threeSeconds = 3 * 1000;
        solver.setSearchParams(SearchParams.forOnePosition(threeSeconds));
        
        for(Puzzle puzzle: puzzles){
            displayHeader(puzzle);
            SearchInfo solveInfo = solver.solve(puzzle);
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
        header.append(" (" + puzzle.getNotes() + ")");
        header.append(EOL);
        AbstractDisplayer displayer = new Displayer(position);
        header.append(displayer.formatBoard());
        out.println(header.toString());
    }
    
    private static void displayResults(SearchInfo solveInfo) {
        StringBuilder result = new StringBuilder();
        long totalNodes = solveInfo.getNodeCount();
        double solveTimeMillis = solveInfo.getElapsedTime();
        result.append(EOL);
        result.append("Score     : ").append(solveInfo.getScore() + EOL);
        result.append("Best line : ").append(Util.toFan(solveInfo.getBestLine()) + EOL);
        result.append("Is mate?  : ").append(solveInfo.isMateOrMated() + EOL);
        result.append("Nodes     : ").append(totalNodes + EOL);
        result.append("Time(ms)  : ").append(solveTimeMillis + EOL); 
        result.append("Nodes/sec : ").append(((solveTimeMillis > 0) ? totalNodes/solveTimeMillis : "--") + EOL);
        result.append("Root Moves: ").append("todo:");
        result.append(EOL);
        
//        // Display the root moves and the best value seen so far in the tree
//        result.append("Root-Move  Best-Value"+EOL);
//        for(int i=0; i<g.numberOfLegalMoves[0]; i++){
//            result.append(Util.displayMoveStr(g.moves[i], false, false));
//            result.append("       " + g.movesValue[i] + EOL);
//        }
//        result.append(EOL);
        result.append("*****************************************************************");
        out.println(result.toString());
    }
}
