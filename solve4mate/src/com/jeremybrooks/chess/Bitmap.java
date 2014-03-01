/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess;

/**
 * A Bitboard is some representation of the chessboard where each bit represents
 * some binary state.
 * For example, a Bitboard with the value 0x0000 0000 0000 FF00
 * would represent all of the white pawns in the starting position.  Where as
 * 0x8100 0000 0000 0000 would represent the black rooks' starting position.
 *
 * Here's the 64-bit internal represention of the chessboard:
 *
 *                                                   MSB           
 *                                                   /             
 *                                                  /              
 *         |----|----|----|----|----|----|----|----|               
 *     8*7 | 56 | 57 | 58 | 59 | 60 | 61 | 62 | 63 |               
 *         |----|----|----|----|----|----|----|----|               
 *     8*6 | 48 | 49 | 50 | 51 | 52 | 53 | 54 | 55 |               
 *         |----|----|----|----|----|----|----|----|               
 *     8*5 | 40 | 41 | 42 | 43 | 44 | 45 | 46 | 47 |               
 *         |----|----|----|----|----|----|----|----|               
 *     8*4 | 32 | 33 | 34 | 35 | 36 | 37 | 38 | 39 |               
 *         |----|----|----|----|----|----|----|----|	           
 *     8*3 | 24 | 25 | 26 | 27 | 28 | 29 | 30 | 31 |	            
 *         |----|----|----|----|----|----|----|----|	           
 *     8*2 | 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 |	           
 *         |----|----|----|----|----|----|----|----|               
 *     8*1 | 08 | 09 | 10 | 11 | 12 | 13 | 14 | 15 |	           
 *         |----|----|----|----|----|----|----|----|	           
 *     8*0 | 00 | 01 | 02 | 03 | 04 | 05 | 06 | 07 |	           
 *         |----|----|----|----|----|----|----|----|               
 *        /  0    1    2    3    4    5    6    7                  
 *       /                                                         
 *    LSB                                                          
 *                                                                 
 *  Where:  a1 is the Least Significant Bit (2^0 bit)              
 *          h8 is the Most Significant Bit (2^63 bit)              
 *                                                                 
 *  Value(square) = row header + column header.                    
 *                                                                 
 *  Examples:                                                      
 *	   For the chessboard squares (a1, h8, and e4):            
 *			a1 = 8*0 + 0 = 0                           
 *			h8 = 8*7 + 7 = 63                          
 *			e4 = 8*3 + 4 = 28                          
 *                                                                 
 *
 *
 *
 *If you wanted a bitboard of all the pieces on the board at the start
 *of the chess game you'd have the following bitboard (typedef'd as bitbrd):
 *
 *	bitbrd initial = 0xFFFF00000000FFFF;
 *
 *The least significant 16 bits (0,1,2,...,14,15) of "initial" represent
 *the 16 white pieces: 
 *  0 = white rook on a1, 1 = white knight on b1,..., 15 = white pawn on h2
 *
 *The next 32 least significant bits (16,17,18,...,46,47) of "initial"
 *represent the empty 32 empty squares in the middle of the board.
 *
 *The most significant 16 bits (48,49,50,...,62,63) of "initial" represent
 *the black pieces: 
 *  48 = black pawn on a7, 49 = black pawn on b7,..., 63 = black rook on h8
 * 
 *In my code I store four bitboards so i can easily compute how a particular
 *rank, file or diagonal is occupied by other pieces (important for move
 *generation).  These bitboards contain a set bit for any kind of piece that
 * *sits on that square on the board and a zero bit for an empty square.
 *
 *  1) bitboard not rotated (like sample bitboard above: 0xFFFF00000000FFFF)
 *  2) bitboard rotated 90 degrees right
 *  3) bitboard rotated 45 degrees left  (main diagonal = a1h8)
 *  4) bitboard rotated 45 degrees right (main diagonal = h1a8) 
 *
 *  Bitboard (1) is for extracting a certain rank's occupation
 *  Bitboard (2) is for extracting a certain file's occupation
 *  Bitboard (3) keeps track of how the 15 diagonals (whose main diagonal
 *                is a1h8) are occupied
 *  Bitboard (4) keeps track of how the 15 diagonals (whose main diagonal
 *               is h1a8) are occupied
 *
 * @author jeremy
 *
 */
public class Bitmap {

	public static final int A1 = 0;
	public static final int B1 = 1;
	public static final int C1 = 2;
	public static final int D1 = 3;
	public static final int E1 = 4;
	public static final int F1 = 5;
	public static final int G1 = 6;
	public static final int H1 = 7;
	public static final int A2 = 8;
	public static final int B2 = 9;
	public static final int C2 = 10;
	public static final int D2 = 11;
	public static final int E2 = 12;
	public static final int F2 = 13;
	public static final int G2 = 14;
	public static final int H2 = 15;
	public static final int A3 = 16;
	public static final int B3 = 17;
	public static final int C3 = 18;
	public static final int D3 = 19;
	public static final int E3 = 20;
	public static final int F3 = 21;
	public static final int G3 = 22;
	public static final int H3 = 23;
	public static final int A4 = 24;
	public static final int B4 = 25;
	public static final int C4 = 26;
	public static final int D4 = 27;
	public static final int E4 = 28;
	public static final int F4 = 29;
	public static final int G4 = 30;
	public static final int H4 = 31;
	public static final int A5 = 32;
	public static final int B5 = 33;
	public static final int C5 = 34;
	public static final int D5 = 35;
	public static final int E5 = 36;
	public static final int F5 = 37;
	public static final int G5 = 38;
	public static final int H5 = 39;
	public static final int A6 = 40;
	public static final int B6 = 41;
	public static final int C6 = 42;
	public static final int D6 = 43;
	public static final int E6 = 44;
	public static final int F6 = 45;
	public static final int G6 = 46;
	public static final int H6 = 47;
	public static final int A7 = 48;
	public static final int B7 = 49;
	public static final int C7 = 50;
	public static final int D7 = 51;
	public static final int E7 = 52;
	public static final int F7 = 53;
	public static final int G7 = 54;
	public static final int H7 = 55;
	public static final int A8 = 56;
	public static final int B8 = 57;
	public static final int C8 = 58;
	public static final int D8 = 59;
	public static final int E8 = 60;
	public static final int F8 = 61;
	public static final int G8 = 62;
	public static final int H8 = 63;
	public static final int MAXSQ = 64;
	public static final int NOSQUARE = 65;

	//Represents the chess board's rank as a mask on the Bitboard
	public static final long FIRSTRANK   = (long) 0xFF;
	public static final long SECONDRANK  = ((long) 0xFF) << A2;
	public static final long THIRDRANK   = ((long) 0xFF) << A3;
	public static final long FOURTHRANK  = ((long) 0xFF) << A4;
	public static final long FIFTHRANK   = ((long) 0xFF) << A5;
	public static final long SIXTHRANK   = ((long) 0xFF) << A5;
	public static final long SEVENTHRANK = ((long) 0xFF) << A7;
	public static final long EIGHTHRANK  = ((long) 0xFF) << A8;

	public enum Rank { RANK1, RANK2, RANK3, RANK4, RANK5, RANK6, RANK7, RANK8 };
	public enum File { FILE1, FILE2, FILE3, FILE4, FILE5, FILE6, FILE7, FILE8 };

	
//The following was refactored into Color.java and Piece.java
//
//	//For accessing g.pos.pieces[][] and g.pos.all[]
//	public enum COLOR {WHITE, BLACK, MAXCOLOR};
//	public enum PIECE {PAWNS, KNIGHTS, BISHOPS, ROOKS, QUEENS, ALLPIECES, MAXPIECE};

	//TODO: still needs to be defined somewhere better
	//	public enum XXXXX {ALL, ALL90, ALL45L, ALL45R, MAXALL};
	public static final int ALL = 0;
	public static final int ALL90 = 1;
	public static final int ALL45L = 2;
	public static final int ALL45R = 3;
	public static final int MAXALL = 4;
	
	
	//For g.pos.board[]
	public static final char BOARD_EMPTY_SQUARE = ' ';
	public static final char BOARD_EDGE_SQUARE = 'E';
	
	//TODO: how do i convert the below to java????
	public static final int BOARD_EDGES[] = {
	    A1, B1, C1, D1, E1, F1, G1, H1,
	    A2, A3, A4, A5, A6, A7,
	    H2, H3, H4, H5, H6, H7,
	    A8, B8, C8, D8, E8, F8, G8, H8
	};
	    

	public static final char BOARD_PIECE[] = {
	    'P','N','B','R','Q','K',
	    'p','n','b','r','q','k'	
	};


	//For accessing the constant PIECE and PIECE_VALUE arrays below
	//TODO: this might be confused with Piece.java
	//public enum piece {PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING, NONE};
	public static final int PAWN = 0;
	public static final int KNIGHT = 1;
	public static final int BISHOP = 2;
	public static final int ROOK = 3;
	public static final int QUEEN = 4;
	public static final int KING = 5;
	public static final int NONE = 6;
	
	
	/**
	 * Returns the piece as it is encoded into the "move" integer
	 * Use appropriate constants above for the index 
	 */
	public static final int PIECE[] = {
	    /*  pawn   001 */	1, 	
	    /*  knight 010 */	2,
	    /*  bishop 101 */	5,
	    /*  rook   110 */	6,
	    /*  queen  111 */	7,
	    /*  king   011 */	3,
	    /*  none   100 */   0
	};

	//takes an int index (a board character) and maps it to a corresponding piece
	public static final int TO_PIECE[] = {
	    NONE,       // 0 (no piece)
	    PAWN,       // 1
	    KNIGHT,     // 2
	    KING,       // 3
	    NONE,       // 4 (no piece)
	    BISHOP,     // 5
	    ROOK,       // 6
	    QUEEN       // 7
	};

	//TODO: move PIECE_STR definition to console.h
	public static final char PIECE_CH[] = {'P','N','B','R','Q','K',};

	//TODO: move PIECE_VALUE[] to eval.h maybe?...YES, move it.
	public static final int
	PIECE_VALUE[] = {	
	    /*  pawn   */	100, 	
	    /*  knight */	300,
	    /*  bishop */	300,
	    /*  rook   */	500,
	    /*  queen  */	900,
	    /*  king   */	100000	//TODO: what should the king be worth??
	};


	public static final char
	PIECE_STR[][] = {
	    {'P','N','B','R','Q','K',},
	    {'p','n','b','r','q','k',}
	};

	
	//********************************************************************
	//*	Given a square (A1-H8) the following SQ2BIT??? arrays will       *
	//*   return the corresponding bit position in a bitbrd rotated???   *
	//*   Example: If we want the E4(28) square/bit set in the bitboard  *
	//*            rotated 90 degrees (it's always rotated right for 90) *
	//*            Then use either of the following expressions:         *
	//*                1L << SQ2BIT90R[E4]                        *
	//*                1L << SQ2BIT90R[28]                        *
	//********************************************************************

	public static final int SQ2BIT[] = {
		0,  1,  2,  3,  4,  5,  6,  7,	// 1st rank
		8,  9, 10, 11, 12, 13, 14, 15,	// 2nd-rank
		16, 17, 18, 19, 20, 21, 22, 23,	// 3rd-rank
		24, 25, 26, 27, 28, 29, 30, 31,	// 4th-rank
		32, 33, 34, 35, 36, 37, 38, 39,	// 5th-rank
		40, 41, 42, 43, 44, 45, 46, 47,	// 6th-rank
		48, 49, 50, 51, 52, 53, 54, 55,	// 7th-rank
		56, 57, 58, 59, 60, 61, 62, 63 	// 8th-rank
	};
	public static final int SQ2BIT90R[] = {
	    0,  8, 16, 24, 32, 40, 48, 56,	// a-file 
	    1,  9, 17, 25, 33, 41, 49, 57,	// b-file
	    2, 10, 18, 26, 34, 42, 50, 58,	// c-file
	    3, 11, 19, 27, 35, 43, 51, 59,	// d-file
	    4, 12, 20, 28, 36, 44, 52, 60,	// e-file
	    5, 13, 21, 29, 37, 45, 53, 61,	// f-file
	    6, 14, 22, 30, 38, 46, 54, 62,	// g-file
	    7, 15, 23, 31, 39, 47, 55, 63	// h-file
	};
	
	public static final int SQ2BIT45L[] = {
	    0,  1,  3,  6, 10, 15, 21, 28,
	    2,  4,  7, 11, 16, 22, 29, 36,
	    5,  8, 12, 17, 23, 30, 37, 43,
	    9, 13, 18, 24, 31, 38, 44, 49,
	    14, 19, 25, 32, 39, 45, 50, 54,
	    20, 26, 33, 40, 46, 51, 55, 58,
	    27, 34, 41, 47, 52, 56, 59, 61,
	    35, 42, 48, 53, 57, 60, 62, 63
	};
	
	public static final int SQ2BIT45R[] = { 
	    28, 21, 15, 10,  6,  3,  1,  0,
	    36, 29, 22, 16, 11,  7,  4,  2,
	    43, 37, 30, 23, 17, 12,  8,  5,
	    49, 44, 38, 31, 24, 18, 13,  9,
	    54, 50, 45, 39, 32, 25, 19, 14,
	    58, 55, 51, 46, 40, 33, 26, 20,
	    61, 59, 56, 52, 47, 41, 34, 27,
	    63, 62, 60, 57, 53, 48, 42, 35
	};

	//TODO: this move_t is no longer defined (use an int instead)
	//typedef long int move_t;
	static final int FROM = 0x1F;		 //FROM square
	static final int TO   = 0x1F << 6;    //TO square
	static final int MOV  = 0x7 << 12;    //MOVing piece
	static final int CAP  = 0x7 << 15;    //CAPtured piece
	static final int PRO  = 0x7 << 18;    //PROmotion piece

	
	static long clearBit(long board, int bit){
		board &= ~(1L << bit);
		return board;
	}

	static int getLSBit(long pieces)
	{
		if (pieces != 0L)
		{
			long mask = 1;
			for(int bit=0; bit < 64; bit++, mask <<= 1)
			{
				if(Util.bool(mask & pieces))
				{
					return bit;		
				}
			}
		}
		return -1;
	}

	static int getMSBit(long pieces){
		if (pieces != 0L)
		{
			long mask = 1L << 63;
			for(int bit=63; bit >= 0; bit--, mask >>= 1) 
			{
				if(Util.bool(mask & pieces))
				{
					return bit;		
				}
			}
		}
		return -1;
	}

}
