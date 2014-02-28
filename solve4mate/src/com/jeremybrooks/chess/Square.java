/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess;

/**
 * @author jeremy
 *
 */
public class Square {

	//Represent a chess board square's corresponding bit in the Bitboard
	public static final int A1 = 0;
	public static final int A2 = 1;
	public static final int A3 = 2;
	public static final int A4 = 3;
	public static final int A5 = 4;
	public static final int A6 = 5;
	public static final int A7 = 6;
	public static final int A8 = 7;
	public static final int B1 = 8;
	public static final int B2 = 9;
	public static final int B3 = 10;
	public static final int B4 = 11;
	public static final int B5 = 12;
	public static final int B6 = 13;
	public static final int B7 = 14;
	public static final int B8 = 15;
	public static final int C1 = 16;
	public static final int C2 = 17;
	public static final int C3 = 18;
	public static final int C4 = 19;
	public static final int C5 = 20;
	public static final int C6 = 21;
	public static final int C7 = 22;
	public static final int C8 = 23;
	public static final int D1 = 24;
	public static final int D2 = 25;
	public static final int D3 = 26;
	public static final int D4 = 27;
	public static final int D5 = 28;
	public static final int D6 = 29;
	public static final int D7 = 30;
	public static final int D8 = 31;
	public static final int E1 = 32;
	public static final int E2 = 33;
	public static final int E3 = 34;
	public static final int E4 = 35;
	public static final int E5 = 36;
	public static final int E6 = 37;
	public static final int E7 = 38;
	public static final int E8 = 39;
	public static final int F1 = 40;
	public static final int F2 = 41;
	public static final int F3 = 42;
	public static final int F4 = 43;
	public static final int F5 = 44;
	public static final int F6 = 45;
	public static final int F7 = 46;
	public static final int F8 = 47;
	public static final int G1 = 48;
	public static final int G2 = 49;
	public static final int G3 = 50;
	public static final int G4 = 51;
	public static final int G5 = 52;
	public static final int G6 = 53;
	public static final int G7 = 54;
	public static final int G8 = 55;
	public static final int H1 = 56;
	public static final int H2 = 57;
	public static final int H3 = 58;
	public static final int H4 = 59;
	public static final int H5 = 60;
	public static final int H6 = 61;
	public static final int H7 = 62;
	public static final int H8 = 63;
	public static final int MAX_SQUARE = 64; //renamed from MAXSQ
	public static final int NO_SQUARE = 65;  //renamed from NOSQUARE
	
	
	public static final void main(String[] args)
	{
		int bit = 0;
		for(char letter='A'; letter <= 'H'; letter++){
			for(int number=1; number <= 8; number++){
				System.out.println("public static final int " + letter + number + " = " + bit++ + ";");
			}
		}
	}

}
