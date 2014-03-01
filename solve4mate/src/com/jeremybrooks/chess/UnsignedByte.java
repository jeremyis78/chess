package com.jeremybrooks.chess;

public class UnsignedByte {
	public static final int MIN_VALUE = 0;
	public static final int MAX_VALUE = 256;
	
	private static final int BYTE_MASK = 0xFF;
	
	private int unsignedByte;
	
	public UnsignedByte(){}

	public UnsignedByte(int i){
		set(i);
	}
	
	public int get() {
		return unsignedByte;
	}

	public void set(int unsignedByte) {
		if (unsignedByte > 0x80 || unsignedByte < 0x00)
			throw new IllegalArgumentException(unsignedByte + " can't be greater than " + MAX_VALUE);
		this.unsignedByte = unsignedByte;
	}
	
	public void shiftLeft(int shift){
		this.unsignedByte = this.unsignedByte << shift;
		this.unsignedByte &= BYTE_MASK;  //chop to size  
	}

	public void shiftRight(int shift){
		this.unsignedByte = this.unsignedByte >> shift;
		//we should have to worry....right shifting does rotate right?
	}
}

