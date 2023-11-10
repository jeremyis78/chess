package com.jeremybrooks.chess.util;

public class LongDisplayer extends AbstractDisplayer {

	private static final LongDisplayer INSTANCE = new LongDisplayer();
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
	
	public static String paste(String rightHeader, long rightLong, String leftHeader, long leftLong)
	{
		String EOL = AbstractDisplayer.EOL;
		StringBuilder right = new StringBuilder(rightHeader).append(EOL);
		INSTANCE.setLong(rightLong);
		right.append(INSTANCE.formatBoard());
		StringBuilder left = new StringBuilder(leftHeader).append(EOL);
		INSTANCE.setLong(leftLong);
		left.append(INSTANCE.formatBoard());
		return paste(right.toString(), left.toString());
	}
	
	private static String paste(String right, String left)
	{
		String EOL = AbstractDisplayer.EOL;
		String[] rightSide = right.split(EOL);
		String[] leftSide = left.split(EOL);
		if(rightSide.length != leftSide.length)
			throw new IllegalArgumentException("right side lines != left side lines");
		StringBuilder pasteTogether = new StringBuilder();
		for(int lineNo=0; lineNo<rightSide.length; lineNo++)
		{
			pasteTogether.append(rightSide[lineNo].replace(EOL, ""));
			pasteTogether.append("\t");
			pasteTogether.append(leftSide[lineNo].replace(EOL, ""));
			pasteTogether.append("\n");
		}
		return pasteTogether.toString();
	}
	
}
