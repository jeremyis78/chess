package com.jeremybrooks.chess.util;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Square;

public class BishopMagicsTest {

	private static final int BISHOP_ATTACK_SETS_PER_SQUARE = 512;
	
	private static final SlidingMagics instance = new BishopMagics();
	private static Magic[] bishopMagics;
	private static long[][] bishopMoves;

	@BeforeClass
	public static void initialize()
	{
		//MUST be generated in this order (e.g. call xMagics before xMoves)
		//bishop magics and moves in sequence
		bishopMagics = instance.generateMagics();
		bishopMoves  = instance.generateMoves(bishopMagics);
	}
	
	@Test
	public void testPublicAPIForBishop()
	{
		assertEquals("needs a magic for each square", 64, bishopMagics.length);
		for(int bit=0; bit<=63; bit++)
			assertEquals("needs this many attackSets per square", 
					BISHOP_ATTACK_SETS_PER_SQUARE, bishopMoves[bit].length);
	}
	
	@Test
	public void testShiftsYieldIndexesWithinNineBitRange()
	{
		for(int bit=0; bit<=63; bit++)
		{
			Magic magic = bishopMagics[bit];
			int bitsInIndex = 64-magic.shift;
			assertTrue(bitsInIndex >= 0);
			assertTrue(bitsInIndex <= 9); //2^9 = 512
		}
	}

	@Test
	public void givenBishopOnE8AndOccupiedCombinationNo9() 
	{
		long occupied = populateSquares("b5,d7");
		long moveBits = bishopAttacks(Bitmap.E8, occupied);
		//System.out.println(LongDisplayer.paste("occ", occupied, "att", moveBits ));
		
		String actualMoves = Util.displaySquaresStr(moveBits);
		String expectedMoves = "h5 g6 d7 f7 ";
		assertEquals(expectedMoves, actualMoves);
	}

	@Test
	public void testGenerateBishopOccupancyMasks() {
		long[] masks = instance.getOccupancyMasks();
        assertTrue(0x40201008040200L==masks[ 0] && 6==Long.bitCount(masks[ 0])); //B@a1
        assertTrue(  0x402010080400L==masks[ 1] && 5==Long.bitCount(masks[ 1])); //B@b1
        assertTrue(    0x4020100a00L==masks[ 2] && 5==Long.bitCount(masks[ 2])); //B@c1
        assertTrue(      0x40221400L==masks[ 3] && 5==Long.bitCount(masks[ 3])); //B@d1
        assertTrue(       0x2442800L==masks[ 4] && 5==Long.bitCount(masks[ 4])); //B@e1
        assertTrue(     0x204085000L==masks[ 5] && 5==Long.bitCount(masks[ 5])); //B@f1
        assertTrue(   0x20408102000L==masks[ 6] && 5==Long.bitCount(masks[ 6])); //B@g1
        assertTrue( 0x2040810204000L==masks[ 7] && 6==Long.bitCount(masks[ 7])); //B@h1
        assertTrue(0x20100804020000L==masks[ 8] && 5==Long.bitCount(masks[ 8])); //B@a2
        assertTrue(0x40201008040000L==masks[ 9] && 5==Long.bitCount(masks[ 9])); //B@b2
        assertTrue(  0x4020100a0000L==masks[10] && 5==Long.bitCount(masks[10])); //B@c2
        assertTrue(    0x4022140000L==masks[11] && 5==Long.bitCount(masks[11])); //B@d2
        assertTrue(     0x244280000L==masks[12] && 5==Long.bitCount(masks[12])); //B@e2
        assertTrue(   0x20408500000L==masks[13] && 5==Long.bitCount(masks[13])); //B@f2
        assertTrue( 0x2040810200000L==masks[14] && 5==Long.bitCount(masks[14])); //B@g2
        assertTrue( 0x4081020400000L==masks[15] && 5==Long.bitCount(masks[15])); //B@h2
        assertTrue(0x10080402000200L==masks[16] && 5==Long.bitCount(masks[16])); //B@a3
        assertTrue(0x20100804000400L==masks[17] && 5==Long.bitCount(masks[17])); //B@b3
        assertTrue(0x4020100a000a00L==masks[18] && 7==Long.bitCount(masks[18])); //B@c3
        assertTrue(  0x402214001400L==masks[19] && 7==Long.bitCount(masks[19])); //B@d3
        assertTrue(   0x24428002800L==masks[20] && 7==Long.bitCount(masks[20])); //B@e3
        assertTrue( 0x2040850005000L==masks[21] && 7==Long.bitCount(masks[21])); //B@f3
        assertTrue( 0x4081020002000L==masks[22] && 5==Long.bitCount(masks[22])); //B@g3
        assertTrue( 0x8102040004000L==masks[23] && 5==Long.bitCount(masks[23])); //B@h3
        assertTrue( 0x8040200020400L==masks[24] && 5==Long.bitCount(masks[24])); //B@a4
        assertTrue(0x10080400040800L==masks[25] && 5==Long.bitCount(masks[25])); //B@b4
        assertTrue(0x20100a000a1000L==masks[26] && 7==Long.bitCount(masks[26])); //B@c4
        assertTrue(0x40221400142200L==masks[27] && 9==Long.bitCount(masks[27])); //B@d4
        assertTrue( 0x2442800284400L==masks[28] && 9==Long.bitCount(masks[28])); //B@e4
        assertTrue( 0x4085000500800L==masks[29] && 7==Long.bitCount(masks[29])); //B@f4
        assertTrue( 0x8102000201000L==masks[30] && 5==Long.bitCount(masks[30])); //B@g4
        assertTrue(0x10204000402000L==masks[31] && 5==Long.bitCount(masks[31])); //B@h4
        assertTrue( 0x4020002040800L==masks[32] && 5==Long.bitCount(masks[32])); //B@a5
        assertTrue( 0x8040004081000L==masks[33] && 5==Long.bitCount(masks[33])); //B@b5
        assertTrue(0x100a000a102000L==masks[34] && 7==Long.bitCount(masks[34])); //B@c5
        assertTrue(0x22140014224000L==masks[35] && 9==Long.bitCount(masks[35])); //B@d5
        assertTrue(0x44280028440200L==masks[36] && 9==Long.bitCount(masks[36])); //B@e5
        assertTrue( 0x8500050080400L==masks[37] && 7==Long.bitCount(masks[37])); //B@f5
        assertTrue(0x10200020100800L==masks[38] && 5==Long.bitCount(masks[38])); //B@g5
        assertTrue(0x20400040201000L==masks[39] && 5==Long.bitCount(masks[39])); //B@h5
        assertTrue( 0x2000204081000L==masks[40] && 5==Long.bitCount(masks[40])); //B@a6
        assertTrue( 0x4000408102000L==masks[41] && 5==Long.bitCount(masks[41])); //B@b6
        assertTrue( 0xa000a10204000L==masks[42] && 7==Long.bitCount(masks[42])); //B@c6
        assertTrue(0x14001422400000L==masks[43] && 7==Long.bitCount(masks[43])); //B@d6
        assertTrue(0x28002844020000L==masks[44] && 7==Long.bitCount(masks[44])); //B@e6
        assertTrue(0x50005008040200L==masks[45] && 7==Long.bitCount(masks[45])); //B@f6
        assertTrue(0x20002010080400L==masks[46] && 5==Long.bitCount(masks[46])); //B@g6
        assertTrue(0x40004020100800L==masks[47] && 5==Long.bitCount(masks[47])); //B@h6
        assertTrue(   0x20408102000L==masks[48] && 5==Long.bitCount(masks[48])); //B@a7
        assertTrue(   0x40810204000L==masks[49] && 5==Long.bitCount(masks[49])); //B@b7
        assertTrue(   0xa1020400000L==masks[50] && 5==Long.bitCount(masks[50])); //B@c7
        assertTrue(  0x142240000000L==masks[51] && 5==Long.bitCount(masks[51])); //B@d7
        assertTrue(  0x284402000000L==masks[52] && 5==Long.bitCount(masks[52])); //B@e7
        assertTrue(  0x500804020000L==masks[53] && 5==Long.bitCount(masks[53])); //B@f7
        assertTrue(  0x201008040200L==masks[54] && 5==Long.bitCount(masks[54])); //B@g7
        assertTrue(  0x402010080400L==masks[55] && 5==Long.bitCount(masks[55])); //B@h7
        assertTrue( 0x2040810204000L==masks[56] && 6==Long.bitCount(masks[56])); //B@a8
        assertTrue( 0x4081020400000L==masks[57] && 5==Long.bitCount(masks[57])); //B@b8
        assertTrue( 0xa102040000000L==masks[58] && 5==Long.bitCount(masks[58])); //B@c8
        assertTrue(0x14224000000000L==masks[59] && 5==Long.bitCount(masks[59])); //B@d8
        assertTrue(0x28440200000000L==masks[60] && 5==Long.bitCount(masks[60])); //B@e8
        assertTrue(0x50080402000000L==masks[61] && 5==Long.bitCount(masks[61])); //B@f8
        assertTrue(0x20100804020000L==masks[62] && 5==Long.bitCount(masks[62])); //B@g8
        assertTrue(0x40201008040200L==masks[63] && 6==Long.bitCount(masks[63])); //B@h8
		int bitCountSum = 0;
		int maxBitCount = 0;
		for(int square=0; square<64; square++)
		{
			int bitCount = Long.bitCount(masks[square]);
			bitCountSum += bitCount;
			if(bitCount > maxBitCount) {
				maxBitCount = bitCount;
			}
		}
		assertEquals(364, bitCountSum);
		int expectedIndexTableSize = BishopMagics.twoToPowerOf(9);
		int actualIndexTableSize   = BishopMagics.twoToPowerOf(maxBitCount);
		assertEquals(expectedIndexTableSize, actualIndexTableSize);
	}
	
	private static long bishopAttacks(int bishopSquare, long occupied)
    {
    	Magic magic = bishopMagics[bishopSquare];
    	int occupiedIndex = (int)((occupied * magic.number) >>> magic.shift);
    	return bishopMoves[bishopSquare][occupiedIndex];
    }
	
    private static long populateSquares(String string) {
		long populated = 0L;
		String[] squares = string.split(",");
		for(String square: squares)
		{
			int bitSet = Square.squareOf(square);
			populated |= Bitmap.withOneBitSet(bitSet);
		}
		return populated;
	}

	
	public static void main(String[] args)
	{
		System.out.println("bishop (occupied and validMoves):");
		BishopMagicsTest.initialize();
		long[] masks = instance.getOccupancyMasks();
		long[][] occupancyVariation = instance.getOccupancyVariation();
		for(int square=0; square<=63; square++)
		{
			long mask = masks[square];
			int variationCount = 1 << Long.bitCount(mask);
			for(int variationIndex=0; variationIndex<variationCount; variationIndex++)
			{
				long occupied = occupancyVariation[square][variationIndex];
				long validMoves = BishopMagicsTest.bishopAttacks(square, occupied);
				String occupiedAndMovesSideBySide =
						LongDisplayer.paste(Square.named(square) + " occupied-"+variationIndex, occupied, "\tMoves", validMoves);
				System.out.println(occupiedAndMovesSideBySide);
			}
		}
	}


}
