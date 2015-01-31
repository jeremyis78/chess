package com.jeremybrooks.chess;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.eval.Evaluator;
import com.jeremybrooks.chess.movegen.DefaultGenerator;
import com.jeremybrooks.chess.search.IterativeDeepeningSearch;
import com.jeremybrooks.chess.search.Search;
import com.jeremybrooks.chess.search.SearchInfo;
import com.jeremybrooks.chess.search.SearchParams;
import com.jeremybrooks.chess.search.TimeMgmt;
import com.jeremybrooks.chess.util.Util;

public class Solver {
    private static final int MILLIS_PER_SEC = 1000;
    private static final Logger log = Logger.getLogger(Solver.class);
    private static final long NANOS_PER_MILLI = 1000000;
    private DefaultGenerator moveGenerator;
    private Evaluator evaluator;
    private Search search;
    private SearchParams searchParams;

    
    public Solver() {
        DefaultGenerator mg = new DefaultGenerator();
        Evaluator eval = new Evaluator();
        setMoveGenerator(mg);
        setEvaluator(eval);
        search = new IterativeDeepeningSearch(80);
        search.setEvaluator(evaluator);
        search.setMoveGenerator(moveGenerator);
        search.setTimer(new TimeMgmt());
    }

    public SearchInfo search(GameState g, int maxDepth)
    {
        assert(maxDepth <= GameState.MAX_NUM_MOVES_MADE);
        
        search.setGameState(g);
        moveGenerator.setGameState(g);
        log.debug("Searching...");
        search.search(searchParams);
        SearchInfo info = search.getInfo();
        return info;
    }

    public SearchInfo solve(Puzzle puzzle)
    {
        int movesToMate = puzzle.getMovesToMate();
        GameState g = new GameState();
        search.setGameState(g);
        search.setParams(searchParams);
        moveGenerator.setGameState(g);
        g.set(puzzle.getFen());

        log.debug("Searching for mate in "+movesToMate+"...");
        search.search(searchParams);
        SearchInfo solveInfo = search.getInfo();
        return solveInfo;
    }

    public void doPerft(GameState state, int depth, PrintStream out)
    {
        moveGenerator.setGameState(state);
        long start = System.nanoTime();
        long nodeCount = perft(state, depth);
        long elapsedMillis = (System.nanoTime() - start) / NANOS_PER_MILLI;
        String format = "Perft (%d): %d, Time: %4.3f s";
        Object[] args = new Object[]{depth, nodeCount, elapsedMillis / 1000.0};
        out.println(String.format(format, args));
//        out.println("time (ms)    : " + elapsedMillis);
//        out.println("nodes (all)  : " + nodeCount);
//        out.println("nodes/second : " + (nodeCount * MILLIS_PER_SEC / elapsedMillis));
    }

    public void doPerft2(GameState state, int depth, PrintStream out)
    {
        moveGenerator.setGameState(state);
        long start = System.nanoTime();
        //There must be a faster algorithm to count leaf nodes...
        long nodeCountAtD         = perft(state, depth);
        long nodeCountAtDminusOne = perft(state, depth-1);
        long elapsedMillis = (System.nanoTime() - start) / NANOS_PER_MILLI;
        String format = "Perft (%d): %d, Time: %4.3f s";
        Object[] args = new Object[]{depth, nodeCountAtD - nodeCountAtDminusOne, elapsedMillis / 1000.0};
        out.println(String.format(format, args));
//        out.println("time (ms)    : " + elapsedMillis);
//        out.println("nodes (all)  : " + nodeCount);
//        out.println("nodes/second : " + (nodeCount * MILLIS_PER_SEC / elapsedMillis));
    }

    public void doDivide(GameState state, int depth, PrintStream out) {
        Deque<Integer> deque = new ArrayDeque<>();
        moveGenerator.setGameState(state);
        SortedSet<String> moveSet = new TreeSet<String>();
        int sideToMove = state.isWhiteToMove()?0:1;
        int nodeCountTotal = 0;
        int moveCount = 0;
        long start = System.nanoTime();
        List<Integer> moves = moveGenerator.generateMoves(sideToMove, false);
        for(int move: moves)
        {
            if(!moveGenerator.isLegalMove(state, move))
            {
                System.err.println("illegal: " + Util.toUciMove(move) + " (as int) " +move);
                continue;
            }
            long nodeCountForMove = 0;
            moveCount++;
            try { //make
                System.out.println(String.format("make %s", Util.displayMoveStr(move, false, false)));
                state.makeMove(move);
                deque.addFirst(move);
            } catch (IllegalStateException e) {
                System.out.println("move stack: " + deque.toArray());
                System.out.println(e.getMessage());
                System.exit(1);
            }
            nodeCountForMove += perft(state, depth-1);
            nodeCountTotal += nodeCountForMove;
            System.out.println(Util.toUciMove(move) + " " + nodeCountForMove); // + " " + state.get());
            try { //undo
//                System.out.println(String.format("undo %s", Util.displayMoveStr(move, false, false)));
                state.undoMove();
                deque.removeFirst();
            } catch (IllegalStateException e) {
                System.out.println("move stack: " + deque.toArray());
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        long elapsedMillis = (System.nanoTime() - start) / NANOS_PER_MILLI;
        StringBuilder sb = new StringBuilder();
        for(String moveLine: moveSet)
            sb.append(moveLine).append("\n");
        out.print(sb.toString());
        out.println("Moves : " + moveCount);
        if(depth > 1) 
            out.println("Nodes : " + nodeCountTotal);
//        out.println("time (ms)    : " + elapsedMillis);
//        out.println("nodes/second : " + (nodeCountTotal * MILLIS_PER_SEC / elapsedMillis));
    }

    private long perft(GameState state, int depth) {
        if(depth == 0)
            return 1;
        int sideToMove = state.isWhiteToMove()?0:1;
        long nodeCount = 0;
        List<Integer> moves = moveGenerator.generateMoves(sideToMove, false);
        for(int move: moves)
        {
            if(!moveGenerator.isLegalMove(state, move)) continue;
            state.makeMove(move);
            nodeCount += perft(state, depth-1);
            state.undoMove();
        }
        return nodeCount;
    }

    public List<Integer> getLegalMoves(GameState g) {
        
        moveGenerator.setGameState(g);
        int side = g.isWhiteToMove()?Piece.WHITE:Piece.BLACK;
        boolean onlyDangerousMoves = false;
        List<Integer> moves = moveGenerator.generateMoves(side, onlyDangerousMoves);
        List<Integer> legalMoves = new ArrayList<Integer>(256);
        for(int move: moves)
        {
            if(moveGenerator.isLegalMove(g, move))
                legalMoves.add(move);
        }
        return legalMoves;
    }

    public void setMoveGenerator(DefaultGenerator moveGenerator) {
        this.moveGenerator = moveGenerator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public void setSearchParams(SearchParams params) {
        this.searchParams = params;
    }
    

}