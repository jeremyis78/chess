package com.jeremybrooks.chess.search;

import java.util.ArrayList;
import java.util.List;

import com.jeremybrooks.chess.util.Util;

/**
 * Holds the results of a search
 * @author jeremy
 */
public class SearchInfo {
    private RootMove bestRootMove;
    private int score;
    private String oldSolutionMoves;
    private List<ScoredMove> oldBestLine;
    private boolean mate;
    private long nodeCount;
    private int elapsedMillis;
    private String scoredRootMoves;

    public SearchInfo(){}
    
    public SearchInfo(RootMove bestRootMove, boolean mate, long nodeCount,
            int elapsedMillis) {
        super();
        this.bestRootMove = bestRootMove;
        this.mate = mate;
        this.nodeCount = nodeCount;
        this.elapsedMillis = elapsedMillis;
    }

    public int getScore() {
//        return score;
        return bestRootMove.getScore();
    }
    
    public boolean isMateOrMated(){
        if(Math.abs(getScore()) >= Search.CHECKMATE / 2) 
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
    
    public void setScore(int score) {
        this.score = score;
//        if(score != getScore())
//            throw new IllegalArgumentException("new bestRootMove score doesn't match old score: " + score + " vs " +getScore());
    }
    
    public List<Integer> getBestLine()
    {
        if(bestRootMove.getPvLength() == 0)
        {
            int noMove = 0;
            List<Integer> list = new ArrayList<>(1);
            list.add(noMove);
            return list;
        }
        List<Integer> list = new ArrayList<>(bestRootMove.getPvLength());
        for(int i=0; i<bestRootMove.getPvLength(); i++)
        {
            list.add(bestRootMove.getPvMove(i));
        }
        return list;
    }
    
    public int getPliesInBestLine()
    {
        return bestRootMove.getPvLength();
    }

    public String getBestLineFormatted()
    {
        if(bestRootMove.getPvLength() == 0)
        {
            return "<none>";
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<bestRootMove.getPvLength(); i++)
        {
            sb.append(Util.displayMoveStr(bestRootMove.getPvMove(i), false, false));
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
    
    /**
     * @return a human readable list of moves
     */
    public String getOldSolutionMoves() {
        return oldSolutionMoves;
    }

    public void setOldSolutionMoves(String solutionMoves) {
        this.oldSolutionMoves = solutionMoves;
    }

    /**
     * @return a List of the best moves indexed by depth (ie, moves on the principal variation)
     */
    public List<ScoredMove> getOldBestLine() {
        return oldBestLine;
    }
    
    public void setOldBestLine(ScoredMove[] pvLine) {
        this.oldBestLine = new ArrayList<ScoredMove>(50);
        int index=0;
        StringBuilder sb = new StringBuilder();
        for(ScoredMove sm: pvLine)
        {
            if(sm == null) break;
            oldBestLine.add(sm); 
            index++;
            if(index % 2 == 1) sb.append((index+1) / 2).append(". ");
            sb.append(Util.displayMoveStr(sm.getMove(), false, false)).append(" ");
        }
//        System.out.println("best line depth is " + index);
//        System.out.println("line: " + sb.toString());
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

