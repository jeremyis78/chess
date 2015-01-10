package com.jeremybrooks.chess.search;

import java.util.ArrayList;
import java.util.List;

import com.jeremybrooks.chess.util.Util;

public class RootMove implements Comparable<RootMove> {
    private int move;
    private int score;
    private List<Integer> principalVariationMoves; //key is the depth of the move in the search tree
    private int pvLength;
    
    public RootMove(int move, int score)
    {
        init(move, score);
    }

    private void init(int move, int score) {
        this.move = move;
        this.score = score;
        principalVariationMoves = new ArrayList<>();
        for(int i=0; i<150; i++) //TODO: fix hardcoded size
            principalVariationMoves.add(0); // noMove placeholder
        pvLength = 0;
    }
    
    public int getMove() { return move; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public int getPvLength() {
        return pvLength;
    }
    public void setPvLength(int pvLength) {
        this.pvLength = pvLength;
    }
    public int getPvMove(int depth)
    {
        return principalVariationMoves.get(depth);
    }
    
    public void setPvMove(int move, int depth)
    {
        principalVariationMoves.set(depth, move);
    }
    
    public String toFormattedPvLine()
    {
        StringBuilder sb = new StringBuilder();
        for(int pvMove: principalVariationMoves)
        {
            if(pvMove == 0 /* NO-MOVE placeholder */) break;
            sb.append(Util.displayMoveStr(pvMove, false, false)).append(" ");
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + move;
        return result;
    }
    
    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null)
            return false;
        if (getClass() != that.getClass())
            return false;
        RootMove other = (RootMove) that;
        if (move != other.move)
            return false;
        return true;
    }

    @Override
    public int compareTo(RootMove o) {
        //Descending order (biggest scores first) 
        //To work, the max() call/player needs to be called/play first. It doesn't
        //matter what color plays first as long as the Search.max() call is called first).
        RootMove other = (RootMove) o;
        return -1 * new Integer(this.score).compareTo(other.score);
    }
}
