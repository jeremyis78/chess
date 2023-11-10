package com.jeremybrooks.chess.search;

import static com.jeremybrooks.chess.base.Bitmap.*;

import com.jeremybrooks.chess.base.Piece;
/**
 * Holds the constraints of the search (time remaining, moves to go, depth, etc)
 * 
 * @author jeremy
 *
 */
public class SearchParams {
    static final int MILLIS_PER_SECOND       = 1000;
    static final int ONE_MILLISECOND         =    1;
    public static final int ONE_SECOND              = 1000 * ONE_MILLISECOND;
    public static final int ONE_MINUTE              =   60 * ONE_SECOND;
    static final int FIVE_MINUTES            =    5 * ONE_MINUTE;
    static final int FIFTEEN_MINUTES         =   15 * ONE_MINUTE;
    static final int TWENTY_MINUTES          =   20 * ONE_MINUTE;
    static final int THIRTY_MINUTES          =   30 * ONE_MINUTE;
    public static final int ONE_HOUR                =   60 * ONE_MINUTE;
    public static final int TWO_HOURS               =  120 * ONE_MINUTE;

    private int[] remainingMillis = new int[2];
    private int[] incrementMillis = new int[2];
    private int movesToGo;
    private int depth;

    /**
     * Create an instance suitable for allocating all thinking time
     * to searching one single position.
     * 
     * @param thinkingTimeMillis
     * @return SearchParams with 1 move to go and with given thinking time
     */
    public static SearchParams forOnePosition(int thinkingTimeMillis)
    {
        SearchParams p = new SearchParams(1, thinkingTimeMillis);
        return p;
    }
    
    public SearchParams() {}    
    public SearchParams(int remainingMillis)
    {
         //sudden death
        setTime(Piece.WHITE, remainingMillis);
        setTime(Piece.BLACK, remainingMillis);
    }    
    public SearchParams(int movesToGo, int remainingMillis)
    {
        //sudden death
        setMovesToGo(movesToGo);
        setTime(Piece.WHITE, remainingMillis);
        setIncrement(Piece.WHITE, 0);
        setTime(Piece.BLACK, remainingMillis);
        setIncrement(Piece.BLACK, 0);
    }
    public SearchParams(int movesToGo, int remainingMillis, int incrementMillis)
    {
        //X moves in Y time
        setMovesToGo(movesToGo);
        setTime(Piece.WHITE, remainingMillis);
        setIncrement(Piece.WHITE, incrementMillis);
        setTime(Piece.BLACK, remainingMillis);
        setIncrement(Piece.BLACK, incrementMillis);
    }    
    public int getTime(int color) {
        return remainingMillis[color];
    }
    public void setTime(int color, int remainingMillis) {
        this.remainingMillis[color] = remainingMillis;
    }
    public int getIncrement(int color) {
        return incrementMillis[color];
    }
    public void setIncrement(int color, int incrementMillis) {
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
