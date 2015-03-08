/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.util.AbstractDisplayer.EOL;
import com.jeremybrooks.chess.util.Util;


/**
 * A Bitboard is some representation of the chessboard where each bit represents
 * some binary state.
 * <p>For example, a Bitboard with the value 0x0000 0000 0000 FF00
 * would represent all of the white pawns in the starting position.  Where as
 * 0x8100 0000 0000 0000 would represent the black rooks' starting position.
 *
 * Here's the 64-bit internal represention of the chessboard:
 * <pre>
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
 *       For the chessboard squares (a1, h8, and e4):            
 *            a1 = 8*0 + 0 = 0                           
 *            h8 = 8*7 + 7 = 63                          
 *            e4 = 8*3 + 4 = 28                          
 *                                                                 
 *
 *If you wanted a bitboard of all the pieces on the board at the start
 *of the chess game you'd have the following bitboard (typedef'd as bitbrd):
 *
 *    bitbrd initial = 0xFFFF00000000FFFF;
 *
 *The least significant 16 bits (0,1,2,...,14,15) of "initial" represent
 *the 16 white pieces: 
 *  0 = white rook on a1, 1 = white knight on b1,..., 15 = white pawn on h2
 *
 *The next 32 least significant bits (16,17,18,...,46,47) of "initial"
 *represent the 32 empty squares in the middle of the board.
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
 *</pre>
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

    public static final long FIRSTRANK   = populateBits(new int[]{A1,B1,C1,D1,E1,F1,G1,H1});
    public static final long SECONDRANK  = FIRSTRANK <<  8;
    public static final long THIRDRANK   = FIRSTRANK << 16;
    public static final long FOURTHRANK  = FIRSTRANK << 24;
    public static final long FIFTHRANK   = FIRSTRANK << 32;
    public static final long SIXTHRANK   = FIRSTRANK << 40;
    public static final long SEVENTHRANK = FIRSTRANK << 48;
    public static final long EIGHTHRANK  = FIRSTRANK << 56;

    public static final long A_FILE =  populateBits(new int[]{A1,A2,A3,A4,A5,A6,A7,A8});
    public static final long B_FILE =  A_FILE << 1;
    public static final long C_FILE =  A_FILE << 2;
    public static final long D_FILE =  A_FILE << 3;
    public static final long E_FILE =  A_FILE << 4;
    public static final long F_FILE =  A_FILE << 5;
    public static final long G_FILE =  A_FILE << 6;
    public static final long H_FILE =  A_FILE << 7;

    public enum Rank { RANK1, RANK2, RANK3, RANK4, RANK5, RANK6, RANK7, RANK8 };
    public enum File { FILE1, FILE2, FILE3, FILE4, FILE5, FILE6, FILE7, FILE8 };

    //TODO: still needs to be defined somewhere better
    //    public enum XXXXX {ALL, ALL90, ALL45L, ALL45R, MAXALL};
    public static final int ALL = 0;
    public static final int ALL90 = 1;
    public static final int ALL45L = 2;
    public static final int ALL45R = 3;
    public static final int MAXALL = 4;

	private static final long FLIP_VERTICAL_CONSTANT1 = 0x00FF00FF00FF00FFL;
	private static final long FLIP_VERTICAL_CONSTANT2 = 0x0000FFFF0000FFFFL;
	private static final long MIRROR_HORZTL_CONSTANT1 = 0x5555555555555555L;
	private static final long MIRROR_HORZTL_CONSTANT2 = 0x3333333333333333L;
	private static final long MIRROR_HORZTL_CONSTANT4 = 0x0f0f0f0f0f0f0f0fL;
	private static final long FLIP_DIAGA1H8_CONSTANT1 = 0x5500550055005500L;
	private static final long FLIP_DIAGA1H8_CONSTANT2 = 0x3333000033330000L;
	private static final long FLIP_DIAGA1H8_CONSTANT4 = 0x0f0f0f0f00000000L;

    
    //For g.pos.board[]
    public static final char BOARD_EMPTY_SQUARE = ' ';
    

    //********************************************************************
    //*    Given a square (A1-H8) the following SQ2BIT??? arrays will       *
    //*   return the corresponding bit position in a bitbrd rotated???   *
    //*   Example: If we want the E4(28) square/bit set in the bitboard  *
    //*            rotated 90 degrees (it's always rotated right for 90) *
    //*            Then use either of the following expressions:         *
    //*                1L << SQ2BIT90R[E4]                        *
    //*                1L << SQ2BIT90R[28]                        *
    //********************************************************************

    public static final int SQ2BIT[] = {
        0,  1,  2,  3,  4,  5,  6,  7,    // 1st rank
        8,  9, 10, 11, 12, 13, 14, 15,    // 2nd-rank
        16, 17, 18, 19, 20, 21, 22, 23,    // 3rd-rank
        24, 25, 26, 27, 28, 29, 30, 31,    // 4th-rank
        32, 33, 34, 35, 36, 37, 38, 39,    // 5th-rank
        40, 41, 42, 43, 44, 45, 46, 47,    // 6th-rank
        48, 49, 50, 51, 52, 53, 54, 55,    // 7th-rank
        56, 57, 58, 59, 60, 61, 62, 63     // 8th-rank
    };
    public static final int SQ2BIT90R[] = { 
    	// The name is actually incorrect; it's a combination of a rotation and a flip:
    	// 1) rotated 90 degrees LEFT such that 0=a8, 1=a7, ..., 7=a1
    	// 2) then vertically flipped such that 0=a1, 1=a2, ..., 7=a8 (so we have ascending order)
    	// Therefore a better name might be SQ2FILEBIT / SQUARE_TO_FILE_BIT
        0,  8, 16, 24, 32, 40, 48, 56,    // a-file 
        1,  9, 17, 25, 33, 41, 49, 57,    // b-file
        2, 10, 18, 26, 34, 42, 50, 58,    // c-file
        3, 11, 19, 27, 35, 43, 51, 59,    // d-file
        4, 12, 20, 28, 36, 44, 52, 60,    // e-file
        5, 13, 21, 29, 37, 45, 53, 61,    // f-file
        6, 14, 22, 30, 38, 46, 54, 62,    // g-file
        7, 15, 23, 31, 39, 47, 55, 63     // h-file
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
    static final int FROM = 0x1F;         //FROM square
    static final int TO   = 0x1F << 6;    //TO square
    static final int MOV  = 0x7 << 12;    //MOVing piece
    static final int CAP  = 0x7 << 15;    //CAPtured piece
    static final int PRO  = 0x7 << 18;    //PROmotion piece

    
    /* members */
    private long bitmap = 0L;
    
    private Bitmap()
    {
    }

    private Bitmap(int bitToSet)
    {
        bitmap = 1L << bitToSet;
    }

    private Bitmap(long bitmap)
    {
        this.bitmap = bitmap;
    }

    public long longValue()
    {
        return bitmap;
    }
    
    @Override
    public String toString()
    {
        return format(bitmap);
    }
    
    public Bitmap bitwiseOr(Bitmap bitsToSet)
    {
        long temp = bitmap;
        temp |= bitsToSet.longValue();
        return new Bitmap(temp);
    }

    public Bitmap bitwiseAnd(Bitmap bitsToSet)
    {
        long temp = bitmap;
        temp &= bitsToSet.longValue();
        return new Bitmap(temp);
    }

    public Bitmap bitwiseXor(Bitmap bitsToUnset)
    {
        long temp = bitmap;
        temp ^= bitsToUnset.longValue();
        return new Bitmap(temp);
    }

    public Bitmap notBits()
    {
        return new Bitmap(~bitmap);
    }

    public static Bitmap emptyBitmap()
    {
        return new Bitmap();
    }

    public static Bitmap createBitmap(long bits)
    {
        return new Bitmap(bits);
    }

    /**
     * Returns a bitmap (unrotated) with given bit set.
     * 
     * @param bitToSet the number of the bit to set
     * @return
     */
    public static Bitmap rankBitmap(int bitToSet)
    {
        return new Bitmap(bitToSet);
    }

    /**
     * Returns a bitmap rotated 90 degrees right with given bit set.
     * 
     * @param bitToSet the number of the bit to set
     * @return
     */
    public static Bitmap fileBitmap(int bitToSet)
    {
        return new Bitmap(SQ2BIT90R[bitToSet]);
    }

    /**
     * Returns a bitmap rotated 45 degrees right with the given bit set.
     * 
     * @param bitToSet the number of the bit to set
     * @return
     */
    public static Bitmap rightDiagonalBitmap(int bitToSet)
    {
        return new Bitmap(SQ2BIT45R[bitToSet]);
    }

    /**
     * Returns a bitmap rotated 45 degrees left with given bit set.
     * 
     * @param bitToSet the number of the bit to set
     * @return
     */
    public static Bitmap leftDiagonalBitmap(int bitToSet)
    {
        return new Bitmap(SQ2BIT45L[bitToSet]);
    }

    public boolean isNotEmpty()
    {
        return hasMore(bitmap);
    }

    public static long populateBits(int firstBit, int lastBitExcluded, int increment)
	{	
		long populated = 0L;
		for(int bit=firstBit; bit<lastBitExcluded; bit+=increment) 
			populated |= withOneBitSet(bit & 0x3F); //only first 6 bits are valid
		return populated;
	}

    public static long populateBits(int[] bitIndexes)
	{	
		long populated = 0L;
		for(int bit: bitIndexes) 
			populated |= withOneBitSet(bit & 0x3F); //only first 6 bits are valid
		return populated;
	}

    public static boolean isBitSet(int bit, long bitboard)
    {
    	return Util.bool(bitboard & withOneBitSet(bit));
    }

    public static long withNewBitSet(long bitboard, int newBit)
    {
    	return bitboard |= withOneBitSet(newBit);
    }
    
	public static boolean hasMore(long board)
    {
        return board != 0;
    }
    
    public static long withOneBitSet(int bitToSet)
    {
        if(bitToSet < 0 || bitToSet > 63)
            throw new IllegalArgumentException("bitToSet ("+bitToSet+") must be 0 to 63");
        return 1L << bitToSet;
    }
    
    public static long clearBit(long board, int bit){
        board &= ~(1L << bit);
        return board;
    }

    public static int lowestBitNumber(long pieces)
    {
        if(pieces == 0) return -1;
        //since our bitmaps are zero-indexed numberOfTrailingZeros works
        //and uses an efficient implementation.  1.023X as fast as the naive one.
        return Long.numberOfTrailingZeros(pieces);
    }

    static int highestBitNumber(long pieces){
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
    
    /**
     *  Return the number of the rank (zero-based) on which the given square lies.
     *  
     * @param sq the square to evaluate
     * @return the integer value of <code>sq / 8</code>
     */
    public static int rankNumber(int sq)
    {
        return (sq / 8); //integer division
    }

    /**
     *  Return the number of the file (zero-based) on which the given square lies.
     *  
     * @param sq the square to evaluate
     * @return the value of <code>sq % 8</code>
     */
    public static int fileNumber(int sq)
    { 
        return (sq % 8);
    }

    public static long flipVertical(long b)
    {
    	long k1 = FLIP_VERTICAL_CONSTANT1;
    	long k2 = FLIP_VERTICAL_CONSTANT2;
    	b = ((b >>>  8) & k1) | ((b & k1) <<  8);
    	b = ((b >>> 16) & k2) | ((b & k2) << 16);
    	b = ( b >>> 32)       | ( b       << 32);
    	return b;
    }
    
    public static long mirrorHorizontal(long b) 
    {
    	long k1 = MIRROR_HORZTL_CONSTANT1;
    	long k2 = MIRROR_HORZTL_CONSTANT2;
    	long k4 = MIRROR_HORZTL_CONSTANT4;
    	b = ((b >>> 1) & k1) | ((b & k1) << 1);
    	b = ((b >>> 2) & k2) | ((b & k2) << 2);
    	b = ((b >>> 4) & k4) | ((b & k4) << 4);
    	return b;
    }
    
    public static long flipDiagA1H8(long b)
    {
    	long t;
    	long k1 = FLIP_DIAGA1H8_CONSTANT1;
    	long k2 = FLIP_DIAGA1H8_CONSTANT2;
    	long k4 = FLIP_DIAGA1H8_CONSTANT4;
    	t  = k4 & (b ^ (b  << 28));
    	b ^=       t ^ (t >>> 28) ;
    	t  = k2 & (b ^ (b  << 14));
    	b ^=       t ^ (t >>> 14) ;
    	t  = k1 & (b ^ (b  <<  7));
    	b ^=       t ^ (t >>>  7) ;
    	return b;
    }

    public static String format(long moves){
        return format(moves, 0L);
    }

    /**
     * For the given bitboard, prints X for set bits (- for unset bits) and a + for
     * a bit set in highlighted; rank and file indicators are also printed.  It 
     * orients the board so that a1 is in the lower left hand corner and 
     * h8 is in the upper right hand corner--the standard view of a chess board.
     * 
     * @param b the bitboard to format
     * @param highlighted the bitboard with a single bit set that will be highlighted
     * @return
     */
    public static String format(long b, long highlighted){
        boolean overlaps = Util.bool(b & highlighted);
        if(overlaps)
        	throw new IllegalArgumentException("b and highlighted cannot have any overlapping bits");
        long mask;
        int  numSquaresToDisplay = 64;
        StringBuilder sb = new StringBuilder();
        for(int firstSquareOfRank = numSquaresToDisplay - 8; 
        		firstSquareOfRank>=0; 
        		firstSquareOfRank-=8)
        {
            mask = 1L << firstSquareOfRank;
            int lastSquareOnRank = firstSquareOfRank + 7;
            sb.append(rankNumber(firstSquareOfRank)+1 + " ");
            for(int currentSquare = firstSquareOfRank;
            		currentSquare <= lastSquareOnRank;
            		++currentSquare, mask <<= 1)
            {
                if (Util.bool(mask & highlighted))  //Print the '+' first so
                    sb.append("+ ");                //we don't overwrite a move
                else if (Util.bool(mask & b))
                    sb.append("X ");  
                else
                    sb.append("- ");
            }
            sb.append(EOL);
        }
        return sb.append("  a b c d e f g h"+EOL).toString();
    }

}
