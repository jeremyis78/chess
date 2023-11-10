package com.jeremybrooks.chess.util;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Square;
import com.jeremybrooks.chess.util.MagicsFinder.MagicEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public abstract class SlidingMagics {
	private static final Logger log = LogManager.getLogger();
	protected static final Random random = new Random();
	protected long occupancyMasks[] = new long[64];
	protected long occupancyVariation[][];
	protected long occupancyAttackSet[][];
	private Magic[] magics;
	private long[][] movesDatabase;

	
	public SlidingMagics() {
		super();
	}

	private static boolean isOnTheBitboard(int j) {
	    return j>=0 && j<=63;
	}

	public void generate() {
		//MUST be generated in this order (e.g. call xMagics before xMoves)
		magics  = generateMagics();
		movesDatabase = generateMoves(magics);
	}

	public long getMoves(int square, long occupiedAllPieces) {
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

	public long[] getOccupancyMasks() {
		return occupancyMasks;
	}

	public long[][] getOccupancyVariation() {
		return occupancyVariation;
	}

	public MagicEntry[] generateMagicNumbers() {
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
	    //System.out.println((System.nanoTime() - start) / 1000000 + " millis to find Bishop magics");
	    return magics;
	}

	private MagicEntry findMagicForBit(int bitRef, int bitCount, long occupancyMask) {
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

	protected void populateOccupancyVariationsAndAttackSets() {
	    	allocateOccupanciesAndAttackSets();
	        int[] setBitsInMask, setBitsInIndex;
	        for (int bitRef=0; bitRef<=63; bitRef++)
	        {
	        	long mask = occupancyMasks[bitRef];
	            setBitsInMask = toArrayOfBitsSetIn(mask);
	            int maskBitCount = Long.bitCount(mask);
	            int variationCount = 1 << maskBitCount;
	            for (int variationIndex=0; variationIndex<variationCount; variationIndex++)
	            {
	                // find bits set in index "i" and map them to bits in the 64 bit "occupancyVariation"
	                setBitsInIndex = toArrayOfBitsSetIn(variationIndex); // an array of integers showing which bits are set
	                long occupancy = 0L;
	                for (int j=0; setBitsInIndex[j] != Bitmap.NOSQUARE; j++)
	                {
	                    occupancy |= (1L << setBitsInMask[setBitsInIndex[j]]);
	                }
	                occupancyVariation[bitRef][variationIndex] = occupancy;
	                occupancyAttackSet[bitRef][variationIndex] = createAttackSet(bitRef, occupancy);
	//                long occupiedVariation = occupancyVariation[bitRef][variationIndex];
	//                System.out.println(LongDisplayer.paste(Square.named(bitRef) + " occupied-"+variationIndex, occupiedVariation, "\tAttackSet", attackSet));
	            }
	        }
	    }

	protected abstract long createAttackSet(int bitRef, long occupancy);

	protected abstract void allocateOccupanciesAndAttackSets();
	
	protected abstract int getMaxNumberOccupanciesPerSquare();

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

	public long[][] generateMoveDatabase(Magic[] magics) {
		int maxOccupancyVariationsPerSquare = getMaxNumberOccupanciesPerSquare();
		long[][] magicMoves = new long[64][maxOccupancyVariationsPerSquare];
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

	protected abstract long createValidMoves(int bitRef, long occupied);

	public static void printCodeForMagics(MagicEntry[] magics, String pieceName)
	{
		int maxBitsUsed = 0;
		Object[] args = {pieceName, "Bits/Used", "NumTrialsToFindMagicNumber"};
		String headerFormat = "//%s Magics initialization\t\t\t\t\t\t\t\t%s\t%s";
		String rowFormat    = "%sMagics[%2d] = new Magic(0x%016xL, %d, 0x%016xL);\t// \t%-9s%s\t%d";
		System.out.println(String.format(headerFormat, args));
		int numBitsOnBoard = 64;
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
					pieceName, bit, me.number, me.shift, me.occupiedMask, sBitsUsed, (minimizedMagic?"*":""), me.numAttempts));
		}
		int maxIndex = twoToPowerOf(maxBitsUsed);
		int arraySizeKB = 8 * numBitsOnBoard * maxIndex / 1024;
		String arrayAllocationSizeFormat = "//%sMoves[][] = new long[%d][%d]; //%<d = 2^%d = 2^maxBitsUsed, arraySize=%,d KB";
		System.out.println(String.format(arrayAllocationSizeFormat, pieceName, numBitsOnBoard,maxIndex,maxBitsUsed,arraySizeKB));
		System.out.println("//TODO: initialize "+pieceName+"Moves using new "+pieceName+"Magics object and call generateMoves("+pieceName+"Magics)");
	}

	
	public static int lastSquareOfItsRank(int square) {
		return square + 7 - (1 * Bitmap.fileNumber(square));
	}

	public static int firstSquareOfItsRank(int square) {
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

	public static int lastSquareOfItsFile(int square) {
		return square + (8 * (7 - Bitmap.rankNumber(square)));
	}

	public static int firstSquareOfItsFile(int square) {
		return square % 8;
	}

	public static int twoToPowerOf(int exponent) {
		if(exponent == 0) return 1;
		int result = 1;
		while(exponent-- > 0) {
			result *= 2;
		}
		return result;
	}

}