package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import junit.framework.TestCase;

import org.junit.Assert;

public class PositionTest extends TestCase {
	private static final long EMPTY_BITBOARD = 0L;
//	Position p;
	
	protected void setUp() throws Exception {
		super.setUp();
//		p = new Position();
//		p.clear();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPositionEmpty() {
		Position p = new Position();
		assertEmptyPosition(p);
	}

	public void testClear() {
		Position p = createStartingPosition();
		
		//Test that there are some pieces set
		assertTrue(0 != p.getKingSquare(WHITE));
		assertTrue(0 != p.getKingSquare(BLACK));
		assertEquals(ROOK, p.get(A1).index());
		assertTrue(PIECE[Bitmap.ROOK] == p.getBoard(Bitmap.A1));
		assertEquals(0xFFFF00000000FFFFL, p.getAllPieces(0));// .all[ALL]);
		
		//now clear them
		p.clear();
		assertEmptyPosition(p);
	}

	public void testPlacingTwoWhiteKings()
	{
		Position p = new Position();
		p.placePiece(WHITE, KING, A8);
		try{
			p.placePiece(WHITE, KING, B3);
		} catch (IllegalStateException expected) {
			assertEquals("cannot place white king on b3; already placed on a8; use erasePiece()",
					expected.getMessage());
		}
	}

	public void testMovingTheBlackKing()
	{
		Position p = new Position();
		p.placePiece(BLACK, KING, E3);
		assertPlaced(p, BLACK, KING, E3);
		try{
			p.placePiece(BLACK, KING, G6);
		} catch (IllegalStateException expected) {
			assertEquals("cannot place black king on g6; already placed on e3; use erasePiece()",
					expected.getMessage());
		}
		p.erasePiece(E3);
		assertErased(p, BLACK, KING, E3);
		p.placePiece(BLACK, KING, G6);
		assertPlaced(p, BLACK, KING, G6);
	}

	public void testPlacingKnightWhereKingIs()
	{
		Position p = new Position();
		p.placePiece(BLACK, KING, E3);
		assertPlaced(p, BLACK, KING, E3);
		try{
			p.placePiece(BLACK, KNIGHT, E3);
			fail("placing knight on same square should throw");
		} catch (IllegalStateException expected) {
			assertEquals("e3 is already occupied",
					expected.getMessage());
		}
		assertPlaced(p, BLACK, KING, E3);
	}

	public void testPlacingKingWherePawnIs()
	{
		Position p = new Position();
		p.placePiece(BLACK, PAWN, G7);
		p.placePiece(BLACK, PAWN, D5);
		assertPlaced(p, BLACK, PAWN, D5);
		try {
			p.placePiece(BLACK, KING, D5);
		} catch (IllegalStateException expected) {
			assertEquals("d5 is already occupied", expected.getMessage());
		}
		assertPlaced(p, BLACK, PAWN, D5);
	}

	public void testPlacingBishopWhereKingIs()
	{
		Position p = new Position();
		p.placePiece(WHITE, KING, F7);
		assertPlaced(p, WHITE, KING, F7);
		try {
			p.placePiece(WHITE, BISHOP, F7);
		} catch (IllegalStateException expected) {
			assertEquals("f7 is already occupied", expected.getMessage());
		}
		assertPlaced(p, WHITE, KING, F7);
	}

	public void testErasePieceSucceedsEvenWhenSquareIsAlreadyEmpty() {
		Position p = new Position();
		try {
			p.erasePiece(E4);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("should not throw if square is already empty");
		}
	}
	
	public void testPlaceAndEraseKings() {
		Position p = new Position();

		int sq = E1;
		p.placePiece(WHITE, KING, sq);
		assertEquals(PIECE[KING], p.getBoard(sq));
		assertEquals(sq, p.getKingSquare(WHITE));
		
		//Get the appropriate bitboard masks
        long sqMask = 1L << sq;
        long sqMask90 = 1L << SQ2BIT90R[sq];
        long sqMask45L = 1L << SQ2BIT45L[sq];
        long sqMask45R = 1L << SQ2BIT45R[sq];

		assertEquals(sqMask, p.getAllPieces(0));
		assertEquals(sqMask90, p.getAllPieces(90));
		assertEquals(sqMask45L, p.getAllPieces(-45));
		assertEquals(sqMask45R, p.getAllPieces(45));

		p.erasePiece(sq);

		//Set the black king
		sq = Bitmap.G6;  //Change the placement
		p.placePiece(Bitmap.BLACK, KING, sq);
		assertEquals(-PIECE[KING], p.getBoard(sq));
		assertEquals(sq, p.getKingSquare(BLACK));
		
		//Get the appropriate bitboard masks
        sqMask = 1L << sq;
        sqMask90 = 1L << SQ2BIT90R[sq];
        sqMask45L = 1L << SQ2BIT45L[sq];
        sqMask45R = 1L << SQ2BIT45R[sq];

		assertEquals(sqMask, p.getAllPieces(0));
		assertEquals(sqMask90, p.getAllPieces(90));
		assertEquals(sqMask45L, p.getAllPieces(-45));
		assertEquals(sqMask45R, p.getAllPieces(45));
	}

	public void testPlaceAndErasePieces()
	{
		Position p = new Position();
		int sq = Bitmap.A3;
		for(int color=Bitmap.WHITE; color <= Bitmap.BLACK; color++)
		{
			for(int piece=PAWN; piece<=QUEEN; piece++)
			{
				sq++; //just for a good test so we are setting different bits each time
				p.placePiece(color, piece, sq);
				assertPlaced(p,color,piece,sq);
				p.erasePiece(sq);
			}
		}
		assertEmptyPosition(p);

	}

	public void testSetPieces()
	{
		long bitmap = 6L;
		Position p = new Position();
		p.setPieces(Bitmap.WHITE, PAWN, bitmap);
		assertEquals(bitmap, p.getPawns(WHITE));
		assertEquals(bitmap, p.getPieces(WHITE));
	}
	
	public void testGettingAnInvalidAllPiecesBitboard()
	{
		Position p = new Position();
		try {
			p.getAllPieces(37);
		} catch (IllegalArgumentException expected) {
			assertEquals("invalid rotation 37; rotation must be -45, 0, 45 or 90", expected.getMessage());
		}
	}
	

	public void testMovingPiecesQueensGambitAccepted()
	{
		Position p = createStartingPosition();
		assertStartingPosition(p);
		
		// 1. e4
		p.placePiece(WHITE, PAWN, E4);
		assertPlaced(p, WHITE, PAWN, E4);
		p.erasePiece(Bitmap.E2);
		assertErased(p, WHITE, PAWN, E2);
		
		// 1. ... d5
		p.placePiece(BLACK, PAWN, D5);
		assertPlaced(p, BLACK, PAWN, D5);
		p.erasePiece(Bitmap.D7);
		assertErased(p, BLACK, PAWN, D7);
		
		// 2. e4xd5
		p.erasePiece(D5);
		assertErased(p, BLACK, PAWN, D5);
		p.erasePiece(E4);
		assertErased(p, WHITE, PAWN, E4);
		p.placePiece(WHITE, PAWN, D5);
		assertPlaced(p, WHITE, PAWN, D5);

		String expectedBoard = 
                "   -----------------\n" +
				"8 | r n b q k b n r |\n" +
				"7 | p p p - p p p p |\n" +
				"6 | - - - - - - - - |\n" +
				"5 | - - - P - - - - |\n" +
				"4 | - - - - - - - - |\n" +
				"3 | - - - - - - - - |\n" +
				"2 | P P P P - P P P |\n" +
				"1 | R N B Q K B N R |\n" +
				"   -----------------\n" +
				"    a b c d e f g h\n";
		Assert.assertEquals(expectedBoard, new Displayer().formatBoard(p));

		String expectedBitboard =
                "   -----------------\n" +
				"8 | r n b q - b n r |\n" +
				"7 | p p p - p p p p |\n" +
				"6 | - - - - - - - - |\n" +
				"5 | - - - P - - - - |\n" +
				"4 | - - - - - - - - |\n" +
				"3 | - - - - - - - - |\n" +
				"2 | P P P P - P P P |\n" +
				"1 | R N B Q - B N R |\n" +
				"   -----------------\n" +
				"    a b c d e f g h\n";
		Assert.assertEquals(expectedBitboard, new BitboardDisplayer().formatBoard(p));
	}
	
	public void testIsSameColor() {
		int whitePiece = 1;
		int blackPiece = -1;
		assertTrue("precondition: white pieces are positive", whitePiece > 0);
		assertTrue("precondition: black pieces are negative", blackPiece < 0);
		
		assertTrue(Position.isSameColor(Bitmap.WHITE, whitePiece));
		assertTrue(Position.isSameColor(Bitmap.BLACK, blackPiece));
		assertFalse(Position.isSameColor(Bitmap.WHITE, blackPiece));
		assertFalse(Position.isSameColor(Bitmap.BLACK, whitePiece));
	}

	public void testIsAndIsNotEmpty() {
		Position p = createStartingPosition();

		for(int currentSquare = Bitmap.A1;
				currentSquare <= Bitmap.H2;
				currentSquare++)
		{
			
			assertFalse(p.isEmpty(currentSquare));
			assertTrue(p.isNotEmpty(currentSquare));
		}

		for(int currentSquare = Bitmap.A3;
				currentSquare <= Bitmap.H6;
				currentSquare++)
		{
			assertTrue(p.isEmpty(currentSquare));
			assertFalse(p.isNotEmpty(currentSquare));
		}
		
		for(int currentSquare = Bitmap.A7;
				currentSquare <= Bitmap.H8;
				currentSquare++)
		{
			assertFalse(p.isEmpty(currentSquare));
			assertTrue(p.isNotEmpty(currentSquare));
		}

	}

	private void assertEmptyPosition(Position p) {
		assertEquals(EMPTY_BITBOARD, p.getPieces(WHITE));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.WHITE, PAWN));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.WHITE, KNIGHT));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.WHITE, BISHOP));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.WHITE, ROOK));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.WHITE, QUEEN));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.WHITE, KING));
		assertEquals(EMPTY_BITBOARD, p.getPawns(Bitmap.WHITE));
		assertEquals(EMPTY_BITBOARD, p.getKnights(Bitmap.WHITE));
		assertEquals(EMPTY_BITBOARD, p.getBishops(Bitmap.WHITE));
		assertEquals(EMPTY_BITBOARD, p.getRooks(Bitmap.WHITE));
		assertEquals(EMPTY_BITBOARD, p.getQueens(Bitmap.WHITE));
		assertEquals(EMPTY_BITBOARD, p.getKing(Bitmap.WHITE));

		assertEquals(EMPTY_BITBOARD, p.getPieces(BLACK));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.BLACK, PAWN));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.BLACK, KNIGHT));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.BLACK, BISHOP));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.BLACK, ROOK));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.BLACK, QUEEN));
		assertEquals(EMPTY_BITBOARD, p.getPieces(Bitmap.BLACK, KING));
		assertEquals(EMPTY_BITBOARD, p.getOpponentPawns(Bitmap.WHITE));
		assertEquals(EMPTY_BITBOARD, p.getOpponentKnights(Bitmap.WHITE));
		assertEquals(EMPTY_BITBOARD, p.getOpponentBishops(Bitmap.WHITE));
		assertEquals(EMPTY_BITBOARD, p.getOpponentRooks(Bitmap.WHITE));
		assertEquals(EMPTY_BITBOARD, p.getOpponentQueens(Bitmap.WHITE));
		assertEquals(EMPTY_BITBOARD, p.getOpponentKing(Bitmap.WHITE));
        
		assertFalse(p.isKingPlaced(WHITE));
		assertEquals(-1, p.getKingSquare(WHITE));
		assertFalse(p.isKingPlaced(BLACK));
		assertEquals(-1, p.getKingSquare(BLACK));
		assertEquals(EMPTY_BITBOARD, p.getAllPieces(0));
		assertEquals(EMPTY_BITBOARD, p.getAllPieces(90));
		assertEquals(EMPTY_BITBOARD, p.getAllPieces(45));
		assertEquals(EMPTY_BITBOARD, p.getAllPieces(-45));
		
		for(int square = Bitmap.A1; square <= Bitmap.H8; square++)
		{
			assertEquals(BOARD_EMPTY_SQUARE, p.getBoard(square));
		}
	}
	
	private static void assertPlaced(Position p, int color, int piece, int sq)
	{
		int multiplier = (color == 0 ? 1 : -1);
		assertEquals(multiplier * PIECE[piece], p.getBoard(sq));
		assertEquals(multiplier * PIECE[piece], p.get(sq).encodedByColor());
		//Get the appropriate bitboard masks
        long sqMask = 1L << sq;
        long sqMask90 = 1L << SQ2BIT90R[sq];
        long sqMask45L = 1L << SQ2BIT45L[sq];
        long sqMask45R = 1L << SQ2BIT45R[sq];
        String bitNotSetMsg = "bit "+sq+" was not set";
		assertEquals(bitNotSetMsg, sqMask, p.getPieces(color,piece) & sqMask);
		assertEquals(bitNotSetMsg, sqMask, p.getPieces(color) & sqMask);
        assertEquals(bitNotSetMsg, sqMask, p.getAllPieces(0) & sqMask);
		assertEquals(bitNotSetMsg, sqMask90, p.getAllPieces(90) & sqMask90);
		assertEquals(bitNotSetMsg, sqMask45L, p.getAllPieces(-45) & sqMask45L);
		assertEquals(bitNotSetMsg, sqMask45R, p.getAllPieces(45) & sqMask45R);
		if(piece == KING)
		{
			assertEquals("king should be placed", Square.named(sq), Square.named(p.getKingSquare(color)));
			assertEquals(bitNotSetMsg, sqMask, p.getKing(color));
		}
	}

	private static void assertErased(Position p, int color, int piece, int sq)
	{
		assertEquals(Bitmap.BOARD_EMPTY_SQUARE, p.getBoard(sq));
		assertEquals(NONE, p.get(sq).index());
		//Get the appropriate bitboard masks
        long sqMask = 1L << sq;
        long sqMask90 = 1L << SQ2BIT90R[sq];
        long sqMask45L = 1L << SQ2BIT45L[sq];
        long sqMask45R = 1L << SQ2BIT45R[sq];
        String bitNotClearedMsg = "bit "+sq+" was not cleared";
		assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getPieces(color,piece) & sqMask);
        assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getPieces(color) & sqMask);
        assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getAllPieces(0) & sqMask);
		assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getAllPieces(90) & sqMask90);
		assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getAllPieces(-45) & sqMask45L);
		assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getAllPieces(45) & sqMask45R);
		if(piece == KING)
		{
			assertEquals("king should be unplaced", "", Square.named(p.getKingSquare(color)));
			assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getKing(color) & sqMask);
		}
	}
	
	private void assertStartingPosition(Position p) {
		assertEquals("a2 b2 c2 d2 e2 f2 g2 h2 ", toSquares(p, Bitmap.WHITE, PAWN));
		assertEquals("a1 h1 ", toSquares(p, Bitmap.WHITE, ROOK));
		assertEquals("b1 g1 ", toSquares(p, Bitmap.WHITE, KNIGHT));
		assertEquals("c1 f1 ", toSquares(p, Bitmap.WHITE, BISHOP));
		assertEquals("d1 ", toSquares(p, Bitmap.WHITE, QUEEN));
		assertEquals("e1 ", toSquares(p, Bitmap.WHITE, KING));

		assertEquals("a7 b7 c7 d7 e7 f7 g7 h7 ", toSquares(p, Bitmap.BLACK, PAWN));
		assertEquals("a8 h8 ", toSquares(p, Bitmap.BLACK, ROOK));
		assertEquals("b8 g8 ", toSquares(p, Bitmap.BLACK, KNIGHT));
		assertEquals("c8 f8 ", toSquares(p, Bitmap.BLACK, BISHOP));
		assertEquals("d8 ", toSquares(p, Bitmap.BLACK, QUEEN));
		assertEquals("e8 ", toSquares(p, Bitmap.BLACK, KING));
	}

	private String toSquares(Position p, int colorIndex, int piecesIndex) {
		return Util.displaySquaresStr(p.getPieces(colorIndex, piecesIndex));
	}

	private static Position createStartingPosition() {
		return FenParser.parsePieceBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
	}

}
