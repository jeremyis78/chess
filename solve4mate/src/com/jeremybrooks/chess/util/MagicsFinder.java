package com.jeremybrooks.chess.util;

import java.util.Random;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Square;

public class MagicsFinder {

	private static class MagicEntry {
		long number;
		int shift;
		long numAttempts;
		long seed;

		public MagicEntry(long magicNumber, int shift, long numAttempts, long seed) {
			super();
			this.number = magicNumber;
			this.shift = shift;
			this.numAttempts = numAttempts;
			this.seed = seed;
		}
	}
	
	private long[][] magicMovesRook       = new long[64][4096]; //can be initialized dynamically by checking the max(maxIndex) used
	private MagicEntry[] rookMagics		  = new MagicEntry[64];

	private long[][] magicMovesBishop     = new long[64][512]; //can be initialized dynamically by checking the max(maxIndex) used
	private MagicEntry[] bishopMagics     = new MagicEntry[64];
	
    static long occupancyVariation[][] = new long[64][4096];
	static long occupancyAttackSet[][];
	static long rookOccupancyMasks[] = new long[64];
	static long bishOccupancyMasks[] = new long[64];

	static {
		rookOccupancyMasks = generateRookOccupancyMasks();
		bishOccupancyMasks = generateBishopOccupancyMasks();
	}
	
	public static void main(String args[])
	{
		boolean forRook = true;
		boolean forBishop = false;
		MagicsFinder finder = new MagicsFinder();
		finder.generateMagicNumbers(forRook);
		printCodeForMagics(finder, forRook);
		finder.generateMagicNumbers(forBishop);
		printCodeForMagics(finder, forBishop);
		
//		finder.generateMoveDatabase(forRook);
	}
	
	private static void printCodeForMagics(MagicsFinder finder, boolean isRook)
	{
		int maxBitsUsed = 0;
		String pieceName = isRook?"rook":"bishop";
		Object[] args = {pieceName, "magic", "Shift", "BitsUsed", "Seed", "NumTrialsToFindMagicNumber"};
		String headerFormat = "//%s Magics %-18s\t%s\t%s\t%-18s\t%s";
		String rowFormat    = "%sMagics[%d]=0x%016xL;\t// %d\t%-8d\t0x%016xL\t%d";
		System.out.println(String.format(headerFormat, args));//, isRook?"Rook":"Bishop", "Magics", )(isRook?"Rook":"Bishop") +" Magics:\nPlacement\tMagic\tShift\tMaxIndex\tTrials");
		int numBitsOnBoard = 64;
		for(int bit=0; bit<numBitsOnBoard; bit++)
		{
			MagicEntry me = isRook ? finder.rookMagics[bit] : finder.bishopMagics[bit];
			int bitsUsed = ((int) numBitsOnBoard-me.shift);
			if(bitsUsed > maxBitsUsed) maxBitsUsed = bitsUsed;
			System.out.println(String.format(rowFormat, 
					pieceName, bit, me.number, me.shift, bitsUsed, me.seed, me.numAttempts));
		}
		int maxIndex = twoToPowerOf(maxBitsUsed);
		int arraySizeKB = 8 * numBitsOnBoard * maxIndex / 1024;
		String fmt = "//%sMoves[][] = new long[%d][%d]; //%<d = 2^%d = 2^maxBitsUsed, arraySize=%,d KB";
		System.out.println(String.format(fmt,pieceName, numBitsOnBoard,maxIndex,maxBitsUsed,arraySizeKB));
	}
	
	public void generateMoveDatabase(boolean isRook)
    {
        long validMoves;
        int variationCount, bitCount;
        int bitRef, variationIndex, j, magicIndex;
        LongDisplayer d = new LongDisplayer();
 
        for (bitRef=1; bitRef<=63; bitRef++)  //goes from 0 to 63 inclusive
        {
            bitCount = isRook ? Long.bitCount(rookOccupancyMasks[bitRef]) : Long.bitCount(bishOccupancyMasks[bitRef]);
            variationCount = (int)(1L << bitCount);
            MagicEntry magic = isRook ? rookMagics[bitRef] : bishopMagics[bitRef];
            for (variationIndex=0; 
            	 variationIndex<variationCount; 
            	 variationIndex++)
            {
                validMoves = 0;
                long occupied = occupancyVariation[bitRef][variationIndex];
				if (isRook)
                {
                	magic = rookMagics[bitRef];
                    magicIndex = (int)((occupied * magic.number) >>> magic.shift);
                    validMoves |= populateToFirstOccupiedBitPlus8(validMoves, bitRef, occupied);
                    validMoves |= populateToFirstOccupiedBitMinus8Moves(validMoves, bitRef, occupied);
                    validMoves |= populateToFirstOccupiedBitPlus1Moves(validMoves, bitRef, occupied);
                    validMoves |= populateToFirstOccupiedBitMinus1Moves(validMoves, bitRef, occupied);
                    
                    d.setLong(occupied);
                    String varAndMoves = d.formatBoard();
                    d.setLong(validMoves);
                    varAndMoves += d.formatBoard();
                    System.out.println(String.format("B@%s, %d:\n%s",Square.named(bitRef), magicIndex, varAndMoves));
                    magicMovesRook[bitRef][magicIndex] = validMoves;
                }
                else
                {
                	magic = bishopMagics[bitRef];
                    magicIndex = (int)((occupied * magic.number) >>> magic.shift);
                    validMoves |= populateToFirstOccupiedBitPlus9(validMoves, bitRef, occupied);
                    validMoves |= populateToFirstOccupiedBitMinus9(validMoves, bitRef, occupied);
                    validMoves |= populateToFirstOccupiedBitPlus7(validMoves, bitRef, occupied);
                    validMoves |= populateToFirstOccupiedBitMinus7(validMoves, bitRef, occupied);

                    d.setLong(occupied);
                    String varAndMoves = d.formatBoard();
                    d.setLong(validMoves);
                    varAndMoves += d.formatBoard();
                    System.out.println(String.format("B@%s, %d:\n%s",Square.named(bitRef), magicIndex, varAndMoves));
                    magicMovesBishop[bitRef][magicIndex] = validMoves;
                }
            }
        }
    }

	private long populateToFirstOccupiedBitMinus7(long validMoves, int startBit, long occupied) {
		int j;
		for (j=startBit-7; j%8!=0 && j>=0; j-=7) { validMoves |= (1L << j); if(Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long populateToFirstOccupiedBitPlus7(long validMoves, int startBit, long occupied) {
		int j;
		for (j=startBit+7; j%8!=7 && j<=63; j+=7) { validMoves |= (1L << j); if(Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long populateToFirstOccupiedBitMinus9(long validMoves, int startBit, long occupied) {
		int j;
		for (j=startBit-9; j%8!=7 && j>=0; j-=9)  { validMoves |= (1L << j); if(Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long populateToFirstOccupiedBitPlus9(long validMoves, int startBit, long occupied) {
		int j;
		for (j=startBit+9; j%8!=0 && j<=63; j+=9) { validMoves |= (1L << j); if(Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long populateToFirstOccupiedBitMinus1Moves(long validMoves, int startBit, long occupied) {
		int j;
		for (j=startBit-1; j%8!=7 && j>=0; j--) { validMoves |= (1L << j); if (Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long populateToFirstOccupiedBitPlus1Moves(long validMoves, int startBit, long occupied) {
		int j;
		for (j=startBit+1; j%8!=0; j++)         { validMoves |= (1L << j); if (Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long populateToFirstOccupiedBitMinus8Moves(long validMoves, int startBit, long occupied) {
		int j;
		for (j=startBit-8; j>=0; j-=8)          { validMoves |= (1L << j); if (Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long populateToFirstOccupiedBitPlus8(long validMoves, int startBit, long occupied) {
		int j;
		for (j=startBit+8; j<=63; j+=8)         { validMoves |= (1L << j); if (Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}
	
	//http://www.afewmorelines.com/understanding-magic-bitboards-in-chess-programming/ 
	public void generateMagicNumbers(boolean isRook)
    {
		generateAttackSetBySquareAndOccupancyVariation(isRook);
        int variationIndex, j, bitRef, variationCount;
 
        long seed = System.nanoTime();
        Random r = new Random(seed);
        long magicNumber = 0;
        int testIndex;
        long occupiedVariation;
        long attackSet;
        long constructiveCollisions = 0L;
 
        for (bitRef=0; bitRef<=63; bitRef++)
        {
            int bitCount = Long.bitCount(isRook ? rookOccupancyMasks[bitRef] : bishOccupancyMasks[bitRef]);
            variationCount = 1 << bitCount;
            int magicShift = 64-bitCount;

            long usedBy[] = new long[variationCount];
            boolean indexAlreadyUsed;
            int attempts = 0;
			do
            {
                magicNumber = r.nextLong() & r.nextLong() & r.nextLong(); // generate a random number with not many bits set
                for (j=0; j<variationCount; j++) usedBy[j] = 0; //initial condition: none have been used
                attempts++;
                indexAlreadyUsed=false;
                for (variationIndex=0; 
                	 variationIndex<variationCount; 
                	 variationIndex++)
                {
                    occupiedVariation = occupancyVariation[bitRef][variationIndex];
                    attackSet = occupancyAttackSet[bitRef][variationIndex];
                    //compute the index and if the index is used by an attack set that is incorrect for this occupancy variation
                    //we have to start again with the next magic number
					testIndex = (int)((occupiedVariation * magicNumber) >>> magicShift);
					boolean isIndexUsed = usedBy[testIndex] != 0;
					boolean usedForDifferentAttackSet = usedBy[testIndex] != attackSet;
					indexAlreadyUsed = isIndexUsed && usedForDifferentAttackSet;
					if(indexAlreadyUsed) break;
					if(isIndexUsed && !usedForDifferentAttackSet)
						constructiveCollisions++;
                    usedBy[testIndex] = attackSet;
//                    if(testIndex > maxIndex) maxIndex = testIndex;
                }
            } 
            while (indexAlreadyUsed);
            MagicEntry magicEntry = new MagicEntry(magicNumber,magicShift,attempts, seed);
			if (isRook)
                rookMagics[bitRef] = magicEntry;
            else
                bishopMagics[bitRef] = magicEntry;
        }
        System.out.println((isRook?"Rook":"Bishop") + " seed="+seed+" constructiveCollisions="+constructiveCollisions);
    }
	
	private static MagicEntry generateMagicEntryForBit(int bitRef, int bitCount, Random r, long seed)
	{
        int variationCount = 1 << bitCount;
        long magicNumber = 0L;
        int magicShift = 64-bitCount;

        long usedBy[] = new long[variationCount];
        boolean indexAlreadyUsed;
        int constructiveCollisions = 0;
        int attempts = 0;
		do
        {
            magicNumber = r.nextLong() & r.nextLong() & r.nextLong(); // generate a random number with not many bits set
            for (int j=0; j<variationCount; j++) usedBy[j] = 0; //initial condition: none have been used
            attempts++;
            indexAlreadyUsed=false;
            for (int variationIndex=0; 
            	 variationIndex<variationCount; 
            	 variationIndex++)
            {
                long occupiedVariation = occupancyVariation[bitRef][variationIndex];
                long attackSet = occupancyAttackSet[bitRef][variationIndex];
                //compute the index and if the index is used by an attack set that is incorrect for this occupancy variation
                //we have to start again with the next magic number
				int testIndex = (int)((occupiedVariation * magicNumber) >>> magicShift);
				boolean isIndexUsed = usedBy[testIndex] != 0;
				boolean usedForDifferentAttackSet = usedBy[testIndex] != attackSet;
				indexAlreadyUsed = isIndexUsed && usedForDifferentAttackSet;
				if(indexAlreadyUsed) break;
				if(isIndexUsed && !usedForDifferentAttackSet)
					constructiveCollisions++;
                usedBy[testIndex] = attackSet;
//                if(testIndex > maxIndex) maxIndex = testIndex;
            }
        } 
        while (indexAlreadyUsed);
        return new MagicEntry(magicNumber,magicShift,attempts, seed);
	}

	private static long[] generateRookOccupancyMasks()
    {
    	for(int square=0; square<64; square++)
    	{
    		long rookMask = 0L;
    		//compute rank mask
    		int increment = 1;
    		int firstSquare = firstSquareOfItsRank(square) + increment; //skip the outer bits
    		int lastSquare  = lastSquareOfItsRank(square);
    		rookMask = Bitmap.populateBits(firstSquare, lastSquare, increment);
    		
    		//compute file mask (and OR it in)
			increment = 8;
			firstSquare = firstSquareOfItsFile(square) + increment;  //skip the outer bits
			lastSquare = lastSquareOfItsFile(square);
			rookMask |= Bitmap.populateBits(firstSquare, lastSquare, increment);
			
			rookMask &= ~Bitmap.withOneBitSet(square); //exclude the rook
			rookOccupancyMasks[square] = rookMask;
    	}
    	return rookOccupancyMasks;
    }

    private static long[] generateBishopOccupancyMasks()
    {
    	long outerBitsMask = 0xFF818181818181FFL;
    	//diagonal indexes (0-15) are different between right/left iterators
    	//so we need to compute each side separately while adding each into
    	//the mask.
    	//For instance, RightDiagonalIterator(0).startSquare() != LeftDiagonalIterator(0).startSquare();
        for(int d = 0; d < 15; d++){ //for each diagonal
        	long bishopMask = 0L;
            RightDiagonalIterator rightDiagIterator = new RightDiagonalIterator(d);
            int diagonalLength = rightDiagIterator.diagonalLength();
            int increment = rightDiagIterator.nextSquareOffset();
            int firstSquareOfDiagonal = rightDiagIterator.startSquare();
            while(rightDiagIterator.hasNext()) //for each square in the diagonal
            {
            	int square = rightDiagIterator.next();
				int firstSquare = firstSquareOfDiagonal;
            	int lastSquare  = firstSquareOfDiagonal + (increment * diagonalLength-1);
            	bishopMask = Bitmap.populateBits(firstSquare, lastSquare, increment);
            	bishopMask &= ~Bitmap.withOneBitSet(square); //exclude the bishop
            	bishopMask &= ~outerBitsMask;				 //exclude the outer bits on the edges
            	bishOccupancyMasks[square] |= bishopMask;     //add to the existing occupancy
            }
        }
        for(int d = 0; d < 15; d++){ //for each diagonal
        	long bishopMask = 0L;

            LeftDiagonalIterator leftDiagIterator = new LeftDiagonalIterator(d);
            int diagonalLength = leftDiagIterator.diagonalLength();
            int increment = leftDiagIterator.nextSquareOffset();
            int firstSquareOfDiagonal = leftDiagIterator.startSquare();
            while(leftDiagIterator.hasNext()) //for each square in the diagonal
            {
            	int square = leftDiagIterator.next();
				int firstSquare = firstSquareOfDiagonal;
            	int lastSquare  = firstSquareOfDiagonal + (increment * diagonalLength-1);
            	bishopMask = Bitmap.populateBits(firstSquare, lastSquare, increment);
            	bishopMask &= ~Bitmap.withOneBitSet(square); //exclude the bishop
            	bishopMask &= ~outerBitsMask;				 //exclude the outer bits on the edges
            	bishOccupancyMasks[square] |= bishopMask;     //add to the existing occupancy
            }
        }
        return bishOccupancyMasks;
    }
    
    public static void generateOccupancyVariationsBySquareAndVariationIndex(boolean isRook)
    {
        long mask;
        int variationCount;
        int[] setBitsInMask, setBitsInVariationNo;
        int bitCount[] = new int[64];
 
        for (int bitRef=0; bitRef<=63; bitRef++)
        {
            mask = isRook ? rookOccupancyMasks[bitRef] : bishOccupancyMasks[bitRef];
            setBitsInMask = toArrayOfBitsSetIn(mask);
            bitCount[bitRef] = Long.bitCount(mask); //Bitboards.countSetBits(mask);
            variationCount = (int)(1L << bitCount[bitRef]);
//            System.out.println((isRook?"R@":"B@")+Square.named(bitRef)+": bitCount(mask)="+bitCount[bitRef]+ " variationCnt="+variationCount);
            for (int variationIndex=0; variationIndex<variationCount; variationIndex++) //i={0, 1, 2, 3}
            {
                occupancyVariation[bitRef][variationIndex] = 0; 
 
                // find bits set in index "variationIndex" and map them to bits in the 64 bit "occupySet"
 
                setBitsInVariationNo = toArrayOfBitsSetIn(variationIndex); // an array of integers showing which bits are set
				for (int j=0; setBitsInVariationNo[j] != Bitmap.NOSQUARE; j++)
                {
					int setBitInVariationNo = setBitsInVariationNo[j];
                    occupancyVariation[bitRef][variationIndex] |= Bitmap.withOneBitSet(setBitsInMask[setBitInVariationNo]);
                }
				long occupiedVariation = occupancyVariation[bitRef][variationIndex];
//				System.out.println(String.format(" %d/%d=%d or %<#016x", 
//						bitRef, variationNo, occupiedVariation));// + "  "+Util.);
				LongDisplayer disp = new LongDisplayer();
//				disp.setLong(occupiedVariation);
//				System.out.println(disp.formatBoard());
				
                
            }
        }
    }

    public static void generateAttackSetBySquareAndOccupancyVariation(boolean isRook)
    {
    	boolean rook = true;
    	generateOccupancyVariationsBySquareAndVariationIndex(rook);
    	if(isRook) occupancyAttackSet = new long[64][4900];
    	else       occupancyAttackSet = new long[64][1428];
        long mask;
        int variationCount;
        int bitCount[] = new int[64];
 
        for (int bitRef=0; bitRef<=63; bitRef++)
        {
            mask = isRook ? rookOccupancyMasks[bitRef] : bishOccupancyMasks[bitRef];
            bitCount[bitRef] = Long.bitCount(mask); //Bitboards.countSetBits(mask);
            variationCount = (int)(1L << bitCount[bitRef]);
//            System.out.println((isRook?"R@":"B@")+Square.named(bitRef)+": bitCount(mask)="+bitCount[bitRef]+ " variationCnt="+variationCount);
            for (int variationNo=0; variationNo<variationCount; variationNo++) //i={0, 1, 2, 3}
            {
				long occupiedVariation = occupancyVariation[bitRef][variationNo];
				
				//generate the attackSets, finding the first blocker in each direction
				
				//sample code for h1=0, a8=63, NOT my bit-index mapping
				int bitToSet;
                if (isRook)
                {
                    bitToSet = plus8Blocker(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	occupancyAttackSet[bitRef][variationNo] |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = minus8Blocker(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	occupancyAttackSet[bitRef][variationNo] |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = plus1Blocker(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	occupancyAttackSet[bitRef][variationNo] |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = minus1Blocker(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	occupancyAttackSet[bitRef][variationNo] |= Bitmap.withOneBitSet(bitToSet);
                }
                else
                {
                    bitToSet = plus9Blocker(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	occupancyAttackSet[bitRef][variationNo] |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = minus9Blocker(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	occupancyAttackSet[bitRef][variationNo] |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = plus7Blocker(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	occupancyAttackSet[bitRef][variationNo] |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = minus7Blocker(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	occupancyAttackSet[bitRef][variationNo] |= Bitmap.withOneBitSet(bitToSet);
                }
                long attackSet = occupancyAttackSet[bitRef][variationNo];

            }
        }
    }

	private static boolean isOnTheBitboard(int j) {
		return j>=0 && j<=63;
	}

	private static int minus7Blocker(int bitRef, long occupancyVariation) {
		int j;
		for (j=bitRef-7; j%8!=7 && j%8!=0 && j>=8 && (occupancyVariation & (1<<j)) == 0; j-=7);
		return j;
	}

	private static int plus7Blocker(int bitRef, long occupancyVariation) {
		int j;
		for (j=bitRef+7; j%8!=7 && j%8!=0 && j<=55 && (occupancyVariation & (1<<j)) == 0; j+=7);
		return j;
	}

	private static int minus9Blocker(int bitRef, long occupancyVariation) {
		int j;
		for (j=bitRef-9; j%8!=7 && j%8!=0 && j>=8 && (occupancyVariation & (1<<j)) == 0; j-=9);
		return j;
	}

	private static int plus9Blocker(int bitRef, long occupancyVariation) {
		int j;
		for (j=bitRef+9; j%8!=7 && j%8!=0 && j<=55 && (occupancyVariation & (1<<j)) == 0; j+=9);
		return j;
	}

	private static int minus1Blocker(int bitRef, long occupancyVariation) {
		int j;
		for (j=bitRef-1; j%8!=7 && j%8!=0 && j>=0 && (occupancyVariation & (1<<j)) == 0; j--);
		return j;
	}

	private static int plus1Blocker(int bitRef, long occupancyVariation) {
		int j;
		for (j=bitRef+1; j%8!=7 && j%8!=0 && (occupancyVariation & (1<<j)) == 0; j++);
		return j;
	}

	private static int minus8Blocker(int bitRef, long occupancyVariation) {
		int j;
		for (j=bitRef-8; j>=8 && (occupancyVariation & (1<<j)) == 0; j-=8);
		return j;
	}

	private static int plus8Blocker(int bitRef, long occupancyVariation) {
		int j;
		for (j=bitRef+8; j<=55 && (occupancyVariation & (1<<j)) == 0; j+=8);
		return j;
	}
    
    /**
     * Return an array of the bit indexes set in mask
     * @param mask the given bits we want to use
     * @return
     */
    public static int[] toArrayOfBitsSetIn(long mask) {
		int[] bitIndexes = new int[64];
		int index = 0;
		for(int bit=0; bit<64; bit++)
		{
			if(Util.bool(mask & Bitmap.withOneBitSet(bit)))
			{
				bitIndexes[index++] = bit;
			}
					
		}
		bitIndexes[index++] = Bitmap.NOSQUARE;
		return bitIndexes;
	}

	public static int lastSquareOfItsRank(int square)
    {
    	return square + 7 - (1 * Bitmap.fileNumber(square));
    }
    
    public static int firstSquareOfItsRank(int square)
    {
    	//	s	f	7 - file(s) + 1 * (rank(s) * 1)
    	//	0	0	0 * 0 * 1 = 0
    	//	1	0	1 * 0 * 1 = 0  
    	//	2	0   2 * 0 * 1 = 0
    	//	3	0
    	//	4	0
    	//	5	0
    	//	6	0
    	//	7	0
    	//
    	//	8	8	0 * 1 * 1 = 
    	//	9	8
    	//	10	8
    	//	...	8
    	//	15	8
    	//
    	//TODO: it should be simpler than this!!!!
//    	return (Bitmap.fileNumber(square) + 7) % 7;
    	return lastSquareOfItsRank(square) - 7;
    }

    public static int lastSquareOfItsFile(int square)
    {
    	return square + (8 * (7 - Bitmap.rankNumber(square)));
    }

    public static int firstSquareOfItsFile(int square)
    {
    	return square % 8;
    }
    
    public static int twoToPowerOf(int exponent)
	{
		if(exponent == 0) return 1;
		int result = 1;
		while(exponent-- > 0) {
			result *= 2;
		}
		return result;
	}
}
