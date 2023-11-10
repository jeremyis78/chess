package com.jeremybrooks.chess.util;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.util.MagicsFinder.MagicEntry;

public class RookMagics extends SlidingMagics {

	public static void main(String[] args)
	{
		SlidingMagics m = new RookMagics();
		MagicEntry[] magics = m.generateMagicNumbers();
		printCodeForMagics(magics, "rook");
	}

	public RookMagics() {
		super();
		populateOccupancyMasks();
		populateOccupancyVariationsAndAttackSets();
	}

	private void populateOccupancyMasks()
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
    
    @Override
    protected void allocateOccupanciesAndAttackSets()
    {
        occupancyVariation = new long[64][4096];
        occupancyAttackSet = new long[64][4900];
    }

    @Override
    protected long createAttackSet(int bitRef, long occupancy)
    {
        long attackSet = 0L;
        int j;
        for (j=bitRef+8; j<=55 && (occupancy & (1L << j)) == 0; j+=8);
        if (j>=0 && j<=63) attackSet |= (1L << j);
        for (j=bitRef-8; j>=8 && (occupancy & (1L << j)) == 0; j-=8);
        if (j>=0 && j<=63) attackSet |= (1L << j);
        if(bitRef%8!=7) //we're not on the right edge of the board
        {
        	for (j=bitRef+1; j%8!=7 && j%8!=0 && (occupancy & (1L << j)) == 0; j++);
        	if (j>=0 && j<=63) attackSet |= (1L << j);
        }
        if(bitRef%8!=0) //we're not on the left edge of the board
        {
        	for (j=bitRef-1; j%8!=7 && j%8!=0 && j>=0 && (occupancy & (1L << j)) == 0; j--);
        	if (j>=0 && j<=63) attackSet |= (1L << j);
        }
        return attackSet;
    }

    @Override
	protected long createValidMoves(int bitRef, long occupied) {
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

	@Override
	protected int getMaxNumberOccupanciesPerSquare() {
		return 4096;
	}

}
