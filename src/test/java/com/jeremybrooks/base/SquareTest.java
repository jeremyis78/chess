package com.jeremybrooks.base;

import static org.junit.Assert.*;

import com.jeremybrooks.chess.base.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jeremybrooks.chess.base.Piece.Color;

public class SquareTest {

    private Square square;
    
    @Before
    public void setUp(){
        square = new Square();
    }
    
    @Test
    public void testIsOccupied() {
        assertFalse(square.isOccupied());
        square = new Square(new Empty());
        assertFalse("absent/empty piece on a square is still an unoccupied square",
                square.isOccupied());
    }

    @Test
    public void testClear() {
        Pawn whitePawn = new Pawn(Color.W);
        square = new Square(whitePawn);
        assertSquareIsOccupiedBy(whitePawn);
        
        square.clear();
        assertSquareIsUnoccupied();
        
        square.set(new Empty());
        square.clear();
        assertSquareIsUnoccupied();
    }

    @Test
    public void testSetAndGetPiece() {
        Bishop blackBishop = new Bishop(Color.B);
        square = new Square();

        square.set(blackBishop);
        assertSquareIsOccupiedBy(blackBishop);
        
        square.set(null);
        assertSquareIsUnoccupied();
    }

    @Test @Ignore
    public void testUnoccupied() {
        fail("Not yet implemented");
    }

    @Test @Ignore
    public void testFromBoard() {
        fail("Not yet implemented");
    }

    @Test
    public void testSquareNamed() {
        assertEquals("", Square.named(Bitmap.A1-1));
        assertEquals("a1", Square.named(Bitmap.A1));
        assertEquals("b2", Square.named(Bitmap.B2));
        assertEquals("c3", Square.named(Bitmap.C3));
        assertEquals("d4", Square.named(Bitmap.D4));
        assertEquals("e5", Square.named(Bitmap.E5));
        assertEquals("f6", Square.named(Bitmap.F6));
        assertEquals("g7", Square.named(Bitmap.G7));
        assertEquals("h8", Square.named(Bitmap.H8));
        assertEquals("", Square.named(Bitmap.H8+1));
    }

    @Test
    public void testSquareOf() {
        assertEquals(Bitmap.A1, Square.squareOf("A1"));
        assertEquals(Bitmap.B2, Square.squareOf("B2"));
        assertEquals(Bitmap.C3, Square.squareOf("C3"));
        assertEquals(Bitmap.D4, Square.squareOf("D4"));
        assertEquals(Bitmap.E5, Square.squareOf("E5"));
        assertEquals(Bitmap.F6, Square.squareOf("F6"));
        assertEquals(Bitmap.G7, Square.squareOf("G7"));
        assertEquals(Bitmap.H8, Square.squareOf("H8"));
        assertEquals(Bitmap.NOSQUARE, Square.squareOf("-"));
        assertEquals(Bitmap.NOSQUARE, Square.squareOf("I4"));
        assertEquals(Bitmap.NOSQUARE, Square.squareOf("A9"));
    }
    
    @Test
    public void testAdjacentSquares() {
        assertTrue("A1 is adjacent to B1", Square.adjacentSquares(Bitmap.A1, Bitmap.B1));
        assertTrue("A1 is adjacent to A2", Square.adjacentSquares(Bitmap.A1, Bitmap.A2));
        assertTrue("A1 is adjacent to B2", Square.adjacentSquares(Bitmap.A1, Bitmap.B2));

        assertFalse("A1 is not adjacent to A3", Square.adjacentSquares(Bitmap.A1, Bitmap.A3));
        assertFalse("A1 is not adjacent to B3", Square.adjacentSquares(Bitmap.A1, Bitmap.B3));
        assertFalse("A1 is not adjacent to C3", Square.adjacentSquares(Bitmap.A1, Bitmap.C3));
        assertFalse("A1 is not adjacent to C2", Square.adjacentSquares(Bitmap.A1, Bitmap.C2));
        assertFalse("A1 is not adjacent to C1", Square.adjacentSquares(Bitmap.A1, Bitmap.C1));
    }
    
    @Test
    public void testNotOnThirdRank(){
        assertTrue(Square.notOnThirdRank(Bitmap.G1));
        assertTrue(Square.notOnThirdRank(Bitmap.H2));
        for(int square = Bitmap.A3; square <= Bitmap.H3; square++)
        {
            assertFalse(Square.notOnThirdRank(square));
        }
        assertTrue(Square.notOnThirdRank(Bitmap.A4));
        assertTrue(Square.notOnThirdRank(Bitmap.B5));
        assertTrue(Square.notOnThirdRank(Bitmap.C6));
        assertTrue(Square.notOnThirdRank(Bitmap.D7));
        assertTrue(Square.notOnThirdRank(Bitmap.F8));
    }

    @Test
    public void testNotOnSixthRank(){
        assertTrue(Square.notOnSixthRank(Bitmap.A8));
        assertTrue(Square.notOnSixthRank(Bitmap.A7));
        for(int square = Bitmap.A6; square <= Bitmap.H6; square++)
        {
            assertFalse(Square.notOnSixthRank(square));
        }
        assertTrue(Square.notOnSixthRank(Bitmap.H5));
        assertTrue(Square.notOnSixthRank(Bitmap.B4));
        assertTrue(Square.notOnSixthRank(Bitmap.C3));
        assertTrue(Square.notOnSixthRank(Bitmap.D2));
        assertTrue(Square.notOnSixthRank(Bitmap.F1));
    }
    
    @Test
    public void testSquareLeftOf(){
        assertEquals("left of A1 would be an index 'off the bitboard'",
                -1, Square.squareLeftOf(Bitmap.A1));
        assertEquals(Bitmap.A1, Square.squareLeftOf(Bitmap.B1));
        assertEquals(Bitmap.B1, Square.squareLeftOf(Bitmap.C1));
        assertEquals(Bitmap.C1, Square.squareLeftOf(Bitmap.D1));
        assertEquals(Bitmap.D1, Square.squareLeftOf(Bitmap.E1));
        assertEquals(Bitmap.E1, Square.squareLeftOf(Bitmap.F1));
        assertEquals(Bitmap.F1, Square.squareLeftOf(Bitmap.G1));
        assertEquals(Bitmap.G1, Square.squareLeftOf(Bitmap.H1));
        assertEquals(Bitmap.H1, Square.squareLeftOf(Bitmap.A2));
        assertEquals(Bitmap.G8, Square.squareLeftOf(Bitmap.H8));
    }

    @Test
    public void testSquareRightOf(){
        assertEquals("right of H8 would be an index 'off the bitboard'",
                64, Square.squareRightOf(Bitmap.H8));
        assertEquals(Bitmap.H8, Square.squareRightOf(Bitmap.G8));
        assertEquals(Bitmap.C8, Square.squareRightOf(Bitmap.B8));
        assertEquals("right of h-file wraps to square on next rank", Bitmap.A8, Square.squareRightOf(Bitmap.H7));
        assertEquals(Bitmap.B1, Square.squareRightOf(Bitmap.A1));
    }
    
    @Test
    public void testIsEighthRank(){
        int white = Piece.WHITE;
        for(int square = Bitmap.A1; square < Bitmap.A2; square++)
        {
            assertFalse(Square.isEighthRank(square, white));
            assertTrue (Square.isEighthRank(square, ~white));
        }
        for(int square = Bitmap.A2; square < Bitmap.A8; square++)
        {
            assertFalse(Square.isEighthRank(square, white));
            assertFalse(Square.isEighthRank(square, ~white));
        }
        for(int square = Bitmap.A8; square <= Bitmap.H8; square++)
        {
            assertTrue (Square.isEighthRank(square, white));
            assertFalse(Square.isEighthRank(square, ~white));
        }
        assertFalse(Square.isEighthRank(Bitmap.MAXSQ, white));
        assertFalse(Square.isEighthRank(Bitmap.MAXSQ, ~white));
        assertFalse(Square.isEighthRank(Bitmap.NOSQUARE, white));
        assertFalse(Square.isEighthRank(Bitmap.NOSQUARE, ~white));
    }

    private void assertSquareIsUnoccupied() {
        assertFalse(square.isOccupied());
        assertNotNull(square.get());
    }

    private void assertSquareIsOccupiedBy(Piece piece) {
        assertTrue(square.isOccupied());
        assertSame(piece, square.get());
    }

}
