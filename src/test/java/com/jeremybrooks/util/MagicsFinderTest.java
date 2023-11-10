package com.jeremybrooks.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Square;
import com.jeremybrooks.chess.util.MagicsFinder;


public class MagicsFinderTest {

//	@Test
//	public void testGenerateOccupancyVariation()
//	{
//		MagicsFinder.generateOccupancyCombination(true);
//		
//		long a = 32L;
//		System.out.println(Long.toString(a, 2));
//	}
	
	@Test
	public void testGenerateRookOccupancyMasks() {
		long[] masks = MagicsFinder.rookOccupancyMasks;
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
		int expectedIndexTableSize = MagicsFinder.twoToPowerOf(12);
		int actualIndexTableSize   = MagicsFinder.twoToPowerOf(maxBitCount);
		assertEquals(expectedIndexTableSize, actualIndexTableSize);

	}
	
	@Test
	public void testGenerateBishopOccupancyMasks() {
		long[] masks = MagicsFinder.bishOccupancyMasks;
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
		int expectedIndexTableSize = MagicsFinder.twoToPowerOf(9);
		int actualIndexTableSize   = MagicsFinder.twoToPowerOf(maxBitCount);
		assertEquals(expectedIndexTableSize, actualIndexTableSize);
	}
	@Test
	public void testPowerOfTwo()
	{
		assertEquals(1, MagicsFinder.twoToPowerOf(0));
		assertEquals(2, MagicsFinder.twoToPowerOf(1));
		assertEquals(4, MagicsFinder.twoToPowerOf(2));
		assertEquals(8, MagicsFinder.twoToPowerOf(3));
	}
	
	@Test
	public void testSetBitsArray() {
		for(int mask=0; mask<64; mask++)
		{
			int[] actualBit = MagicsFinder.toArrayOfBitsSetIn(mask);
			int actualMask = 0;
			for(int index=0; 
					index < actualBit.length && actualBit[index] != Bitmap.NOSQUARE; 
					index++)
			{
				actualMask |= Bitmap.withOneBitSet(actualBit[index]);
			}
			assertEquals(mask, actualMask);
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println("rook occupancy masks test code generation:");
		long[] masks = MagicsFinder.rookOccupancyMasks;
		for(int square=0; square<64; square++)
		{
			long mask = masks[square];
			int bitCount = Long.bitCount(mask);
			System.out.println(String.format("assertTrue(  %#16xL==masks[%2d] && %d==Long.bitCount(masks[%2d])); //R@%s",mask, square, bitCount, square, Square.named(square)));
		}
		System.out.println("bishop occupancy mask test code generation:");
		masks = MagicsFinder.bishOccupancyMasks;
        for(int square=0; square<64; square++)
        {
			long mask = masks[square];
			int bitCount = Long.bitCount(mask);
			System.out.println(String.format("assertTrue(%#16xL==masks[%2d] && %d==Long.bitCount(masks[%2d])); //B@%s",mask, square, bitCount, square, Square.named(square)));
        }
	}

}
