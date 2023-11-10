package com.jeremybrooks.chess.util;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.util.MagicsFinder.MagicEntry;

public class BishopMagics extends SlidingMagics {
	static final Logger log = Logger.getLogger(BishopMagics.class);

	public static void main(String[] args)
	{
		SlidingMagics m = new BishopMagics();
		MagicEntry[] magics = m.generateMagicNumbers();
		printCodeForMagics(magics, "bishop");
	}
	
	public BishopMagics() {
		super();
		populateOccupancyMasks();
		populateOccupancyVariationsAndAttackSets();
	}
	
    private void populateOccupancyMasks()
    {
    	long outerBitsMask = 0xFF818181818181FFL;
    	//diagonal indexes (0-15) are different between right/left iterators
    	//so we need to compute each side separately while adding each into
    	//the mask.
    	//For instance, RightDiagonalIterator(0).startSquare() != LeftDiagonalIterator(0).startSquare();
        for(int diagonal = 0; diagonal < 15; diagonal++){ //for each diagonal
        	long bishopMask = 0L;
            RightDiagonalIterator rightDiagIterator = new RightDiagonalIterator(diagonal);
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
            	occupancyMasks[square] |= bishopMask;     //add to the existing occupancy
            }
        }
        for(int diagonal = 0; diagonal < 15; diagonal++){ //for each diagonal
        	long bishopMask = 0L;

            LeftDiagonalIterator leftDiagIterator = new LeftDiagonalIterator(diagonal);
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
            	occupancyMasks[square] |= bishopMask;     //add to the existing occupancy
            }
        }
    }

    @Override
    protected void allocateOccupanciesAndAttackSets()
    {
        occupancyVariation = new long[64][512];
        occupancyAttackSet = new long[64][1428];
    }

    @Override
    protected long createAttackSet(int bitRef, long occupancy)
    {
        long attackSet = 0L;
        int j;
        for (j=bitRef+9; j%8!=7 && j%8!=0 && j<=55 && (occupancy & (1L << j)) == 0; j+=9);
        if (j>=0 && j<=63) attackSet |= (1L << j);
        for (j=bitRef-9; j%8!=7 && j%8!=0 && j>=8 && (occupancy & (1L << j)) == 0; j-=9);
        if (j>=0 && j<=63) attackSet |= (1L << j);
        for (j=bitRef+7; j%8!=7 && j%8!=0 && j<=55 && (occupancy & (1L << j)) == 0; j+=7);
        if (j>=0 && j<=63) attackSet |= (1L << j);
        for (j=bitRef-7; j%8!=7 && j%8!=0 && j>=8 && (occupancy & (1L << j)) == 0; j-=7);
        if (j>=0 && j<=63) attackSet |= (1L << j);
        return attackSet;
    }
    
    @Override
    protected long createValidMoves(int bitRef, long occupied) {
		long validMoves = 0L;
		validMoves |= fillBitsToBlockerPlus9(bitRef, occupied);
		validMoves |= fillBitsToBlockerMinus9(bitRef, occupied);
		validMoves |= fillBitsToBlockerPlus7(bitRef, occupied);
		validMoves |= fillBitsToBlockerMinus7(bitRef, occupied);
		return validMoves;
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

	@Override
	protected int getMaxNumberOccupanciesPerSquare() {
		return 512;
	}

}
