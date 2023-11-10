package com.jeremybrooks.chess.search;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import static com.jeremybrooks.chess.search.ScoredMove.Precision;

public class CacheEnabledSearch extends IterativeDeepeningSearch {

    private static final Logger log = Logger.getLogger(CacheEnabledSearch.class);
    private static final int INITIAL_CACHE_SIZE = 65536;

    /*
     * Holds the cache of previously seen nodes (aka transposition table) indexed by zobristKey 
     */
    private Map<Long, ScoredMove> cache;
    


    public CacheEnabledSearch(int maxDepth) {
        super(maxDepth);
        cache = new HashMap<>(INITIAL_CACHE_SIZE);
    }

    @Override
    protected ScoredMove lookup(long key) {
        return cache.get(key);
        
    }

    @Override
    protected void store(Long key, int score, Precision metadata, int depth) {
        ScoredMove node = new ScoredMove();
        node.setScore(score);
        node.setPrecision(metadata);
        node.setDepthOfSearch(depth);
        node.setMove(bestMove(depth));
        log.debug("storing " + node);
        cache.put(key, node);
    }

    private int bestMove(int depth) {
        System.err.println("TODO: return the best move at depth " + depth + " here, or pass the move in to store(), not 0");
        return 0;
    }
    
    @Override
    protected boolean isCacheHit(ScoredMove node, int alpha, int beta, int depth) {
        if(node != null && node.getDepthOfSearch() > depth)
        {
            return true;
        }
        return false;
    }

    @Override
    protected Integer cachedScore(ScoredMove node, int alpha, int beta, int depth)
    {
        switch(node.getPrecision())
        {
        case EXACT: return node.getScore();
        case LOWER_BOUND:
            if(node.getScore() <= alpha)
                return alpha;
        case UPPER_BOUND: 
            if(node.getScore() >= beta)
                return beta;
        default: 
            log.debug("cachedScore would return null for " + node.getPrecision());
            return 0; 
        }
    }




}
