/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

import java.io.PrintStream;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * @author jeremy
 *
 */
public class Attacks {
	
	private static Logger log = Logger.getLogger(Attacks.class);
	private static PrintStream out = System.out;

	//This is a constant for the diagonal length
	//To access the length of the main diagonal use dlen[7]
	//The first diagonal is the zero-th.
	static final int DLEN[] = {1,2,3,4,5,6,7,8,7,6,5,4,3,2,1};
	
	static final int dstartsqRight45[] = {7,6,5,4,3,2,1,0,8,16,24,32,40,48,56}; //should have 15 elements
	
	static final int dstartsqLeft45[] = {0,1,2,3,4,5,6,7,15,23,31,39,47,55,63};  //should be 15 elements
    //TODO: could replace above with (for clarity maybe??) 
    // {A1,B1,C1,D1,E1,F1,G1,H1,H2,H3,H4,H5,H6,H7,H8}

	private static final int FILE1 = 0;
	private static final int FILE8 = 7;
	private static final int RANK8 = 7;

	short base[][] = new short[8][64]; //datatype needs to be 8bits, using short instead to avoid some problem

	//Masks have a one bit set corresponding to the square on the board.
	//The array index is the square number on the board, an element of {0,1,...,63}
	//If it's a 2D array, the first index is the square number and
	//the second is the status of the ray the piece is aligned on
	
	 //TODO: should these be statically initialized instead?????
	long mask[] = new long[64];    //TODO: do we need these?  (see SQ2BIT??[] in defs.h)
    long mask90[] = new long[64];  //TODO: do we need these?     "
	long mask45R[] = new long[64]; //TODO: do we need these?     "
	long mask45L[] = new long[64]; //TODO: do we need these?     "
	long king[] = new long[64];    //king attacks/moves for all squares 
	long knight[] = new long[64];  //knight attacks/moves for all squares
	long rank[][] = new long[64][64];//*rank[64];//rank[64][64],  //rank attacks/moves for all squares
	long file[][] = new long[64][64];//*file[64];//file[64][64],  //file attacks/moves for all squares
	long R45[][] = new long[64][64];//*R45[64];//R45[64][64],   //45D RIGHT attacks/moves for all squares
	long L45[][] = new long[64][64];//*L45[64];//L45[64][64],   //45D LEFT attacks/moves for all squares
	long pawn[][] = new long[2][64];//*pawn[2];//pawn[2][64],   //pawn[0][sq] = attacks for white pawn on sq
                                         //pawn[1][sq] = attacks for black pawn on sq
	long whitepawn[] = new long[64];
	long blackpawn[] = new long[64];		

    long plus1[] = new long[64];
    long plus7[] = new long[64];
    long plus8[] = new long[64];
    long plus9[] = new long[64];
    long minus1[] = new long[64];
    long minus7[] = new long[64];
    long minus8[] = new long[64];
    long minus9[] = new long[64];


    private static Attacks attacks;
    
    public static Attacks getInstance(){
    	if(attacks == null){
    		attacks = new Attacks();
    	}
    	return attacks;
    }
    
    public Attacks(){
      //init to proper values
      _GenAllMasks();
      genBaseAttacks();
      genRankAttacks();
      genFileAttacks();
      genDiagonal45DegreesRightAttacks();
      genDiagonal45DegreesLeftAttacks();
      genPawnAttacks();
      genKingKnightAttacks();
  }

  void _GenAllMasks(){
	  	log.info("generating masks");
	  	genMask();
	  	genMask90();

    	genMask45DegreesRight();
        genMask45DegreesLeft();
        genMasksPlusMinus();
    }

	void genMask() {
		long m = 1;
		int i;
		for(i=0; i < 64; i++){
			mask[i] = m << i;
			//out.printf("0x%08X\n", mask[i]);
		}
	}
  
	void genMask90() {
		long m;
		int i;
		m = 1;      //set bitbrd back to 1
		int x = 0;  //x runs from 0 to 63 inclusive
			//i runs 56 to 63, step 1
			//    j starts at i
		//    and runs while (j >= 0), step -8	
		for(i=56; i < 64; i++){
			for(int j=i; j >= 0; j-=8, x++){
				mask90[x] = m << j;
		    }
		}
	}


    void genMask45DegreesRight(){
	  	log.info("generating masks rotated 45 deg right");

    	//Set the bit in mask45R[s] (bitbrd rotated 45 degrees right)
    //that corresponds to the square s.  Do this for s = {0,...,63}.

    //ALGORITHM:
    //x runs from 0 to 63 inclusive
    //i = start square {7,6,5,4,3,2,1,0,8,16,24,32,40,48,56) of the diagonal
    //j is the square in the diagonal starting at i, 
//        by adding 9 to j each inner loop we mark off the squares of the diagonal
//        increment x each inner loop
//        stop the inner loop once we've touched on all squares in the diagonal 
    // len is the length of the diagonal we're currently on
    // it must incremented by one for i = {7,6,...,0} 
    // and decremented by one for i = {8,16,...,56}


    	//For diagonals starting on the first rank and 
    	//extending up and to the right, starting at h1.
    	//These diagonals: {h1, g1-h2, f1-h3,..., a1-h8}
    	long mask = 1;   //set bitbrd back to 1
    	int x = 0;  //x runs from 0 to 35 inclusive


    	//i and len are declared outside loop for compatibility w/ 
    	//microsoft Visual C++ 6.0 compiler
    	int i, len;

    	for(i=7, len=1; i >= 0; i--, len++){
    		for(int j=i, d=len; d > 0; j+=9, d--){
    			mask45R[x] = mask << j;
    			//out.println(j);
    		}
    	}

    	//For diagonals starting on the a-file and 
    	//extending up and to the right, starting at a2.
    	//These diagonals: {a2-g8, a3-f8,..., a7-b8, a8}
    	mask = 1;  //set bitbrd back to 1
    	//Now, x runs from 36 to 63 inclusive
    	for(i=8, len=7; i <= 56; i+=8, len--){
    		for(int j=i, d=len; d > 0; j+=9, d--){
    			mask45R[x] = mask << j;
    			//out.println(j);
    		}
    	}
    }

    void genMask45DegreesLeft(){
	  	log.info("generating masks rotated 45 deg left");

    //Set the bit in mask45L[s] (bitbrd rotated 45 degrees left)
    //that corresponds to the square s.  Do for all s = {0,...,63}.

    //ALGORITHM:
    //x runs from 0 to 63 inclusive
    //i = start square {0,1,2,3,4,5,6,7,15,23,31,39,47,55,63} of the diagonal
    //j is the square in the diagonal starting at i, 
//        by adding 7 to j each inner loop we mark off the squares of the diagonal
//        increment x each inner loop
//        stop the inner loop once we've touched on all squares in the diagonal 
    // len is the length of the diagonal we're currently on
    // it must incremented by one for i ={0,1,...,7} 
    // and decremented by one for i = {15,23,...,63}

    	//For diagonals starting starting on the first rank and 
    	//extending up and to the left.
    	//These diagonals: {a1, b1-a2, c1-a3,..., h1-a8}
    	long mask = 1;
    	int x = 0;  


    	//i and len are declared outside loop for compatibility w/ 
    	//microsoft Visual C++ 6.0 compiler
    	int i, len;

    	for(i=0, len=1; i <= 7; i++, len++){
    		for(int j=i, d=len; d > 0; j+=7, x++, d--){
    			mask45L[x] = mask << j;
    		}
    	}

    	//For diagonals starting on the h-file and extending up and to the left
    	//These diagonals: {h2-b8, h3-c8, h4-d8,..., h7-g8, h8}
    	mask = 1;
    	x = 36; 
    	for(i=15, len=7; i <= 63; i+=8, len--){
    		for(int j=i, d=len; d > 0; j+=7, x++, d--){
    			mask45L[x] = mask << j;
    		}
    	}
    }

    void genMasksPlusMinus(){
	  	log.info("generating plus/minus masks");

        //For each square, generate the masks plus[1,7,8,9] and minus[1,7,8,9]
        //Also, there should never be more than 7 squares attacked 
        //in the each bitmap
        
        for (int sq=A1; sq<=H8; sq++){
            plus1[sq] = 0;
            for(int bit=sq+1; fileNumber(bit) != FILE1; bit++){
                plus1[sq] |= 1L << bit;
            }
            
            minus1[sq] = 0;
            for(int bit=sq-1; fileNumber(bit) != FILE8 && bit >= 0; bit--){
                minus1[sq] |= 1L << bit;
            }

            plus8[sq] = 0;
            for(int bit=sq+8; Bitmap.rankNumber(bit) <= RANK8; bit+=8){
                plus8[sq] |= 1L << bit;
            }
            
            minus8[sq] = 0;
            for(int bit=sq-8; bit >= 0; bit-=8){
                minus8[sq] |= 1L << bit;
            }
            
            plus9[sq] = 0;
            for (int bit=sq+9; Bitmap.fileNumber(bit) != 0 && Bitmap.rankNumber(bit) != 8; bit+=9){ 
                plus9[sq] |= 1L << bit;
            }

            minus9[sq] = 0;
            for (int bit=sq-9; Bitmap.fileNumber(bit) != 7 && bit >= 0; bit-=9){
                minus9[sq] |= 1L << bit;
            }

            plus7[sq] = 0;
            for(int bit=sq+7; Bitmap.fileNumber(bit) != FILE8 && Bitmap.rankNumber(bit) != 8; bit+=7){
                plus7[sq] |= 1L << bit;
            }

            minus7[sq] = 0;
            for(int bit=sq-7; Bitmap.fileNumber(bit) != 0 && bit >= 0; bit-=7){
                minus7[sq] |= 1L << bit;
            }
        }
    }


    void genBaseAttacks(){
    /*
     *  Generate RANK (base) attacks from squares a1 to h1
     *
     *  attacks[][64] is what we will use for the first rank of the chessboard.
     ****************************************************************************  
     *  Generates the rank attacks for a strictly rank-moving piece on each
     *  square of the chessboard given a bitmap (unsigned char) of the occupied
     *  squares on that rank.  It starts at a1 (square 0) and runs to 
     *  h1 (square 7). There are 256 different occupied states for a single 
     *  rank. Since the squares at both edges of the board (a-th and h-th)
     *  are are attacked independent of whether they are occupied or not, 
     *  we only need keep track of the 6 bits in between (bits 1-6 instead of 
     *  all 8 bits).  That means I only need attacks[64][2^6] instead of 
     *  rank_attacks[64][2^8]; 2^6 = 64 and 2^8 = 256 so this means I save 75%
     *  on memory storage. 
     *
     *  ALGORITHM:
     *  
     *  //Generate moves for rank-mover on each square from a1 to h1
     *   FOR each square on chessboard (square = 0 to 7 inclusive)
     *      FOR each status of rank (status = 0 to 63 inclusive)
     *	   occupied <-- status BITSHIFT-LEFT 1
     *	   left  <-- found bit/square of 1st blocking piece on left
     *	   right <-- found bit/square of 1st blocking piece on right
     *         IF (no blocking piece on right and/or left)
     *            Set the bit at the far right and/or left
     *         Set left and right bounds on moves
     *	   Set all bits in moves between left and right blocker
     *         Delete piece's current square as a valid move
     *	   Store moves
     *       ROF
     *    ROF		
     **************************************************************************
     */
	  	log.info("generating base attacks");

    	final int LEFT_END = 0x80;
    	final int RIGHT_END = 0x01;
    	
        int square;
        int status;
        
        //These shouldn't exceeded what would fit in a byte
        //We use int's because shifting in java always upcasts to an int anyway
        int piece = 0x01; //originally an unsigned char in C++
        int occ = 0;      //originally an unsigned char in C++
        int left = 0;     //originally an unsigned char in C++
        int right = 0;    //originally an unsigned char in C++
        int p = 0;        //originally an unsigned char in C++
        int attacks = 0;    //originally an unsigned char in C++
        //occ is the occupied squares on the rank BITWISE-ANDed with 01111110b
        
        for (square=0; piece != 0x100; piece <<= 1, ++square){
            // square = 0  --> piece = 0x01
            // square = 1  --> piece = 0x02
            // square = 2  --> piece = 0x04
            //   .
            //   .
            // square = 7  --> piece = 0x80
        	// exit when piece = 0x100

        	//test("406piece", piece);
        	//System.err.println("testIndex (should not be > 8): " + testIndex++ );
        	//if (square == 8) break;
        	
            for (status = 0; status < 64; ++status){
                occ = status << 1;
                left = right = 0;
                
                //Find square of 1st blocking piece on left
                p = piece << 1;
                for (; left == 0 && p != 0; p <<= 1){
                    left = occ & p;
                }
                if (left == 0){
                    left = LEFT_END;
                }
                
                //Find square of 1st blocking piece on right
                p = (byte) (piece >> 1);
                for (; right == 0 && p != 0; p >>>= 1){ //do an UNSIGNED SHIFT
                	right = occ & p;
                }
                if (right == 0)
                    right = RIGHT_END;
                
                // AT THIS POINT:
                //left is the bitmap of farthest valid move to the 
                //left (including the capture of the blocking piece).
                //right is the bitmap of farthest valid move to the 
                //right (including capture)
                
                //Set left and right bounds on moves
                attacks = left | right;
                
                //Set all bits between two blocking pieces
                while (left > right){
                    right <<= 1;
                    left >>= 1;  //should this be an unsigned shift >>>= ????
                    attacks = attacks | left | right;
                }
                //Delete piece's current square as a valid move
                attacks = attacks ^ piece;	
                
                //Store moves
                //Even though we are storing attacks in a short
                //attacks should never exceed what would fit in a byte
                //assert(!Util.bool(0xFFFFFF00 & attacks)); 
                base[square][status] = (short) attacks;
   
                //out.println(Util.formatPieceOccupiedMoves(piece, status << 1, base[square][status]));

            }//end for status
        }//end for piece
    }

    
    void genRankAttacks(){
        //Generate RANK attacks for all squares (a1 to h8)
	  	log.info("generating rank attacks");

        int shift;
        for (int sq = Bitmap.A1; sq <= Bitmap.H8; ++sq){
            shift = 8 * (sq / 8);   //int division required
            for (int st = 0; st < 64; ++st){
                //board = base[sq % 8][st];
                rank[sq][st] = ((long)base[fileNumber(sq)][st]) << shift;
            }
        }
    }

    void genFileAttacks(){
    //Generate FILE attacks for all squares (a1 to h8)
    //A graphic of the board rotated 90 degrees left.
    //The position of the square in the graphic represent the bit positions
    //within the bitboard/bitmap.  For example, the 56 square in the lower
    //left represents the 2^0 bit in the bitmap because it's in the LSB's spot.
    //Also, the 18 square represents the 2^21 bit in the bitboard/bitmap.
    //To read the 64bit int from this graphic, start in the lower left (the LSB)
    //and read it from left to right until you reach the 	is read from
    //the lower left to the upper right
//                                                       MSB
//                                                       /
//               8    7    6    5    4    3    2    1   /
//             |----|----|----|----|----|----|----|----|
//           h | 63 | 55 | 47 | 39 | 31 | 23 | 15 | 07 |
//             |----|----|----|----|----|----|----|----|
//           g | 62 | 54 | 46 | 38 | 30 | 22 | 14 | 06 |
//             |----|----|----|----|----|----|----|----|
//           f | 61 | 53 | 45 | 37 | 29 | 21 | 13 | 05 |
//             |----|----|----|----|----|----|----|----|
//           e | 60 | 52 | 44 | 36 | 28 | 20 | 12 | 04 |
//             |----|----|----|----|----|----|----|----|	
//           d | 59 | 51 | 43 | 35 | 27 | 19 | 11 | 03 |	
//             |----|----|----|----|----|----|----|----|	
//           c | 58 | 50 | 42 | 34 | 26 | 18 | 10 | 02 |	
//             |----|----|----|----|----|----|----|----|
//           b | 57 | 49 | 41 | 33 | 25 | 17 | 09 | 01 |	
//             |----|----|----|----|----|----|----|----|	
//           a | 56 | 48 | 40 | 32 | 24 | 16 | 08 | 00 |	
//             |----|----|----|----|----|----|----|----|
//            /  8    7    6    5    4    3    2    1
//           /
//        LSB
    //
    //
	  	log.info("generating file attacks");

        byte mask = 1;
        int rankNumber, maskindex;
        for (int f = A1; f <= H8; ++f){
            rankNumber = rankNumber(f);
            for (int st=0; st < 64; ++st){
                for (int i=0; i < 8; ++i){
                    maskindex = fileNumber(f) + (i * 8);
                    if((base[rankNumber][st] & (mask << i)) > 0)	//if it's set, set bit
                        file[f][st] |= 1L << maskindex;
                }
            }
        } 
    }


    void genDiagonal45DegreesRightAttacks(){
    //These are the starting squares of the diagonals
    //that are read (from left to right) from a chessboard
    //rotated 45 degrees right.  Square 7 at the bottom, 56 at the top
    //The main diagonal is a1 to h8.
    //  Diagonal #    /\
//          14       /56\
//                  /----\
//          13     /48  57\
//                /--------\
//          12   / 40 49 58 \
//          .   /------------\
//          .   Middle of  the
//          .  board  goes here
//             \---------------/
//          3   \4  13  22  31/
//               \-----------/
//          2     \5  14  23/
//                 \-------/
//          1       \6  15/
//                   \---/
//          0         \7/
//                     -
	  	log.info("generating diagonal attacks 45 deg right (a1-h8)");
    	
        //dstartsq are the corresponding diagonals as read from
        //left to right from the above chessboard rotated 45R
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
            RightDiagonalIterator rightDiagIterator = new RightDiagonalIterator(d);
        	int len = rightDiagIterator.diagonalLength();
            int maxstatus = getMaxStatus(len);
            //NOW one loop for each sq in diagonal
            for (int i=0; i < len ; i++){
                //And finally, one loop for each status
            	int sq = rightDiagIterator.next();
                for (byte st = 0; st <= maxstatus; st++){
                    R45[sq][st] = getDiagonalSet(d, base[i][st], new RightDiagonalIterator(d));
                }
            }
        }
    }


    long getA1H8diag(int diagonal, short bitmap){
    	return getDiagonalSet(diagonal, bitmap, new RightDiagonalIterator(diagonal));
    }

    void genDiagonal45DegreesLeftAttacks(){
    //These are the starting squares of the diagonals
    //that are read (from left to right) from a chessboard
    //rotated 45 degrees left.  Square 0 at the bottom, 63 at the top
    //The main diagonal is h1 to a8.
    //  Diagonal #    /\
//          14       /63\
//                  /----\
//          13     /55  62\
//                /--------\
//          12   / 47 54 61 \
//          .   /------------\
//          .   Middle of  the
//          .  board  goes here
//             \---------------/
//          3   \3  10  17  24/
//               \-----------/
//          2     \2   9  16/
//                 \-------/
//          1       \1  8 /
//                   \---/
//          0         \0/
//                     -
	  	log.info("generating diagonal attacks 45 deg left (h1-a8)");

    	//dstartsq are the corresponding diagonals as read from
        //left to right from the above chessboard graphic
        
        
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
        	LeftDiagonalIterator leftDiagIterator = new LeftDiagonalIterator(d);
        	int len = leftDiagIterator.diagonalLength();
            int maxstatus = getMaxStatus(len);
            //NOW one loop for each sq in diagonal
            int i = 0;
            while(leftDiagIterator.hasNext())
            {
            	int currentSquare = leftDiagIterator.next();
                //And finally, one loop for each status
                for (byte st = 0; st <= maxstatus; st++){
                    L45[currentSquare][st] = getDiagonalSet(d, base[i][st], new LeftDiagonalIterator(d));
                }
                i++;
            }
        }
    }

    long getH1A8diag(int diagonal, short bitmap){
    	return getDiagonalSet(diagonal, bitmap, new LeftDiagonalIterator(diagonal));
    }

    long getDiagonalSet(int diagonal, short bitmap, DiagonalIterator iterator){
    //"diagonal" is the diagonal that we want to set to "b"
    //"diagonal" is in range 0..14 inclusive 
    //"diagonal" corresponds to these diagonals: "diagonal"={diagonal squares)
//    	0={0}, 1={1,8}, 2={2,9,16},...,7={7,14,21,28,35,42,49,56},...,14={63}
    	
        //set the bits set in b to the diagonal dnum in a.
        long board = 0;
        int diagonalLength = DLEN[diagonal];
        //int bitToCheck=0;
        for (int bitToCheck=0;
        		bitToCheck < diagonalLength;
        		bitToCheck++)
        {
        	int currentSquare=iterator.next();
        	if(isBitSet(bitmap, bitToCheck)) 
                board |= (1L << currentSquare);	//set bit
        }
        //cout << "\t\tdnum: " << dnum << "\t loops: " << x << endl;
        return board;
    }

    //get the correct maximum status to count to
    //maxstatus is the diagonal minus the outer bit on each end
	private static int getMaxStatus(int len) {
		int maxstatus;
		if (len < 3)
		    maxstatus = 0;
		else
		    maxstatus = (1 << (len-2)) - 1;  //maxstatus = 2^(len-2) - 1
		return maxstatus;
	}

	private boolean isBitSet(short bitmap, int index) {
		return (bitmap & (1 << index)) != 0;
	}

    void genPawnAttacks(){
	  	log.info("generating padiagonal attacks 45 deg right (a1-h8)");

		int w = 0, b = 1;
        int i;

        //NOTE: we compute pawn attacks for all squares
        //not just the ones a pawn can be on legallly but
        //because isAttacker() and Attackers() uses them
        
        //System.out.println("*********** white pawns **************");
	  	log.info("generating white pawn attacks");
        for (i = Bitmap.A1; i <= Bitmap.H7; i++){
        	//System.out.println("i: " + i);
        	boolean isLeftEdge = fileNumber(i) == 0;
        	boolean isRightEdge = fileNumber(i+1) == 0;
            if(!isLeftEdge && !isRightEdge){ 
                //i-th square is not on the board's edge
                whitepawn[i] = pawn[w][i] = mask[i+7] | mask[i+9];
            } else {	
                //i-th square is on the edge of the board
                if( isLeftEdge ){   // left edge
                    whitepawn[i] = pawn[w][i] = mask[i+9];
                } else { // ((j+1) % 8) == 0 // right edge		
                    whitepawn[i] = pawn[w][i] = mask[i+7];
                }
            }
            //Util.DisplayBoard(whitepawn[i]);
        }

	  	log.info("generating black pawn attacks");
        for (i = Bitmap.H8; i >= Bitmap.A2; i--){
        	//System.out.println("i: " + i);
        	boolean isLeftEdge = fileNumber(i) == 0;
        	boolean isRightEdge = fileNumber(i + 1) == 0;
        	if(!isLeftEdge && !isRightEdge){ 
                //i-th square is not on the board's edge
                blackpawn[i] = pawn[b][i] = mask[i-7] | mask[i-9];
            } else {	
                //i-th square is on the edge of the board
                if(isLeftEdge){
                    blackpawn[i] = pawn[b][i] =	mask[i-7];
                } else { // right edge		
                    blackpawn[i] = pawn[b][i] = mask[i-9];
                }
            }
            //Util.DisplayBoard(blackpawn[i]);
        }
    }


    /*  The newest and fastest version of generating King and Knight 
     *  attack bitmaps.  This version is 4 times faster than previous
     *  version which used the STL classes and set_intersection().
     */
    void genKingKnightAttacks(){
        // Comparison of the time (self seconds) of the two functions, 
        // _GenKingKnightAttacksBitmasks() and GenKingKnightAttacksSTL() 
        // which use bitmasking and the STL class 'set', respectively.
        //
        //Each sample counts as 0.01 seconds.
        //  %   cumulative   self              self     total           
        // time   seconds   seconds    calls  ms/call  ms/call  name
        //1.63      3.89     0.08     1000     0.08     1.92  brd::_STL()
        //0.66      3.00     0.02     1000     0.02     0.02  brd::_Bitmasks()

        //**************************************************************************
        // IMPORTANT COMMENTS START HERE!!!!
        //
        //A 5x5 grid of squares, indexed from 0 to 24, can represent the
        //squares a piece (king or knight) can move to.  The piece rests
        //on square 12 (12th bit if zero-indexed) in the 5X5 grid.  
        //
        //For a king the valid moves/bits are as follows:
        //		{6,7,8,11,13,16,17,18} = 0x00A1122A
        //
        //For a knight the valid moves/bits are as follows:
        //		{1,3,5,9,15,19,21,23} = 0x000729C0
        //
        //  The following are the sets of squares
        //that are currently on the board(meaning they don't represent squares
        //that are off-the-board.  For instance, the valid squares when a piece
        //sits on a1 is the intersection (or bitwise ANDing) of the sets
        //R[0] and C[0] which yields the following:
        //
        //                0x01FFFC00	// R[0]
        //	BITWISE-AND   0x01CE739C	// C[0]
        // ---------------------------------
        //                0x01CE7000	// 5x5 grid of valid moves  
        //
        //NOTE: The 12th bit in the king5x5 and knight5x5 represents
        //      the current square the piece is resting on.  Make sure
        //      the 12th bit in either the king5x5 or knight5x5 is always
        //      zero.
        //      This will create incorrect results, like the following:
        //      Nf3-f3
        //      Kg7-g7

	  	log.info("generating king/knight attacks");

        int king5x5		= 0x000729C0;
        int knight5x5	= 0x00A8822A;

        //Mapping from the 5x5 grid to the 8x8 chessboard represented
        //in a 64-bit bitboard (long).
        //
        //Define a set of numbers which represent the 25 squares that
        //surround the King or Knight on the regular chessboard. 
        //Add these numbers to the square the piece is resting on to
        //give the corresponding bit in the 64-bit bitboard (long).
        //
        //Mapping from the 5x5 grid to the 8x8 chessboard represented
        //in a 64-bit bitboard (long).
        //TODO: should map be final?
        int map[] = {-18,-17,-16,-15,-14,-10,-9,-8,-7,-6,-2,-1,0,
                       1,2,6,7,8,9,10,14,15,16,17,18}; //should be size 25
            
        //Define Row and Column sets (each element is a 5x5 grid bitmap)
        final int M = 8;
        int R[] = new int[M]; //int R[M] = {0};  //initialize to zeroes
        int C[] = new int[M]; //int C[M] = {0};  //initialize to zeroes
        
        //R[i] defines a mask of valid squares (not off the chessboard),
        //where i is the rank index, an element of {0,1,...,7}
        R[0] = 0x01FFFC00;	//first rank
        R[1] = 0x01FFFFE0;	//second rank
        R[6] = 0x000FFFFF;	//seventh rank
        R[7] = 0x00007FFF;	//eighth rank
        //All bits in the 5x5 grid are set if we're not on
        //any of the two outermost ranks of the chessboard.
        R[2] = R[3] = R[4] = R[5] = 0x01FFFFFF; 
        
        //C[i] defines a mask of valid squares (not off the chessboard),
        //where i is the column index, an element of {0,1,...,7}
        C[0] = 0x01CE739C;	//a-file
        C[1] = 0x01EF7BDE;	//b-file
        C[6] = 0x00F7BDEF;	//g-file
        C[7] = 0x00739CE7;	//h-file
        //All bits in the 5x5 grid are set if we're not on
        //any of the two outermost columns/files of the chessboard.
        C[2] = C[3] = C[4] = C[5] = 0x01FFFFFF;
        
        //cout << "square	valid moves relative to square\n";
        //Initialize king_attacks and knight_attacks arrays
        
        for (int i = 0; i < 8; i++){
        	for (int j = 0; j < 8; j++){
        		int sq = j + (i << 3);  //sq = linear index = j + (8 * i)
        		int K = R[i] & C[j] & king5x5;   //Possible moves for king
        		int N = R[i] & C[j] & knight5x5; //Possible moves for knight

        		//Loop through all squares in the 5x5 grid
        		//If there's a move there (a bit set) then set the corresponding
        		//bit in the bitbrd king[sq]/knight[sq], respectively.

        		for (int gridIndex = 0; gridIndex < 25; gridIndex++){
        			if ( Util.bool(K & (1 << gridIndex)) ){ 
        				king[sq] |= 1L << (sq + map[gridIndex]);
        			}
        			if ( Util.bool(N & (1 << gridIndex)) ){
        				knight[sq] |= 1L << (sq + map[gridIndex]);
        			}
        		}
        	}
        }
    }

    public String baseAttacksAsHumanReadableString(){
    	String msg =
    		"    The following attacks are generated by a rook given all\n" +
    		"possible ways that a rank is occupied by other pieces.\n" +
    		"We do this by starting a piece (rook or Queen) on A1 and\n" +
    		"moving it along the first rank one square at a time until it\n" +
    		"reaches H1. For each square the piece sits on we loop\n" +
    		"through all possible occupied 'status'es and compute what\n" +
    		"squares that rook/Queen would attack.\n" +
    		"    The status is a value [1..63] that represents a bitmap of\n" +
    		"the middle six bits (B1 through G1).  Since A1 and H1 are attacked\n" +
    		"whether there is a piece there or not we save space by ignoring those\n" +
    		"squares/bits when we are calculating how the rank is occupied. A\n" +
    		"status is converted to an occupied bitmap by shifting it left one.\n" +
    		"    In the following the piece bitmap represents the rook/Queen's placement.\n" +
    		"The occupied bitmap represents the how the rank is occupied with other pieces.\n" +
    		"The attacks bitmap shows what squares the rook/Queen attacks given the\n" +
    		"current configuration of pieces. A1 is on the left and H1 on the far right.\n" +
    		"An empty square is denoted with a '-' and an occupied or\n" +
    		"attacked square is represented with an 'X'\n\n";
    	StringBuffer sb = new StringBuffer();
    	sb.append(msg);
        for(int square = 0; square <= 7; square++){
			int pieceBitmap = 1 << square;
        	for(int status = 0; status < 64; status++){
    			int occupiedBitmap = status << 1;
    			int attacks = base[square][status];
    			sb.append(Util.formatBaseAttacks(pieceBitmap, occupiedBitmap, attacks) + "\n");
    		}
    	}
        return sb.toString().trim();
    }
}
