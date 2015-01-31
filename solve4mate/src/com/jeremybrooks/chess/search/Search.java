/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.eval.Evaluator;
import com.jeremybrooks.chess.movegen.DefaultGenerator;
import com.jeremybrooks.chess.search.ScoredMove.Precision;
import com.jeremybrooks.chess.util.Util;

/**
 * @author jeremy
 *
 */
public class Search {

//    private static class StackEntry {
//        private int currentMove;
//        private int depth;
//    }
    
    private static final Logger log = Logger.getLogger(Search.class);
    private static transient final boolean isTrace = false; //log.isTraceEnabled();
    private static transient final boolean showPvUpdates = false; //logs principle variation changes
    static final int MAX_DEPTH_LIMIT  = 120;
    
    public static final boolean SEARCH_DEBUG = false; //SEARCH_DB 
    public static final boolean DEBUG = false; //DB
    public static final boolean EVAL = false;
    public static final int MAXWINDOW    = 99999;   //To pass to alpha beta for initial window (+inf, -inf)
    public static final int LOWER_BOUND  = -MAXWINDOW;
    public static final int UPPER_BOUND  = MAXWINDOW;
    public static final int MAX_MATE     = 50000;          //value for sideToMove mates opponent
    public static final int MIN_MATE     = MAX_MATE / 2;   //values greater indicate a mate score
    public static final int MATES        = MAX_MATE;       //value for sideToMove GIVES mates
    public static final int MATED        = -MAX_MATE;      //value for sideToMove IS mated
    public static final int DRAW         = 0;              //value for draw

    private DefaultGenerator moveGenerator = new DefaultGenerator();
    private Evaluator evaluator = new Evaluator();
    protected SearchParams params;
    protected TimeMgmt timer;
        
    /*
     * Current state of the chess game 
     */
    protected GameState g;
    
    /* 
     * Initialize search's stack depth to this value
     */
    protected int stackSize = 0;
    
    /*
     * Maximum allowable depth limit (e.g. for a call to search(side, depthLimit))
     */
    protected int maxDepthLimit;
    
    /*
     * Stop the search (ie, call eval()) when the depth reaches this limit 
     */
    private int depthLimit = 0;

    /*
     * The number of nodes expanded during the search (doesn't include leaf nodes currently) 
     */
    protected long nodeCount;
    
    /*
     * The line of play currently being searched, indexed by depth 
     * (currentMove[0]=firstMove, currentMove[1]=secondMove, etc) 
     */
    private int[] currentMove;

    protected List<RootMove> rootMoves;
    protected int currentRootMoveIndex;
    protected SearchInfo info;
    protected int startTime;
    protected int effectiveDepth;
    protected volatile boolean hasMoreTime = true;
    
    


    public Search() {
        super();
        initStack(MAX_DEPTH_LIMIT);
    }

    public Search(int maxDepth)
    {
        super();
        initStack(maxDepth);
    }

    private void initStack(int maxDepth) 
    {
        maxDepthLimit = maxDepth;
        stackSize = maxDepth + 30; //add buffer for quiescent search
        log.debug("set stack size to " + stackSize + " (maxDepth=" + maxDepth + ")");
        currentMove = new int[stackSize];
        rootMoves = new ArrayList<RootMove>();
    }
    
    public Search(TimeMgmt timer)
    {
        super();
        this.timer = timer;
    }
    
    public void search(SearchParams params)
    {
        int minimax = -MAXWINDOW; //we're looking for the largest score possible so we start at the lowest score possible
        timer.setParams(params);
        log.info("ab-search whiteTime " + params.getTime(Piece.WHITE) + " blackTime " + params.getTime(Piece.BLACK) + " movesToGo " + params.getMovesToGo());

        int elapsedTimeMillis = 0;
        int upToThisDepth = maxDepthLimit;
        int side = g.isWhiteToMove()?0:1;
        
        //UciDriver.sendResponse("info depth %d time %d", depth, (Util.milliTime() - startTime));
        startTime = Util.milliTime();
        minimax = search(side, upToThisDepth);
        elapsedTimeMillis = Util.milliTime() - startTime;
        sortRootMoves();
        RootMove bestRootMove = getBestRootMove();
        if(log.isDebugEnabled()) {
            int nodesPerSecond = (int)(nodeCount / (elapsedTimeMillis/1000.0));
            log.debug(upToThisDepth + "/" + effectiveDepth + " ply in " + elapsedTimeMillis + "ms and " + nodeCount + " nodes " + 
                    "(" + nodesPerSecond + " nps) yielded " + minimax);
        }
        log.info("best " + Util.displayMoveStr(bestRootMove.getMove(),false,false) + " score " + bestRootMove.getScore() 
              +" millis " + elapsedTimeMillis + " pv " + Util.toFan(bestRootMove.getPvMoves()));
        info = new SearchInfo(bestRootMove, getNodeCount(), elapsedTimeMillis);
    }

    public void sortRootMoves() {
        Collections.sort(rootMoves);
//        for(RootMove rm: rootMoves)
//        {
//            System.out.println(Util.displayMoveStr(rm.getMove(), false, false) + " score: " + rm.getScore() + ", pv " + rm.toFormattedPvLine());
//        }
    }
    
    /**
     * Search for the minimax value of the given GameState.
     * @param side  side to move
     * 
     * @return the minimax value of the search
     */
    public int search(int side){
        return search(side, maxDepthLimit);
    }

    /**
     * Search, up to the depth limit, for the minimax value of the given GameState
     * @param side  side to move
     * @param depthLimit  stop the search when this depth is reached
     *
     * @return the minimax value of the search
     */
    public int search(int side, int depthLimit)
    {
        if(depthLimit > maxDepthLimit)
            throw new IllegalArgumentException("depthLimit " + depthLimit 
                    + " cannot exceed " + maxDepthLimit);
        this.depthLimit = depthLimit;
        int minimax = alphabetaMaxWindow(side);
        return minimax;
    }

    /**
     * Performs a full-width alpha-beta search.
     * 
     * A full window search means:
     * alpha = -Infinity
     * beta = +Infinity
     * @param side  side to move
     * 
     * @return the minimax value of the search
     */
    protected int alphabetaMaxWindow(int side) {
        return alphabeta(side, -MAXWINDOW, +MAXWINDOW);
    }

    /**
     * <p>Performs an alpha-beta search, starting as the max player.
     * 
     * <p>Where the goal is to find the exact value of the search between the
     * lower bound (alpha) and the upper bound (beta). Therefore, calling alpha-beta has
     * three possible outcomes: 
     * The returned value will represent
     *    <ul>
     *    <li>SUCCESS: when the value is between alpha and beta
     *    
     *    <li>FAIL LOW: when the value is less than alpha (alpha is returned)
     *    
     *    <li>FAIL HIGH: when the value is greater than beta (beta is returned)
     *    </ul>
     * 
     * <p>The side having the move is represented as the MAX player.
     * 
     * @param side  side to move
     * @param alpha  represents the lower bound of the target window
     * @param beta  represents the upper bound of the target window
     * 
     * @return the minimax value of the search from the MAX player perspective
     */
    protected int alphabeta(int side, int alpha, int beta){
        int minimaxValue;
        int depth = 0;
        boolean quiescentSearch = false;
        
        //We always start as the max player, despite what side to move
        minimaxValue = max(alpha, beta, side, depth, quiescentSearch); 
        if(rootMoves.size() == 0)
        {
            //noMove placeholder with value of search
            rootMoves.add(new RootMove(0, minimaxValue)); 
        }
        return minimaxValue;
    }


    // max  - returns max value from state 'g' 
    //         
    //
    // alpha - value of best alternative for MAX along path to state 'g'
    // beta  - value of best alternative for MIN along path to state 'g'
    //
    //    int alphaBetaMax( int alpha, int beta, int depthleft ) {
    //           if ( depthleft == 0 ) return evaluate();
    //           for ( all moves) {
    //              score = alphaBetaMin( alpha, beta, depthleft - 1 );
    //              if( score >= beta )
    //                 return beta;   // fail hard beta-cutoff
    //              if( score > alpha )
    //                 alpha = score; // alpha acts like max in MiniMax
    //           }
    //           return alpha;
    //        }
    protected int max(int alpha, int beta, int side, int depth, boolean quiescentSearch){
        long nodeKey = g.fullZobristKey();
        Precision nodePrecision = Precision.LOWER_BOUND;
        ScoredMove node = lookup(nodeKey);
        if(isCacheHit(node, alpha, beta, depth))
        {
            int score = cachedScore(node, alpha, beta, depth);
            if(log.isDebugEnabled()) log.debug("cache hit (score="+score+") on "+g.get());
            return score;
        }

        if(isMaxDepth(depth) && !quiescentSearch){
            return quiescentSearch(alpha, beta, side, depth);
        }
        boolean dangerousMovesOnly = quiescentSearch ? true : false;
        List<Integer> moves = generateLegalMoves(side, dangerousMovesOnly);
        int numMoves = moves.size();
//        if(isTrace)
//            log.debug("max player: num moves at depth " + depth + ": "+numMoves);
        if(quiescentSearch && 0 == numMoves)
        {
            effectiveDepth = depth;
            if(!g.inCheck()) return evaluate(side, depth); //recursion base case, a quiet position (no captures, checks or promotions)
        }
        
        for(int i=0; i<numMoves && hasMoreTime; i++){
            int move = moves.get(i);
            if(!moveGenerator.isLegalMove(g, move)) continue;
            nodeCount++;
            if(depth == 0)
            {
                rootMoves.add(new RootMove(move, -MAXWINDOW));
                currentRootMoveIndex = i;
                String moveStr = Util.displayMoveStr(move, false, false);
                if(isTrace) 
                    log.debug("checking " + moveStr + " ("+(i+1)+" of "+(numMoves)+")");
            }
            currentMove[depth] = move;
            g.makeMove(move);
            int val = min(alpha,beta,Util.opposing(side),depth+1,quiescentSearch); //recurse!
            g.undoMove();
            if(isTrace)
                logMove(depth, move, val, alpha, beta);
            if (val >= beta){
                if(isTrace){
                    log.trace(indent(depth) + "@" + depth + " " + val + ">=" + beta + 
                            " prunes sub-graph (fail hard beta-cutoff) at move " + Util.displayMoveStr(move, false, false));
                }
                store(nodeKey, beta, Precision.UPPER_BOUND, depth);
                return beta;
            }
            if(val > alpha)
            {    
                alpha = val; // alpha acts like max in MiniMax  (shrinks the window up from -Infinity)
                nodePrecision = Precision.EXACT;
                updatePrincipalVariationLine(g, depth, val, move);
            }
            if(timer.hasExpired(side, startTime))
            {
                hasMoreTime = false;
                log.info("time's up at depth " + depth);
            }
        }
        alpha = (0 == numMoves 
                ? scoreForNoMoves(depth, side) 
                : alpha);
        store(nodeKey, alpha, nodePrecision, depth);
        return alpha;
    }

    private int quiescentSearch(int alpha, int beta, int side, int depth) {
        boolean quiescentSearch = true;
        int minimaxValue;
        if(side == Piece.WHITE){
          minimaxValue = max(alpha, beta, side, depth, quiescentSearch);
        } else {
          minimaxValue = min(alpha, beta, side, depth, quiescentSearch);
        }
        return minimaxValue;
    }

    private int scoreForNoMoves(int depth, int side) 
    {
        //With no moves, we have either mate or stalemate
        if(g.inCheck())
            return scoreForMateAt(depth); 
        else 
            return DRAW;
    }

    public int scoreForMateAt(int depth) {
        //Mate-at-Depth-One (mate-in-one) > Mate-at-Depth-Three (mate-in-two) ... etc 
        boolean isEvenDepth = (0 == depth % 2);
        return (MAX_MATE - depth) * (isEvenDepth?-1:+1);  //positive at odd depths, negative at even depths (even 0 depth)
    }

    protected void updatePrincipalVariationLine(GameState g, int depth, int score, int move) {
        RootMove rm = rootMoves.get(currentRootMoveIndex);
        rm.setScore(score);
        rm.setPvMove(currentMove[depth], depth);
        if(showPvUpdates) 
        {
            StringBuilder pv = new StringBuilder(score+": ");
            for(int i=0; i<=depth; i++) 
                pv.append(Util.displayMoveStr(currentMove[i], false, false)).append(" ");
            log.info("new pv: " + pv.toString());
        }
    }

    // min   - returns minimum value from state 'g'
    //
    // alpha - value of best alternative for MAX along path to state 'g'
    // beta  - value of best alternative for MIN along path to state 'g'
    //
    //    int alphaBetaMin( int alpha, int beta, int depthleft ) {
    //           if ( depthleft == 0 ) return -evaluate();
    //           for ( all moves) {
    //              score = alphaBetaMax( alpha, beta, depthleft - 1 );
    //              if( score <= alpha )
    //                 return alpha; // fail hard alpha-cutoff
    //              if( score < beta )
    //                 beta = score; // beta acts like min in MiniMax
    //           }
    //           return beta;
    //        }
    protected int min(int alpha, int beta, int side, int depth, boolean quiescentSearch){
        long nodeKey = g.fullZobristKey();
        Precision nodePrecision = Precision.UPPER_BOUND;
        ScoredMove node = lookup(nodeKey);
        if(isCacheHit(node, alpha, beta, depth))
        {
            int score = cachedScore(node, alpha, beta, depth);
            if(log.isDebugEnabled()) log.debug("cache hit (score="+score+") on "+g.get());
            return score;
        }

        if(isMaxDepth(depth) && !quiescentSearch){
            return quiescentSearch(alpha, beta, side, depth);
        }
        boolean dangerousMovesOnly = quiescentSearch ? true : false;
        List<Integer> moves = generateLegalMoves(side, dangerousMovesOnly);
        int numMoves = moves.size();
        if(quiescentSearch && 0 == numMoves)
        {
            effectiveDepth = depth;
            if(!g.inCheck()) return evaluate(side, depth); //recursion base case, a quiet position (no captures, checks or promotions)
        }
        
        for(int i=0; i<numMoves && hasMoreTime; i++){
            int move = moves.get(i);
            if(!moveGenerator.isLegalMove(g, move)) continue;
            nodeCount++;
            if(depth == 0)
            {
                rootMoves.add(new RootMove(move, -MAXWINDOW));
                currentRootMoveIndex = i;
                String moveStr = Util.displayMoveStr(move, false, false);
                if(isTrace) log.trace("checking " + moveStr + " ("+(i+1)+" of "+(numMoves)+")");
            }
            currentMove[depth] = move;
            g.makeMove(move);
            int val = max(alpha,beta,Util.opposing(side),depth+1,quiescentSearch);  //recurse!
            g.undoMove();
            if(isTrace) 
                logMove(depth, move, val, alpha, beta);
            if (val <= alpha){   //Found a cutoff
                if(isTrace){
                    log.trace(indent(depth) + "@" + depth + " " + val + "<=" + alpha + 
                        " prunes sub-graph (fail hard alpha-cutoff) at move " + Util.displayMoveStr(move, false, false));
                }
                store(nodeKey, alpha, Precision.LOWER_BOUND, depth);
                return alpha;
            }
            if(val < beta)
            {   
                beta = val; // beta acts like min in MiniMax (shrinks the window down from +Infinity)
                nodePrecision = Precision.EXACT;
                updatePrincipalVariationLine(g, depth, val, move);
            }
            if(timer.hasExpired(side, startTime))
            {
                hasMoreTime = false;
                log.info("time's up at depth " + depth);
            }
        }
        beta = (0 == numMoves 
               ? scoreForNoMoves(depth, side) 
               : beta);
        store(nodeKey, beta, nodePrecision, depth);
        return beta;
    }

    protected boolean isMaxDepth(int depth) {
        boolean isMaxDepth = (depth == depthLimit);
        //if(isMaxDepth) log.debug("max depth reached: " + depth);
        return isMaxDepth;
    }

    protected List<Integer> generateLegalMoves(int side, boolean dangerousMovesOnly) {
        return moveGenerator.generateMoves(side, dangerousMovesOnly);
    }

    //
    // Returns an evaluation score for the side to move 'side'
    // A more positive number is better for white.
    // A more negative number is better for black.
    int evaluate(int side, int depth){
        return evaluator.evaluate(g, side, depth, currentMove, SEARCH_DEBUG, EVAL);
    }
    
    protected ScoredMove lookup(long key) {
        //no cache by default;
        return null; 
    }

    protected void store(Long key, int score, ScoredMove.Precision metadata, int depth) {
        //no cache by default
    }

    protected boolean isCacheHit(ScoredMove node, int alpha, int beta, int depth) {
        //no cache by default;
        return false;
    }

    protected Integer cachedScore(ScoredMove node, int alpha, int beta, int depth)
    {
        //no cache by default;
        return null;
    }

    public RootMove getBestRootMove() 
    {
        if(rootMoves.size() == 0)
        {
            return new RootMove(0, -MAXWINDOW); //noMove placeholder
        }
        return rootMoves.get(0);
    }

    /**
     * @return the results of the search
     */
    public SearchInfo getInfo() {
        return info;
    }
    
    public long getNodeCount() {
        return nodeCount;
    }

    public GameState getGameState() {
        return g;
    }

    public void setGameState(GameState gameState) {
        this.g = gameState;
    }

    public void setMoveGenerator(DefaultGenerator defaultGenerator) {
        this.moveGenerator = defaultGenerator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }
    
    public void setParams(SearchParams params) {
        this.params = params;
    }

    public void setTimer(TimeMgmt timer) {
        this.timer = timer;
    }


    protected void logMove(int depth, int move, int moveScore, int alpha, int beta) {
            String line = "";
            for(int j=0; j<depth; j++){
                line += Util.displayMoveStr(currentMove[j], false, false)+ " ";
            }

                log.trace(indent(depth) + "@" + depth + " " + line + " " + 
                        Util.displayMoveStr(move, false, false) + 
                        String.format("  score=%d  alpha=%d  beta=%d", moveScore, alpha, beta));
    }

    private static String indent(int d){
      String indent = "";
      for (int i=0;i<d;i++)
        indent += "  ";
      return indent;
    }
    
}
