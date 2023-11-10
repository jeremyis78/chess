package com.jeremybrooks.util;

import java.util.ArrayList;
import java.util.List;

import com.jeremybrooks.chess.util.OutputBuilder;
import com.jeremybrooks.chess.util.Util;
import junit.framework.TestCase;

import com.jeremybrooks.chess.UciDriver;
import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.King;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Square;
import com.jeremybrooks.chess.base.Piece.Color;
import com.jeremybrooks.chess.base.PieceFactory;
import com.jeremybrooks.chess.base.Queen;
import com.jeremybrooks.chess.base.Rook;

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
        assertEquals(Piece.BLACK, Util.opposing(Piece.WHITE));
        assertEquals(Piece.WHITE, Util.opposing(Piece.BLACK));
    }

    public void testDisplaySquares() {
        long wking = Bitmap.populateBits(new int[]{Bitmap.E1});
        assertEquals("e1 ", Util.displaySquaresStr(wking));

        long wrooks = Bitmap.populateBits(new int[]{Bitmap.A1, Bitmap.H1});
        assertEquals("a1 h1 ", Util.displaySquaresStr(wrooks));
        
        long wpawns = Bitmap.SECONDRANK;
        assertEquals("a2 b2 c2 d2 e2 f2 g2 h2 ", Util.displaySquaresStr(wpawns));

        long bpawns = Bitmap.SEVENTHRANK;
        assertEquals("a7 b7 c7 d7 e7 f7 g7 h7 ", Util.displaySquaresStr(bpawns));
    }

    public void testBitCount(){
            assertEquals( 1, Util.bitCount(1L));
            assertEquals( 1, Util.bitCount(0x8000000000000000L));
            assertEquals( 8, Util.bitCount(0x00FF000000000000L));
            assertEquals(64, Util.bitCount(0xFFFFFFFFFFFFFFFFL));
    }

    public void testDisplayMoveStrWithNoMove() {
        int from = 0; //ie, Bitmap.A1;
        int to   = 0; //ie, Bitmap.A1
        boolean check = false;
        boolean mate = false;
        
        int move = getPawnMove(from, to);
        assertEquals("<none>", Util.displayMoveStr(move, check, mate));
        
        move = 0; //noMove placeholder
        assertEquals("<none>", Util.displayMoveStr(move, check, mate));
    }
    
    public void testDisplayMoveStrWithCheckAndMateFlags() {
        int from = Bitmap.A2;
        int to = Bitmap.A4;
        int mov = Piece.ENCODED[Piece.PAWN];
        int cap = Piece.ENCODED[Piece.NONE];
        int pro = Piece.ENCODED[Piece.NONE];
        boolean check = false;
        boolean mate = false;
        
        int aMove = 0;
        int a2a4 = getPawnMove(Bitmap.A2, Bitmap.A4);
        assertEquals("Pa2-a4", Util.displayMoveStr(a2a4, check, mate));

        int a2xb3 = getPawnCapture(Bitmap.A2, Bitmap.B3, Piece.ENCODED[Piece.PAWN]); 
        assertEquals("Pa2xb3", Util.displayMoveStr(a2xb3, check, mate));
    
        int kingSideCastle = getCastleMove(Bitmap.E1, Bitmap.G1); 
        assertEquals("Ke1-g1", Util.displayMoveStr(kingSideCastle, check, mate));

        int queenSideCastle = getCastleMove(Bitmap.E1, Bitmap.C1); 
        assertEquals("Ke1-c1", Util.displayMoveStr(queenSideCastle, check, mate));

        int queenCapturesRook = getQueenCaptures(Bitmap.E1, Bitmap.C1, Piece.ENCODED[Piece.QUEEN]);
        check = true;
        //no check indication for now
        assertEquals("Qe1xc1", Util.displayMoveStr(queenCapturesRook, check, mate));

        //Pawn captures knight promotes to queen and mates
        from = Bitmap.D7;
        to = Bitmap.E8;
        mov = Piece.ENCODED[Piece.PAWN];
        cap = Piece.ENCODED[Piece.KNIGHT];
        pro = Piece.ENCODED[Piece.QUEEN];
        check = false;
        mate = true;
        aMove = getMove(from,to,mov,cap,pro);
        //no mate indication for now
        assertEquals("Pd7xe8Q", Util.displayMoveStr(aMove, check, mate));
    }

    public void testToFan() {
        List<Integer> moves = new ArrayList<>();
        int a2a4 = getPawnMove(Bitmap.A2, Bitmap.A4);
        moves.add(a2a4);
        int a2xb3 = getPawnCapture(Bitmap.A2, Bitmap.B3, Piece.ENCODED[Piece.PAWN]); 
        moves.add(a2xb3);
        int kingSideCastle = getCastleMove(Bitmap.E1, Bitmap.G1); 
        moves.add(kingSideCastle);
        int queenSideCastle = getCastleMove(Bitmap.E1, Bitmap.C1); 
        moves.add(queenSideCastle);
        int queenCapturesRook = getQueenCaptures(Bitmap.E1, Bitmap.C1, Piece.ENCODED[Piece.QUEEN]);
        moves.add(queenCapturesRook);
        String expected = "Pa2-a4 Pa2xb3 Ke1-g1 Ke1-c1 Qe1xc1";
        assertEquals(expected, Util.toFan(moves));
    }

    public void testFormatMoveInFanPromotionPieceWithoutAPawn()
    {
    	GameState state = new GameState(2);
    	//state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"); //doesn't matter
    	Piece rook = PieceFactory.create('R');
    	Piece queen = PieceFactory.create('Q');
    	int move = Util.EncodeMove(Bitmap.E7, Bitmap.E8, rook.encoded(), 0, queen.encoded());
		try {
			Util.formatMoveInFan(move, state);
		} catch (Exception e) {
			assertEquals("R: promotion is only valid for pawns", e.getMessage());
		}
    }

    public void testFormatMoveInFanNoMove()
    {
    	GameState state = new GameState(2);
    	int move = 0;
    	assertEquals("<none>", Util.formatMoveInFan(move, state));

    	//verify zeros in from/to with upper bits set is still no move
    	Piece pawn = PieceFactory.create('P');
    	Piece rook = PieceFactory.create('r');
    	Piece queen = PieceFactory.create('Q');
    	move = Util.EncodeMove(0, 0, pawn.encoded(), rook.encoded(), queen.encoded());
    	assertEquals("<none>", Util.formatMoveInFan(move, state));
    }

    public void testParseUciMoveBadArguments()
    {
    	GameState state = new GameState(2);
    	//state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"); //doesn't matter
    	String uciMove = "f4f5";
		try {
			Util.parseUciMove(uciMove, null);
		} catch (Exception e) {
			assertEquals("state is null", e.getMessage());
		}
		try {
			Util.parseUciMove(null, state);
		} catch (Exception e) {
			assertEquals("uciMove is null", e.getMessage());
		}
		try {
			Util.parseUciMove("e4", state);
		} catch (Exception e) {
			assertEquals("'e4': must be in long algebraic notation (4 or 5 characters)", e.getMessage());
		}
		try {
			Util.parseUciMove("e8q", state);
		} catch (Exception e) {
			assertEquals("'e8q': must be in long algebraic notation (4 or 5 characters)", e.getMessage());
		}
		try {
			Util.parseUciMove("Pe7xd8R", state);
		} catch (Exception e) {
			assertEquals("'Pe7xd8R': must be in long algebraic notation (4 or 5 characters)", e.getMessage());
		}
    }

    public void testParseUciMoveNoPieceExists()
    {
    	GameState state = new GameState(2);
    	state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    	String uciMove = "f4f5";
		try {
			Util.parseUciMove(uciMove, state);
		} catch (IllegalArgumentException e) {
			assertEquals("'f4f5': no piece exists on f4", e.getMessage());
		}
    }

    public void testParseUciMoveNonCapture()
    {
    	GameState state = new GameState(2);
    	state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    	String uciMove = "d2d4";
    	int move = Util.parseUciMove(uciMove, state);
    	assertEquals("Pd2-d4", Util.formatMoveInFan(move, state));//(move, false, false));
    	assertEquals(uciMove, Util.toUciMove(move));
    }

    public void testParseUciMoveCapture()
    {
    	GameState state = new GameState(2);
    	state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    	String uciMove = "b4a5";
    	int move = Util.parseUciMove(uciMove, state);
    	assertEquals("Bb4xa5", Util.formatMoveInFan(move, state));
    	assertEquals(uciMove, Util.toUciMove(move));
    }

    public void testParseUciMoveCaptureEnPassantWhite()
    {
    	GameState state = new GameState(2);
    	state.set("4k3/1ppp2pp/8/4Pp2/pP6/8/8/4K3 w - f6 0 1");
    	String uciMove = "e5f6";
    	int move = Util.parseUciMove(uciMove, state);
    	assertEquals("Pe5xf6", Util.formatMoveInFan(move, state));
    	assertEquals(uciMove, Util.toUciMove(move));
    }

    public void testParseUciMoveCaptureEnPassantBlack()
    {
    	GameState state = new GameState(2);
    	state.set("4k3/1ppp2pp/8/4Pp2/pP6/8/8/4K3 b - b3 0 1");
    	int toSquare = state.getEnPassantSquare();
    	Piece pieceBehindEPSquare = state.getPosition().get(Square.squareBehind(toSquare, Piece.BLACK));
    	assertTrue("precondition: pawn must be behind ep target square", pieceBehindEPSquare.isPawn());
    	String uciMove = "a4b3";
    	int move = Util.parseUciMove(uciMove, state);
    	assertEquals("Pa4xb3", Util.formatMoveInFan(move, state));
    	assertEquals(uciMove, Util.toUciMove(move));
    }

    public void testParseUciMoveIllegalStateForEnPassantBlack()
    {
    	GameState state = new GameState(2);
    	state.set("4k3/1ppp2pp/8/4Pp2/pB6/8/8/4K3 b - b3 0 1");
    	int toSquare = state.getEnPassantSquare();
    	Piece pieceBehindEPSquare = state.getPosition().get(Square.squareBehind(toSquare, Piece.BLACK));
		assertFalse("precondition: the position must be invalid with a white bishop (or non pawn) here instead of a pawn", 
    			pieceBehindEPSquare.isPawn());
    	String uciMove = "a4b3";
		try {
			Util.parseUciMove(uciMove, state);
		} catch (IllegalStateException e) {
			assertEquals("invalid en passant state: ep=b3 and b4 contains a 'B'", e.getMessage());
		}
    }

    public void testParseUciMoveNoPromotionPieceSpecified()
    {
    	GameState state = new GameState(2);
    	state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 b kq - 0 1");
    	String uciMove = "b2b1";
		try {
			Util.parseUciMove(uciMove, state);
		} catch (IllegalArgumentException e) {
			assertEquals("'b2b1': promotion piece not given; add one of [qrnb]", e.getMessage());
		}
    }

    public void testParseUciMovePromotion()
    {
    	GameState state = new GameState(2);
    	state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 b kq - 0 1");
    	String uciMove[] = new String[]{"b2b1q","b2b1r","b2b1b","b2b1n",};
    	String fanMove[] = new String[]{"Pb2-b1Q","Pb2-b1R","Pb2-b1B","Pb2-b1N",};
    	for(int moveIndex=0; moveIndex<uciMove.length; moveIndex++)
    	{
    		int move = Util.parseUciMove(uciMove[moveIndex], state);
    		assertEquals(fanMove[moveIndex], Util.formatMoveInFan(move, state));
    		assertEquals(uciMove[moveIndex], Util.toUciMove(move));
    	}
    }

    public void testParseUciMoveInvalidPromotionPiece()
    {
    	GameState state = new GameState(2);
    	state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 b kq - 0 1");
    	String uciMove[] = new String[]{"b2b1p","b2b1K","b2b1x","b2b19"};
    	for(String move: uciMove)
    	{
    		try {
    			Util.parseUciMove(move, state);
    		} catch (IllegalArgumentException e) {
    			assertTrue(e.getMessage().contains("promotion piece is invalid"));
    		}
    	}
    }

    public void testParseUciMoveNonPawnPromotesIsInvalid()
    {
    	GameState state = new GameState(2);
    	state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pb1P2PP/R2Q1RK1 b kq - 0 1");
    	assertEquals("precondition: moving piece must not be a pawn", 'b', state.getPosition().get(Bitmap.B2).toChar());
    	String uciMove[] = new String[]{"b2c1q"};
    	for(String move: uciMove)
    	{
    		try {
    			Util.parseUciMove(move, state);
    		} catch (IllegalArgumentException e) {
    			assertEquals("'b2c1q': only four characters allowed for non-pawn moves", e.getMessage());
    		}
    	}
    }

    public void testParseUciMoveCaptureAndPromotion()
    {
    	GameState state = new GameState(2);
    	state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 b kq - 0 1");
    	String uciMove[] = new String[]{"b2a1q","b2a1r","b2a1b","b2a1n"};
    	String fanMove[] = new String[]{"Pb2xa1Q","Pb2xa1R","Pb2xa1B","Pb2xa1N"};
    	for(int moveIndex=0; moveIndex<uciMove.length; moveIndex++)
    	{
    		int move = Util.parseUciMove(uciMove[moveIndex], state);
    		assertEquals(fanMove[moveIndex], Util.formatMoveInFan(move, state));
    		assertEquals(uciMove[moveIndex], Util.toUciMove(move));
    	}
    }

    public void testParseUciMoveBlackCastling()
    {
    	GameState state = new GameState(2);
    	state.set("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 b kq - 0 1");
    	//For testing move conversion and formatting it doesn't matter that 
    	//the e8g8 castling move is illegal (due to white knight on h6 attacking g8),
    	String uciMove[] = new String[]{"e8c8","e8g8",};
    	String fanMove[] = new String[]{"Ke8-c8","Ke8-g8",};
    	for(int moveIndex=0; moveIndex<uciMove.length; moveIndex++)
    	{
    		int move = Util.parseUciMove(uciMove[moveIndex], state);
    		assertEquals(fanMove[moveIndex], Util.formatMoveInFan(move, state));
    		assertEquals(uciMove[moveIndex], Util.toUciMove(move));
    	}
    }

    private int getPawnMove(int from, int to)
    {
        int mov = Piece.ENCODED[Piece.PAWN];
        int cap = 0;
        int pro = 0;
        return getMove(from,to,mov,cap,pro);
    }

    private int getPawnCapture(int from, int to, int cap)
    {
        int mov = Piece.ENCODED[Piece.PAWN];
        int pro = 0;
        return getMove(from,to,mov,cap,pro);
    }
    
    private int getCastleMove(int from, int to)
    {
        int mov = Piece.ENCODED[Piece.KING];
        return getMove(from, to, mov, 0, 0);
    }

    private int getQueenCaptures(int from, int to, int cap)
    {
        from = Bitmap.E1;
        to = Bitmap.C1;
        int mov = Piece.ENCODED[Piece.QUEEN];
        int pro = Piece.ENCODED[Piece.NONE];
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
         OutputBuilder expected = new OutputBuilder();
         expected.append("piece   : - - - - X - - -"); 
         expected.append("occupied: - X - - - - - -");
         expected.append("attacks : - X X X - X X X");
         assertEquals(expected.toString(), Util.formatBaseAttacks(piece, occupied, attacks));
    
         int piece2 = 0x02;
         int occupied2 = 0x0B;
         int attacks2 = 0x0D;
         expected = new OutputBuilder();
         expected.append("piece   : - X - - - - - -");
         expected.append("occupied: X X - X - - - -");
         expected.append("attacks : X - X X - - - -");
         assertEquals(expected.toString(), Util.formatBaseAttacks(piece2, occupied2, attacks2));

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
