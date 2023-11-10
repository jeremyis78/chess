package com.jeremybrooks.chess.util;

import java.util.Random;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Square;

/**
 * <pre>
 * Generates/finds magic numbers and moves for sliding piece attacks.
 * 
 * Supports generation of rook/bishop magic numbers and moves in two ways:
 * 1) create an instance and use generateXMagics(), and generateXMoves(magics) 
 *    to dynamically create the rook/bishop magics/moves in client code 
 * 
 * 2) use main() to print out the magics array code generation, paste the
 * generated code in client code, then use generateXMoves(magics) to generate
 * the move database. Can be used in the future for finding more optimal magics.
 * 
 * TODO: we can test to see if reductions in the size of the magics arrays
 * (ie, denser tables) improves performance.
 * </pre> 
 * @author jeremy
 *
 */
public class MagicsFinder {

	public static class MagicEntry extends Magic {
		public long numAttempts;
		public long seed;

		public MagicEntry(long magicNumber, int shift, long occupiedMask, long numAttempts, long seed) {
			super(magicNumber, shift, occupiedMask);
			this.numAttempts = numAttempts;
			this.seed = seed;
		}
		
		public Magic toMagic()
		{
			return new Magic(number, shift, occupiedMask);
		}
	}
	
	static final boolean GENERATE_FOR_ROOK   = true;
	static final boolean GENERATE_FOR_BISHOP = false;
	
	
	private Magic[] rookMagics		    = new MagicEntry[64];
	private static long[][] rookMovesDB = new long[64][4096]; //can be initialized dynamically by checking the max(maxIndex) used
//
	private Magic[] bishopMagics          = new MagicEntry[64];
	private static long[][] bishopMovesDB = new long[64][512]; //can be initialized dynamically by checking the max(maxIndex) used
	
	static long rookOccupancyMasks[]       = new long[64];
    static long rookOccupancyVariation[][] = new long[64][4096];
	static long rookOccupancyAttackSet[][] = new long[64][4900];
    
	static long bishOccupancyMasks[]       = new long[64];
	static long bishOccupancyVariation[][] = new long[64][512];
	static long bishOccupancyAttackSet[][] = new long[64][1428];


	static {
		rookOccupancyMasks = generateRookOccupancyMasks();
		bishOccupancyMasks = generateBishopOccupancyMasks();
	}

	private boolean generatedForRook;
	
	
	public MagicsFinder() {
		super();
	}
	
	public void initialize()
	{
		//MUST be generated in this order (e.g. call xMagics before xMoves)
		
		//rook magics and moves in sequence
		rookMagics  = generateRookMagics();
		rookMovesDB = generateRookMoves(rookMagics);
		
		//bishop magics and moves in sequence
		bishopMagics  = generateBishopMagics();
		bishopMovesDB = generateBishopMoves(bishopMagics);
	}

	
	public Magic[] generateRookMagics() {
		//convert to external API
		Magic[] magics = new Magic[64];
		populateOccupancyVariationsBySquareAndVariationIndex(GENERATE_FOR_ROOK);
		MagicEntry[] magicEntries = generateMagicNumbers(GENERATE_FOR_ROOK);
		for(int bit=0; bit<=63; bit++)
		{
			magics[bit] = magicEntries[bit].toMagic();
		}
		generatedForRook = true;
		return magics;
	}
	
	public long[][] generateRookMoves(Magic[] rookMagics) {
		if(!generatedForRook)
			throw new IllegalStateException("must call generateRookMagics() first");
		return generateMoveDatabase(rookMagics, GENERATE_FOR_ROOK);
	}

	public Magic[] generateBishopMagics() {
		//convert to external API
		Magic[] magics = new Magic[64];
		populateOccupancyVariationsBySquareAndVariationIndex(GENERATE_FOR_BISHOP);
		MagicEntry[] magicEntries = generateMagicNumbers(GENERATE_FOR_BISHOP);
		for(int bit=0; bit<=63; bit++)
		{
			magics[bit] = magicEntries[bit].toMagic();
		}
		generatedForRook = false;
		return magics;
	}
	
	public long[][] generateBishopMoves(Magic[] bishopMagics) {
		if(generatedForRook)
			throw new IllegalStateException("must call generateBishopMagics() first");
		return generateMoveDatabase(bishopMagics, GENERATE_FOR_BISHOP);
	}


	private static void printCodeForMagics(MagicEntry[] magics, boolean isRook)
	{
		int maxBitsUsed = 0;
		String pieceName = isRook?"rook":"bishop";
		Object[] args = {pieceName, "Bits/Used", "Seed", "NumTrialsToFindMagicNumber"};
		String headerFormat = "//%s Magics initialization\t\t\t\t\t%s\t%-18s\t%s";
		String rowFormat    = "%sMagics[%d]=new Magic(0x%016xL, %d);\t// \t%-9s%s\t0x%016xL\t%d";
		System.out.println(String.format(headerFormat, args));
		int numBitsOnBoard = 64;
//		int bit = 14;
		for(int bit=0; bit<numBitsOnBoard; bit++)
		{
			MagicEntry me = magics[bit]; //isRook ? finder.rookMagics[bit] : finder.bishopMagics[bit];
			int bitsUsed = ((int) numBitsOnBoard-me.shift);
			if(bitsUsed > maxBitsUsed) 
				maxBitsUsed = bitsUsed;
			int totalBits = Long.bitCount(isRook ? rookOccupancyMasks[bit] : bishOccupancyMasks[bit]);
			boolean minimizedMagic = bitsUsed < totalBits;
			String sBitsUsed = totalBits + "/" + bitsUsed;
			System.out.println(String.format(rowFormat, 
					pieceName, bit, me.number, me.shift, sBitsUsed, (minimizedMagic?"*":""), me.seed, me.numAttempts));
		}
		int maxIndex = twoToPowerOf(maxBitsUsed);
		int arraySizeKB = 8 * numBitsOnBoard * maxIndex / 1024;
		String fmt = "//%sMoves[][] = new long[%d][%d]; //%<d = 2^%d = 2^maxBitsUsed, arraySize=%,d KB";
		System.out.println(String.format(fmt,pieceName, numBitsOnBoard,maxIndex,maxBitsUsed,arraySizeKB));
	}
	
	//http://www.afewmorelines.com/understanding-magic-bitboards-in-chess-programming/ 
	public MagicEntry[] generateMagicNumbers(/*long[][] occupancyVariations,*/ boolean isRook)
    {
//		System.out.println("generate magics for " +(isRook?"rook":"bishop"));
		populateAttackSetBySquareAndOccupancyVariation(isRook);
		MagicEntry[] magics = new MagicEntry[64];
 
        long seed = System.nanoTime();
        Random r = new Random(seed);
        long constructiveCollisions = 0L;
//        int maxSearchTimePerSquare = 5*1000*1000*1000; //10 seconds to start??
//        boolean hasMoreTime = true;
        for (int bitRef=0; bitRef<=63; bitRef++)
        {
            int bitCount = Long.bitCount(isRook ? rookOccupancyMasks[bitRef] : bishOccupancyMasks[bitRef]);
//            int maxBitsUsed = 0;
            MagicEntry magic;
            MagicEntry bestMagic = null;
//            long startTime = System.nanoTime();
//            do {
            	magic = findMagicForBit(bitRef, bitCount, r, isRook);
//            	int bitsUsed = 64-magic.shift;
//            	if(bitsUsed<maxBitsUsed)
//            	{
//            		bestMagic = magic;
//            	}
//            	seed &= 0x2890014020030e01L; //so we don't start with the same seed
//            	hasMoreTime = (System.nanoTime() - startTime) < maxSearchTimePerSquare;
//            	//System.out.println(hasMoreTime);
//            }  while(maxBitsUsed<=bitCount && hasMoreTime);
            magics[bitRef] = bestMagic != null ? bestMagic : magic;
//			if (isRook)
//                rookMagics[bitRef] = bestMagic != null ? bestMagic : magic;
//            else
//            	bishopMagics[bitRef] = bestMagic != null ? bestMagic : magic;
//			System.out.println(Square.named(bitRef) + " magic found");
        }
//        System.out.println((isRook?"Rook":"Bishop") + " seed="+seed+" constructiveCollisions="+constructiveCollisions);
        return magics;
    }
	
	private static MagicEntry findMagicForBit(int bitRef, int bitCount, Random r, /*, long[][] occupancyVariation, long[][] occupancyAttackSet, */ boolean isRook)
	{
        int variationCount = 1 << bitCount;
        long magicNumber = 0L;
        int magicShift = 64-bitCount;
        long[] occupiedForBit = isRook ? rookOccupancyVariation[bitRef] : bishOccupancyVariation[bitRef];
        long[] attackedForBit = isRook ? rookOccupancyAttackSet[bitRef] : bishOccupancyAttackSet[bitRef];

        long attackSetUsedBy[] = new long[variationCount];
        boolean indexAlreadyUsed;
        int constructiveCollisions = 0;
        int attempts = 0;
		do
        {
            magicNumber = r.nextLong() & r.nextLong() & r.nextLong(); // generate a random number with not many bits set
            for (int j=0; j<variationCount; j++) attackSetUsedBy[j] = 0; //initial condition: none have been used
            attempts++;
            indexAlreadyUsed=false;
            for (int variationIndex=0; 
            	 variationIndex<variationCount; 
            	 variationIndex++)
            {
                long occupied = occupiedForBit[variationIndex];
				long attacked = attackedForBit[variationIndex];
                //compute the index and if the index is used by an attack set that is incorrect for this occupancy variation
                //we have to start again with the next magic number
                
                //TODO: I think this is how it works (to find the overlapping attack sets, ie dense magics)
                //Find the blockers in every direction (per piece)
                //then loop over all already used attackSets looking for one with the same blockers in every direction
                //and then assign the same attackSet to the testIndex.
                
				int testIndex = (int)((occupied * magicNumber) >>> magicShift);
				boolean isIndexUsed = attackSetUsedBy[testIndex] != 0;
				boolean usedForDifferentAttackSet = attackSetUsedBy[testIndex] != attacked;
				indexAlreadyUsed = isIndexUsed && usedForDifferentAttackSet;
				if(indexAlreadyUsed) break;
				if(isIndexUsed && !usedForDifferentAttackSet)
					constructiveCollisions++;
                attackSetUsedBy[testIndex] = attacked;
//                //This is close but not quite right
//                for(int v=0; v<variationCount; v++)
//                {
//                	long existingAttackSet = attackSetUsedBy[v];
//					if(isSameAttackSet(existingAttackSet, attackSet, bitRef, isRook))
//                	{
//                		attackSetUsedBy[testIndex] = existingAttackSet;
//                	}
//                }
            }
        }
        while (indexAlreadyUsed);
        return new MagicEntry(magicNumber, magicShift, rookOccupancyMasks[bitRef], attempts, 0);
	}

//	private static boolean isSameAttackSet(long firstSet, long secondSet, int currentBit, boolean isRook) {
//		long a = isRook ? rookBlockers(currentBit, firstSet)  : bishopBlockers(currentBit, firstSet);
//		long b = isRook ? rookBlockers(currentBit, secondSet) : bishopBlockers(currentBit, secondSet);
//		return a == b;
//	}
//
//	static long rookBlockers(int currentBit, long bitboard) {
//		long blockers = 0L;
//		int bitToSet = bitBlockerPlus8(currentBit, bitboard);
//		blockers |= isOnTheBitboard(bitToSet) ? Bitmap.withOneBitSet(bitToSet) : 0;
//		bitToSet = bitBlockerMinus8(currentBit, bitboard);
//		blockers |= isOnTheBitboard(bitToSet) ? Bitmap.withOneBitSet(bitToSet) : 0;
//		bitToSet = bitBlockerPlus1(currentBit, bitboard);
//		blockers |= isOnTheBitboard(bitToSet) ? Bitmap.withOneBitSet(bitToSet) : 0;
//		bitToSet = bitBlockerMinus1(currentBit, bitboard);
//		blockers |= isOnTheBitboard(bitToSet) ? Bitmap.withOneBitSet(bitToSet) : 0;
//		return blockers;
//	}
//
//	static long bishopBlockers(int currentBit, long bitboard) {
//		long blockers = 0L;
//		int bitToSet = bitBlockerPlus9(currentBit, bitboard);
//		blockers |= isOnTheBitboard(bitToSet) ? Bitmap.withOneBitSet(bitToSet) : 0;
//		bitToSet = bitBlockerMinus9(currentBit, bitboard);
//		blockers |= isOnTheBitboard(bitToSet) ? Bitmap.withOneBitSet(bitToSet) : 0;
//		bitToSet = bitBlockerPlus7(currentBit, bitboard);
//		blockers |= isOnTheBitboard(bitToSet) ? Bitmap.withOneBitSet(bitToSet) : 0;
//		bitToSet = bitBlockerMinus7(currentBit, bitboard);
//		blockers |= isOnTheBitboard(bitToSet) ? Bitmap.withOneBitSet(bitToSet) : 0;
//		return blockers;
//	}

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
    
    public static void populateOccupancyVariationsBySquareAndVariationIndex(boolean isRook)
    {
//    	long[][] occupancyVariation;
//		if(isRook) occupancyVariation = new long[64][4096];
//    	else       occupancyVariation = new long[64][512];

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
            for (int variationIndex=0; 
            	 variationIndex<variationCount;
            	 variationIndex++)
            {
                // find bits set in index "variationIndex" and map them to bits in the 64 bit "occupySet"
            	long occupied = 0L;
                setBitsInVariationNo = toArrayOfBitsSetIn(variationIndex); // an array of integers showing which bits are set
				for (int j=0; setBitsInVariationNo[j] != Bitmap.NOSQUARE; j++)
                {
					int setBitInVariationNo = setBitsInVariationNo[j];
                    occupied |= Bitmap.withOneBitSet(setBitsInMask[setBitInVariationNo]);
                }
//				long occupiedVariation = occupancyVariation[bitRef][variationIndex];
				if(isRook) rookOccupancyVariation[bitRef][variationIndex] = occupied;
				else       bishOccupancyVariation[bitRef][variationIndex] = occupied;
//				System.out.println(String.format(" %d/%d=%d or %<#016x", 
//						bitRef, variationIndex, occupiedVariation));// + "  "+Util.);
//				LongDisplayer disp = new LongDisplayer();
//				disp.setLong(occupiedVariation);
//				System.out.println(disp.formatBoard());
            }
        }
//        return occupancyVariation;
    }

    public static void populateAttackSetBySquareAndOccupancyVariation(boolean isRook /*, long[][] occupancyVariation*/)
    {
//    	long[][] occupancyAttackSet;
//		if(isRook) occupancyAttackSet = new long[64][4900];
//    	else       occupancyAttackSet = new long[64][1428];
        long mask;
        int variationCount;
        int bitCount[] = new int[64];
 
        for (int bitRef=0; bitRef<=63; bitRef++)
        {
            mask = isRook ? rookOccupancyMasks[bitRef] : bishOccupancyMasks[bitRef];
            bitCount[bitRef] = Long.bitCount(mask);
            variationCount = (int)(1L << bitCount[bitRef]);
//            System.out.println((isRook?"R@":"B@")+Square.named(bitRef)+": bitCount(mask)="+bitCount[bitRef]+ " variationCnt="+variationCount);
            for (int variationNo=0; variationNo<variationCount; variationNo++) //i={0, 1, 2, 3}
            {
				long occupiedVariation = isRook ? rookOccupancyVariation[bitRef][variationNo]
												: bishOccupancyVariation[bitRef][variationNo];
				long attackSet = 0L;
				//generate the attackSets, finding the first blocker in each direction
				
				//sample code for h1=0, a8=63, NOT my bit-index mapping
				int bitToSet;
                if (isRook)
                {
                    bitToSet = bitBlockerPlus8(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	attackSet |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = bitBlockerMinus8(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	attackSet |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = bitBlockerPlus1(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	attackSet |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = bitBlockerMinus1(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	attackSet |= Bitmap.withOneBitSet(bitToSet);
                }
                else
                {
                    bitToSet = bitBlockerPlus9(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	attackSet |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = bitBlockerMinus9(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	attackSet |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = bitBlockerPlus7(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	attackSet |= Bitmap.withOneBitSet(bitToSet);
                    bitToSet = bitBlockerMinus7(bitRef, occupiedVariation);
                    if (isOnTheBitboard(bitToSet)) 
                    	attackSet |= Bitmap.withOneBitSet(bitToSet);
                }
                LongDisplayer d = new LongDisplayer();
                d.setLong(attackSet);
                String board = d.formatBoard();
                if(isRook) rookOccupancyAttackSet[bitRef][variationNo] = attackSet;
                else       bishOccupancyAttackSet[bitRef][variationNo] = attackSet;
            }
        }
//        return occupancyAttackSet;
    }

	private static boolean isOnTheBitboard(int j) {
		return j>=0 && j<=63;
	}

	private static int bitBlockerMinus7(int startBit, long occupied) {
		int j;
		for (j=startBit-7; j%8!=7 && j%8!=0 && j>=8 && (occupied & (1<<j)) == 0; j-=7);
		return j;
	}

	private static int bitBlockerPlus7(int startBit, long occupied) {
		int j;
		for (j=startBit+7; j%8!=7 && j%8!=0 && j<=55 && (occupied & (1<<j)) == 0; j+=7);
		return j;
	}

	private static int bitBlockerMinus9(int startBit, long occupied) {
		int j;
		for (j=startBit-9; j%8!=7 && j%8!=0 && j>=8 && (occupied & (1<<j)) == 0; j-=9);
		return j;
	}

	private static int bitBlockerPlus9(int startBit, long occupied) {
		int j;
		for (j=startBit+9; j%8!=7 && j%8!=0 && j<=55 && (occupied & (1<<j)) == 0; j+=9);
		return j;
	}

	private static int bitBlockerMinus1(int startBit, long occupied) {
		int j;
		for (j=startBit-1; j%8!=7 && j%8!=0 && j>=0 && (occupied & (1<<j)) == 0; j--);
		return j;
	}

	private static int bitBlockerPlus1(int startBit, long occupied) {
		int j;
		for (j=startBit+1; j%8!=7 && j%8!=0 && (occupied & (1<<j)) == 0; j++);
		return j;
	}

	private static int bitBlockerMinus8(int startBit, long occupied) {
		int j;
		for (j=startBit-8; j>=8 && (occupied & (1<<j)) == 0; j-=8);
		return j;
	}

	private static int bitBlockerPlus8(int startBit, long occupied) {
		int j;
		for (j=startBit+8; j<=55 && (occupied & (1<<j)) == 0; j+=8);
		return j;
	}
    
	public long[][] generateMoveDatabase(Magic[] magics, /* long[][] occupancyVariation,*/  boolean isRook)
    {
		long[][] magicMoves = isRook ? new long[64][4096] : new long[64][512];  //2048 kB for rook, 256 kB for bishop
        long validMoves;
        LongDisplayer d = new LongDisplayer();
 
        for (int bitRef=0; bitRef<=63; bitRef++)  //goes from 0 to 63 inclusive
        {
            int bitCount = isRook ? Long.bitCount(rookOccupancyMasks[bitRef]) : Long.bitCount(bishOccupancyMasks[bitRef]);
            int variationCount = (int)(1L << bitCount);
            Magic magic = magics[bitRef]; //isRook ? rookMagics[bitRef] : bishopMagics[bitRef];
            for (int variationIndex=0; 
            	 variationIndex<variationCount; 
            	 variationIndex++)
            {
                validMoves = 0L;
//                long occupied = occupancyVariation[bitRef][variationIndex];
				long occupied = isRook ? rookOccupancyVariation[bitRef][variationIndex]
						               : bishOccupancyVariation[bitRef][variationIndex];

				if (isRook)
                {
                    validMoves |= fillBitsToBlockerPlus8(bitRef, occupied);
                    validMoves |= fillBitsToBlockerMinus8Moves(bitRef, occupied);
                    validMoves |= fillBitsToBlockerPlus1Moves(bitRef, occupied);
                    validMoves |= fillBitsToBlockerMinus1Moves(bitRef, occupied);
                    
                    d.setLong(occupied);
                    String varAndMoves = d.formatBoard();
                    d.setLong(validMoves);
                    varAndMoves += d.formatBoard();
//                    System.out.println(String.format("B@%s, %d:\n%s",Square.named(bitRef), magicIndex, varAndMoves));
                }
                else
                {
                    validMoves |= fillBitsToBlockerPlus9(bitRef, occupied);
                    validMoves |= fillBitsToBlockerMinus9(bitRef, occupied);
                    validMoves |= fillBitsToBlockerPlus7(bitRef, occupied);
                    validMoves |= fillBitsToBlockerMinus7(bitRef, occupied);

                    d.setLong(occupied);
                    String varAndMoves = d.formatBoard();
                    d.setLong(validMoves);
                    varAndMoves += d.formatBoard();
//                    System.out.println(String.format("B@%s, %d:\n%s",Square.named(bitRef), magicIndex, varAndMoves));
                }
//				System.out.println(LongDisplayer.paste(Square.named(bitRef) + " occupied"+variationIndex, occupied, "\tAttacks", validMoves));
				int magicIndex = (int)((occupied * magic.number) >>> magic.shift);
				magicMoves[bitRef][magicIndex] = validMoves;
            }
        }
        return magicMoves;
    }

	private long fillBitsToBlockerMinus7(int startBit, long occupied) {
		long validMoves = 0L;
		for (int j=startBit-7; j%8!=0 && j>=0; j-=7) { validMoves |= (1L << j); if(Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long fillBitsToBlockerPlus7(int startBit, long occupied) {
		long validMoves = 0L;
		for (int j=startBit+7; j%8!=7 && j<=63; j+=7) { validMoves |= (1L << j); if(Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long fillBitsToBlockerMinus9(int startBit, long occupied) {
		long validMoves = 0L;
		for (int j=startBit-9; j%8!=7 && j>=0; j-=9)  { validMoves |= (1L << j); if(Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long fillBitsToBlockerPlus9(int startBit, long occupied) {
		long validMoves = 0L;
		for (int j=startBit+9; j%8!=0 && j<=63; j+=9) { validMoves |= (1L << j); if(Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long fillBitsToBlockerMinus1Moves(int startBit, long occupied) {
		long validMoves = 0L;
		for (int j=startBit-1; j%8!=7 && j>=0; j--) { validMoves |= (1L << j); if (Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long fillBitsToBlockerPlus1Moves(int startBit, long occupied) {
		long validMoves = 0L;
		for (int j=startBit+1; j%8!=0; j++)         { validMoves |= (1L << j); if (Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long fillBitsToBlockerMinus8Moves(int startBit, long occupied) {
		long validMoves = 0L;
		for (int j=startBit-8; j>=0; j-=8)          { validMoves |= (1L << j); if (Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long fillBitsToBlockerPlus8(int startBit, long occupied) {
		long validMoves = 0L;
		for (int j=startBit+8; j<=63; j+=8)         { validMoves |= (1L << j); if (Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
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

	public static void main(String args[])
	{
		boolean forRook = true;
		boolean forBishop = false;
		MagicsFinder finder = new MagicsFinder();
		MagicEntry[] rookMag = finder.generateMagicNumbers(forRook);
		printCodeForMagics(rookMag, forRook);
		MagicEntry[] bishopMag = finder.generateMagicNumbers(forBishop);
		printCodeForMagics(bishopMag, forBishop);
		
//		finder.generateMoveDatabase(forRook);
	}
	

}
