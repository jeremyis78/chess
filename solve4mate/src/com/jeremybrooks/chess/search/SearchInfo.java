package com.jeremybrooks.chess.search;

/**
 * Holds the results of a search
 * @author jeremy
 */
public class SearchInfo {
    private int score;
    private String solutionMoves;
    private ScoredMove[] bestLine;
    private boolean mate;
    private long nodeCount;
    private int elapsedMillis;

    private String scoredRootMoves;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return a human readable list of moves
     */
    public String getSolutionMoves() {
        return solutionMoves;
    }

    public void setSolutionMoves(String solutionMoves) {
        this.solutionMoves = solutionMoves;
    }

    /**
     * @return an array of the best moves indexed by depth (ie, moves on the principal variation)
     */
    public ScoredMove[] getBestLine() {
        return bestLine;
    }
    
    public void setBestLine(ScoredMove[] bestLine) {
        this.bestLine = bestLine;
    }
    
    public boolean isMate() {
        return mate;
    }

    public void setMate(boolean mate) {
        this.mate = mate;
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

    public String getScoredRootMoves() {
        return scoredRootMoves;
    }

    public void setScoredRootMoves(String scoredRootMoves) {
        this.scoredRootMoves = scoredRootMoves;
    }    
}

