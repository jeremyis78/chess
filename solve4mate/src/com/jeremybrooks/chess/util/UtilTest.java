package com.jeremybrooks.chess.util;

import com.jeremybrooks.chess.base.Bitmap;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

    private static final int BIT_COUNT_ITERATIONS = 2000000;
    Util u;
    
    protected void setUp() throws Exception {
        super.setUp();
        u = new Util();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    
    public void testOpp() {
        assertEquals(Bitmap.BLACK, Util.opposing(Bitmap.WHITE));
        assertEquals(Bitmap.WHITE, Util.opposing(Bitmap.BLACK));
    }

    public void testDisplaySquares() {
        long wking = getBitboard(Bitmap.E1);
        assertEquals("e1 ", Util.displaySquaresStr(wking));

        long wrooks = getBitboard(Bitmap.A1,Bitmap.A8);
        assertEquals("a1 a8 ", Util.displaySquaresStr(wrooks));
        
        long wpawns = getBitboard(Bitmap.A2,Bitmap.B2,Bitmap.C2,Bitmap.D2,
            Bitmap.E2,Bitmap.F2,Bitmap.G2,Bitmap.H2);
        assertEquals("a2 b2 c2 d2 e2 f2 g2 h2 ", Util.displaySquaresStr(wpawns));

        long bpawns = getBitboard(Bitmap.A7,Bitmap.B7,Bitmap.C7,Bitmap.D7,
            Bitmap.E7,Bitmap.F7,Bitmap.G7,Bitmap.H7);
        assertEquals("a7 b7 c7 d7 e7 f7 g7 h7 ", Util.displaySquaresStr(bpawns));
    }

    public void testBitCount(){
        for (int i=0; i<BIT_COUNT_ITERATIONS; i++){
            assertEquals(1, Util.bitCount(1L));
            assertEquals(1, Util.bitCount(0x8000000000000000L));
            assertEquals(8, Util.bitCount(0x00FF000000000000L));
            assertEquals(64, Util.bitCount(0xFFFFFFFFFFFFFFFFL));
        }
    }

    public void testDisplayMoveStrIncludesCheckAndMateFlags() {
        int from = Bitmap.A2;
        int to = Bitmap.A4;
        int mov = Bitmap.PIECE[Bitmap.PAWN];
        int cap = Bitmap.PIECE[Bitmap.NONE];
        int pro = Bitmap.PIECE[Bitmap.NONE];
        boolean check = false;
        boolean mate = false;
        
        int aMove = 0;
        int a2a4 = getPawnMove(Bitmap.A2, Bitmap.A4);
        assertEquals("Pa2-a4", Util.displayMoveStr(a2a4, check, mate));

        int a2xb3 = getPawnCapture(Bitmap.A2, Bitmap.B3, Bitmap.PIECE[Bitmap.PAWN]); 
        assertEquals("Pa2xb3", Util.displayMoveStr(a2xb3, check, mate));
    
        int kingSideCastle = getCastleMove(Bitmap.E1, Bitmap.G1); 
        assertEquals("Ke1-g1 0-0", Util.displayMoveStr(kingSideCastle, check, mate));

        int queenSideCastle = getCastleMove(Bitmap.E1, Bitmap.C1); 
        assertEquals("Ke1-c1 0-0-0", Util.displayMoveStr(queenSideCastle, check, mate));

        int queenCapturesRook = getQueenCaptures(Bitmap.E1, Bitmap.C1, Bitmap.PIECE[Bitmap.QUEEN]);
        check = true;
        assertEquals("Qe1xc1+", Util.displayMoveStr(queenCapturesRook, check, mate));

        //Pawn captures knight promotes to queen and mates
        from = Bitmap.D7;
        to = Bitmap.E8;
        mov = Bitmap.PIECE[Bitmap.PAWN];
        cap = Bitmap.PIECE[Bitmap.KNIGHT];
        pro = Bitmap.PIECE[Bitmap.QUEEN];
        check = false;
        mate = true;
        aMove = getMove(from,to,mov,cap,pro);
        assertEquals("Pd7xe8Q#", Util.displayMoveStr(aMove, check, mate));
    }
    
    private int getPawnMove(int from, int to)
    {
        int mov = Bitmap.PIECE[Bitmap.PAWN];
        int cap = 0;
        int pro = 0;
        return getMove(from,to,mov,cap,pro);
    }

    private int getPawnCapture(int from, int to, int cap)
    {
        int mov = Bitmap.PIECE[Bitmap.PAWN];
        int pro = 0;
        return getMove(from,to,mov,cap,pro);
    }
    
    private int getCastleMove(int from, int to)
    {
        int mov = Bitmap.PIECE[Bitmap.KING];
        return getMove(from, to, mov, 0, 0);
    }

    private int getQueenCaptures(int from, int to, int cap)
    {
        from = Bitmap.E1;
        to = Bitmap.C1;
        int mov = Bitmap.PIECE[Bitmap.QUEEN];
        int pro = Bitmap.PIECE[Bitmap.NONE];
        return getMove(from, to, mov, cap, pro);
    }
    
    public void testFormatByteBitmap(){
        byte a = 1;
        byte b = 5;
        byte c = 25;
        byte d = Byte.MAX_VALUE;
        byte e = -128; //most sig. bit set
        assertEquals("X - - - - - - -", Util.formatByteBitmap(a));
        assertEquals("X - X - - - - -", Util.formatByteBitmap(b));
        assertEquals("X - - X X - - -", Util.formatByteBitmap(c));
        assertEquals("X X X X X X X -", Util.formatByteBitmap(d));
        assertEquals("- - - - - - - X", Util.formatByteBitmap(e));
        
        //byte z = (byte)0x80;
    }

    public void testFormatByteBitmapShort(){
        short c = 0x000c;
        short f = 0x00f0;

        assertEquals("- - X X - - - -", Util.formatByteBitmap(c));
        assertEquals("- - - - X X X X", Util.formatByteBitmap(f));

        
        //Set the most significant bit in a short
        //ie, 0x8000 (we can't use this (0x8000) value, otherwise we would for clarity)
        short maxInvalid = (short)0x8000;
        
        //Set the least significant bit of a short's most significant byte
        //ie, 0x0100
        short minInvalid = 1 << 9;  

        try {
            Util.formatByteBitmap(maxInvalid);
            fail("should have thrown");
        } catch (IllegalArgumentException e){}
        try {
            Util.formatByteBitmap(minInvalid);
            fail("should have thrown");
        } catch (IllegalArgumentException e){}
    }

    public void testFormatByteBitmapInt(){
        int c = 0x000c;
        int f = 0x00f0;

        assertEquals("- - X X - - - -", Util.formatByteBitmap(c));
        assertEquals("- - - - X X X X", Util.formatByteBitmap(f));

        
        //Set the most significant bit in a short
        //ie, 0x 8000 0000
        int maxInvalid = 0x80000000;
        
        //Set the least significant bit of a short's most significant byte
        //ie, 0x 0000 0100
        int minInvalid = 0x00000100;  

        try {
            Util.formatByteBitmap(maxInvalid);
            fail("should have thrown");
        } catch (IllegalArgumentException e){}
        try {
            Util.formatByteBitmap(minInvalid);
            fail("should have thrown");
        } catch (IllegalArgumentException e){}
    }

    
    public void testFormatBaseAttacks(){

         int piece = 0x10;
         int occupied = 0x02;
         int attacks =  0xEE;
         String output ="piece   : - - - - X - - -\n" + 
                         "occupied: - X - - - - - -\n" +
                         "attacks : - X X X - X X X\n";
         assertEquals(output, Util.formatBaseAttacks(piece, occupied, attacks));
    
         int piece2 = 0x02;
         int occupied2 = 0x0B;
         int attacks2 = 0x0D;
         String output2 ="piece   : - X - - - - - -\n" + 
                          "occupied: X X - X - - - -\n" +
                          "attacks : X - X X - - - -\n";
         assertEquals(output2, Util.formatBaseAttacks(piece2, occupied2, attacks2));

         try{
             Util.formatBaseAttacks(0x03, occupied, attacks);
             fail("should have thrown, more than 1 bit set in pieceBitmap");
         } catch (IllegalArgumentException e){}

         try{
             Util.formatBaseAttacks(piece, 0x00000100, attacks);
             fail("should have thrown, bit set above least sig. byte");
         } catch (IllegalArgumentException e){}

         try{
             Util.formatBaseAttacks(piece, occupied, 0x00000100);
             fail("should have thrown, bit set above least sig. byte");
         } catch (IllegalArgumentException e){}
    }
        
    /* helper methods */
    private long getBitboard(int... indexes){
        long board = 0L;
        for(int i: indexes){
            board |= 1L << i;
        }
        return board;
    }
    
    private int getMove(int from, int to, int movingPiece, int capturedPiece, int promotionPiece)
    {
        int move = 0;
        move = from;
        move |= (to << 6);
        move |= (movingPiece << 12);
        move |= (capturedPiece << 15);
        move |= (promotionPiece << 18);
        return move;
    }

    
}
