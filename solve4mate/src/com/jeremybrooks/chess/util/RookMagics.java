package com.jeremybrooks.chess.util;

import java.util.Random;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Square;
import com.jeremybrooks.chess.util.MagicsFinder.MagicEntry;

public class RookMagics {
	private static final Logger log = Logger.getLogger(RookMagics.class);
	private static Random random = new Random();
	private static Magic[] magics   = new Magic[64];
	private static long[][] movesDatabase = new long[64][4096];
    
	static long occupancyMasks[]       = new long[64];
	static long occupancyVariation[][] = new long[64][4096];
	static long occupancyAttackSet[][] = new long[64][4900];

	static {
		populateOccupancyMasks();
		populateOccupancyVariationsAndAttackSets();
	}
	
	public RookMagics() {
		super();
	}
	
	public void generate()
	{
		//MUST be generated in this order (e.g. call xMagics before xMoves)
		magics  = generateMagics();
		movesDatabase = generateMoves(magics);
	}
	
	public long getMoves(int square, long occupiedAllPieces)
	{
		Magic magic = magics[square];
		long relevantOccupancy = occupiedAllPieces & magic.occupiedMask;
		int occupiedIndex = (int)((relevantOccupancy * magic.number) >>> magic.shift);
		return movesDatabase[square][occupiedIndex];
	}
	
	public Magic[] generateMagics() {
		//convert to external API
		Magic[] magics = new Magic[64];
		MagicEntry[] magicEntries = generateMagicNumbers();
		for(int bit=0; bit<=63; bit++)
		{
			magics[bit] = magicEntries[bit].toMagic();
			if(log.isTraceEnabled()) 
				log.trace(Square.named(bit) + ": " + magics[bit].number + " " + magics[bit].shift);
		}
		return magics;
	}
	
	public long[][] generateMoves(Magic[] magics) {
		return generateMoveDatabase(magics);
	}

	private void printCodeForMagics(MagicEntry[] magics, boolean isRook)
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
			int totalBits = Long.bitCount(me.occupiedMask);
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
	public MagicEntry[] generateMagicNumbers()
    {
		long start = System.nanoTime();
		MagicEntry[] magics = new MagicEntry[64];
 
        for (int bitRef=0; bitRef<=63; bitRef++)
        {
            long occupancyMask = occupancyMasks[bitRef];
			int bitCount = Long.bitCount(occupancyMask);
            //System.out.print(bitCount +",");
            MagicEntry magic = findMagicForBit(bitRef, bitCount, occupancyMask);
            magics[bitRef] = magic;
        }
        //System.out.println((System.nanoTime() - start) / 1000000 + " millis to find Rook magics");
        return magics;
    }
	
	private MagicEntry findMagicForBit(int bitRef, int bitCount, long occupancyMask)
	{
        int variationCount = 1 << bitCount;
        long magicNumber = 0L;
        int magicShift = 64-bitCount;
        long[] occupiedForBit = occupancyVariation[bitRef];
        long[] attackedForBit = occupancyAttackSet[bitRef];

        long attackSetUsedBy[] = new long[variationCount];
        boolean indexAlreadyUsed;
        int constructiveCollisions = 0;
        int attempts = 0;
		do
        {
            magicNumber = random.nextLong() & random.nextLong() & random.nextLong(); // generate a random number with not many bits set
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
            }
        } 
        while (indexAlreadyUsed);
        return new MagicEntry(magicNumber, magicShift, occupancyMask, attempts, 0);
	}
	
	private static void populateOccupancyMasks()
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
			occupancyMasks[square] = rookMask;
    	}
    }
    
	private static void populateOccupancyVariationsAndAttackSets()
    {
        int variationIndex, j, bitRef;
        long mask;
        int variationCount;
        int[] setBitsInMask, setBitsInIndex;
        int bitCount[] = new int[64];
 
        for (bitRef=0; bitRef<=63; bitRef++)  //0..63
        {
            mask = occupancyMasks[bitRef];
            setBitsInMask = toArrayOfBitsSetIn(mask);
            bitCount[bitRef] = Long.bitCount(mask);
            variationCount = (int)(1L << bitCount[bitRef]);
            for (variationIndex=0; variationIndex<variationCount; variationIndex++)
            {
                occupancyVariation[bitRef][variationIndex] = 0; 
 
                // find bits set in index "i" and map them to bits in the 64 bit "occupancyVariation"
 
                setBitsInIndex = toArrayOfBitsSetIn(variationIndex); // an array of integers showing which bits are set
                for (j=0; setBitsInIndex[j] != Bitmap.NOSQUARE; j++)
                {
                    occupancyVariation[bitRef][variationIndex] |= (1L << setBitsInMask[setBitsInIndex[j]]);
                }
 
                for (j=bitRef+8; j<=55 && (occupancyVariation[bitRef][variationIndex] & (1L << j)) == 0; j+=8);
                if (j>=0 && j<=63) occupancyAttackSet[bitRef][variationIndex] |= (1L << j);
                for (j=bitRef-8; j>=8 && (occupancyVariation[bitRef][variationIndex] & (1L << j)) == 0; j-=8);
                if (j>=0 && j<=63) occupancyAttackSet[bitRef][variationIndex] |= (1L << j);
                if(bitRef%8!=7) //we're not on the right edge of the board
                {
                	for (j=bitRef+1; j%8!=7 && j%8!=0 && (occupancyVariation[bitRef][variationIndex] & (1L << j)) == 0; j++);
                	if (j>=0 && j<=63) occupancyAttackSet[bitRef][variationIndex] |= (1L << j);
                }
                if(bitRef%8!=0) //we're not on the left edge of the board
                {
                	for (j=bitRef-1; j%8!=7 && j%8!=0 && j>=0 && (occupancyVariation[bitRef][variationIndex] & (1L << j)) == 0; j--);
                	if (j>=0 && j<=63) occupancyAttackSet[bitRef][variationIndex] |= (1L << j);
                }
                long occupiedVariation = occupancyVariation[bitRef][variationIndex];
                long attackSet = occupancyAttackSet[bitRef][variationIndex];
                //                    System.out.println(LongDisplayer.paste(Square.named(bitRef) + " occupied-"+variationIndex, occupiedVariation, "\tAttackSet", attackSet));
            }
        }
    }

	private static boolean isOnTheBitboard(int j) {
		return j>=0 && j<=63;
	}
    
	public long[][] generateMoveDatabase(Magic[] magics)
    {
		long[][] magicMoves = new long[64][4096];
        LongDisplayer d = new LongDisplayer();
 
        for (int bitRef=0; bitRef<=63; bitRef++)
        {
            int bitCount = Long.bitCount(occupancyMasks[bitRef]);
            int variationCount = (int)(1L << bitCount);
            Magic magic = magics[bitRef];
            for (int variationIndex=0; 
            	 variationIndex<variationCount; 
            	 variationIndex++)
            {
				long occupied = occupancyVariation[bitRef][variationIndex];
				long validMoves = createValidMoves(bitRef, occupied);
//				System.out.println(LongDisplayer.paste(Square.named(bitRef) + " occupied"+variationIndex, occupied, "\tAttacks", validMoves));
				int magicIndex = (int)((occupied * magic.number) >>> magic.shift);
				magicMoves[bitRef][magicIndex] = validMoves;
            }
        }
        return magicMoves;
    }

	private long createValidMoves(int bitRef, long occupied) {
		long validMoves = 0L;
		validMoves |= fillBitsToBlockerPlus8(bitRef, occupied);
		validMoves |= fillBitsToBlockerMinus8(bitRef, occupied);
		validMoves |= fillBitsToBlockerPlus1(bitRef, occupied);
		validMoves |= fillBitsToBlockerMinus1(bitRef, occupied);
		return validMoves;
	}

	private long fillBitsToBlockerMinus1(int startBit, long occupied) {
		long validMoves = 0L;
		for (int j=startBit-1; j%8!=7 && j>=0; j--) { validMoves |= (1L << j); if (Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long fillBitsToBlockerPlus1(int startBit, long occupied) {
		long validMoves = 0L;
		for (int j=startBit+1; j%8!=0; j++)         { validMoves |= (1L << j); if (Bitmap.isBitSet(j, occupied)) break; }
		return validMoves;
	}

	private long fillBitsToBlockerMinus8(int startBit, long occupied) {
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

}
