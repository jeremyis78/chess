/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 10, 2010
 */
package com.jeremybrooks.chess.search;

import static com.jeremybrooks.chess.base.Bitmap.WHITE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.eval.Evaluator;
import com.jeremybrooks.chess.movegen.AbstractGenerator;
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
    private static final int MAX_DEPTH_LIMIT  = 120;
    
    public static final boolean SEARCH_DEBUG = false; //SEARCH_DB 
    public static final boolean DEBUG = false; //DB
    public static final boolean EVAL = false;
    public static final int MAXWINDOW = 99999;   //To pass to alpha beta for initial window (+inf, -inf)
    public static final int LOWER_BOUND = -MAXWINDOW;
    public static final int UPPER_BOUND =  MAXWINDOW;
    public static final int CHECKMATE = 50000;   //value for sideToMove mates opponent
    public static final int MATES = CHECKMATE;   //value for sideToMove GIVES mates
    public static final int MATED = -CHECKMATE;  //value for sideToMove IS mated
    
    public static final int DRAW = 0;              //value for draw

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

    /*
     * Holds the best moves found (that is, moves on the principle variation) in ascending order of depth
     */
    protected ScoredMove[] pvLine;
    
    /*
     * Holds the current moves at this depth in the search.
     * If depth is 0, it holds all moves from that position
     * If depth is 1, it holds all moves available after an initial first move
     * and so on.
     */
    private ScoredMove[] rootMove;    
    
    protected List<RootMove> myRootMoves;
    protected int myRootMoveIndex;
    
    /*
     * Holds results of the search
     */
    protected SearchInfo info;
    
    /*
     * Time we started searching
     */
    protected int startTime;
    protected int effectiveDepth;
    
    


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
        pvLine = new ScoredMove[stackSize];
        currentMove = new int[stackSize];
        rootMove = new ScoredMove[AbstractGenerator.MAX_NUM_GENERATED_MOVES];
        myRootMoves = new ArrayList<RootMove>();
    }
    
    public Search(TimeMgmt timer)
    {
        super();
        this.timer = timer;
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

    public ScoredMove[] getRootMove() {
        return rootMove;
    }

    public void setRootMove(ScoredMove[] rootMove) {
        this.rootMove = rootMove;
    }
    
    protected String getScoredRootMoves() {
        StringBuilder sb = new StringBuilder("\n");
        for(ScoredMove sm: rootMove)
        {
            if(sm == null) break;
            sb.append(Util.displayMoveStr(sm.getMove(),false,false));
            sb.append("("+sm.getScore()+")\n");
        }
        return sb.toString();
    }

    public String getPVMoveLine()
    {
        if(pvLine[0] == null) //catches mate or stalemate where there is no "best" move
        {
            return "<none>";
        }
        StringBuilder line = new StringBuilder();
        for(int nodeIndex = 0;
                nodeIndex < depthLimit;
                nodeIndex++)
        {
            
            ScoredMove node = pvLine[nodeIndex];
            line.append(Util.displayMoveStr(node.getMove(), false, false));
            line.append(" ");
        }
        return line.toString();
    }

    public ScoredMove[] getPVLine()
    {
        return pvLine;
    }
    

    /**
     * @return the results of the search
     */
    public SearchInfo getInfo() {
        return info;
    }

    public void search(SearchParams params)
    {
        int minimax = -MAXWINDOW; //we're looking for the largest score possible so we start at the lowest score possible
        timer.setParams(params);
        log.info("search time limits are not enforced");
//        log.debug("whiteTime " + params.getTime(Bitmap.WHITE));
//        log.debug("blackTime " + params.getTime(Bitmap.BLACK));
//        log.debug("movesToGo " + params.getMovesToGo());

        boolean mate = false;
        int elapsedTimeMillis = 0;
        startTime = Util.milliTime();
        int upToThisDepth = maxDepthLimit;
        int side = g.isWhiteToMove()?0:1;
        
        //UciDriver.sendResponse("info depth %d time %d", depth, (Util.milliTime() - startTime));
        minimax = search(side, upToThisDepth);
        String pvLine = getPVMoveLine();
        if(log.isDebugEnabled()) {
            int searchTimeMillis = Util.milliTime() - startTime;
            int nodesPerSecond = (int)(nodeCount / (searchTimeMillis/1000.0));
            log.debug(upToThisDepth + "/" + effectiveDepth + " ply in " + searchTimeMillis + "ms and " + nodeCount + " nodes " + 
                    "(" + nodesPerSecond + " nps) yielded (" + minimax + ") " + pvLine);
        }
        mate = Math.abs(minimax) > Search.CHECKMATE / 2;
        sortRootMoves();
        RootMove bestRootMove = getBestRootMove();
        log.info("best " + Util.displayMoveStr(bestRootMove.getMove(),false,false) + " " + bestRootMove.getScore() 
                +" pv " + bestRootMove.toFormattedPvLine());
        
        elapsedTimeMillis = Util.milliTime() - startTime;
        info = new SearchInfo(bestRootMove, mate, getNodeCount(), elapsedTimeMillis);
        info.setScore(minimax);
    }

    public void sortRootMoves() {
        Collections.sort(myRootMoves);
        for(RootMove rm: myRootMoves)
        {
            System.out.println(Util.displayMoveStr(rm.getMove(), false, false) + " score: " + rm.getScore() + ", pv " + rm.toFormattedPvLine());
        }
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
        moveGenerator.setGameState(g); //FIXME: works for now but needs fixing (F1): gross!
        boolean quiescentSearch = false;
        
        //We always start as the max player, despite what side to move
        minimaxValue = max(alpha, beta, side, depth, quiescentSearch); 
        if(myRootMoves.size() == 0)
        {
            //noMove placeholder with value of search
            myRootMoves.add(new RootMove(0, minimaxValue)); 
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
        
        for(int i=0; i<numMoves /* && timer.hasTimeLeft(side, startTime, params)*/; i++){
            nodeCount++;
            int move = moves.get(i);
            if(depth == 0)
            {
                myRootMoves.add(new RootMove(move, -MAXWINDOW));
                myRootMoveIndex = i;
                String moveStr = Util.displayMoveStr(move, false, false);
                if(isTrace) 
                    log.debug("checking " + moveStr + " ("+(i+1)+" of "+(numMoves)+")");
            }
            currentMove[depth] = move;
            g.makeMove(move, side==WHITE);
            int val = min(alpha,beta,Util.opposing(side),depth+1,quiescentSearch); //recurse!
            if(depth == 0)
                myRootMoves.get(myRootMoveIndex).setScore(val);
            g.undoMove(move, side==WHITE);
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
        if(side == Bitmap.WHITE){
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
            return 0; //drawScore, TODO what value should it be?
    }

    public int scoreForMateAt(int depth) {
        //Mate-at-Depth-One (mate-in-one) > Mate-at-Depth-Three (mate-in-two) ... etc 
        boolean isEvenDepth = (0 == depth % 2);
        return (CHECKMATE - depth) * (isEvenDepth?-1:+1);  //positive at odd depths, negative at even depths (even 0 depth)
    }

    protected void updatePrincipalVariationLine(GameState g, int depth, int score, int move) {
        ScoredMove existingNode = pvLine[depth];
//        log.debug("update PV at depth " + depth);
        
        RootMove rm = myRootMoves.get(myRootMoveIndex);
        rm.setPvMove(currentMove[depth], depth);
        if(depth >= rm.getPvLength())
        {   
//            System.out.println("updating root move " + myRootMoveIndex + " length to " + depth);
            rm.setPvLength(depth + 1); //pvLength is zeroBased
        }
        for(int i=0; i<=depth; i++)
        {
            int currentLineMove = currentMove[i]; //next move to copy to PV
            ScoredMove newNode = new ScoredMove(currentLineMove, score);
            if(showPvUpdates)
            {
                if(existingNode != null)
                {
                    log.debug("d="+depth+" replacing PV node "+existingNode+" with "+newNode);
                } else {
                    log.debug("d=" + depth + " adding PV["+i+"] node "+newNode);
                }
            }
            pvLine[i] = newNode;
        }
        StringBuilder pv = new StringBuilder(pvLine[0].getScore()+": ");
        for(int i=0; i<=depth; i++) 
            pv.append(Util.displayMoveStr(pvLine[i].getMove(), false, false)).append(" ");
        if(showPvUpdates) log.debug("new pv: " + pv.toString());
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
        
        for(int i=0; i<numMoves /*&& timer.hasTimeLeft(side, startTime, params)*/; i++){
            nodeCount++;
            int move = moves.get(i);
            if(depth == 0)
            {
                myRootMoves.add(new RootMove(move, -MAXWINDOW));
                myRootMoveIndex = i;
                String moveStr = Util.displayMoveStr(move, false, false);
                if(isTrace) log.trace("checking " + moveStr + " ("+(i+1)+" of "+(numMoves)+")");
            }
            currentMove[depth] = move;
            g.makeMove(move, side==WHITE);
            int val = max(alpha,beta,Util.opposing(side),depth+1,quiescentSearch);  //recurse!
            if(depth == 0)
                myRootMoves.get(myRootMoveIndex).setScore(val);
            g.undoMove(move, side==WHITE);
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
        if(myRootMoves.size() == 0)
        {
            return new RootMove(0, -MAXWINDOW); //noMove placeholder
        }
        return myRootMoves.get(0);
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
