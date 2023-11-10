package com.jeremybrooks.base;

import static com.jeremybrooks.chess.base.Bitmap.*;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.base.Square;
import junit.framework.TestCase;

import org.junit.Assert;

import com.jeremybrooks.chess.util.FenParser;
import com.jeremybrooks.chess.util.Util;

public class PositionTest extends TestCase {
    private static final long EMPTY_BITBOARD = 0L;
    
    protected void setUp() throws Exception {
        super.setUp();
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
        assertTrue(0 != p.getKingSquare(Piece.WHITE));
        assertTrue(0 != p.getKingSquare(Piece.BLACK));
        assertEquals(Piece.ROOK, p.get(A1).index());
        assertTrue(Piece.ENCODED[Piece.ROOK] == p.getBoard(Bitmap.A1));
        assertEquals(0xFFFF00000000FFFFL, p.getOccupied(0));// .all[ALL]);
        
        //now clear them
        p.clear();
        assertEmptyPosition(p);
    }

    public void testPlacingTwoWhiteKings()
    {
        Position p = new Position();
        p.placePiece(Piece.WHITE, Piece.KING, A8);
        try{
            p.placePiece(Piece.WHITE, Piece.KING, B3);
        } catch (IllegalStateException expected) {
            assertEquals("cannot place white king on b3; already placed on a8; use erasePiece()",
                    expected.getMessage());
        }
      //Position.validate(p); //TODO: I believe this exposes bugs so don't enable this until I can troubleshoot.
    }

    public void testMovingTheBlackKing()
    {
        Position p = new Position();
        p.placePiece(Piece.BLACK, Piece.KING, E3);
        assertPlaced(p, Piece.BLACK, Piece.KING, E3);
        try{
            p.placePiece(Piece.BLACK, Piece.KING, G6);
        } catch (IllegalStateException expected) {
            assertEquals("cannot place black king on g6; already placed on e3; use erasePiece()",
                    expected.getMessage());
        }
        p.erasePiece(E3);
        assertErased(p, Piece.BLACK, Piece.KING, E3);
        p.placePiece(Piece.BLACK, Piece.KING, G6);
        assertPlaced(p, Piece.BLACK, Piece.KING, G6);
    }

    public void testPlacingKnightWhereKingIs()
    {
        Position p = new Position();
        p.placePiece(Piece.BLACK, Piece.KING, E3);
        assertPlaced(p, Piece.BLACK, Piece.KING, E3);
        try{
            p.placePiece(Piece.BLACK, Piece.KNIGHT, E3);
            fail("placing knight on same square should throw");
        } catch (IllegalStateException expected) {
            assertEquals("e3 is already occupied",
                    expected.getMessage());
        }
        assertPlaced(p, Piece.BLACK, Piece.KING, E3);
    }

    public void testPlacingKingWherePawnIs()
    {
        Position p = new Position();
        p.placePiece(Piece.BLACK, Piece.PAWN, G7);
        p.placePiece(Piece.BLACK, Piece.PAWN, D5);
        assertPlaced(p, Piece.BLACK, Piece.PAWN, D5);
        try {
            p.placePiece(Piece.BLACK, Piece.KING, D5);
        } catch (IllegalStateException expected) {
            assertEquals("d5 is already occupied", expected.getMessage());
        }
        assertPlaced(p, Piece.BLACK, Piece.PAWN, D5);
    }

    public void testPlacingBishopWhereKingIs()
    {
        Position p = new Position();
        p.placePiece(Piece.WHITE, Piece.KING, F7);
        assertPlaced(p, Piece.WHITE, Piece.KING, F7);
        try {
            p.placePiece(Piece.WHITE, Piece.BISHOP, F7);
        } catch (IllegalStateException expected) {
            assertEquals("f7 is already occupied", expected.getMessage());
        }
        assertPlaced(p, Piece.WHITE, Piece.KING, F7);
    }

    public void testErasePieceSucceedsEvenWhenSquareIsAlreadyEmpty() {
        Position p = new Position();
        try {
            p.erasePiece(E4);
        } catch (ArrayIndexOutOfBoundsException e) {
            fail("should not throw if square is already empty");
        }
        Position.validate(p);
    }
    
    public void testPlaceAndEraseKings() {
        Position p = new Position();

        int sq = E1;
        p.placePiece(Piece.WHITE, Piece.KING, sq);
        assertEquals(Piece.ENCODED[Piece.KING], p.getBoard(sq));
        assertEquals(sq, p.getKingSquare(Piece.WHITE));
        
        //Get the appropriate bitboard masks
        long sqMask = 1L << sq;
        long sqMask90 = 1L << SQ2BIT90R[sq];
        long sqMask45L = 1L << SQ2BIT45L[sq];
        long sqMask45R = 1L << SQ2BIT45R[sq];

        assertEquals(sqMask, p.getOccupied(0));
        assertEquals(sqMask90, p.getOccupied(90));
        assertEquals(sqMask45L, p.getOccupied(-45));
        assertEquals(sqMask45R, p.getOccupied(45));

        p.erasePiece(sq);

        //Set the black king
        sq = Bitmap.G6;  //Change the placement
        p.placePiece(Piece.BLACK, Piece.KING, sq);
        assertEquals(-Piece.ENCODED[Piece.KING], p.getBoard(sq));
        assertEquals(sq, p.getKingSquare(Piece.BLACK));
        
        //Get the appropriate bitboard masks
        sqMask = 1L << sq;
        sqMask90 = 1L << SQ2BIT90R[sq];
        sqMask45L = 1L << SQ2BIT45L[sq];
        sqMask45R = 1L << SQ2BIT45R[sq];

        assertEquals(sqMask, p.getOccupied(0));
        assertEquals(sqMask90, p.getOccupied(90));
        assertEquals(sqMask45L, p.getOccupied(-45));
        assertEquals(sqMask45R, p.getOccupied(45));
    }

    public void testPlaceAndErasePieces()
    {
        Position p = new Position();
        int sq = Bitmap.A3;
        for(int color=Piece.WHITE; color <= Piece.BLACK; color++)
        {
            for(int piece=Piece.PAWN; piece<=Piece.QUEEN; piece++)
            {
                sq++; //just for a good test so we are setting different bits each time
                p.placePiece(color, piece, sq);
                assertPlaced(p,color,piece,sq);
                p.erasePiece(sq);
            }
        }
        assertEmptyPosition(p);

    }

    public void testGettingAnInvalidAllPiecesBitboard()
    {
        Position p = new Position();
        try {
            p.getOccupied(37);
        } catch (IllegalArgumentException expected) {
            assertEquals("invalid rotation 37; rotation must be -45, 0, 45 or 90", expected.getMessage());
        }
    }
    

    public void testMovingPiecesQueensGambitAccepted()
    {
        Position p = createStartingPosition();
        assertStartingPosition(p);
        
        // 1. e4
        p.placePiece(Piece.WHITE, Piece.PAWN, E4);
        assertPlaced(p, Piece.WHITE, Piece.PAWN, E4);
        p.erasePiece(Bitmap.E2);
        assertErased(p, Piece.WHITE, Piece.PAWN, E2);
        
        // 1. ... d5
        p.placePiece(Piece.BLACK, Piece.PAWN, D5);
        assertPlaced(p, Piece.BLACK, Piece.PAWN, D5);
        p.erasePiece(Bitmap.D7);
        assertErased(p, Piece.BLACK, Piece.PAWN, D7);
        
        // 2. e4xd5
        p.erasePiece(D5);
        assertErased(p, Piece.BLACK, Piece.PAWN, D5);
        p.erasePiece(E4);
        assertErased(p, Piece.WHITE, Piece.PAWN, E4);
        p.placePiece(Piece.WHITE, Piece.PAWN, D5);
        assertPlaced(p, Piece.WHITE, Piece.PAWN, D5);
    }
    
    public void testIsSameColor() {
        int whitePiece = 1;
        int blackPiece = -1;
        assertTrue("precondition: white pieces are positive", whitePiece > 0);
        assertTrue("precondition: black pieces are negative", blackPiece < 0);
        
        assertTrue(Position.isSameColor(Piece.WHITE, whitePiece));
        assertTrue(Position.isSameColor(Piece.BLACK, blackPiece));
        assertFalse(Position.isSameColor(Piece.WHITE, blackPiece));
        assertFalse(Position.isSameColor(Piece.BLACK, whitePiece));
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
    
    public void testGetSquareOfLeastValuablePieceWhite()
    {
        Position pos = createStartingPosition();
        long rank1 = Bitmap.FIRSTRANK;
        long rank2 = Bitmap.SECONDRANK;
        long targetSquares = rank2;
        int side = Piece.WHITE;
        assertSquareEquals(A2, pos.getSquareOfLeastValuablePiece(targetSquares, side));
        targetSquares = rank1 | rank2;
        assertSquareEquals(A2, pos.getSquareOfLeastValuablePiece(targetSquares, side));
        targetSquares &= ~pos.getPawns(side); //erase the pawns from targets
        assertSquareEquals(B1, pos.getSquareOfLeastValuablePiece(targetSquares, side));
        int erasedSquare[]   = new int[]{B1,G1,F1,C1,A1,H1,D1,E1};
        int expectedSquare[] = new int[]{G1,C1,C1,A1,H1,D1,E1,NOSQUARE};
        for(int index=0; index<expectedSquare.length; index++)
        {
            targetSquares &= ~(1L << erasedSquare[index]);
            int actualSquare = pos.getSquareOfLeastValuablePiece(targetSquares, side);
            assertSquareEquals(expectedSquare[index], actualSquare);
        }
    }
    
    public void testGetSquareOfLeastValuablePieceBlack()
    {
        Position pos = createStartingPosition();
        long rank8 = Bitmap.EIGHTHRANK;
        long rank7 = Bitmap.SEVENTHRANK;
        System.out.println("rank8: " +Util.displaySquaresStr(rank8));
        System.out.println("rank7: " +Util.displaySquaresStr(rank7));
        long targetSquares = rank7;
        int side = Piece.BLACK;
        assertSquareEquals(A7, pos.getSquareOfLeastValuablePiece(targetSquares, side));
        targetSquares = rank8 | rank7;
        assertSquareEquals(A7, pos.getSquareOfLeastValuablePiece(targetSquares, side));
        targetSquares &= ~pos.getPawns(side); //erase the pawns from targets
        assertSquareEquals(B8, pos.getSquareOfLeastValuablePiece(targetSquares, side));
        int erasedSquare[]   = new int[]{B8,G8,F8,C8,A8,H8,D8,E8};
        int expectedSquare[] = new int[]{G8,C8,C8,A8,H8,D8,E8,NOSQUARE};
        for(int index=0; index<expectedSquare.length; index++)
        {
            targetSquares &= ~(1L << erasedSquare[index]);
            int actualSquare = pos.getSquareOfLeastValuablePiece(targetSquares, side);
            assertSquareEquals(expectedSquare[index], actualSquare);
        }
    }

    
//    public void testToString()
//    {
//        Position p = createStartingPosition();
//        String expected = "";
//        assertEquals(expected, p.toString());
//    }
    
    private void assertSquareEquals(int expectedSquare, int actualSquare) {
        assertEquals(Square.named(expectedSquare), Square.named(actualSquare));
    }

    private void assertEmptyPosition(Position p) {
        assertEquals(EMPTY_BITBOARD, p.getAllPiecesAndKing(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.WHITE, Piece.PAWN));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.WHITE, Piece.KNIGHT));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.WHITE, Piece.BISHOP));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.WHITE, Piece.ROOK));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.WHITE, Piece.QUEEN));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.WHITE, Piece.KING));
        assertEquals(EMPTY_BITBOARD, p.getPawns(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getKnights(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getBishops(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getRooks(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getQueens(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getKing(Piece.WHITE));

        assertEquals(EMPTY_BITBOARD, p.getAllPiecesAndKing(Piece.BLACK));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.BLACK, Piece.PAWN));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.BLACK, Piece.KNIGHT));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.BLACK, Piece.BISHOP));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.BLACK, Piece.ROOK));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.BLACK, Piece.QUEEN));
        assertEquals(EMPTY_BITBOARD, p.getPieces(Piece.BLACK, Piece.KING));
        assertEquals(EMPTY_BITBOARD, p.getOpponentPawns(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getOpponentKnights(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getOpponentBishops(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getOpponentRooks(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getOpponentQueens(Piece.WHITE));
        assertEquals(EMPTY_BITBOARD, p.getOpponentKing(Piece.WHITE));
        
        assertFalse(p.isKingPlaced(Piece.WHITE));
        assertEquals(-1, p.getKingSquare(Piece.WHITE));
        assertFalse(p.isKingPlaced(Piece.BLACK));
        assertEquals(-1, p.getKingSquare(Piece.BLACK));
        assertEquals(EMPTY_BITBOARD, p.getOccupied(0));
        assertEquals(EMPTY_BITBOARD, p.getOccupied(90));
        assertEquals(EMPTY_BITBOARD, p.getOccupied(45));
        assertEquals(EMPTY_BITBOARD, p.getOccupied(-45));
        
        for(int square = Bitmap.A1; square <= Bitmap.H8; square++)
        {
            assertEquals(BOARD_EMPTY_SQUARE, p.getBoard(square));
        }
        Position.validate(p); //TODO: I believe this exposes bugs so don't enable this until I can troubleshoot.
    }
    
    private static void assertPlaced(Position p, int color, int piece, int sq)
    {
        int multiplier = (color == 0 ? 1 : -1);
        assertEquals(multiplier * Piece.ENCODED[piece], p.getBoard(sq));
        assertEquals(multiplier * Piece.ENCODED[piece], p.get(sq).encodedByColor());
        //Get the appropriate bitboard masks
        long sqMask = 1L << sq;
        long sqMask90 = 1L << SQ2BIT90R[sq];
        long sqMask45L = 1L << SQ2BIT45L[sq];
        long sqMask45R = 1L << SQ2BIT45R[sq];
        String bitNotSetMsg = "bit "+sq+" was not set";
        assertEquals(bitNotSetMsg, sqMask, p.getPieces(color,piece) & sqMask);
        assertEquals(bitNotSetMsg, sqMask, p.getAllPiecesAndKing(color) & sqMask);
        assertEquals(bitNotSetMsg, sqMask, p.getOccupied(0) & sqMask);
        assertEquals(bitNotSetMsg, sqMask90, p.getOccupied(90) & sqMask90);
        assertEquals(bitNotSetMsg, sqMask45L, p.getOccupied(-45) & sqMask45L);
        assertEquals(bitNotSetMsg, sqMask45R, p.getOccupied(45) & sqMask45R);
        if(piece == Piece.KING)
        {
            assertEquals("king should be placed", Square.named(sq), Square.named(p.getKingSquare(color)));
            assertEquals(bitNotSetMsg, sqMask, p.getKing(color));
        }
        Position.validate(p); //TODO: I believe this exposes bugs so don't enable this until I can troubleshoot.
    }

    private static void assertErased(Position p, int color, int piece, int sq)
    {
        assertEquals(Bitmap.BOARD_EMPTY_SQUARE, p.getBoard(sq));
        assertEquals(Piece.NONE, p.get(sq).index());
        //Get the appropriate bitboard masks
        long sqMask = 1L << sq;
        long sqMask90 = 1L << SQ2BIT90R[sq];
        long sqMask45L = 1L << SQ2BIT45L[sq];
        long sqMask45R = 1L << SQ2BIT45R[sq];
        String bitNotClearedMsg = "bit "+sq+" was not cleared";
        assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getPieces(color,piece) & sqMask);
        assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getAllPiecesAndKing(color) & sqMask);
        assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getOccupied(0) & sqMask);
        assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getOccupied(90) & sqMask90);
        assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getOccupied(-45) & sqMask45L);
        assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getOccupied(45) & sqMask45R);
        if(piece == Piece.KING)
        {
            assertEquals("king should be unplaced", "", Square.named(p.getKingSquare(color)));
            assertEquals(bitNotClearedMsg, EMPTY_BITBOARD, p.getKing(color) & sqMask);
        }
        Position.validate(p); //TODO: I believe this exposes bugs so don't enable this until I can troubleshoot.
    }
    
    private void assertStartingPosition(Position p) {
        assertEquals("a2 b2 c2 d2 e2 f2 g2 h2 ", toSquares(p, Piece.WHITE, Piece.PAWN));
        assertEquals("a1 h1 ", toSquares(p, Piece.WHITE, Piece.ROOK));
        assertEquals("b1 g1 ", toSquares(p, Piece.WHITE, Piece.KNIGHT));
        assertEquals("c1 f1 ", toSquares(p, Piece.WHITE, Piece.BISHOP));
        assertEquals("d1 ", toSquares(p, Piece.WHITE, Piece.QUEEN));
        assertEquals("e1 ", toSquares(p, Piece.WHITE, Piece.KING));

        assertEquals("a7 b7 c7 d7 e7 f7 g7 h7 ", toSquares(p, Piece.BLACK, Piece.PAWN));
        assertEquals("a8 h8 ", toSquares(p, Piece.BLACK, Piece.ROOK));
        assertEquals("b8 g8 ", toSquares(p, Piece.BLACK, Piece.KNIGHT));
        assertEquals("c8 f8 ", toSquares(p, Piece.BLACK, Piece.BISHOP));
        assertEquals("d8 ", toSquares(p, Piece.BLACK, Piece.QUEEN));
        assertEquals("e8 ", toSquares(p, Piece.BLACK, Piece.KING));
        Position.validate(p); //TODO: I believe this exposes bugs so don't enable this until I can troubleshoot.
    }

    private String toSquares(Position p, int colorIndex, int piecesIndex) {
        return Util.displaySquaresStr(p.getPieces(colorIndex, piecesIndex));
    }

    private static Position createStartingPosition() {
        return FenParser.parsePieceBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
    }

}
