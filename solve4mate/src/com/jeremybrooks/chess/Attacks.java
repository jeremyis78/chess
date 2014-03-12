/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import java.io.PrintStream;

import org.apache.log4j.Logger;

/**
 * @author jeremy
 *
 */
public class Attacks {
	
	 Logger log = Logger.getLogger(Attacks.class);
		
	private static PrintStream out = System.out;
	

	/*
	 
	 typedef unsigned char unsigned8;
	  
	 */


	//This is a constant for the diagonal length
	//To access the length of the main diagonal use dlen[7]
	//The first diagonal is the zero-th.
	static final int DLEN[] = {1,2,3,4,5,6,7,8,7,6,5,4,3,2,1};

	private static final int FILE1 = 0;
	private static final int FILE8 = 7;
	private static final int RANK8 = 7;

	/*
	 
	 unsigned8 *base[8];//base[8][64];//attacks/moves for 8 sq's and all possible statuses
	 
	 */
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


  //*************************************************************************/
  //**************************** Class brd **********************************/
  // Meant to be instantiated only once (singleton) in the chess program*/
  // Is this the proper way to create a singleton?
    //*************************************************************************/

    private static Attacks attacks;
    
    public static Attacks getInstance(){
    	if(attacks == null){
    		attacks = new Attacks();
    	}
    	return attacks;
    }
    
//    public void init(){
//        _GenAllMasks();
//        _GenBaseAttacks();
//        _GenRankAttacks();
//        _GenFileAttacks();
//        _GenD45RAttacks();
//        _GenD45LAttacks();
//        _GenPawnAttacks();
//        _GenKingKnightAttacks();
//    }
    
    public Attacks(){

  /*Debug -- displays what version this was compiled on*/
	//  	displayCompiler(); 

      // In Java these long arrays will already be init'ed to zeroes.

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
	
		//initialize mask[]
		for(i=0; i < 64; i++){
			mask[i] = m << i;
			//out.printf("0x%08X\n", mask[i]);
		}
	}
  
	void genMask90() {
		long m;
		int i;
	//		_genMask();
	
		//initialize mask90[]
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
            assert(Util.PieceCount(plus1[sq]) < 8);
            
            minus1[sq] = 0;
            for(int bit=sq-1; fileNumber(bit) != FILE8 && bit >= 0; bit--){
                minus1[sq] |= 1L << bit;
            }
            assert(Util.PieceCount(minus1[sq]) < 8);

            plus8[sq] = 0;
            for(int bit=sq+8; Bitmap.rankNumber(bit) <= RANK8; bit+=8){
                plus8[sq] |= 1L << bit;
            }
            assert(Util.PieceCount(plus8[sq]) < 8);
            
            minus8[sq] = 0;
            for(int bit=sq-8; bit >= 0; bit-=8){
                minus8[sq] |= 1L << bit;
            }
            assert(Util.PieceCount(minus8[sq]) < 8);
            
            plus9[sq] = 0;
            for (int bit=sq+9; Bitmap.fileNumber(bit) != 0 && Bitmap.rankNumber(bit) != 8; bit+=9){ 
                plus9[sq] |= 1L << bit;
            }
            assert(Util.PieceCount(plus9[sq]) < 8);

            minus9[sq] = 0;
            for (int bit=sq-9; Bitmap.fileNumber(bit) != 7 && bit >= 0; bit-=9){
                minus9[sq] |= 1L << bit;
            }
            assert(Util.PieceCount(minus9[sq]) < 8);

            plus7[sq] = 0;
            for(int bit=sq+7; Bitmap.fileNumber(bit) != FILE8 && Bitmap.rankNumber(bit) != 8; bit+=7){
                plus7[sq] |= 1L << bit;
            }
            assert(Util.PieceCount(plus7[sq]) < 8);

            minus7[sq] = 0;
            for(int bit=sq-7; Bitmap.fileNumber(bit) != 0 && bit >= 0; bit-=7){
                minus7[sq] |= 1L << bit;
            }
            assert(Util.PieceCount(minus7[sq]) < 8);

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
                assert(!Util.bool(0xFFFFFF00 & attacks)); 
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
        int dstartsq[] = {7,6,5,4,3,2,1,0,8,16,24,32,40,48,56}; //should have 15 elements
        //TODO: could replace above with (for clarity maybe??) 
        // {H1,G1,F1,E1,D1,C1,B1,A1,A2,A3,A4,A5,A6,A7,A8}
        int len, maxstatus;

        for(int d = 0; d < 15; d++){ //one loop for each diagonal
            len = DLEN[d];	//get diagonal length
            //get the correct maximum status to count to
            //maxstatus is the diagonal minus the outer bit on each end
            if (len < 3)
                maxstatus = 0;
            else
                maxstatus = (1 << (len-2)) - 1;  //maxstatus = 2^(len-2) - 1
            
            //NOW one loop for each sq in diagonal
            int i=0;		//keeps track of which attacks[?] to use
            for (int sq = dstartsq[d]; i < len ; sq += 9, i++){
                //cout << sq << " -->";
                
                //Proper rank square is "r"
                
                //And finally, one loop for each status
                for (byte st = 0; st <= maxstatus; st++){
                    //cout << int(st) << ',';
                    R45[sq][st] = getA1H8diag(d, base[i][st]);
                }
            }
        }
    }


    long getA1H8diag(int diagonal, short b){  // 1/2/2010 - b used to be a byte
    	
    //"diagonal" is the diagonal that we want to set to "b"
    //"diagonal" is in range 0..14 inclusive 
    //"diagonal" corresponds to these diagonals dnum = {diagonal squares)
//    	0={7}, 1={6,15}, 2={5,14,23},...,7={0,9,18,27,36,45,54,63},...,14={56}
    	
        int dstartsq[] = {7,6,5,4,3,2,1,0,8,16,24,32,40,48,56}; //should have 15 elements
        //TODO: could replace above with (for clarity maybe??) 
        // {H1,G1,F1,E1,D1,C1,B1,A1,A2,A3,A4,A5,A6,A7,A8}
        
        //access length of diagonal dnum by DLEN[dnum]
        //dstartsq is the diagonal starting square
        
        //set the bits set in b to the diagonal dnum in a.
        long board = 0;
        int len = DLEN[diagonal];
        byte mask = 1;
        int x=0;
        for (int d = dstartsq[diagonal];  x < len; d += 9){
        	boolean something = (b & (mask << x++)) != 0;
            if(something) 
                //board |= (long(1) << d );	//set bit
            	board |= (1L << d);
        }
        //cout << "\t\tdnum: " << dnum << "\t loops: " << x << endl;
        return board;
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
        int dstartsq[] = {0,1,2,3,4,5,6,7,15,23,31,39,47,55,63};  //should be 15 elements
        //TODO: could replace above with (for clarity maybe??) 
        // {A1,B1,C1,D1,E1,F1,G1,H1,H2,H3,H4,H5,H6,H7,H8}
        
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
            int len = DLEN[d];	//get diagonal length
            int maxstatus;		//maximum status to count to
            //get the correct maximum status to count to
            //maxstatus is the diagonal minus the outer bit on each end
            if (len < 3)
                maxstatus = 0;
            else
                maxstatus = (1 << (len-2)) - 1;  //maxstatus = 2^(len-2) - 1
            
            //NOW one loop for each sq in diagonal
            int i=0;		//keeps track of which attacks[?] to use
            for (int sq = dstartsq[d]; i < len ; sq += 7, i++){
                //cout << sq << " -->";
                
                //Proper rank square is "r"
                
                //And finally, one loop for each status
                for (byte st = 0; st <= maxstatus; st++){
                    //cout << int(st) << ',';
                    L45[sq][st] = getH1A8diag(d, base[i][st]);
                }//end st
                //cout << endl;
            }//end sq
        }//end d
    }

    long getH1A8diag(int diagonal, short b){  // 1/2/2010 b used to be a byte
    //"diagonal" is the diagonal that we want to set to "b"
    //"diagonal" is in range 0..14 inclusive 
    //"diagonal" corresponds to these diagonals: "diagonal"={diagonal squares)
//    	0={0}, 1={1,8}, 2={2,9,16},...,7={7,14,21,28,35,42,49,56},...,14={63}
    	
        int dstartsq[] = {0,1,2,3,4,5,6,7,15,23,31,39,47,55,63};  //should be 15 elements
        //TODO: could replace above with (for clarity maybe??) 
        // {A1,B1,C1,D1,E1,F1,G1,H1,H2,H3,H4,H5,H6,H7,H8}
        //access length of diagonal "diagonal" by DLEN[diagonal]
        //dstartsq is the diagonal starting square
        
        //set the bits set in b to the diagonal dnum in a.
        long board = 0;
        int len = DLEN[diagonal];
        byte mask = 1;
        int x=0;
        for (int d=dstartsq[diagonal];  x < len; d += 7){
            boolean isSet = (b & (mask << x++)) != 0;
        	if(isSet) 
                board |= (1L << d );	//set bit
        }
        //cout << "\t\tdnum: " << dnum << "\t loops: " << x << endl;
        return board;
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

    

//    public String maskAsHumanReadableString(){
//    	String msg =
//    		"The following are masks for each bit in the bitboard.\n" +
//    		"The number preceding each graphic represents the what\n" +
//    		"'number' each board should represent.  For example, mask[0]\n" +
//    		"would should represent 2^0 = 1 so a bitmap equaling 1 would\n" +
//    		"be printed -- a1 would be set (1 would set b1, ... , 63\n" +
//    		"would set h8).\n\n";
//    	StringBuffer sb = new StringBuffer();
//    	sb.append(msg);
//    	for(int i= Bitboard.A1; i <= Bitboard.H8; i++){
//    		sb.append("2^" + i + "\n");
//    		sb.append(Util.formatLongBitmapAsBoard(mask[i]) + "\n");
//    	}
//    	return sb.toString().trim();
//    }

//	public String mask90AsHumanReadableString() {
//		
//		String msg =
//			"The following are masks for each bit in the bitboard \n" + 
//			"rotated 90 degrees right (a1 becomes a8, h8 becomes h1).\n" + 
//			"So each preceding number represents the index into the\n" + 
//			"mask90[] array.  Here's a comparison with the mask[] array.\n" + 
//			"\n" + 
//			"mask[0]   -> sets the a1 bit\n" + 
//			"mask90[0] -> sets the a8 bit\n" + 
//			"\n" + 
//			"mask[1]   -> sets the b1 bit\n" + 
//			"mask90[1] -> sets the a7 bit\n" + 
//			"\n" + 
//			"and so on.\n" + 
//			"\n";
//    	StringBuffer sb = new StringBuffer();
//    	sb.append(msg);
//    	for(int i= Bitboard.A1; i <= Bitboard.H8; i++){
//    		sb.append("mask90[" + i + "]\n");
//    		sb.append(Util.formatLongBitmapAsBoard(mask90[i]) + "\n");
//    	}
//    	return sb.toString().trim();
//    }
//
//	public String mask45RAsHumanReadableString() {
//		
//		String msg =
//			"The following are masks for each bit in the bitboard \n" + 
//			"rotated 45 degrees right (TODO: A becomes B,  C becomes D).\n" + 
//			"So each preceding number represents the index into the\n" + 
//			"mask45R[] array.  Here's a comparison with the mask[] array.\n" + 
//			"\n" + 
//			"mask[0]    -> sets the X bit\n" + 
//			"mask45R[0] -> sets the X bit\n" + 
//			"\n" + 
//			"mask[1]    -> sets the X bit\n" + 
//			"mask45R[1] -> sets the X bit\n" + 
//			"\n" + 
//			"and so on.\n" + 
//			"\n";
//    	StringBuffer sb = new StringBuffer();
//    	sb.append(msg);
//    	for(int i= Bitboard.A1; i <= Bitboard.H8; i++){
//    		sb.append("mask45R[" + i + "]\n");
//    		sb.append(Util.formatLongBitmapAsBoard(mask45R[i]) + "\n");
//    	}
//    	return sb.toString().trim();
//    }

    

    public String plusMinusAttacksAsHumanReadableString(){
    	StringBuffer sb = new StringBuffer();
    	String msg = "plus1[] masks ***************\n";
    	sb.append(msg);
    	for(int i= Bitmap.A1; i <= Bitmap.H8; i++){
    		sb.append("plus1[" + i + "]\n");
    		long piece = 1L << i;
    		sb.append(Util.formatLongBitmapAsBoard(piece, plus1[i]) + "\n");
    	}
    	
    	msg = "minus1[] masks ***************\n";
    	sb.append(msg);
    	for(int i= Bitmap.A1; i <= Bitmap.H8; i++){
    		sb.append("minus1[" + i + "]\n");
    		long piece = 1L << i;
    		sb.append(Util.formatLongBitmapAsBoard(piece, minus1[i]) + "\n");
    	}

    	msg = "plus8[] masks ***************\n";
    	sb.append(msg);
    	for(int i= Bitmap.A1; i <= Bitmap.H8; i++){
    		sb.append("plus8[" + i + "]\n");
    		long piece = 1L << i;
    		sb.append(Util.formatLongBitmapAsBoard(piece, plus8[i]) + "\n");
    	}

    	msg = "minus8[] masks ***************\n";
    	sb.append(msg);
    	for(int i= Bitmap.A1; i <= Bitmap.H8; i++){
    		sb.append("minus8[" + i + "]\n");
    		long piece = 1L << i;
    		sb.append(Util.formatLongBitmapAsBoard(piece, minus8[i]) + "\n");
    	}

    	msg = "plus9[] masks ***************\n";
    	sb.append(msg);
    	for(int i= Bitmap.A1; i <= Bitmap.H8; i++){
    		sb.append("plus9[" + i + "]\n");
    		long piece = 1L << i;
    		sb.append(Util.formatLongBitmapAsBoard(piece, plus9[i]) + "\n");
    	}

    	msg = "minus9[] masks ***************\n";
    	sb.append(msg);
    	for(int i= Bitmap.A1; i <= Bitmap.H8; i++){
    		sb.append("minus9[" + i + "]\n");
    		long piece = 1L << i;
    		sb.append(Util.formatLongBitmapAsBoard(piece, minus9[i]) + "\n");
    	}
    	
    	msg = "plus7[] masks ***************\n";
    	sb.append(msg);
    	for(int i= Bitmap.A1; i <= Bitmap.H8; i++){
    		sb.append("plus7[" + i + "]\n");
    		long piece = 1L << i;
    		sb.append(Util.formatLongBitmapAsBoard(piece, plus7[i]) + "\n");
    	}

    	msg = "minus7[] masks ***************\n";
    	sb.append(msg);
    	for(int i= Bitmap.A1; i <= Bitmap.H8; i++){
    		sb.append("minus7[" + i + "]\n");
    		long piece = 1L << i;
    		sb.append(Util.formatLongBitmapAsBoard(piece, minus7[i]) + "\n");
    	}

    	return sb.toString().trim();
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

    public String rankAttacksAsHumanReadableString(){
    	String msg = "TODO: describe me\n\n";
    	StringBuffer sb = new StringBuffer();
    	sb.append(msg);
    	for (int sq = Bitmap.A1; sq <= Bitmap.H8; ++sq){
			long pieceBitmap = 1L << sq;
    		for (int st = 0; st < 64; ++st){
    			sb.append(Util.formatByteBitmap("o:", st << 1) + "\n");
    			sb.append(Util.formatLongBitmapAsBoard(pieceBitmap, rank[sq][st]) + "\n");
            }
        }
    	return sb.toString().trim();
    }

    
    public String fileAttacksAsHumanReadableString(){
    	String msg = "TODO: describe me\n\n";
    	StringBuffer sb = new StringBuffer();
    	sb.append(msg);
    	for (int sq = Bitmap.A1; sq <= Bitmap.H8; ++sq){
			long pieceBitmap = 1L << sq;
    		for (int st = 0; st < 64; ++st){
    			sb.append(Util.formatByteBitmap("o:", st << 1) + "\n");
    			sb.append(Util.formatLongBitmapAsBoard(pieceBitmap, file[sq][st]) + "\n");
            }
        }
    	return sb.toString().trim();
    }

    
    public void rankAttacksAsRealLifeScenario(){
    	Displayer d = new Displayer();
    	String msg = "TODO: describe me\n\n";
    	StringBuffer sb = new StringBuffer();
    	sb.append(msg);
    	Position position = new Position();
    	int counter = 0;
    	for (int sq = Bitmap.A1; sq <= Bitmap.H8; ++sq){
			long pieceBitmap = 1L << sq;
    		for (int st = 0; st < 64; ++st){
//    			System.out.println("Square: " + sq + " rank: " + rankNumber(sq) + " rank*8: "+(rankNumber(sq) * 8) + " occupied: " + (st << 1));
//    			System.out.println(Util.formatByteBitmap("o:", st << 1) + "\n");
//    			System.out.println(Util.formatLongBitmapAsBoard(pieceBitmap, rank[sq][st]) + "\n");
    			long occupied = (st << 1);
    			int shiftLeft = rankNumber(sq) * 8;
				long blockingPieces = occupied << shiftLeft;
    			position.setPieces(BLACK, PAWN, blockingPieces); //occupied status represented by pawns
    			position.placePiece(WHITE, ROOK, sq);
//    			System.out.println(d.formatBoard(position));
//    			System.out.println(counter++ + "(" + sq + "," + st + ")  given: " + position.getFen() + "  attacks along rank: " + Util.formatSquares(rank[sq][st]));

    			System.out.println(sq +"\t" + st + "\t" + "\""+Util.formatSquares(rank[sq][st])+"\"" + "\t#FEN: " + position.getFen());

    			position.clear();
    		}
        }
    	return; //sb.toString().trim();
    }

    public String diagonal45DegreesRightAttacksAsHumanReadableString(){
    	String msg = "TODO: describe me\n\n";
    	StringBuffer sb = new StringBuffer();
    	sb.append(msg);
//    	for (int sq = Bitboard.A1; sq <= Bitboard.H8; ++sq){
//			long pieceBitmap = 1L << sq; //Bitboard.SQ2BIT45R[sq];
//    		for (int st = 0; st < 64; ++st){
//    			sb.append(Util.formatByteBitmap("o:", st << 1) + "\n");
//    			sb.append(Util.formatLongBitmapAsBoard(pieceBitmap, R45[sq][st]) + "\n");
//            }
//        }
//    	return sb.toString().trim();
    
    
    
    
        int dstartsq[] = {7,6,5,4,3,2,1,0,8,16,24,32,40,48,56}; //should have 15 elements
        int len, maxstatus;

        for(int d = 0; d < 15; d++){ //one loop for each diagonal
            len = DLEN[d];	//get diagonal length
            if (len < 3)
                maxstatus = 0;
            else
                maxstatus = (1 << (len-2)) - 1;
            
            //NOW one loop for each sq in diagonal
            int i=0;		//keeps track of which attacks[?] to use
            for (int sq = dstartsq[d]; i < len ; sq += 9, i++){
    			long pieceBitmap = 1L << sq;
            	//And finally, one loop for each status
                for (byte st = 0; st <= maxstatus; st++){
                    //cout << int(st) << ',';
        			sb.append(Util.formatByteBitmap("o:", st << 1) + "\tstatus: " + st + "\n");
        			sb.append(Util.formatLongBitmapAsBoard(pieceBitmap, R45[sq][st]) + "\n");
                }
            }
        }
        return sb.toString();
    }
    
    public String diagonal45DegreesLeftAttacksAsHumanReadableString(){
    	String msg = "TODO: describe me\n\n";
    	StringBuffer sb = new StringBuffer();
    	sb.append(msg);
//    	for (int sq = Bitboard.A1; sq <= Bitboard.H8; ++sq){
//			long pieceBitmap = 1L << sq;
//    		for (int st = 0; st < 64; ++st){
//    			sb.append(Util.formatByteBitmap("o:", st << 1) + "\n");
//    			sb.append(Util.formatLongBitmapAsBoard(pieceBitmap, L45[sq][st]) + "\n");
//            }
//        }
        int dstartsq[] = {0,1,2,3,4,5,6,7,15,23,31,39,47,55,63};  //should be 15 elements
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
            int len = DLEN[d];	//get diagonal length
            int maxstatus;		//maximum status to count to
            //get the correct maximum status to count to
            //maxstatus is the diagonal minus the outer bit on each end
            if (len < 3)
                maxstatus = 0;
            else
                maxstatus = (1 << (len-2)) - 1;
            
            //NOW one loop for each sq in diagonal
            int i=0;		//keeps track of which attacks[?] to use
            for (int sq = dstartsq[d]; i < len ; sq += 7, i++){
    			long pieceBitmap = 1L << sq;
            	//And finally, one loop for each status
                for (byte st = 0; st <= maxstatus; st++){
        			sb.append(Util.formatByteBitmap("o:", st << 1) + "\tstatus: " + st + "\n");
        			sb.append(Util.formatLongBitmapAsBoard(pieceBitmap, L45[sq][st]) + "\n");
                }
            }
        }
    	return sb.toString().trim();
    }

    
    
//    void DisplayAll(){
//        int sq;
//        byte st; //unsigned char st;
//
///*
//        String piecenames[][] = {
//            {	"White Pawns (WP)",
//                    "White kNights (WN)",
//                    "White Bishops (WB)",
//                    "White Rooks (WR)",
//                    "White Queens (WQ)",
//                    "White King (WK)" 
//    	},
//            {	"Black Pawns (BP)",
//                    "Black kNights (BN)",
//                    "Black Bishops (BB)",
//                    "Black Rooks (BR)",
//                    "Black Queens (BQ)",
//    			"Black King (BK)"
//            }
//        };	
//
//        String allpiecenames[] = {
//            "All pieces (ALL)",
//            "All pieces rotated 90 degrees(ALL90)",
//            "All pieces rotated 45 degrees left(ALL45L)",
//            "All pieces rotated 45 degrees right(ALL45R)"
//        };
//
//        cout << "PIECE_START[][]\n";
//    	for (int c = 0; c < MAXCOLOR; c++){
//    		for (int p = 0; p < MAXPIECE; p++){
//    			cout << "**************************************\n";
//    			cout << piecenames[c][p] << endl;
//    	//		DisplayBitbrd(PIECE_START[i]);				
//    			DisplayBoard(PIECE_START[i]);				
//    		}
//    	}
//    	for (int i = 0; i < MAXALL; i++){
//    			cout << "**************************************\n";
//    			cout << piecenames[i] << endl;
//    	//		DisplayBitbrd(ALL_START[i]);				
//    			DisplayBoard(ALL_START[i]);				
//    	}
//*/
//
//    	out.print("**************************************\n");
//    	out.print("Base attacks:\n");
//        for(int square = 0; square <= 7; square++){
//    		for(int status = 0; status < 64; status++){
//    			out.printf("%d %d: %08X\n", square, status, base[square][status]);
//    		}
//    	}
//        
//    	out.print("**************************************\n");
//    	out.print("King attacks:\n");
//    	for (sq = 0; sq < 64; sq++)
//    			Util.DisplayBoard(king[sq], sq);				
//
//    	out.print("**************************************\n");
//    	out.print("Knight attacks:\n");
//    	for (sq = 0; sq < 64; sq++)
//    		Util.DisplayBoard(knight[sq], sq);				
//
//    	out.print("**************************************\n");
//    	out.print("White Pawn attacks:\n");
//    	for (sq = 0; sq < 64; sq++)
//    		Util.DisplayBoard(whitepawn[sq], sq);				
//
//    	out.print("**************************************\n");
//    	out.print("Black Pawn attacks:\n");
//    	for (sq = 0; sq < 64; sq++)
//    		Util.DisplayBoard(blackpawn[sq], sq);				
//
//        out.print("**************************************\n");
//        out.print("Rank attacks:  rank[sq][st]\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            for (st = 0; st < 64; st++){
//                out.print("rank[" + sq + "][" + (int)st + "] ");
//                out.print("\tRank's occupied status: ");
//                Util.DisplayStatus(st);
//                //	  << hex << setw(2) << setfill('0') << int(st) << endl;
//                Util.DisplayBoard(rank[sq][st], sq);				
//            }
//        }
//        out.print("**************************************\n");
//        out.print("File attacks: file[sq][st]\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            for (st = 0; st < 64; st++){
//                out.print("file[" + sq + "][" + (int)st + "] ");
//                out.print("\tFile's occupied status: ");
//                Util.DisplayStatus(st);
//                //	  << hex << setw(2) << setfill('0') << int(st) << endl;
//                Util.DisplayBoard(file[sq][st], sq);				
//            }
//        }
//        out.print("**************************************\n");
//        out.print("R45 (A1H8) attacks: R45[sq][st]\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            for (st = 0; st < 64; st++){
//                out.print("R45[" + sq + "][" + (int)st + "] ");
//                out.print("\tR45's (a1h8) occupied status: ");
//                Util.DisplayStatus(st);
//                //  << hex << setw(2) << setfill('0') << int(st) << endl;
//                Util.DisplayBoard(R45[sq][st], sq);				
//            }
//        }
//        out.print("**************************************\n");
//        out.print("L45 (H1A8) attacks: L45[sq][st]\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            for (st = 0; st < 64; st++){
//                out.print("L45[" + sq + "][" + (int)st + "] ");
//                out.print("\tL45's (h1a8) occupied status: ");
//                Util.DisplayStatus(st);
//    			//	  << hex << setw(2) << setfill('0') << int(st) << endl;
//                Util.DisplayBoard(L45[sq][st], sq);				
//            }
//        }
//
//        // Plus*
//        out.print("**************************************\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            out.print("plus1[" + sq + "]\n");
//            Util.DisplayBoard(plus1[sq], sq);
//        }
//        out.print("**************************************\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            out.print("plus7[" + sq + "]\n");
//            Util.DisplayBoard(plus7[sq], sq);
//        }
//        out.print("**************************************\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            out.print("plus8[" + sq + "]\n");
//            Util.DisplayBoard(plus8[sq], sq);
//        }
//        out.print("**************************************\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            out.print("plus9[" + sq + "]\n");
//            Util.DisplayBoard(plus9[sq], sq);
//        }
//
//        // Minus*
//        out.print("**************************************\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            out.print("minus1[" + sq + "]\n");
//            Util.DisplayBoard(minus1[sq], sq);
//        }
//        out.print("**************************************\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            out.print("minus7[" + sq + "]\n");
//            Util.DisplayBoard(minus7[sq], sq);
//        }
//        out.print("**************************************\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            out.print("minus8[" + sq + "]\n");
//            Util.DisplayBoard(minus8[sq], sq);
//        }
//        out.print("**************************************\n");
//        for (sq = Bitboard.A1; sq <= Bitboard.H8; sq++){
//            out.print("minus9[" + sq + "]\n");
//            Util.DisplayBoard(minus9[sq], sq);
//        }
//
//
//        
//
//    }

    
    public static final void main(String[] args){
    	Attacks attacks = new Attacks();
//    	out.print(attacks.baseAttacksAsHumanReadableString());
//    	System.out.println("\n**************************************");
//    	out.print(attacks.maskAsHumanReadableString());
//    	System.out.println("\n**************************************");
//    	out.print(attacks.mask90AsHumanReadableString());
    
//    	out.print(attacks.rankAttacksAsHumanReadableString());
    	//attacks.DisplayAll();
    	//System.out.print(attacks.getBaseAttacks());
    	
    	attacks.rankAttacksAsRealLifeScenario();
    }
    
}
