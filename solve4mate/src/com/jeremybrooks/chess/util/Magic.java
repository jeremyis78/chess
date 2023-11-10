package com.jeremybrooks.chess.util;

public class Magic {
	public final long number;
	public final int shift;
	public final long occupiedMask;
	
	public Magic(long number, int shift, long occupiedMask) {
		super();
		this.occupiedMask = occupiedMask;
		this.number = number;
		this.shift = shift;
	}
}
