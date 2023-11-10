package com.jeremybrooks.chess.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Square;

public class RookMagicsTest {

	private static final RookMagics instance = new RookMagics();
	private static Magic[] rookMagics;
	private static long[][] rookMoves;
	
	@BeforeClass
	public static void initialize()
	{
		//MUST be generated in this order (e.g. call xMagics before xMoves)
		//rook magics and moves in sequence
		rookMagics   = instance.generateMagics();
		rookMoves    = instance.generateMoves(rookMagics);
	}
	
	@Test
	public void testA1with65808()
	{
		long occupied = 72057594046415249L;
		long rookAttacks = rookAttacks(Bitmap.A1, occupied);
//		System.out.println(LongDisplayer.paste("occupied", occupied, "moves", rookAttacks));
		assertEquals("b1 c1 d1 e1 a2 ", Util.displaySquaresStr(rookAttacks));
	}
	
	@Test
	public void testShiftsYieldIndexesWithinTwelveBitRange()
	{
		for(int bit=0; bit<=63; bit++)
		{
			Magic magic = rookMagics[bit];
			int bitsInIndex = 64-magic.shift;
			assertTrue(bitsInIndex >= 0);
			assertTrue(bitsInIndex <= 12); //2^12 = 4096
		}
	}

	@Test
	public void testGenerateRookOccupancyMasks() {
		long[] masks = instance.getOccupancyMasks();
		assertTrue(   0x101010101017eL==masks[ 0] && 12==Long.bitCount(masks[ 0])); //R@a1
		assertTrue(   0x202020202027cL==masks[ 1] && 11==Long.bitCount(masks[ 1])); //R@b1
		assertTrue(   0x404040404047aL==masks[ 2] && 11==Long.bitCount(masks[ 2])); //R@c1
		assertTrue(   0x8080808080876L==masks[ 3] && 11==Long.bitCount(masks[ 3])); //R@d1
		assertTrue(  0x1010101010106eL==masks[ 4] && 11==Long.bitCount(masks[ 4])); //R@e1
		assertTrue(  0x2020202020205eL==masks[ 5] && 11==Long.bitCount(masks[ 5])); //R@f1
		assertTrue(  0x4040404040403eL==masks[ 6] && 11==Long.bitCount(masks[ 6])); //R@g1
		assertTrue(  0x8080808080807eL==masks[ 7] && 12==Long.bitCount(masks[ 7])); //R@h1
		assertTrue(   0x1010101017e00L==masks[ 8] && 11==Long.bitCount(masks[ 8])); //R@a2
		assertTrue(   0x2020202027c00L==masks[ 9] && 10==Long.bitCount(masks[ 9])); //R@b2
		assertTrue(   0x4040404047a00L==masks[10] && 10==Long.bitCount(masks[10])); //R@c2
		assertTrue(   0x8080808087600L==masks[11] && 10==Long.bitCount(masks[11])); //R@d2
		assertTrue(  0x10101010106e00L==masks[12] && 10==Long.bitCount(masks[12])); //R@e2
		assertTrue(  0x20202020205e00L==masks[13] && 10==Long.bitCount(masks[13])); //R@f2
		assertTrue(  0x40404040403e00L==masks[14] && 10==Long.bitCount(masks[14])); //R@g2
		assertTrue(  0x80808080807e00L==masks[15] && 11==Long.bitCount(masks[15])); //R@h2
		assertTrue(   0x10101017e0100L==masks[16] && 11==Long.bitCount(masks[16])); //R@a3
		assertTrue(   0x20202027c0200L==masks[17] && 10==Long.bitCount(masks[17])); //R@b3
		assertTrue(   0x40404047a0400L==masks[18] && 10==Long.bitCount(masks[18])); //R@c3
		assertTrue(   0x8080808760800L==masks[19] && 10==Long.bitCount(masks[19])); //R@d3
		assertTrue(  0x101010106e1000L==masks[20] && 10==Long.bitCount(masks[20])); //R@e3
		assertTrue(  0x202020205e2000L==masks[21] && 10==Long.bitCount(masks[21])); //R@f3
		assertTrue(  0x404040403e4000L==masks[22] && 10==Long.bitCount(masks[22])); //R@g3
		assertTrue(  0x808080807e8000L==masks[23] && 11==Long.bitCount(masks[23])); //R@h3
		assertTrue(   0x101017e010100L==masks[24] && 11==Long.bitCount(masks[24])); //R@a4
		assertTrue(   0x202027c020200L==masks[25] && 10==Long.bitCount(masks[25])); //R@b4
		assertTrue(   0x404047a040400L==masks[26] && 10==Long.bitCount(masks[26])); //R@c4
		assertTrue(   0x8080876080800L==masks[27] && 10==Long.bitCount(masks[27])); //R@d4
		assertTrue(  0x1010106e101000L==masks[28] && 10==Long.bitCount(masks[28])); //R@e4
		assertTrue(  0x2020205e202000L==masks[29] && 10==Long.bitCount(masks[29])); //R@f4
		assertTrue(  0x4040403e404000L==masks[30] && 10==Long.bitCount(masks[30])); //R@g4
		assertTrue(  0x8080807e808000L==masks[31] && 11==Long.bitCount(masks[31])); //R@h4
		assertTrue(   0x1017e01010100L==masks[32] && 11==Long.bitCount(masks[32])); //R@a5
		assertTrue(   0x2027c02020200L==masks[33] && 10==Long.bitCount(masks[33])); //R@b5
		assertTrue(   0x4047a04040400L==masks[34] && 10==Long.bitCount(masks[34])); //R@c5
		assertTrue(   0x8087608080800L==masks[35] && 10==Long.bitCount(masks[35])); //R@d5
		assertTrue(  0x10106e10101000L==masks[36] && 10==Long.bitCount(masks[36])); //R@e5
		assertTrue(  0x20205e20202000L==masks[37] && 10==Long.bitCount(masks[37])); //R@f5
		assertTrue(  0x40403e40404000L==masks[38] && 10==Long.bitCount(masks[38])); //R@g5
		assertTrue(  0x80807e80808000L==masks[39] && 11==Long.bitCount(masks[39])); //R@h5
		assertTrue(   0x17e0101010100L==masks[40] && 11==Long.bitCount(masks[40])); //R@a6
		assertTrue(   0x27c0202020200L==masks[41] && 10==Long.bitCount(masks[41])); //R@b6
		assertTrue(   0x47a0404040400L==masks[42] && 10==Long.bitCount(masks[42])); //R@c6
		assertTrue(   0x8760808080800L==masks[43] && 10==Long.bitCount(masks[43])); //R@d6
		assertTrue(  0x106e1010101000L==masks[44] && 10==Long.bitCount(masks[44])); //R@e6
		assertTrue(  0x205e2020202000L==masks[45] && 10==Long.bitCount(masks[45])); //R@f6
		assertTrue(  0x403e4040404000L==masks[46] && 10==Long.bitCount(masks[46])); //R@g6
		assertTrue(  0x807e8080808000L==masks[47] && 11==Long.bitCount(masks[47])); //R@h6
		assertTrue(  0x7e010101010100L==masks[48] && 11==Long.bitCount(masks[48])); //R@a7
		assertTrue(  0x7c020202020200L==masks[49] && 10==Long.bitCount(masks[49])); //R@b7
		assertTrue(  0x7a040404040400L==masks[50] && 10==Long.bitCount(masks[50])); //R@c7
		assertTrue(  0x76080808080800L==masks[51] && 10==Long.bitCount(masks[51])); //R@d7
		assertTrue(  0x6e101010101000L==masks[52] && 10==Long.bitCount(masks[52])); //R@e7
		assertTrue(  0x5e202020202000L==masks[53] && 10==Long.bitCount(masks[53])); //R@f7
		assertTrue(  0x3e404040404000L==masks[54] && 10==Long.bitCount(masks[54])); //R@g7
		assertTrue(  0x7e808080808000L==masks[55] && 11==Long.bitCount(masks[55])); //R@h7
		assertTrue(0x7e01010101010100L==masks[56] && 12==Long.bitCount(masks[56])); //R@a8
		assertTrue(0x7c02020202020200L==masks[57] && 11==Long.bitCount(masks[57])); //R@b8
		assertTrue(0x7a04040404040400L==masks[58] && 11==Long.bitCount(masks[58])); //R@c8
		assertTrue(0x7608080808080800L==masks[59] && 11==Long.bitCount(masks[59])); //R@d8
		assertTrue(0x6e10101010101000L==masks[60] && 11==Long.bitCount(masks[60])); //R@e8
		assertTrue(0x5e20202020202000L==masks[61] && 11==Long.bitCount(masks[61])); //R@f8
		assertTrue(0x3e40404040404000L==masks[62] && 11==Long.bitCount(masks[62])); //R@g8
		assertTrue(0x7e80808080808000L==masks[63] && 12==Long.bitCount(masks[63])); //R@h8
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
		assertEquals(672, bitCountSum);
		int expectedIndexTableSize = RookMagics.twoToPowerOf(12);
		int actualIndexTableSize   = RookMagics.twoToPowerOf(maxBitCount);
		assertEquals(expectedIndexTableSize, actualIndexTableSize);

	}

	private static long rookAttacks(int rookSquare, long occupied)
    {
    	Magic magic = rookMagics[rookSquare];
    	occupied &= magic.occupiedMask;
    	//System.out.println("occupied after mask: "+ occupied);
    	int occupiedIndex = (int)((occupied * magic.number) >>> magic.shift);
    	return rookMoves[rookSquare][occupiedIndex];
    }

	public static void main(String[] args)
	{
		System.out.println("rook (occupied and validMoves):");
		RookMagicsTest.initialize();
		long[] masks = instance.getOccupancyMasks();
		long[][] occupancyVariation = instance.getOccupancyVariation();
		for(int square=0; square<=63; square++)
		{
			long mask = masks[square];
			int variationCount = 1 << Long.bitCount(mask);
			for(int variationIndex=0; variationIndex<variationCount; variationIndex++)
			{
				long occupied = occupancyVariation[square][variationIndex];
				long validMoves = RookMagicsTest.rookAttacks(square, occupied);
				String occupiedAndMovesSideBySide =
						LongDisplayer.paste(Square.named(square) + " occupied-"+variationIndex, occupied, "\tMoves", validMoves);
				System.out.println(occupiedAndMovesSideBySide);
			}
		}
	}

}
