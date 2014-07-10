package com.jeremybrooks.chess.search;

import static com.jeremybrooks.chess.base.Bitmap.*;
/**
 * Holds the constraints of the search (time remaining, moves to go, depth, etc)
 * 
 * @author jeremy
 *
 */
public class SearchParams {
    static final int MILLIS_PER_SECOND       = 1000;
    static final int ONE_MILLISECOND         =    1;
    static final int ONE_SECOND              = 1000 * ONE_MILLISECOND;
    static final int ONE_MINUTE              =   60 * ONE_SECOND;
    static final int FIVE_MINUTES            =    5 * ONE_MINUTE;
    static final int FIFTEEN_MINUTES         =   15 * ONE_MINUTE;
    static final int TWENTY_MINUTES          =   20 * ONE_MINUTE;
    static final int THIRTY_MINUTES          =   30 * ONE_MINUTE;
    static final int ONE_HOUR                =   60 * ONE_MINUTE;
    static final int TWO_HOURS               =  120 * ONE_MINUTE;

    private int[] remainingMillis = new int[2];
    private int[] incrementMillis = new int[2];
    private int movesToGo;
    private int depth;

     public SearchParams() {}    
     public SearchParams(int remainingMillis)
    {
         //sudden death
        setRemainingMillisFor(WHITE, remainingMillis);
        setRemainingMillisFor(BLACK, remainingMillis);
    }    
    public SearchParams(int remainingMillis, int incrementMillis)
    {
        //sudden death with increment
        setRemainingMillisFor(WHITE, remainingMillis);
        setIncrementMillisFor(WHITE, incrementMillis);
        setRemainingMillisFor(BLACK, remainingMillis);
        setIncrementMillisFor(BLACK, incrementMillis);
    }
     public SearchParams(int movesToGo, int remainingMillis, int incrementMillis)
    {
         //X moves in Y time
         setMovesToGo(movesToGo);
        setRemainingMillisFor(WHITE, remainingMillis);
        setIncrementMillisFor(WHITE, incrementMillis);
        setRemainingMillisFor(BLACK, remainingMillis);
        setIncrementMillisFor(BLACK, incrementMillis);
    }    
    public int getRemainingMillisFor(int color) {
        return remainingMillis[color];
    }
    public void setRemainingMillisFor(int color, int remainingMillis) {
        this.remainingMillis[color] = remainingMillis;
    }
    public int getIncrementMillisFor(int color) {
        return incrementMillis[color];
    }
    public void setIncrementMillisFor(int color, int incrementMillis) {
        this.incrementMillis[color] = incrementMillis;
    }
    public int getMovesToGo() {
        return movesToGo;
    }
    public void setMovesToGo(int movesToGo) {
        this.movesToGo = movesToGo;
    }
    public int getDepth() {
        return depth;
    }
    public void setDepth(int depth) {
        this.depth = depth;
    }
    public boolean isSuddenDeath(){
        return 0 == movesToGo;
    }
}
