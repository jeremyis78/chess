package com.jeremybrooks.chess.util;

public class LongDisplayer extends AbstractDisplayer {

	private static final char SET_BIT_CHAR = 'X';
	private long bitboard;
	
	public LongDisplayer() {
		super();
	}

	public LongDisplayer(long bitboard){
		this.bitboard = bitboard;
	}

	public void setLong(long bitboard) {
		this.bitboard = bitboard;
	}

	@Override
	void appendPiece(int currentSquare) {
		
        if(Util.bool(bitboard & (1L << currentSquare)))
        {
            display.append(SET_BIT_CHAR);
        } else {
            display.append("-");
        }
	}

}
