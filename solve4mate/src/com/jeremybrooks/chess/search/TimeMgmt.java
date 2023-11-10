package com.jeremybrooks.chess.search;

import com.jeremybrooks.chess.util.Util;

public class TimeMgmt 
{
    private static final int BUFFER_TIME_MILLIS = 10;
    
    //Guess how many moves remaining for a sudden death game
    //TODO: This is completely arbitrary for now (needs testing to confirm)
    private static final int MOVES_TO_GO_FOR_SUDDEN_DEATH = 50;
    private SearchParams params;

    public boolean hasExpired(int color, int startTime) {
        //For time control do the naive simplistic algorithm (time / movesLeft)
        //And ignore the increment
        boolean hasTime = true;
        int timePerMove = params.getTime(color) / movesToGo();
        int now = Util.milliTime();
        hasTime = ((now - startTime) + BUFFER_TIME_MILLIS) < timePerMove;
//        System.out.println("time/move: " + timePerMove  + "\ntime left: " + (now - startTime));
        return !hasTime;
    }
    
    public int estimatedTimePerMove(int color){
        return params.getTime(color) / movesToGo();
    }

    public int movesToGo() {
        return params.isSuddenDeath() ? MOVES_TO_GO_FOR_SUDDEN_DEATH : params.getMovesToGo();
    }

    public void setParams(SearchParams params) {
        this.params = params;
    }
    
}
