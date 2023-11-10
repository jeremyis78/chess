package com.jeremybrooks.base;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.util.OutputBuilder;
import junit.framework.TestCase;
import org.junit.Test;

import static com.jeremybrooks.chess.base.Bitmap.*;

public class BitmapTest extends TestCase {

    private static final long LETTER_R_BITBOARD = Bitmap.populateBits(1, 57, 8) |
            1L << Bitmap.G1 | 1L << Bitmap.F2 |
            1L << Bitmap.E3 | 1L << Bitmap.D4 |
            1L << Bitmap.C4 | 1L << Bitmap.E5 |
            1L << Bitmap.F6 | 1L << Bitmap.F7 |
            7L << 58;
    
    long Long1 = 1L;
    long Long2 = 1L;
    long LongShift8 = Long1 << 8;

    /* DO NOT CHANGE THESE */
    private static final int[] SQ2BIT = {
        0,  1,  2,  3,  4,  5,  6,  7,     // 1st rank
        8,  9, 10, 11, 12, 13, 14, 15,     // 2nd-rank
        16, 17, 18, 19, 20, 21, 22, 23,    // 3rd-rank
        24, 25, 26, 27, 28, 29, 30, 31,    // 4th-rank
        32, 33, 34, 35, 36, 37, 38, 39,    // 5th-rank
        40, 41, 42, 43, 44, 45, 46, 47,    // 6th-rank
        48, 49, 50, 51, 52, 53, 54, 55,    // 7th-rank
        56, 57, 58, 59, 60, 61, 62, 63     // 8th-rank
    };
    private static final int[] SQ2BIT90R = {
        0,  8, 16, 24, 32, 40, 48, 56,    // a-file 
        1,  9, 17, 25, 33, 41, 49, 57,    // b-file
        2, 10, 18, 26, 34, 42, 50, 58,    // c-file
        3, 11, 19, 27, 35, 43, 51, 59,    // d-file
        4, 12, 20, 28, 36, 44, 52, 60,    // e-file
        5, 13, 21, 29, 37, 45, 53, 61,    // f-file
        6, 14, 22, 30, 38, 46, 54, 62,    // g-file
        7, 15, 23, 31, 39, 47, 55, 63     // h-file
    };
    private static final int[] SQ2BIT45L = {
         0,  1,  3,  6, 10, 15, 21, 28,
         2,  4,  7, 11, 16, 22, 29, 36,
         5,  8, 12, 17, 23, 30, 37, 43,
         9, 13, 18, 24, 31, 38, 44, 49,
        14, 19, 25, 32, 39, 45, 50, 54,
        20, 26, 33, 40, 46, 51, 55, 58,
        27, 34, 41, 47, 52, 56, 59, 61,
        35, 42, 48, 53, 57, 60, 62, 63
    };            
    private static final int[] SQ2BIT45R = { 
        28, 21, 15, 10,  6,  3,  1,  0,
        36, 29, 22, 16, 11,  7,  4,  2,
        43, 37, 30, 23, 17, 12,  8,  5,
        49, 44, 38, 31, 24, 18, 13,  9,
        54, 50, 45, 39, 32, 25, 19, 14,
        58, 55, 51, 46, 40, 33, 26, 20,
        61, 59, 56, 52, 47, 41, 34, 27,
        63, 62, 60, 57, 53, 48, 42, 35
    };

    
    
    public void setup(){
    }
    
    public void testLongEquivalence(){
        assertEquals("1L should equal (long)1", Long1, Long2);
        assertEquals("1L << 8 should be 256", 256, LongShift8 );
    }

    
    public void testLongToHexString(){
        //System.out.printf("0x%I64X\n", Long1);
        String hex = String.format("0x%016X", new Object[]{Long1});
        assertEquals("0x0000000000000001", hex);

        String hexShifted = String.format("0x%016X", Long1 << 3);
        assertEquals("0x0000000000000008", hexShifted);

        String hexShiftedMax = String.format("0x%016X", Long1 << 63);
        assertEquals("0x8000000000000000", hexShiftedMax);

    }

    public void testShiftingBeyondRangeIsActuallyARotation(){
        //In Java a left shift (<<) operation is not a shift but a rotate operation
        String hexShiftedZero = String.format("0x%016X", Long1 << 64);
        assertEquals("0x0000000000000001", hexShiftedZero);

        String hexShiftedMore = String.format("0x%016X", Long1 << 66);
        assertEquals("0x0000000000000004", hexShiftedMore);
    }
    
    public void testSQ2BIT(){
        for(int i=0; i < 64; i++){
            assertEquals(SQ2BIT[i], Bitmap.SQ2BIT[i]);
        }
    }
    
    public void testSQ2BIT90R(){
        for(int i=0; i < 64; i++){
            assertEquals(SQ2BIT90R[i], Bitmap.SQ2BIT90R[i]);
        }
    }

    public void testSQ2BIT45R(){
        for(int i=0; i < 64; i++){
            assertEquals(SQ2BIT45R[i], Bitmap.SQ2BIT45R[i]);
        }
    }

    public void testSQ2BIT45L(){
        for(int i=0; i < 64; i++){
            assertEquals(SQ2BIT45L[i], Bitmap.SQ2BIT45L[i]);
        }
    }

    public void testClearPiece()
    {
        long fourthBitSet = 1L << 4;
        assertEquals(16L, fourthBitSet);
        long fourthBitCleared = Bitmap.clearBit(fourthBitSet, 4);
        assertEquals(0L, fourthBitCleared);
    }

    public void testClearPieceForEveryBit()
    {
        long allBitsSet = -1L;
        long current = allBitsSet;
        
        for(int bitToClear = 0; bitToClear < 64; bitToClear++)
        {
            current = Bitmap.clearBit(current, bitToClear);
            long notCurrent = ~current;
            int mostSignificantBitSet = Bitmap.highestBitNumber(notCurrent);
            assertEquals(bitToClear, mostSignificantBitSet);
        }
    }
    
    public void testGetLSBit()
    {
        long fourthBitSet = 1L << 4;
        long fiftiethBitSet = 1L << 50;
        assertEquals(16L, fourthBitSet);
        assertEquals(1125899906842624L, fiftiethBitSet);
        
        long leastSignificantBitSet = Bitmap.lowestBitNumber(fourthBitSet);
        assertEquals(4, leastSignificantBitSet);
        
        leastSignificantBitSet = Bitmap.lowestBitNumber(fiftiethBitSet);
        assertEquals(50, leastSignificantBitSet);
        
        long fourthORFiftieth = fourthBitSet | fiftiethBitSet;
        leastSignificantBitSet = Bitmap.lowestBitNumber(fourthORFiftieth);
        assertEquals(4, leastSignificantBitSet);
    }
    
    public void testLowestBitNumberOnBoundaries()
    {
        long firstBitSet = 1L; //1L << 0;
        long lastBitSet = 1L << 63;
        long allBitsSet = -1L;
        long noBitSet = 0L;
        
        long leastSignificantBitSet = Bitmap.lowestBitNumber(noBitSet);
        assertEquals(-1, leastSignificantBitSet);
        
        leastSignificantBitSet = Bitmap.lowestBitNumber(firstBitSet);
        assertEquals(0, leastSignificantBitSet);
        
        leastSignificantBitSet = Bitmap.lowestBitNumber(lastBitSet);
        assertEquals(63, leastSignificantBitSet);
        
        leastSignificantBitSet = Bitmap.lowestBitNumber(allBitsSet);
        assertEquals(0, leastSignificantBitSet);
        
        long firstAndLastBitSet = firstBitSet | lastBitSet;
        leastSignificantBitSet = Bitmap.lowestBitNumber(firstAndLastBitSet);
        assertEquals(0, leastSignificantBitSet);
    }

    public void testHighestBitNumberOnBoundaries()
    {
        long firstBitSet = 1L; //1l << 0;
        long lastBitSet = 1L << 63;
        long allBitsSet = -1L;
        long noBitSet = 0L;
        
        long mostSignificantBitSet = Bitmap.highestBitNumber(noBitSet);
        assertEquals(-1, mostSignificantBitSet);
        
        mostSignificantBitSet = Bitmap.highestBitNumber(firstBitSet);
        assertEquals(0, mostSignificantBitSet);

        mostSignificantBitSet = Bitmap.highestBitNumber(lastBitSet);
        assertEquals(63, mostSignificantBitSet);
        
        mostSignificantBitSet = Bitmap.highestBitNumber(allBitsSet);
        assertEquals(63, mostSignificantBitSet);
        
        long firstAndLastBitSet = firstBitSet | lastBitSet;
        mostSignificantBitSet = Bitmap.highestBitNumber(firstAndLastBitSet);
        assertEquals(63, mostSignificantBitSet);
    }

    @Test
    public void testSet() {
        Bitmap b = rankBitmap(E4);
        Bitmap d = rankBitmap(H8);
        
        Bitmap bORd = b.bitwiseOr(d);
        assertEquals(rankBitmap(E4).toString(), b.toString());
        assertEquals(rankBitmap(E4).longValue() | rankBitmap(H8).longValue(), bORd.longValue());

        Bitmap bXORd = b.bitwiseXor(d);
        assertEquals(rankBitmap(E4).toString(), b.toString());
        assertEquals(rankBitmap(E4).bitwiseOr(rankBitmap(H8)).toString(), bXORd.toString());
        
        Bitmap a3h8 = rankBitmap(A3).bitwiseOr(rankBitmap(H8));
        assertEquals(emptyBitmap().toString(), a3h8.bitwiseXor(a3h8).toString());
    }

    @Test
    public void testGivenFormatForLetterR() {
        OutputBuilder expected = new OutputBuilder();
        expected.append("8 - - X X X - - - ");
        expected.append("7 - X - - - X - - ");
        expected.append("6 - X - - - X - - ");
        expected.append("5 - X - - X - - - ");
        expected.append("4 - X X X - - - - ");
        expected.append("3 - X - - X - - - ");
        expected.append("2 - X - - - X - - ");
        expected.append("1 - X - - - - X - ");
        expected.append("  a b c d e f g h");
        assertEquals(expected.toString(), Bitmap.format(LETTER_R_BITBOARD));
    }

    @Test
    public void testGivenFormatForLetterRWithHighlightedSquare() {
        long highlightedSquare = 1L << Bitmap.G4;
        OutputBuilder expected = new OutputBuilder();
        expected.append("8 - - X X X - - - ");
        expected.append("7 - X - - - X - - ");
        expected.append("6 - X - - - X - - ");
        expected.append("5 - X - - X - - - ");
        expected.append("4 - X X X - - + - ");
        expected.append("3 - X - - X - - - ");
        expected.append("2 - X - - - X - - ");
        expected.append("1 - X - - - - X - ");
        expected.append("  a b c d e f g h");
        assertEquals(expected.toString(), Bitmap.format(LETTER_R_BITBOARD, highlightedSquare));
    }

    @Test
    public void testGivenFlipVerticalOfLetterR()
    {
        OutputBuilder expected = new OutputBuilder();
        expected.append("8 - X - - - - X - ");
        expected.append("7 - X - - - X - - ");
        expected.append("6 - X - - X - - - ");
        expected.append("5 - X X X - - - - ");
        expected.append("4 - X - - X - - - ");
        expected.append("3 - X - - - X - - ");
        expected.append("2 - X - - - X - - ");
        expected.append("1 - - X X X - - - ");
        expected.append("  a b c d e f g h");
        long flipped = Bitmap.flipVertical(LETTER_R_BITBOARD);
        assertEquals(expected.toString(), Bitmap.format(flipped));
    }
    
    @Test
    public void testGivenMirrorHorizontalOfLetterR()
    {
        OutputBuilder expected = new OutputBuilder();
        expected.append("8 - - - X X X - - ");
        expected.append("7 - - X - - - X - ");
        expected.append("6 - - X - - - X - ");
        expected.append("5 - - - X - - X - ");
        expected.append("4 - - - - X X X - ");
        expected.append("3 - - - X - - X - ");
        expected.append("2 - - X - - - X - ");
        expected.append("1 - X - - - - X - ");
        expected.append("  a b c d e f g h");
        long mirrored = Bitmap.mirrorHorizontal(LETTER_R_BITBOARD);
        assertEquals(expected.toString(), Bitmap.format(mirrored));
    }

    @Test
    public void testGivenFlipThenMirrorOfLetterR()
    {
        OutputBuilder expected = new OutputBuilder();
        expected.append("8 - X - - - - X - ");
        expected.append("7 - - X - - - X - ");
        expected.append("6 - - - X - - X - ");
        expected.append("5 - - - - X X X - ");
        expected.append("4 - - - X - - X - ");
        expected.append("3 - - X - - - X - ");
        expected.append("2 - - X - - - X - ");
        expected.append("1 - - - X X X - - ");
        expected.append("  a b c d e f g h");
        long flipped = Bitmap.flipVertical(LETTER_R_BITBOARD);
        long flippedAndMirrored = Bitmap.mirrorHorizontal(flipped);
        assertEquals(expected.toString(), Bitmap.format(flippedAndMirrored));
    }

    @Test
    public void testGivenFlipDiagA1H8OfLetterR()
    {
        OutputBuilder expected = new OutputBuilder();
        expected.append("8 - - - - - - - - ");
        expected.append("7 X - - - - - - - ");
        expected.append("6 - X - - - X X - ");
        expected.append("5 - - X - X - - X ");
        expected.append("4 - - - X - - - X ");
        expected.append("3 - - - X - - - X ");
        expected.append("2 X X X X X X X - ");
        expected.append("1 - - - - - - - - ");
        expected.append("  a b c d e f g h");
        long flipDiagA1H8 = Bitmap.flipDiagA1H8(LETTER_R_BITBOARD);
        assertEquals(expected.toString(), Bitmap.format(flipDiagA1H8));
    }
    
    @Test
    public void testRankMasks()
    {
        long rank1 = 0xFFL;
        assertEquals(rank1 <<  0, Bitmap.FIRSTRANK);
        assertEquals(rank1 <<  8, Bitmap.SECONDRANK);
        assertEquals(rank1 << 16, Bitmap.THIRDRANK);
        assertEquals(rank1 << 24, Bitmap.FOURTHRANK);
        assertEquals(rank1 << 32, Bitmap.FIFTHRANK);
        assertEquals(rank1 << 40, Bitmap.SIXTHRANK);
        assertEquals(rank1 << 48, Bitmap.SEVENTHRANK);
        assertEquals(rank1 << 56, Bitmap.EIGHTHRANK);
    }

    @Test
    public void testFileMasks()
    {
        final long file_A = 0x0101010101010101L;
        assertEquals(file_A << 0, Bitmap.A_FILE);
        assertEquals(file_A << 1, Bitmap.B_FILE);
        assertEquals(file_A << 2, Bitmap.C_FILE);
        assertEquals(file_A << 3, Bitmap.D_FILE);
        assertEquals(file_A << 4, Bitmap.E_FILE);
        assertEquals(file_A << 5, Bitmap.F_FILE);
        assertEquals(file_A << 6, Bitmap.G_FILE);
        assertEquals(file_A << 7, Bitmap.H_FILE);
    }


}
