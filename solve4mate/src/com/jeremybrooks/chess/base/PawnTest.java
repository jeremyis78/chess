package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.jeremybrooks.chess.base.Piece.Color;
import com.jeremybrooks.chess.util.Util;

public class PawnTest {

    private Piece pawn;
    
    @Test
    public void givenWhitePawn() {
        pawn = new Pawn(Color.W);
        assertPawn();
        assertEquals('P', pawn.toChar());
        assertEquals(PIECE[PAWN], pawn.encodedByColor());
    }

    @Test
    public void givenBlackPawn() {
        pawn = new Pawn(Color.B);
        assertPawn();
        assertEquals('p', pawn.toChar());
        assertEquals(-1 * PIECE[PAWN], pawn.encodedByColor());
    }

    private void assertPawn() {
        assertTrue(pawn.exists());
        assertEquals(PAWN, pawn.index());
        assertEquals(PIECE[PAWN], pawn.encoded());
    }

    @Test
    public void givenWhitePawnOnStartingRank() {
        Pawn myPawn = new Pawn(Color.W);
        pawn = myPawn;
        Position p = new Position();
        p.placePiece(WHITE, PAWN, E2);
        long emptySquares = ~p.getAllPieces(0);
        long attacks = pawn.attacks(E2, p);
        long advances = pawn.advances(E2, p);
        long pushesTwo = ((advances & THIRDRANK) << 8) & emptySquares;
        assertEquals("d3 f3 ", Util.displaySquaresStr(attacks));
        assertEquals("e3 ", Util.displaySquaresStr(advances));
        assertEquals("e4 ", Util.displaySquaresStr(pushesTwo));
    }

    @Test
    public void givenBlackPawnOnSecondRank() {
        Pawn myPawn = new Pawn(Color.B); 
        pawn = myPawn;
        Position p = new Position();
        p.placePiece(BLACK, PAWN, H7);
        long emptySquares = ~p.getAllPieces(0);
        long attacks = pawn.attacks(H7, p);
        long advances = pawn.advances(H7, p);
        long pushesTwo = ((advances & SIXTHRANK) >> 8) & emptySquares;
        assertEquals("g6 ", Util.displaySquaresStr(attacks));
        assertEquals("h6 ", Util.displaySquaresStr(advances));
        assertEquals("h5 ", Util.displaySquaresStr(pushesTwo));
    }

    @Test
    public void givenWhitePawnsOnStartingRank() {
        Pawn myPawn = new Pawn(Color.W);
        pawn = myPawn;
        Position p = new Position();
        for(int square=A2; square <= H2; square++)
        {
            p.placePiece(WHITE, PAWN, square);
        }
        long emptySquares = ~p.getAllPieces(0);
        long pushes = myPawn.advances(NOSQUARE, p);
        long pushesTwo = ((pushes & THIRDRANK) << 8) & emptySquares;
        assertEquals("a3 b3 c3 d3 e3 f3 g3 h3 ", Util.displaySquaresStr(pushes));
        assertEquals("a4 b4 c4 d4 e4 f4 g4 h4 ", Util.displaySquaresStr(pushesTwo));
    }

    @Test
    public void givenAllWhitePawnsOnSeventhRank() {
        Pawn myPawn = new Pawn(Color.W);
        pawn = myPawn;
        Position p = new Position();
        for(int square=A7; square <= H7; square++)
        {
            p.placePiece(WHITE, PAWN, square);
        }
        long emptySquares = ~p.getAllPieces(0);
        long pushes = myPawn.advances(NOSQUARE, p);
        long promoters = pushes & EIGHTHRANK & emptySquares;
        String expectedPushesAndPromoters = "a8 b8 c8 d8 e8 f8 g8 h8 ";
        assertEquals(expectedPushesAndPromoters, Util.displaySquaresStr(pushes));
        assertEquals(expectedPushesAndPromoters, Util.displaySquaresStr(promoters));
    }

    @Test
    public void givenSomeBlackPawnsOnSecondRank() {
        Pawn myPawn = new Pawn(Color.B);
        pawn = myPawn;
        Position p = pawnsFor(BLACK, "a2 d2 f2");
        long emptySquares = ~p.getAllPieces(0);
        long pushes = myPawn.advances(NOSQUARE, p);
        long promoters = pushes & FIRSTRANK & emptySquares;
        String expectedPushesAndPromoters = "a1 d1 f1 ";
        assertEquals(expectedPushesAndPromoters, Util.displaySquaresStr(pushes));
        assertEquals(expectedPushesAndPromoters, Util.displaySquaresStr(promoters));
    }

    @Test
    public void givenDoubledBlackPawns() {
        Pawn myPawn = new Pawn(Color.B);
        pawn = myPawn;
        Position p = pawnsFor(BLACK, "b7 b6 d7");
        
        long pushes = myPawn.advances(NOSQUARE, p);
        String expectedPushes = "b5 d6 ";
        assertEquals(expectedPushes, Util.displaySquaresStr(pushes));
    }

    private Position pawnsFor(int side, String onSquares) {
        Position p = new Position();
        String[] square = onSquares.split(" ");
        for(String sq: square)
        {
            p.placePiece(side, PAWN, Square.squareOf(sq));
        }
        return p;
    }
    
    

}
