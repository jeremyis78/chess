package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import junit.framework.TestCase;

import org.junit.Assert;

public class PositionTest extends TestCase {

	public static final String[] FEN;

	static
	{
		FEN = new String[]{
			"rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
			"k1K1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p",
			"8/8/8/8/8/8/8/k1K5",
			"1k1K1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1",
			"q1n5/1P3P2/2P5/8/K7/8/k6P/8",
            "1K1Q1R1B/1N4P1/8/8/8/8/1k1q1r1b/1n4p1"
			};
	}
	
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
		assertEquals(0L, p.getPieces(Bitmap.WHITE, PAWN));
		assertEquals(0L, p.getPieces(Bitmap.WHITE, KNIGHT));
		assertEquals(0L, p.getPieces(Bitmap.WHITE, BISHOP));
		assertEquals(0L, p.getPieces(Bitmap.WHITE, ROOK));
		assertEquals(0L, p.getPieces(Bitmap.WHITE, QUEEN));
		assertEquals(0L, p.getPieces(Bitmap.WHITE, KING));
		assertEquals(0L, p.getPawns(Bitmap.WHITE));
		assertEquals(0L, p.getKnights(Bitmap.WHITE));
		assertEquals(0L, p.getBishops(Bitmap.WHITE));
		assertEquals(0L, p.getRooks(Bitmap.WHITE));
		assertEquals(0L, p.getQueens(Bitmap.WHITE));
		assertEquals(0L, p.getKing(Bitmap.WHITE));

		assertEquals(0L, p.getPieces(Bitmap.BLACK, PAWN));
		assertEquals(0L, p.getPieces(Bitmap.BLACK, KNIGHT));
		assertEquals(0L, p.getPieces(Bitmap.BLACK, BISHOP));
		assertEquals(0L, p.getPieces(Bitmap.BLACK, ROOK));
		assertEquals(0L, p.getPieces(Bitmap.BLACK, QUEEN));
		assertEquals(0L, p.getPieces(Bitmap.BLACK, KING));
		assertEquals(0L, p.getOpponentPawns(Bitmap.WHITE));
		assertEquals(0L, p.getOpponentKnights(Bitmap.WHITE));
		assertEquals(0L, p.getOpponentBishops(Bitmap.WHITE));
		assertEquals(0L, p.getOpponentRooks(Bitmap.WHITE));
		assertEquals(0L, p.getOpponentQueens(Bitmap.WHITE));
		assertEquals(0L, p.getOpponentKing(Bitmap.WHITE));
		
		for(int square = Bitmap.A1; square <= Bitmap.H8; square++)
		{
			assertEquals(BOARD_EMPTY_SQUARE, p.getBoard(square));
		}
	}

	public void testClear() {
		Position p = createStartingPosition();
		
		//Test that there are some pieces set
		assertTrue(0 != p.getKingSquare(WHITE));
		assertTrue(0 != p.getKingSquare(BLACK));
		assertTrue(PIECE[Bitmap.ROOK] == p.getBoard(Bitmap.A1));
		assertEquals(0xFFFF00000000FFFFL, p.getAllPieces(0));// .all[ALL]);
		
		//now clear them
		p.clear();
		assertEquals(0L, p.getPieces(Bitmap.WHITE, PAWN));
		assertEquals(0L, p.getPieces(Bitmap.WHITE, KNIGHT));
		assertEquals(0L, p.getPieces(Bitmap.WHITE, BISHOP));
		assertEquals(0L, p.getPieces(Bitmap.WHITE, ROOK));
		assertEquals(0L, p.getPieces(Bitmap.WHITE, QUEEN));
		assertEquals(0L, p.getPieces(Bitmap.WHITE, KING));
		assertEquals(0L, p.getPawns(Bitmap.WHITE));
		assertEquals(0L, p.getKnights(Bitmap.WHITE));
		assertEquals(0L, p.getBishops(Bitmap.WHITE));
		assertEquals(0L, p.getRooks(Bitmap.WHITE));
		assertEquals(0L, p.getQueens(Bitmap.WHITE));
		assertEquals(0L, p.getKing(Bitmap.WHITE));

		assertEquals(0L, p.getPieces(Bitmap.BLACK, PAWN));
		assertEquals(0L, p.getPieces(Bitmap.BLACK, KNIGHT));
		assertEquals(0L, p.getPieces(Bitmap.BLACK, BISHOP));
		assertEquals(0L, p.getPieces(Bitmap.BLACK, ROOK));
		assertEquals(0L, p.getPieces(Bitmap.BLACK, QUEEN));
		assertEquals(0L, p.getPieces(Bitmap.BLACK, KING));
		assertEquals(0L, p.getOpponentPawns(Bitmap.WHITE));
		assertEquals(0L, p.getOpponentKnights(Bitmap.WHITE));
		assertEquals(0L, p.getOpponentBishops(Bitmap.WHITE));
		assertEquals(0L, p.getOpponentRooks(Bitmap.WHITE));
		assertEquals(0L, p.getOpponentQueens(Bitmap.WHITE));
		assertEquals(0L, p.getOpponentKing(Bitmap.WHITE));
        
		assertEquals(-1, p.getKingSquare(WHITE));
		assertEquals(-1, p.getKingSquare(BLACK));
		assertEquals(0x0L, p.getAllPieces(0));
		
	}

	public void testSetAndGetFen()
	{
		Position p = new Position();
		for(int i=0; i<FEN.length; i++)
		{
			p.set(FEN[i]);
			assertEquals(FEN[i], p.getFen());
			p.clear();
		}
		
	}
	
	public void testSetPieces()
	{
		long bitmap = 6L;
		Position p = new Position();
		p.setPieces(Bitmap.WHITE, PAWN, bitmap);
	}
	
	public void testNewStartingPosition()
	{
		Position p = createStartingPosition();
		assertStartingPosition(p);
		String expectedBoard = 
                        "   -----------------\n" +
						"8 | r n b q k b n r |\n" +
						"7 | p p p p p p p p |\n" +
						"6 | - - - - - - - - |\n" +
						"5 | - - - - - - - - |\n" +
						"4 | - - - - - - - - |\n" +
						"3 | - - - - - - - - |\n" +
						"2 | P P P P P P P P |\n" +
						"1 | R N B Q K B N R |\n" +
						"   -----------------\n" +
						"    a b c d e f g h\n";
		Assert.assertEquals(expectedBoard, new Displayer().formatBoard(p));

		String expectedBitboard =
                        "   -----------------\n" +
						"8 | r n b q - b n r |\n" +
						"7 | p p p p p p p p |\n" +
						"6 | - - - - - - - - |\n" +
						"5 | - - - - - - - - |\n" +
						"4 | - - - - - - - - |\n" +
						"3 | - - - - - - - - |\n" +
						"2 | P P P P P P P P |\n" +
						"1 | R N B Q - B N R |\n" +
						"   -----------------\n" +
						"    a b c d e f g h\n";
		Assert.assertEquals(expectedBitboard, new BitboardDisplayer().formatBoard(p));
		
		assertEquals(1L<<Bitmap.E1, p.getKing(Bitmap.WHITE));
		assertEquals(1L<<Bitmap.E8, p.getOpponentKing(Bitmap.WHITE));
	}

	private Position createStartingPosition() {
		return new Position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
	}

	public void testMovingPiecesQueensGambitAccepted()
	{
		Position p = createStartingPosition();
		assertStartingPosition(p);
		
		// 1. e4
		p.placePiece(Bitmap.WHITE, PAWN, Bitmap.E4);
		assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPPPPPP/RNBQKBNR", p.getFen());
		p.erasePiece(Bitmap.E2);
		assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR", p.getFen());
		
		// 1. ... d5
		p.placePiece(Bitmap.BLACK, PAWN, Bitmap.D5);
		assertEquals("rnbqkbnr/pppppppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR", p.getFen());
		p.erasePiece(Bitmap.D7);
		assertEquals("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR", p.getFen());
		
		// 2. e4xd5
		p.erasePiece(Bitmap.D5);
		assertEquals("rnbqkbnr/ppp1pppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR", p.getFen());
		p.erasePiece(Bitmap.E4);
		assertEquals("rnbqkbnr/ppp1pppp/8/8/8/8/PPPP1PPP/RNBQKBNR", p.getFen());
		p.placePiece(Bitmap.WHITE, PAWN, Bitmap.D5);
		assertEquals("rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR", p.getFen());

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

	public void testPlacePieceOverwritesExistingPiece()
	{
		Position p = createStartingPosition();
		
		int squareToOverwrite = Bitmap.A1;
		int squareOfWhiteQueen = Bitmap.D1;
		assertEquals(PIECE[Bitmap.ROOK], p.getBoard(squareToOverwrite));
		assertEquals(PIECE[Bitmap.QUEEN], p.getBoard(squareOfWhiteQueen));
		
		p.placePiece(Bitmap.WHITE, QUEEN, squareToOverwrite);
		assertEquals(PIECE[Bitmap.QUEEN], p.getBoard(squareToOverwrite));
		assertEquals(PIECE[Bitmap.QUEEN], p.getBoard(squareOfWhiteQueen));
		
		p.erasePiece(squareToOverwrite); //erase queen
		assertEquals(BOARD_EMPTY_SQUARE, p.getBoard(squareToOverwrite));
		assertEquals(PIECE[Bitmap.QUEEN], p.getBoard(squareOfWhiteQueen));
	}
	
	public void testSetTooManyWhiteKings()
	{
		String tooManyWhiteKingsOnDifferentRanks = "2K5/8/8/8/8/8/8/7K";
		assertInvalid(tooManyWhiteKingsOnDifferentRanks, "board has too many white kings");
	}

	public void testSetTooManyBlackKings()
	{
		String tooManyBlackKingsOnSameRank = "8/k6k/8/8/8/8/8/8";
		assertInvalid(tooManyBlackKingsOnSameRank, "board has too many black kings");
	}

	public void testSetTooManyRanks()
	{
		String tooManyRanks = "8/8/8/8/8/8/8/8/8";
		assertInvalid(tooManyRanks, "board must contain eight ranks");
	}

	public void testSetTooManyFiles()
	{
		String tooManyFiles = "k8/8/8/8/8/8/8/8";
		assertInvalid(tooManyFiles, "board pieces and empty squares on rank #8 do not fit on eight files: k8");
	}
	
	public void testSetUnknownPiece()
	{
		String invalidPiece = "k6K/8/8/8/8/8/8/7z";
		assertInvalid(invalidPiece, "board contains invalid piece 'z'; allowed piece characters are: KkQqRrBbNnPp");
	}

	private void assertInvalid(String position, String expectedError) {
		try {
			setupPosition(position);
			fail(position+" did not throw '"+expectedError+"'");
		} catch (IllegalArgumentException e) {
			assertEquals(expectedError, e.getMessage());
		}	
	}

	private void setupPosition(String FENString) {
		Position p = new Position();
		p.set(FENString);
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
				assertCorrectPlacement(p,sq,color,piece,piece);
				p.erasePiece(sq);
			}
		}
	}
	
	private static void assertCorrectPlacement(Position p, int sq, int color, int piece, int boardPieceIndex)
	{
		int multiplier = (color == 0 ? 1 : -1);
		assertEquals(multiplier * PIECE[boardPieceIndex], p.getBoard(sq));

		
		//Get the appropriate bitboard masks
        long sqMask = 1L << sq;
        long sqMask90 = 1L << SQ2BIT90R[sq];
        long sqMask45L = 1L << SQ2BIT45L[sq];
        long sqMask45R = 1L << SQ2BIT45R[sq];

        assertEquals(sqMask, p.getPieces(color,piece));
        assertEquals(sqMask, p.getAllPieces(0));
		assertEquals(sqMask90, p.getAllPieces(90));
		assertEquals(sqMask45L, p.getAllPieces(-45));
		assertEquals(sqMask45R, p.getAllPieces(45));
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

	public void testIsEmpty() {
		Position p = createStartingPosition();

		for(int currentSquare = Bitmap.A1;
				currentSquare <= Bitmap.H2;
				currentSquare++)
		{
			
			assertFalse(p.isEmpty(currentSquare));
		}

		for(int currentSquare = Bitmap.A3;
				currentSquare <= Bitmap.H6;
				currentSquare++)
		{
			assertTrue(p.isEmpty(currentSquare));
		}
		
		for(int currentSquare = Bitmap.A7;
				currentSquare <= Bitmap.H8;
				currentSquare++)
		{
			
			assertFalse(p.isEmpty(currentSquare));
		}

	}
	
}
