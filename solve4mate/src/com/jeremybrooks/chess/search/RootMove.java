package com.jeremybrooks.chess.search;

import java.util.ArrayList;
import java.util.List;

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

    public int getPvMove(int depth)
    {
        return principalVariationMoves.get(depth);
    }
    
    public void setPvMove(int move, int depth)
    {
        principalVariationMoves.set(depth, move);
        if(depth >= pvLength)
            pvLength = depth + 1;
    }
    
    public List<Integer> getPvMoves()
    {
        int pvSize = getPvLength();
        if(pvSize == 0)
        {
            int noMove = 0;
            List<Integer> list = new ArrayList<>(1);
            list.add(noMove);
            return list;
        }
        List<Integer> list = new ArrayList<>(pvSize);
        for(int i=0; i<pvSize; i++)
        {
            list.add(getPvMove(i));
        }
        return list;
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
