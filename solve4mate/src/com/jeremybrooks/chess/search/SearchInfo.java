package com.jeremybrooks.chess.search;

import java.util.List;

/**
 * Holds the results of a search
 * @author jeremy
 */
public class SearchInfo {
    private RootMove bestRootMove;
    private long nodeCount;
    private int elapsedMillis;

    public SearchInfo(){}
    
    public SearchInfo(RootMove bestRootMove, long nodeCount, int elapsedMillis) {
        super();
        this.bestRootMove = bestRootMove;
        this.nodeCount = nodeCount;
        this.elapsedMillis = elapsedMillis;
    }

    public int getScore() {
        return bestRootMove.getScore();
    }
    
    public boolean isMateOrMated(){
        
        int absScore = Math.abs(getScore());
        if(absScore >= Search.MIN_MATE && absScore <= Search.MAX_MATE) 
            return true;
        return false;
    }
    
    public boolean isLowerBound(){
        if(getScore() == Search.LOWER_BOUND) 
            return true;
        return false;
    }
    
    public boolean isUpperBound(){
        if(getScore() == Search.UPPER_BOUND) 
            return true;
        return false;
        
    }
    
    public List<Integer> getBestLine()
    {
        return bestRootMove.getPvMoves();
    }
    
    public int getPliesInBestLine()
    {
        return bestRootMove.getPvLength();
    }

    public int getElapsedTime() {
        return elapsedMillis;
    }

    public void setElapsedTime(int milliseconds) {
        this.elapsedMillis = milliseconds;
    }

    public long getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(long nodeCount) {
        this.nodeCount = nodeCount;
    }

    public double getNodesPerSecond() {
        return (1.0 * nodeCount / elapsedMillis) * SearchParams.MILLIS_PER_SECOND;
    }

}

