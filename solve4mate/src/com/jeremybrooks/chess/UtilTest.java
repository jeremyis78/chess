package com.jeremybrooks.chess;

import junit.framework.TestCase;

import com.jeremybrooks.chess.Util.UnsignedByte;


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
		assertEquals(Color.BLACK, Util.opp(Color.WHITE));
		assertEquals(Color.WHITE, Util.opp(Color.BLACK));
	}

	public void testToggle() {
		assertEquals(Color.BLACK, Util.Toggle(Color.WHITE));
		assertEquals(Color.WHITE, Util.Toggle(Color.BLACK));
	}

	public void testReverseByte() {
		byte aByte = 1;
		assertEquals(Byte.MIN_VALUE, Util.ReverseBits(aByte));
	}

	public void testReverseBits() {
		long aLong = 1L;
		assertEquals(Long.MIN_VALUE, Util.ReverseBits(aLong));
	}

	public void testAdjacentSquares() {
		assertTrue("A1 is adjacent to B1", Util.adjacentSquares(Bitmap.A1, Bitmap.B1));
		assertTrue("A1 is adjacent to A2", Util.adjacentSquares(Bitmap.A1, Bitmap.A2));
		assertTrue("A1 is adjacent to B2", Util.adjacentSquares(Bitmap.A1, Bitmap.B2));

		assertFalse("A1 is not adjacent to A3", Util.adjacentSquares(Bitmap.A1, Bitmap.A3));
		assertFalse("A1 is not adjacent to B3", Util.adjacentSquares(Bitmap.A1, Bitmap.B3));
		assertFalse("A1 is not adjacent to C3", Util.adjacentSquares(Bitmap.A1, Bitmap.C3));
		assertFalse("A1 is not adjacent to C2", Util.adjacentSquares(Bitmap.A1, Bitmap.C2));
		assertFalse("A1 is not adjacent to C1", Util.adjacentSquares(Bitmap.A1, Bitmap.C1));
	}

	public void testStrToSq() {
		assertEquals(Bitmap.A1, Util.StrToSq("A1"));
		assertEquals(Bitmap.B2, Util.StrToSq("B2"));
		assertEquals(Bitmap.C3, Util.StrToSq("C3"));
		assertEquals(Bitmap.D4, Util.StrToSq("D4"));
		assertEquals(Bitmap.E5, Util.StrToSq("E5"));
		assertEquals(Bitmap.F6, Util.StrToSq("F6"));
		assertEquals(Bitmap.G7, Util.StrToSq("G7"));
		assertEquals(Bitmap.H8, Util.StrToSq("H8"));
		try{
			Util.StrToSq("I4");
			fail("I4 is invalid");
		} catch (IllegalArgumentException e){
		}
		try{
			Util.StrToSq("A9");
			fail("A9 is invalid");
		} catch (IllegalArgumentException e){
		}
	}

	public void testSqToStr() {
		assertEquals("a1", Util.SqToStr(Bitmap.A1));
		assertEquals("b2", Util.SqToStr(Bitmap.B2));
		assertEquals("c3", Util.SqToStr(Bitmap.C3));
		assertEquals("d4", Util.SqToStr(Bitmap.D4));
		assertEquals("e5", Util.SqToStr(Bitmap.E5));
		assertEquals("f6", Util.SqToStr(Bitmap.F6));
		assertEquals("g7", Util.SqToStr(Bitmap.G7));
		assertEquals("h8", Util.SqToStr(Bitmap.H8));
		try{
			Util.SqToStr(64);
			fail("64 is invalid index into bitboard");
		} catch (IllegalArgumentException e){
		}
		try{
			Util.SqToStr(-1);
			fail("-1 is invalid index into bitboard");
		} catch (IllegalArgumentException e){
		}
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

	public void testPieceCount() {
		for (int i=0; i<BIT_COUNT_ITERATIONS; i++){
			assertEquals(1, Util.PieceCount(1L));
			assertEquals(1, Util.PieceCount(0x8000000000000000L));
			assertEquals(8, Util.PieceCount(0x00FF000000000000L));
			assertEquals(64, Util.PieceCount(0xFFFFFFFFFFFFFFFFL));
		}
	}
	
	public void testBitCount(){
		for (int i=0; i<BIT_COUNT_ITERATIONS; i++){
			assertEquals(1, Util.bitCount(1L));
			assertEquals(1, Util.bitCount(0x8000000000000000L));
			assertEquals(8, Util.bitCount(0x00FF000000000000L));
			assertEquals(64, Util.bitCount(0xFFFFFFFFFFFFFFFFL));
		}
	}

	public void testPieceToChar() {
		assertEquals('P', Util.PieceToChar(Color.WHITE, Bitmap.PIECE[Bitmap.PAWN]));
		assertEquals('p', Util.PieceToChar(Color.BLACK, Bitmap.PIECE[Bitmap.PAWN]));

		assertEquals('N', Util.PieceToChar(Color.WHITE, Bitmap.PIECE[Bitmap.KNIGHT]));
		assertEquals('n', Util.PieceToChar(Color.BLACK, Bitmap.PIECE[Bitmap.KNIGHT]));

		assertEquals('K', Util.PieceToChar(Color.WHITE, Bitmap.PIECE[Bitmap.KING]));
		assertEquals('k', Util.PieceToChar(Color.BLACK, Bitmap.PIECE[Bitmap.KING]));

		assertEquals('B', Util.PieceToChar(Color.WHITE, Bitmap.PIECE[Bitmap.BISHOP]));
		assertEquals('b', Util.PieceToChar(Color.BLACK, Bitmap.PIECE[Bitmap.BISHOP]));

		assertEquals('R', Util.PieceToChar(Color.WHITE, Bitmap.PIECE[Bitmap.ROOK]));
		assertEquals('r', Util.PieceToChar(Color.BLACK, Bitmap.PIECE[Bitmap.ROOK]));

		assertEquals('Q', Util.PieceToChar(Color.WHITE, Bitmap.PIECE[Bitmap.QUEEN]));
		assertEquals('q', Util.PieceToChar(Color.BLACK, Bitmap.PIECE[Bitmap.QUEEN]));
		
		try{
			Util.PieceToChar(Color.WHITE, 4);
			fail("4 is not a valid piece");
		} catch (IllegalArgumentException e){}

		try{
			Util.PieceToChar(Color.WHITE, 8);
			fail("8 is not a valid piece");
		} catch (IllegalArgumentException e){}

		try{
			Util.PieceToChar(-1, Bitmap.PIECE[Bitmap.QUEEN]);
			fail("-1 is not a valid color");
		} catch (IllegalArgumentException e){}

		try{
			Util.PieceToChar(2, Bitmap.PIECE[Bitmap.QUEEN]);
			fail("2 is not a valid color");
		} catch (IllegalArgumentException e){}
	}

//	public void testDisplayBitbrd() {
//		fail("Not yet implemented");
//	}

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
//		printMove(aMove);
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
	
	
//	public void testDisplayMove() {
//		fail("Not yet implemented");
//	}

	public void testDisplayMoves() {
		long wpawns = 3 << 8 << 8;
		assertEquals("a3, b3, ", Util.DisplayMovesStr(wpawns));
	}
	
//	public void testDisplayStatusStr(){
////		String s = "- X - - - - - -";
////		assertEquals(s, Util.DisplayStatusStr(2));
////
////		s = "- X X - - - - -";
////		assertEquals(s, Util.DisplayStatusStr(3));
////
////		s = "- - - X - - - -";
////		assertEquals(s, Util.DisplayStatusStr(4));
//		for(int status = 0; status < 64; status++)
//			System.out.println(Util.DisplayStatusStr(status));
//		fail("this is not a test...write one!");
//	}

	public void testDisplayBoardStr() {
		long board = getBitboard(Bitmap.A1);
		board |= getBitboard(Bitmap.B2);
		board |= getBitboard(Bitmap.C3);
		board |= getBitboard(Bitmap.D4);
		board |= getBitboard(Bitmap.E5);
		board |= getBitboard(Bitmap.F6);
		board |= getBitboard(Bitmap.G7);
		board |= getBitboard(Bitmap.H8);
	
		String output =
			"8 - - - - - - - X \n" +
			"7 - - - - - - X - \n" +
			"6 - - - - - X - - \n" +
			"5 - - - - X - - - \n" +
			"4 - - - X - - - - \n" +
			"3 - - X - - - - - \n" +
			"2 - X - - - - - - \n" +
			"1 X - - - - - - - \n" +
			"  a b c d e f g h\n";
		
		assertEquals(output, Util.DisplayBoardStr(board));
	}

	public void testDisplayBoardStrLongLong() {
		long board = getBitboard(Bitmap.A1);
		board |= getBitboard(Bitmap.B2);
		board |= getBitboard(Bitmap.C3);
		board |= getBitboard(Bitmap.D4);
		board |= getBitboard(Bitmap.E5);
		board |= getBitboard(Bitmap.F6);
		board |= getBitboard(Bitmap.G7);
		board |= getBitboard(Bitmap.H8);
		long piece = getBitboard(Bitmap.D1);

		String output =
			"8 - - - - - - - X \n" +
			"7 - - - - - - X - \n" +
			"6 - - - - - X - - \n" +
			"5 - - - - X - - - \n" +
			"4 - - - X - - - - \n" +
			"3 - - X - - - - - \n" +
			"2 - X - - - - - - \n" +
			"1 X - - + - - - - \n" +
			"  a b c d e f g h\n";
		
		assertEquals(output, Util.DisplayBoardStr(board, piece));
	}

	public void testDisplayBoardStrLongInt() {
		long board = getBitboard(Bitmap.A1);
		board |= getBitboard(Bitmap.B2);
		board |= getBitboard(Bitmap.C3);
		board |= getBitboard(Bitmap.D4);
		board |= getBitboard(Bitmap.E5);
		board |= getBitboard(Bitmap.F6);
		board |= getBitboard(Bitmap.G7);
		board |= getBitboard(Bitmap.H8);
		int square = Bitmap.D1;

		String output =
			"8 - - - - - - - X \n" +
			"7 - - - - - - X - \n" +
			"6 - - - - - X - - \n" +
			"5 - - - - X - - - \n" +
			"4 - - - X - - - - \n" +
			"3 - - X - - - - - \n" +
			"2 - X - - - - - - \n" +
			"1 X - - + - - - - \n" +
			"  a b c d e f g h\n";
		
		assertEquals(output, Util.DisplayBoardStr(board, square));
	}
	
//	public void testByteRotation(){
//		byte piece = 0x1;
//		byte status = 0;
//		
//		for (int i=0 ; piece != 0; piece <<= 1, i++){
//			//assertTrue("piece is not greater than zero loop #" + i , piece > 0);
//			for (status = 0; status < 64; ++status){
//				System.out.println("piece: "+getBinaryPadded(piece, 8)+ "  status: " + getBinaryPadded(status, 8)); 
//            }
//        }
//		System.out.println("Success: It finished the loop!");
//	}

//	public void testShortRotation(){
//		short piece = 0x1;
//		byte status = 0;
//		
//		for (int i=0 ; piece != 0; piece <<= 1, i++){
//			//assertTrue("piece is not greater than zero loop #" + i , piece > 0);
//			for (status = 0; status < 64; ++status){
//				System.out.println("piece: "+getBinaryPadded(piece, 8)+ "  status: " + getBinaryPadded(status, 8)); 
//            }
//        }
//		System.out.println("Success: It finished the loop!");
//	}
	
	public void testDisplayMovesStrByte(){
		final String output[] = new String[] {
				"- - - - - - - - \n",
				"X - - - - - - - \n",
				"- X - - - - - - \n",
				"X X - - - - - - \n",
				"- - X - - - - - \n",
				"X - X - - - - - \n",
				"- X X - - - - - \n",
				"- - - - - X X - \n",  //TODO: need to test from here down
				"- - - - - X - X \n",
				"- - - - - X - - \n",
				"- - - - - - X X \n",
				"- - - - - - X - \n",
				"- - - - - - - X \n"
		};
		
		byte moves = 0x7f;
		assertEquals("X X X X X X X - \n", Util.DisplayMovesStr(moves));
	
		for (moves = 0; moves < 7; moves++){
			assertEquals("moves: " + moves, output[moves], Util.DisplayMovesStr(moves));
		}

//		int imoves = 0x80;
//		assertEquals("- - - - - - - X \n", Util.DisplayMovesStr(imoves));

	}

	public void testUnsignedByteClass(){
		UnsignedByte ub = new UnsignedByte(1);
		int i = ub.get() << 2;
		assertEquals(4, i);
	}
	
	public void testOperations(){
		//piece <<= 1
		
		
		//occ = (byte) (status << 1);  //where status is [0..63]
		
		
		
		//p = (byte) (piece << 1);
		
		
		
		//p <<= 1
		

		
		//left = (byte) (occ & p);
		

		
		//left = (byte) 0x80;
	
		
		
		//p = (byte) (piece >> 1);
		
		
		
		//p >>= 1
		
		
		
		//right = (byte) (occ & p);
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
	
	public void testFormatStatusByte(){
		byte status1 = 1;
		byte status2 = 2;
		byte status3 = 3;
		byte status63 = 63; //max status

		assertEquals("- X - - - - - -", Util.formatStatus(status1));
		assertEquals("- - X - - - - -", Util.formatStatus(status2));
		assertEquals("- X X - - - - -", Util.formatStatus(status3));
		assertEquals("- X X X X X X -", Util.formatStatus(status63));
	}
	
	public void testFormatStatusInt(){
		int status1 = 1;
		int status2 = 2;
		int status3 = 3;
		int status63 = 63; //max status

		assertEquals("- X - - - - - -", Util.formatStatus(status1));
		assertEquals("- - X - - - - -", Util.formatStatus(status2));
		assertEquals("- X X - - - - -", Util.formatStatus(status3));
		assertEquals("- X X X X X X -", Util.formatStatus(status63));
	}
	
	public void testFormatLongBitmapAsBoard(){
		long startingPosition = 0xFFFF00000000FFFFL;
		String startingPositionOutput = 
			"8 X X X X X X X X\n" +
			"7 X X X X X X X X\n" +
			"6 - - - - - - - -\n" +
			"5 - - - - - - - -\n" +
			"4 - - - - - - - -\n" +
			"3 - - - - - - - -\n" +
			"2 X X X X X X X X\n" +
			"1 X X X X X X X X\n" +
			"  a b c d e f g h\n";
		assertEquals(startingPositionOutput, 
				Util.formatLongBitmapAsBoard(0x0, startingPosition));

		
		long rookAttacks = 0x01010101010101FEL;
		long rook = 0x0000000000000001L;
		String rookCorrect = 
			"8 X - - - - - - -\n" +
			"7 X - - - - - - -\n" +
			"6 X - - - - - - -\n" +
			"5 X - - - - - - -\n" +
			"4 X - - - - - - -\n" +
			"3 X - - - - - - -\n" +
			"2 X - - - - - - -\n" +
			"1 + X X X X X X X\n" +
			"  a b c d e f g h\n";
		assertEquals(rookCorrect, 
				Util.formatLongBitmapAsBoard(rook, rookAttacks));
		
	     try{
	    	 Util.formatLongBitmapAsBoard(0x3, 0x11);
	    	 fail("should have thrown, more than 1 bit set in pieceBitmap");
	     } catch (IllegalArgumentException e){}

	     try{
	    	 Util.formatLongBitmapAsBoard(0x1, 0x1);
	    	 fail("should have thrown, overlapping bits");
	     } catch (IllegalArgumentException e){}
	}
	
	public void testFormatSideBySide()
	{
		String left = "abc\nghi\n";
		String right = "def\njkl\n";
		final String expected = "abc def\nghi jkl\n";
		assertEquals(expected, Util.formatSideBySide(left,right));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    public void testGetA1H8diagFunction(){
    	for(int i=0; i<15; i++)
    	{
    		String board = Util.formatLongBitmapAsBoard(Attacks.getInstance().getH1A8diag(i,(byte)0xff));
//    		System.out.println(board);
    	}
    }
    
	//This is a constant for the diagonal length
	//To access the length of the main diagonal use dlen[7]
	//The first diagonal is the zero-th.
	static final int DLEN[] = {1,2,3,4,5,6,7,8,7,6,5,4,3,2,1};
 
    private static long getA1H8diag(int diagonal, short b){  // 1/2/2010 - b used to be a byte
	
    //"diagonal" is the diagonal that we want to set to "b"
    //"diagonal" is in range 0..14 inclusive 
    //"diagonal" corresponds to these diagonals dnum = {diagonal squares)
    // 	0={7}, 1={6,15}, 2={5,14,23},...,7={0,9,18,27,36,45,54,63},...,14={56}
    	
        int dstartsq[] = {7,6,5,4,3,2,1,0,8,16,24,32,40,48,56}; //should have 15 elements
        //TODO: could replace above with (for clarity maybe??) 
        // {H1,G1,F1,E1,D1,C1,B1,A1,A2,A3,A4,A5,A6,A7,A8}
        
        //access length of diagonal dnum by DLEN[dnum]
        //dstartsq is the diagonal starting square
        
        //set the bits set in b to the diagonal dnum in a.
        long board = 0;
        int len = DLEN[diagonal];
        byte mask = 1;
        int x=0;
        for (int d = dstartsq[diagonal];  x < len; d += 9){
        	boolean something = (b & (mask << x++)) != 0;
            if(something) 
            	board |= (1L << d);
        }
        //cout << "\t\tdnum: " << dnum << "\t loops: " << x << endl;
        return board;
    }


	
	
	public void testByteBitShifting(){
		byte piece = 1;

		piece <<= 1;
		assertEquals(0x02, piece);
		piece <<= 1;
		assertEquals(0x04, piece);
		piece <<= 1;
		assertEquals(0x08, piece);
		piece <<= 1;
		assertEquals(0x10, piece);
		piece <<= 1;
		assertEquals(0x20, piece);
		piece <<= 1;
		assertEquals(0x40, piece);
		piece <<= 1;
		assertEquals((byte)0x80, piece); //gets interpreted as int when high bit is set, hence the cast
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
	
	private static void printMove(int move){
	    int from, to, mov, cap, pro;
	    from = move & 0x3F;  //grab from square (6 bits)
	    to = (move >> 6) & 0x3F; //grab to square (6 bits)
	    mov = (move >> 12) & 0x7; //grab moving piece (3bits)
	    cap = (move >> 15) & 0x7; //grab captured piece (3bits)
	    pro = (move >> 18) & 0x7; //grab promotion piece (3bits)

		System.out.println("pro cap mov   to    from ");
		System.out.println("--- --- --- ------ ------");
		System.out.print(getBinaryPadded(pro, 3) + " ");
		System.out.print(getBinaryPadded(cap, 3) + " ");
		System.out.print(getBinaryPadded(mov, 3) + " ");
		System.out.print(getBinaryPadded(to, 6) + " ");
		System.out.print(getBinaryPadded(from, 6) + "\n");
	}

	private static String getBinaryPadded(short move, int minPadding){
		return getBinaryPadded((int)move, minPadding);
	}

	private static String getBinaryPadded(byte move, int minPadding){
		return getBinaryPadded((int)move, minPadding);
	}
	
	private static String getBinaryPadded(int move, int minPadding){
		String b = Integer.toBinaryString(move);
		int reqPadding = minPadding - b.length();
		StringBuffer sb = new StringBuffer();
		while (reqPadding-- > 0){
			sb.append("0");
		}
		sb.append(b);
		return sb.toString();
	}

	
}
