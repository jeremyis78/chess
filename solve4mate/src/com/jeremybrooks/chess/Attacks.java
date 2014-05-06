/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.A1;
import static com.jeremybrooks.chess.Bitmap.H8;
import static com.jeremybrooks.chess.Bitmap.fileNumber;
import static com.jeremybrooks.chess.Bitmap.rankNumber;

import org.apache.log4j.Logger;

/**
 * @author jeremy
 *
 */
public class Attacks {
	
	private static final Logger log = Logger.getLogger(Attacks.class);

	private static final int FILE1 = 0;
	private static final int FILE8 = 7;
	private static final int RANK8 = 7;

	short base[][] = new short[8][64]; //datatype needs to be 8bits, using short instead to avoid some problem

	//Masks have a one bit set corresponding to the square on the board.
	//The array index is the square number on the board, an element of {0,1,...,63}
	
	//TODO: These should be statically initialized (or at least constructed only once
	//and injected on the object that needs it.
	//When I can use spring to wire this all together I then won't have
	//to instantiate an instance more than once (ie in Position and MoveGenerator),
	//Then I can make just make static references to these wherever I need them.
	//The current usage of Bitmap.SQ2BIT?? for constructing these on the fly requires 
	//     1) a memory lookup
	//     2) plus a shift operation
	// so it's cheaper (just a memory lookup) to use these precomputed
	// (mask, mask90, mask45L mask45R) masks instead
	long mask[] = new long[64];
    long mask90[] = new long[64];  
	long mask45R[] = new long[64];
	long mask45L[] = new long[64];
	long king[] = new long[64];
	long knight[] = new long[64];

	//The first index is the square number and
	//the second is the status of the ray the piece is aligned on
	long rank[][] = new long[64][64];
	long file[][] = new long[64][64];
	long R45[][] = new long[64][64];
	long L45[][] = new long[64][64];
	long pawn[][] = new long[2][64];
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
      genWhitePawnAttacks();
      genBlackPawnAttacks();
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
		for(int currentSquare=Bitmap.A1; currentSquare <= Bitmap.H8; currentSquare++){
			mask[currentSquare] = m << currentSquare;
			//out.printf("0x%08X\n", mask[i]);
		}
	}
  
	void genMask90() {
		int squareIndex = 0;  //runs from 0 to 63 inclusive
		for(int lastSquareOnFile=Bitmap.A8; 
				lastSquareOnFile <= Bitmap.H8;
				lastSquareOnFile++)
		{
			for(int bitToSet=lastSquareOnFile; 
					bitToSet >= 0;
					bitToSet-=8)
			{
				mask90[squareIndex] = 1L << bitToSet;
    			squareIndex++;
		    }
		}
	}

	void genMask45DegreesRight(){
	  	log.info("generating single-bit bitmasks rotated 45 deg right");

    	int squareIndex = 0;
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
        	DiagonalIterator rightDiagIterator = new RightDiagonalIterator(d);
            while(rightDiagIterator.hasNext())
            {
            	int bitToSet = rightDiagIterator.next();
    			mask45R[squareIndex] = Bitmap.withOneBitSet(bitToSet);
    			squareIndex++;
            }
        }
    }
    
    void genMask45DegreesLeft(){
	  	log.info("generating single-bit bitmasks rotated 45 deg left");

    	int squareIndex = 0;
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
        	LeftDiagonalIterator leftDiagIterator = new LeftDiagonalIterator(d);
            while(leftDiagIterator.hasNext())
            {
            	int bitToSet = leftDiagIterator.next();
    			mask45L[squareIndex] = Bitmap.withOneBitSet(bitToSet);
    			squareIndex++;
            }
        }
    }
    
    void genMasksPlusMinus(){
	  	log.info("generating plus/minus masks");

        //For each square, generate the masks plusN and minusN where N is {1,7,8,9}
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
        
        //These shouldn't exceeded what would fit in a byte
        //We use int's because shifting in java always upcasts to an int anyway
        int piece = 0x01; //originally an unsigned char in C++
        int occupied = 0;      //originally an unsigned char in C++
        //occ is the occupied squares on the rank BITWISE-ANDed with 01111110b
        
        for (int square=0; piece != 0x100; piece <<= 1, ++square){
            // square = 0  --> piece = 0x01
            // square = 1  --> piece = 0x02
            // square = 2  --> piece = 0x04
            //   .
            //   .
            // square = 7  --> piece = 0x80
        	// exit when piece = 0x100
        	
        	//C5 commented code
            for (int occupationCombination = 0; //G19
            		occupationCombination < 64;
            		occupationCombination++)
            {
                occupied = occupationCombination << 1;
                int leftBlocker = firstLeftSideBlockingPiece(piece, occupied);
                int rightBlocker = firstRightSideBlockingPiece(piece, occupied);
                int attacks = attacksIncludingBlockers(piece, leftBlocker, rightBlocker);	
                base[square][occupationCombination] = (short) attacks;
            }
        }
    }

	private static int attacksIncludingBlockers(int piece, int leftBlocker, int rightBlocker) {
		// 1. attack the blockers
		// 2. attack all bits between blockers 
		// 3. exclude the piece doing the attacking
		int attacks = leftBlocker | rightBlocker; 
		while (leftBlocker > rightBlocker){
		    rightBlocker <<= 1;
		    leftBlocker >>= 1;  //should this be an unsigned shift >>>= ????
		    attacks |= leftBlocker | rightBlocker;
		}
		attacks = attacks ^ piece; //exclude the attacker
		return attacks;
	}

	private static int firstRightSideBlockingPiece(int pieceBit, int occupiedBits) {
    	final int RIGHT_END = 0x01;
		int p;
		int right = 0;
		p = (byte) (pieceBit >> 1);
		for (; right == 0 && p != 0; p >>>= 1){ //do an UNSIGNED SHIFT
			right = occupiedBits & p;
		}
		if (right == 0)
		    right = RIGHT_END;
		return right;
	}

	private static int firstLeftSideBlockingPiece(int pieceBit, int occupiedBits) {
    	final int LEFT_END = 0x80;
		int p;
		int left = 0;
		p = pieceBit << 1;
		for (; left == 0 && p != 0; p <<= 1){
		    left = occupiedBits & p;
		}
		if (left == 0){
		    left = LEFT_END;
		}
		return left;
	}

    
    void genRankAttacks(){
        //Generate RANK attacks for all squares (a1 to h8)
	  	log.info("generating rank attacks");

        int shift;
        for (int sq = Bitmap.A1; sq <= Bitmap.H8; ++sq){
            shift = 8 * (sq / 8);   //int division required
            for (int occupationCombination = 0; occupationCombination < 64; ++occupationCombination){
                //board = base[sq % 8][st];
                rank[sq][occupationCombination] = ((long)base[fileNumber(sq)][occupationCombination]) << shift;
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
            for (int occupationCombination=0; occupationCombination < 64; ++occupationCombination){
                for (int i=0; i < 8; ++i){
                    maskindex = fileNumber(f) + (i * 8);
                    if((base[rankNumber][occupationCombination] & (mask << i)) > 0)	//if it's set, set bit
                        file[f][occupationCombination] |= 1L << maskindex;
                }
            }
        } 
    }


    void genDiagonal45DegreesRightAttacks(){
	  	log.info("generating diagonal attacks 45 deg right (a1-h8)");
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
            RightDiagonalIterator rightDiagIterator = new RightDiagonalIterator(d);
        	int len = rightDiagIterator.diagonalLength();
            int maxOccupationCombination = getMaxOccupationCombination(len);
            //NOW one loop for each sq in diagonal
            for (int i=0; i < len ; i++){
                //And finally, one loop for each occupation combination
            	int sq = rightDiagIterator.next();
                for (byte occupationCombination = 0; occupationCombination <= maxOccupationCombination; occupationCombination++){
                    R45[sq][occupationCombination] = getDiagonalSet(base[i][occupationCombination], new RightDiagonalIterator(d));
                }
            }
        }
    }


    long getA1H8diag(int diagonal, short bitmap){
    	return getDiagonalSet(bitmap, new RightDiagonalIterator(diagonal));
    }

    void genDiagonal45DegreesLeftAttacks(){
	  	log.info("generating diagonal attacks 45 deg left (h1-a8)");
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
        	LeftDiagonalIterator leftDiagIterator = new LeftDiagonalIterator(d);
        	int len = leftDiagIterator.diagonalLength();
            int maxOccupationCombination = getMaxOccupationCombination(len);
            //NOW one loop for each sq in diagonal
            int i = 0;
            while(leftDiagIterator.hasNext())
            {
            	int currentSquare = leftDiagIterator.next();
                //And finally, one loop for each occupied combination
                for (byte occupationCombination = 0; occupationCombination <= maxOccupationCombination; occupationCombination++){
                    L45[currentSquare][occupationCombination] = getDiagonalSet(base[i][occupationCombination], new LeftDiagonalIterator(d));
                }
                i++;
            }
        }
    }

    long getH1A8diag(int diagonal, short bitmap){
    	return getDiagonalSet(bitmap, new LeftDiagonalIterator(diagonal));
    }

    long getDiagonalSet(short bitmap, DiagonalIterator iterator){
    //"diagonal" is the diagonal that we want to set to "b"
    //"diagonal" is in range 0..14 inclusive 
    //"diagonal" corresponds to these diagonals: "diagonal"={diagonal squares)
//    	0={0}, 1={1,8}, 2={2,9,16},...,7={7,14,21,28,35,42,49,56},...,14={63}
    	
        //set the bits set in b to the diagonal dnum in a.
        long board = 0;
        int diagonalLength = iterator.diagonalLength();
        for (int bitToCheck=0;
        		bitToCheck < diagonalLength;
        		bitToCheck++)
        {
        	int currentSquare=iterator.next();
        	if(isBitSet(bitmap, bitToCheck)) 
                board |= (1L << currentSquare);	//set bit
        }
        //C5 commented code
        return board;
    }

    //get the correct maximum status to count to
    //maxstatus is the diagonal minus the outer bit on each end
	private static int getMaxOccupationCombination(int len) {
		int maxCombinations;
		if (len < 3)
		    maxCombinations = 0;
		else
		    maxCombinations = (1 << (len-2)) - 1;  //maxstatus = 2^(len-2) - 1
		return maxCombinations;
	}

	private static boolean isBitSet(short bitmap, int index) {
		return (bitmap & (1 << index)) != 0;
	}
	
	private static boolean isBitSet(int bitmap, int index) {
		return (bitmap & (1 << index)) != 0;
	}

    void genWhitePawnAttacks(){ //G30 functions should do one thing
		int white = 0;
        int square;
        int offsetForAttackingRight = 9; //G19
        int offsetForAttackingLeft = 7; //G19

        //NOTE: we compute pawn attacks for every square
        //that is not on the eighth rank (not just the ones a pawn
        //can be on legally) because MoveGenerator.isAttacked()
        //uses it.
        
	  	log.info("generating white pawn attacks");
        for (square = Bitmap.A1; square <= Bitmap.H7; square++){
        	//Use these conditionals below to ensure we don't "go off the board" and
        	//all accesses to the mask[64] array are valid. We'll get an AIOOB if 
        	//we eliminate the redundancy and pull out descriptive local 
        	//variables ie, pawnAttacksLeft, pawnAttacksRight. (G19 again).
			if(isOnLeftEdgeOfBoard(square)){
        		whitepawn[square] = pawn[white][square] = mask[square+offsetForAttackingRight];
        	} else if (isOnRightEdgeOfBoard(square)) {		
        		whitepawn[square] = pawn[white][square] = mask[square+offsetForAttackingLeft];
        	} else {
        		whitepawn[square] = pawn[white][square] =
        				mask[square+offsetForAttackingLeft] | mask[square+offsetForAttackingRight];
        	}
        }
    }

    void genBlackPawnAttacks(){ //G30
		int black = 1;
        int square;
        int offsetForAttackingLeft = -7; //G19
        int offsetForAttackingRight = -9; //G19

        //NOTE: we compute pawn attacks for every square
        //that is not on the eighth rank (not just the ones a pawn
        //can be on legally) because MoveGenerator.isAttacked()
        //uses it.

	  	log.info("generating black pawn attacks");
        for (square = Bitmap.H8; square >= Bitmap.A2; square--){
        	//Use these conditionals below to ensure we don't "go off the board" and
        	//all accesses to the mask[64] array are valid. We'll get an AIOOB if 
        	//we eliminate the redundancy and pull out descriptive local 
        	//variables ie, pawnAttacksLeft, pawnAttacksRight. (G19 again).
        	if(isOnLeftEdgeOfBoard(square)){
        		blackpawn[square] = pawn[black][square] = mask[square + offsetForAttackingLeft];
        	} else if (isOnRightEdgeOfBoard(square)) {		
        		blackpawn[square] = pawn[black][square] = mask[square + offsetForAttackingRight];
        	} else {
        		blackpawn[square] = pawn[black][square] = 
        				mask[square + offsetForAttackingLeft] | mask[square + offsetForAttackingRight];	
        	}
        }
    }

	/** 
	 * Returns true if the square is on the a-file
	 * @param square square to check
	 */
	private boolean isOnLeftEdgeOfBoard(int square) {
		return fileNumber(square) == 0;
	}

	/** 
	 * Returns true if the square is on the h-file
	 * @param square square to check
	 */
    boolean isOnRightEdgeOfBoard(int square) {
		return fileNumber(square + 1) == 0;
	}

    void genKingKnightAttacks(){  //violates G30 by doing more than one thing, both king AND knight
        //A 5x5 grid of squares, indexed from 0 to 24, can represent the
        //squares a king or knight can move to.  The piece rests
        //on square 12 (12th bit if zero-indexed) in the 5X5 grid.  
        //
        //For a king the attacks(bits) are as follows:
        //		{6,7,8,11,13,16,17,18} = 0x00A1122A
        //
        //For a knight the attacks(bits) are as follows:
        //		{1,3,5,9,15,19,21,23} = 0x000729C0
        //
        //  The following are the sets of squares
        //that are currently on the board(meaning they don't represent squares
        //that are off-the-board.  For instance, the valid squares when a piece
        //sits on a1 is the intersection (or bitwise ANDing) of the sets
        //rowGrid[0] and columnGrid[0] which yields the following:
        //
        //                0x01FFFC00	// rowGrid[0]
        //	BITWISE-AND   0x01CE739C	// colGrid[0]
        // ---------------------------------
        //                0x01CE7000	// 5x5 grid of possible moves  
        //
        //NOTE: This method should make sure the 12th bit in either the king5x5 
    	//      or knight5x5 is always zero because you can't make a move to the 
    	//      square you're already on, i.e. Nf3-f3 or Kg7-g7

	  	log.info("generating king/knight attacks");

        int king5x5		= 0x000729C0;
        int knight5x5	= 0x00A8822A;

        //A mapping to an offset in the 64-bit bitboard that is
        //indexed by a bit in the 5x5 grid
        int squareOffset[] = {-18, -17, -16, -15, -14, //G21 (the format and naming demonstrates understanding the algorithm??)  
        		              -10,  -9,  -8,  -7,  -6,
        		               -2,  -1,   0,   1,   2,
        		                6,   7,   8,   9,  10,
        		               14,  15,  16,  17,  18}; //should be size 25
            
        //The algorithm depends on the initialized array values being zero
        final int sizeOfSet = 8;
        int rowGrid[] = new int[sizeOfSet];
        int colGrid[] = new int[sizeOfSet];
        
        //All bits in the 5x5 grid are set if we're not on
        //any of the two outermost ranks(rows) or files(columns) of the chessboard.
        rowGrid[2] = rowGrid[3] = rowGrid[4] = rowGrid[5] = 0x01FFFFFF; //third to sixth rank 
        rowGrid[0] = 0x01FFFC00;	//first rank
        rowGrid[1] = 0x01FFFFE0;	//second rank
        rowGrid[6] = 0x000FFFFF;	//seventh rank
        rowGrid[7] = 0x00007FFF;	//eighth rank
        
        colGrid[2] = colGrid[3] = colGrid[4] = colGrid[5] = 0x01FFFFFF; // c-file to f-file
        colGrid[0] = 0x01CE739C;	//a-file
        colGrid[1] = 0x01EF7BDE;	//b-file
        colGrid[6] = 0x00F7BDEF;	//g-file
        colGrid[7] = 0x00739CE7;	//h-file

        for (int square = 0; square < 64; square++)
        {
        	int rankIndex = rankNumber(square); //G19
        	int fileIndex = fileNumber(square); //G19
        	int kingGrid = rowGrid[rankIndex] & colGrid[fileIndex] & king5x5;  //G19 use explanatory variables
        	int knightGrid = rowGrid[rankIndex] & colGrid[fileIndex] & knight5x5; //G19

        	for (int gridIndex = 0; gridIndex < 25; gridIndex++){
        		int bitToSet = square + squareOffset[gridIndex]; //G19
				if (isBitSet(kingGrid, gridIndex)){ //G28 encapsulate conditionals
        			king[square] |= Bitmap.withOneBitSet(bitToSet); //N1 choose descriptive names
        		}
        		if (isBitSet(knightGrid, gridIndex)){ //G28
        			knight[square] |= Bitmap.withOneBitSet(bitToSet); //N1
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
    	StringBuilder sb = new StringBuilder();
    	sb.append(msg);
        for(int square = 0; square <= 7; square++){
			int pieceBitmap = 1 << square;
        	for(int occupationCombination = 0; occupationCombination < 64; occupationCombination++){
    			int occupiedBitmap = occupationCombination << 1;
    			int attacks = base[square][occupationCombination];
    			sb.append(Util.formatBaseAttacks(pieceBitmap, occupiedBitmap, attacks) + "\n");
    		}
    	}
        return sb.toString().trim();
    }
}
