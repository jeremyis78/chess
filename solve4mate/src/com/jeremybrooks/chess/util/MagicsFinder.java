package com.jeremybrooks.chess.util;

import java.util.Random;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Square;

public class MagicsFinder {

	private static int  occupancyVariation[][] = new int[64][4096];
	private static long occupancyAttackSet[][];

	//http://www.afewmorelines.com/understanding-magic-bitboards-in-chess-programming/ 
//	public static void generateMagicNumbers(boolean isRook)
//    {
//        int i, j, bitRef, variationCount;
// 
//        Random r = new Random();
//        long magicNumber = 0;
//        int index;
//        long attackSet;
// 
//        for (bitRef=0; bitRef<=63; bitRef++)
//        {
//            int bitCount = Long.bitCount(isRook ? rookOccupancyMask[bitRef] : bishOccupancyMask[bitRef]);
//            variationCount = (int)(1L << bitCount);
//            boolean fail;
//            long usedBy[] = new long[(int)(1L << bitCount)];
// 
//            int attempts = 0;
// 
//            do
//            {
//                magicNumber = r.nextLong() & r.nextLong() & r.nextLong(); // generate a random number with not many bits set
//                for (j=0; j<variationCount; j++) usedBy[j] = 0;
//                attempts ++;
// 
//                for (i=0, fail=false; i<variationCount && !fail; i++)
//                {
//                    index = (int)((occupancyVariation[bitRef][i] * magicNumber) >>> (64-bitCount));
// 
//                    // fail if this index is used by an attack set that is incorrect for this occupancy variation
//                    fail = usedBy[index] != 0 && usedBy[index] != occupancyAttackSet[bitRef][i];
// 
//                    usedBy[index] = attackSet;
//                }
//            } 
//            while (fail);
// 
//            if (isRook)
//            {
//                magicNumberRook[bitRef] = magicNumber;
//                magicNumberShiftsRook[bitRef] = (64-bitCount);
//            }
//            else
//            {
//                magicNumberBishop[bitRef] = magicNumber;
//                magicNumberShiftsBishop[bitRef] = (64-bitCount);
//            }
//        }
//    }

    public static long[] generateRookOccupancyMasks()
    {
    	long rookOccupancyMask[] = new long[64];
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
			rookOccupancyMask[square] = rookMask;
    	}
    	return rookOccupancyMask;
    }

    public static long[] generateBishopOccupancyMasks()
    {
    	long bishOccupancyMask[] = new long[64];
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
            	bishOccupancyMask[square] |= bishopMask;     //add to the existing occupancy
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
            	bishOccupancyMask[square] |= bishopMask;     //add to the existing occupancy
            }
        }
        return bishOccupancyMask;
    }
    
    public static void generateOccupancyCombination(boolean isRook)
    {
    	long rookOccupancyMask[] = generateRookOccupancyMasks();
    	long bishOccupancyMask[] = generateBishopOccupancyMasks();
        long mask;
        int variationCount;
        int[] setBitsInMask, setBitsInIndex;
        int bitCount[] = new int[64];
 
        for (int bit=0; bit<=63; bit++)
        {
            mask = isRook ? rookOccupancyMask[bit] : bishOccupancyMask[bit];
            setBitsInMask = toSetBitsArray(mask);
            String bitsInMask = "";
            for(int i=0; i< setBitsInMask.length && setBitsInMask[i] != Bitmap.NOSQUARE; i++)
			{
            	bitsInMask += setBitsInMask[i]+",";
			}
            System.out.println(bitsInMask);
            bitCount[bit] = Long.bitCount(mask); //Bitboards.countSetBits(mask);
            variationCount = (int)(1L << bitCount[bit]);
            System.out.println("bitCount(mask)="+bitCount[bit]+ " variationCnt="+variationCount);
            for (int i=0; i<variationCount; i++) //i={0, 1, 2, 3}
            {
            	System.out.print("bit="+bit+" i="+i);
                occupancyVariation[bit][i] = 0; 
 
                // find bits set in index "i" and map them to bits in the 64 bit "occupancyVariation"
 
                setBitsInIndex = toSetBitsArray(i); // an array of integers showing which bits are set
				for (int j=0; setBitsInIndex[j] != Bitmap.NOSQUARE; j++)
                {
					int indexBit = setBitsInIndex[j];
                    occupancyVariation[bit][i] |= (1L << setBitsInMask[indexBit]);
                }
				int v = occupancyVariation[bit][i];
				System.out.println(" " +Square.named(bit) + " combo="+i+": " +v);// + "  "+Util.);
				
                //generate the attackSets, finding the first blocker in each direction
                
                //sample code for h1=0, a8=63, NOT my bit-index mapping
//                if (isRook)
//                {
//                    for (j=bitRef+8; j<=55 && (occupancyVariation[bitRef][i] & (1L << j)) == 0; j+=8);
//                    if (j>=0 && j<=63) occupancyAttackSet[bitRef][i] |= (1L << j);
//                    for (j=bitRef-8; j>=8 && (occupancyVariation[bitRef][i] & (1L << j)) == 0; j-=8);
//                    if (j>=0 && j<=63) occupancyAttackSet[bitRef][i] |= (1L << j);
//                    for (j=bitRef+1; j%8!=7 && j%8!=0 && (occupancyVariation[bitRef][i] & (1L << j)) == 0; j++);
//                    if (j>=0 && j<=63) occupancyAttackSet[bitRef][i] |= (1L << j);
//                    for (j=bitRef-1; j%8!=7 && j%8!=0 && j>=0 && (occupancyVariation[bitRef][i] & (1L << j)) == 0; j--);
//                    if (j>=0 && j<=63) occupancyAttackSet[bitRef][i] |= (1L << j);
//                }
//                else
//                {
//                    for (j=bitRef+9; j%8!=7 && j%8!=0 && j<=55 && (occupancyVariation[bitRef][i] & (1L << j)) == 0; j+=9);
//                    if (j>=0 && j<=63) occupancyAttackSet[bitRef][i] |= (1L << j);
//                    for (j=bitRef-9; j%8!=7 && j%8!=0 && j>=8 && (occupancyVariation[bitRef][i] & (1L << j)) == 0; j-=9);
//                    if (j>=0 && j<=63) occupancyAttackSet[bitRef][i] |= (1L << j);
//                    for (j=bitRef+7; j%8!=7 && j%8!=0 && j<=55 && (occupancyVariation[bitRef][i] & (1L << j)) == 0; j+=7);
//                    if (j>=0 && j<=63) occupancyAttackSet[bitRef][i] |= (1L << j);
//                    for (j=bitRef-7; j%8!=7 && j%8!=0 && j>=8 && (occupancyVariation[bitRef][i] & (1L << j)) == 0; j-=7);
//                    if (j>=0 && j<=63) occupancyAttackSet[bitRef][i] |= (1L << j);
//                }
            }
        }
    }
    
    /**
     * Return an array of the bit indexes set in mask
     * @param mask the given bits we want to use
     * @return
     */
    public static int[] toSetBitsArray(long mask) {
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
}
