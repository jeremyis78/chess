package com.jeremybrooks.chess;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<CacheKey,Long> nodeCountCache = new HashMap<>(1000000); //maps zobristHashes to nodeCount 
    
    private static final class CacheKey {
    	private final long key;
    	private final int depth;
    	
    	public CacheKey(long key, int depth)
    	{
    		this.key = key;
    		this.depth = depth;
    	}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + depth;
			result = prime * result + (int) (key ^ (key >>> 32));
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheKey other = (CacheKey) obj;
			if (depth != other.depth)
				return false;
			if (key != other.key)
				return false;
			return true;
		}
    }
    
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

    public SearchInfo search(GameState state, int maxDepth)
    {
        assert(maxDepth <= GameState.MAX_NUM_MOVES_MADE);
        
        search.setGameState(state);
        moveGenerator.setGameState(state);
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

    public long computePerft(GameState state, int depth)
    {
        moveGenerator.setGameState(state);
        return perft(state, depth);
    }

    public void printPerft(GameState state, int depth, PrintStream out)
    {
        moveGenerator.setGameState(state);
        long start = System.nanoTime();
        long nodeCount = perft(state, depth);
        long elapsedMillis = (System.nanoTime() - start) / NANOS_PER_MILLI;
        String format = "Perft(%d): %,d, Time: %4.3f s";
        Object[] args = new Object[]{depth, nodeCount, elapsedMillis / 1000.0};
        out.println(String.format(format, args));
    }

    public void printPerft2(GameState state, int depth, PrintStream out)
    {
        moveGenerator.setGameState(state);
        long start = System.nanoTime();
        //There must be a faster algorithm to count leaf nodes...
        long nodeCountAtDminusOne = perft(state, depth-1);
        long nodeCountAtD         = perft(state, depth);
        long elapsedMillis = (System.nanoTime() - start) / NANOS_PER_MILLI;
        String format = "Perft(%d): %,d, Time: %4.3f s";
        Object[] args = new Object[]{depth, nodeCountAtD - nodeCountAtDminusOne, elapsedMillis / 1000.0};
        out.println(String.format(format, args));
    }
    
    public void printPerftFens(GameState state, int depth, PrintStream out)
    {
    	if(out == null)
    		throw new NullPointerException("out is null");
    	moveGenerator.setGameState(state);
    	perftFens(state, depth, out);
    }

    public void doDivide(GameState state, int depth, PrintStream out) {
        moveGenerator.setGameState(state);
        SortedSet<String> moveSet = new TreeSet<String>();
        int sideToMove = state.isWhiteToMove()?0:1;
        int nodeCountTotal = 0;
        int moveCount = 0;
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
            state.makeMove(move);
            nodeCountForMove += perft(state, depth-1);
            nodeCountTotal += nodeCountForMove;
            System.out.println(Util.toUciMove(move) + " " + nodeCountForMove); // + " " + state.get());
            state.undoMove();
        }
        StringBuilder sb = new StringBuilder();
        for(String moveLine: moveSet)
            sb.append(moveLine).append("\n");
        out.print(sb.toString());
        out.println("Moves : " + moveCount);
        if(depth > 1) 
            out.println("Nodes : " + nodeCountTotal);
    }

    private void perftFens(GameState state, int depth, PrintStream out) {
        if(depth == 0)
            return;
        int sideToMove = state.isWhiteToMove()?0:1;
        List<Integer> moves = moveGenerator.generateMoves(sideToMove, false);
        for(int move: moves)
        {
            if(!moveGenerator.isLegalMove(state, move)) continue;
            state.makeMove(move);
            if(out != null) 
            	out.println(state.get());
            perftFens(state, depth-1, out);
            state.undoMove();
        }
    }

    private long perft(GameState state, int depth) {
        if(depth == 0)
            return 1;
        long hash = state.fullZobristKey();
		CacheKey key = new CacheKey(hash, depth);
		if(nodeCountCache.containsKey(key))
        {
			//System.out.println("cache hit for " + state.get());
        	return nodeCountCache.get(key);
        }
		//System.out.println("cache miss for " + state.get());
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
        nodeCountCache.put(key, nodeCount);
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