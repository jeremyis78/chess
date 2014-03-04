package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

import org.junit.Assert;

import junit.framework.TestCase;

public class PositionTest extends TestCase {

	public static final String[] FEN;

	static
	{
		FEN = new String[]{
			"rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
			"k1K1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p",
			"8/8/8/8/8/8/8/k1K5",
			"1k1K1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1/1p1p1p1p/p1p1p1p1",
			"q1n5/1P3P2/2P5/8/K7/8/k6P/8"
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
		assertEquals(0L, p.getPieces(Color.WHITE, Pieces.PAWN));
		assertEquals(0L, p.getPieces(Color.WHITE, Pieces.KNIGHT));
		assertEquals(0L, p.getPieces(Color.WHITE, Pieces.BISHOP));
		assertEquals(0L, p.getPieces(Color.WHITE, Pieces.ROOK));
		assertEquals(0L, p.getPieces(Color.WHITE, Pieces.QUEEN));
		assertEquals(0L, p.getPieces(Color.WHITE, Pieces.KING));

		assertEquals(0L, p.getPieces(Color.BLACK, Pieces.PAWN));
		assertEquals(0L, p.getPieces(Color.BLACK, Pieces.KNIGHT));
		assertEquals(0L, p.getPieces(Color.BLACK, Pieces.BISHOP));
		assertEquals(0L, p.getPieces(Color.BLACK, Pieces.ROOK));
		assertEquals(0L, p.getPieces(Color.BLACK, Pieces.QUEEN));
		assertEquals(0L, p.getPieces(Color.BLACK, Pieces.KING));
	}

//	public void testPositionString() {
//		fail("Not yet implemented");
//	}	
//
//	public void testGetPieces() {
//		fail("Not yet implemented");
//	}
//
//	public void testGet() {
//		fail("Not yet implemented");
//	}

	public void testClear() {
		Position p = createStartingPosition();
		
		//Test that there are some pieces set
		assertTrue(0 != p.kingSq[Color.WHITE]);
		assertTrue(0 != p.kingSq[Color.BLACK]);
		assertTrue(PIECE[Bitmap.ROOK] == p.board[Bitmap.A1]);
		assertEquals(0xFFFF00000000FFFFL, p.all[ALL]);
		
		//now clear them
		p.clear();
        for (int color = 0; color < Color.MAXCOLOR; color++){
            for (int piece = 0; piece < Pieces.MAXPIECE; piece++){
            	assertEquals(0L, p.getPieces(color, piece) );
            }
        }
        
		assertEquals(-1, p.kingSq[Color.WHITE]);
		assertEquals(-1, p.kingSq[Color.BLACK]);
		//assertEquals(PIECE[Chess.ROOK] == p.board[Chess.A1]);
		assertEquals(0x0L, p.all[ALL]);
		
	}

	public void testSet()
	{
		Position p = new Position();
		for(int i=0; i<FEN.length; i++)
		{
			p.set(FEN[i]);
			assertEquals(FEN[i], p.getFen());
			p.clear();
		}
		
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
	}

	private Position createStartingPosition() {
		return new Position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
	}

	public void testMovingPiecesQueensGambitAccepted()
	{
		Position p = createStartingPosition();
		assertStartingPosition(p);
		
		// 1. e4
		p.placePiece(Color.WHITE, Pieces.PAWN, Bitmap.E4);
		assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPPPPPP/RNBQKBNR", p.getFen());
		p.erasePiece(Color.WHITE, Pieces.PAWN, Bitmap.E2);
		assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR", p.getFen());
		
		// 1. ... d5
		p.placePiece(Color.BLACK, Pieces.PAWN, Bitmap.D5);
		assertEquals("rnbqkbnr/pppppppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR", p.getFen());
		p.erasePiece(Color.BLACK, Pieces.PAWN, Bitmap.D7);
		assertEquals("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR", p.getFen());
		
		// 2. e4xd5
		p.erasePiece(Color.BLACK, Pieces.PAWN, Bitmap.D5);
		assertEquals("rnbqkbnr/ppp1pppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR", p.getFen());
		p.erasePiece(Color.WHITE, Pieces.PAWN, Bitmap.E4);
		assertEquals("rnbqkbnr/ppp1pppp/8/8/8/8/PPPP1PPP/RNBQKBNR", p.getFen());
		p.placePiece(Color.WHITE, Pieces.PAWN, Bitmap.D5);
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
		assertEquals("a2 b2 c2 d2 e2 f2 g2 h2 ", toSquares(p, Color.WHITE, Pieces.PAWN));
		assertEquals("a1 h1 ", toSquares(p, Color.WHITE, Pieces.ROOK));
		assertEquals("b1 g1 ", toSquares(p, Color.WHITE, Pieces.KNIGHT));
		assertEquals("c1 f1 ", toSquares(p, Color.WHITE, Pieces.BISHOP));
		assertEquals("d1 ", toSquares(p, Color.WHITE, Pieces.QUEEN));
		assertEquals("e1 ", toSquares(p, Color.WHITE, Pieces.KING));

		assertEquals("a7 b7 c7 d7 e7 f7 g7 h7 ", toSquares(p, Color.BLACK, Pieces.PAWN));
		assertEquals("a8 h8 ", toSquares(p, Color.BLACK, Pieces.ROOK));
		assertEquals("b8 g8 ", toSquares(p, Color.BLACK, Pieces.KNIGHT));
		assertEquals("c8 f8 ", toSquares(p, Color.BLACK, Pieces.BISHOP));
		assertEquals("d8 ", toSquares(p, Color.BLACK, Pieces.QUEEN));
		assertEquals("e8 ", toSquares(p, Color.BLACK, Pieces.KING));
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
		
		p.placePiece(Color.WHITE, Pieces.QUEEN, squareToOverwrite);
		assertEquals(PIECE[Bitmap.QUEEN], p.getBoard(squareToOverwrite));
		assertEquals(PIECE[Bitmap.QUEEN], p.getBoard(squareOfWhiteQueen));
		
		p.erasePiece(Color.WHITE, Pieces.QUEEN, squareToOverwrite);
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
		assertInvalid(tooManyRanks, "fen must contain eight ranks");
	}

	public void testSetTooManyFiles()
	{
		String tooManyFiles = "k8/8/8/8/8/8/8/8";
		assertInvalid(tooManyFiles, "pieces and empty squares on rank do not fit on eight files");
	}
	
	public void testSetUnknownPiece()
	{
		String invalidPiece = "k6K/8/8/8/8/8/8/7z";
		assertInvalid(invalidPiece, "board contains invalid piece 'z'");
	}

	public void testSetAdjacentKings()
	{
		String adjacentKings = "8/8/8/8/8/8/6K1/7k";
		assertInvalid(adjacentKings, "board cannot have adjacent kings");
	}

	public void testSetMissingKingOrKings()
	{
		String missingBlackKing = "8/8/8/8/8/8/6K1/8";
		assertInvalid(missingBlackKing, "board is missing one or both kings");

		String missingWhiteKing = "8/8/8/8/8/8/6k1/8";
		assertInvalid(missingWhiteKing, "board is missing one or both kings");
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

	public void testIsValidRankFen(){
		String[] good = 
			new String[]
			           {
						//"8",
						"1p1p1p1p",
						"p1p1p1p1",
						"RNBQKBNR",
						"2P5",
			           };
		for(int i=0; i<good.length; i++)
		{
			try
			{
				Position.validateFiles(good[i]);
			} catch (IllegalArgumentException e){
				fail("should not throw, valid rank fen " + good[i] + " " + e.getMessage());
			}
		}

	
		String[] bad = 
			new String[]
			           {
						"7",
						"p1p1p1p1p1p",
						"1p1p1p1",
						"RNB3BNR",
						"2P4",
			           };
		for(int i=0; i<good.length; i++)
		{
			try
			{
				Position.validateFiles(bad[i]);
				fail("should throw, invalid rank fen " + bad[i]);
			} catch (IllegalArgumentException e){
			}
		}

	}

	public void testPlaceAndEraseKings() {
		Position p = new Position();

		int sq = Bitmap.E1;
		p.placePiece(Color.WHITE, Pieces.KING, sq);
		assertEquals(PIECE[Bitmap.KING], p.getBoard(sq));
		assertEquals(sq, p.kingSq[Color.WHITE]);
		
		//Get the appropriate bitboard masks
        long sqMask = 1L << sq;
        long sqMask90 = 1L << SQ2BIT90R[sq];
        long sqMask45L = 1L << SQ2BIT45L[sq];
        long sqMask45R = 1L << SQ2BIT45R[sq];

		assertEquals(sqMask, p.all[ALL]);
		assertEquals(sqMask90, p.all[ALL90]);
		assertEquals(sqMask45L, p.all[ALL45L]);
		assertEquals(sqMask45R, p.all[ALL45R]);

		p.erasePiece(Color.WHITE, Pieces.KING, sq);


		//Set the black king
		sq = Bitmap.G6;  //Change the placement
		p.placePiece(Color.BLACK, Pieces.KING, sq);
		assertEquals(-PIECE[Bitmap.KING], p.getBoard(sq));
		assertEquals(sq, p.kingSq[Color.BLACK]);
		
		//Get the appropriate bitboard masks
        sqMask = 1L << sq;
        sqMask90 = 1L << SQ2BIT90R[sq];
        sqMask45L = 1L << SQ2BIT45L[sq];
        sqMask45R = 1L << SQ2BIT45R[sq];

		assertEquals(sqMask, p.all[ALL]);
		assertEquals(sqMask90, p.all[ALL90]);
		assertEquals(sqMask45L, p.all[ALL45L]);
		assertEquals(sqMask45R, p.all[ALL45R]);
	}

	public void testPlaceAndErasePieces()
	{
		Position p = new Position();
		int sq = Bitmap.A3;
		for(int color=Color.WHITE; color <= Color.BLACK; color++)
		{
			for(int piece=Pieces.PAWNS; piece<=Pieces.QUEENS; piece++)
			{
				sq++; //just for a good test so we are setting different bits each time
				p.placePiece(color, piece, sq);
				assertCorrectPlacement(p,sq,color,piece,piece);
				p.erasePiece(color, piece, sq);
			}
		}
	}
	
	private static void assertCorrectPlacement(Position p, int sq, int color, int piece, int boardPieceIndex)
	{
		int multiplier = (color == 0 ? 1 : -1);
		assertEquals(multiplier * PIECE[boardPieceIndex], p.board[sq]);

		
		//Get the appropriate bitboard masks
        long sqMask = 1L << sq;
        long sqMask90 = 1L << SQ2BIT90R[sq];
        long sqMask45L = 1L << SQ2BIT45L[sq];
        long sqMask45R = 1L << SQ2BIT45R[sq];

        assertEquals(sqMask, p.pieces[color][piece]);
        assertEquals(sqMask, p.all[ALL]);
		assertEquals(sqMask90, p.all[ALL90]);
		assertEquals(sqMask45L, p.all[ALL45L]);
		assertEquals(sqMask45R, p.all[ALL45R]);
	}

	public void testMovePiece() {
		fail("Not yet implemented");
	}

	public void testIsValid() {
		fail("Not yet implemented");
	}

	public void testIsSameColor() {
		int whitePiece = 1;
		int blackPiece = -1;
		assertTrue("precondition: white pieces are positive", whitePiece > 0);
		assertTrue("precondition: black pieces are negative", blackPiece < 0);
		
		assertTrue(Position.isSameColor(Color.WHITE, whitePiece));
		assertTrue(Position.isSameColor(Color.BLACK, blackPiece));
		assertFalse(Position.isSameColor(Color.WHITE, blackPiece));
		assertFalse(Position.isSameColor(Color.BLACK, whitePiece));
	
	}

	public void testIsEmpty() {
		Position p = createStartingPosition();

		for(int currentSquare = Bitmap.A1;
				currentSquare <= Bitmap.H2;
				currentSquare++)
		{
			
			assertFalse(Position.isEmpty(p.getBoard(currentSquare)));
		}

		for(int currentSquare = Bitmap.A3;
				currentSquare <= Bitmap.H6;
				currentSquare++)
		{
			assertTrue(Position.isEmpty(p.getBoard(currentSquare)));
		}
		
		for(int currentSquare = Bitmap.A7;
				currentSquare <= Bitmap.H8;
				currentSquare++)
		{
			
			assertFalse(Position.isEmpty(p.getBoard(currentSquare)));
		}

	}
	
}
