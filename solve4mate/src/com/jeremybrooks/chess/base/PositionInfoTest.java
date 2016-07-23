package com.jeremybrooks.chess.base;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.base.Bitmap.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PositionInfoTest {

    public static final String[] CASTLING_OPTIONS = new String[]{
            "-",   "K",   "Q",   "KQ",   //  0,  1,  2,  3 
            "k",  "Kk",  "Qk",  "KQk",   //  4,  5,  6,  7
            "q",  "Kq",  "Qq",  "KQq",   //  8,  9, 10, 11
            "kq", "Kkq", "Qkq", "KQkq"}; // 12, 13, 14, 15

    
    private PositionInfo info;
    
    @Before
    public void setUp(){
        info = new PositionInfo();
        //set all the bits allowed to ensure the tests erase the existing bits
        info.setMoveNumber(511);
        info.setReversiblePlies(511);
        info.setCastleOptionsFromFen("KQkq");
        info.setEnPassantSquare(42);
    }
    
    @Test
    public void testConstructor()
    {
        info = new PositionInfo();
        assertEquals(       1, info.getMoveNumber());
        assertEquals(       0, info.getReversiblePlies());
        assertEquals(       0, info.getCastleOptions());
        assertEquals(     "-", info.getCastleOptionsAsFen());
        assertEquals(NOSQUARE, info.getEnPassantSquare());
    }
    
    @Test
    public void givenMinimumMoveNumber() {
        int moveNumber = 1;
        info.setMoveNumber(moveNumber);
        assertMoveNumberEquals(moveNumber);
    }

    @Test
    public void givenMaximumMoveNumber() {
        int moveNumber = 511;
        info.setMoveNumber(moveNumber);
        assertMoveNumberEquals(moveNumber);
    }

    @Test
    public void givenInvalidMoveNumber() {
        int moveNumber = 0;
        try {
            info.setMoveNumber(moveNumber);
        } catch (IllegalArgumentException e) {
            assertEquals("moveNumber must be between 1 and 511", e.getMessage());
        }
        moveNumber = 512;
        try {
            info.setMoveNumber(moveNumber);
        } catch (IllegalArgumentException e) {
            assertEquals("moveNumber must be between 1 and 511", e.getMessage());
        }
    }

    @Test
    public void givenMinimumReversiblePlies() {
        int reversiblePlies = 0;
        info.setReversiblePlies(reversiblePlies);
        assertReversiblePliesEquals(reversiblePlies);
    }

    @Test
    public void givenMaximumReversiblePlies() {
        int reversiblePlies = 511;
        info.setReversiblePlies(reversiblePlies);
        assertReversiblePliesEquals(reversiblePlies);
    }

    @Test
    public void givenInvalidReversiblePlies() {
        int reversiblePlies = -1;
        try {
            info.setReversiblePlies(reversiblePlies);
        } catch (IllegalArgumentException e) {
            assertEquals("reversiblePlies must be between 0 and 511", e.getMessage());
        }
        reversiblePlies = 512;
        try {
            info.setReversiblePlies(reversiblePlies);
        } catch (IllegalArgumentException e) {
            assertEquals("reversiblePlies must be between 0 and 511", e.getMessage());
        }
    }

    @Test
    public void givenShortCastleOptionsForWhite() {
        String hasIt = "K";
        info.setCastleOptionsFromFen(hasIt);
        assertEquals(true, info.hasShortCastleOption(Piece.WHITE));
        
        String doesntHaveIt = "Qkq";
        info.setCastleOptionsFromFen(doesntHaveIt);
        assertEquals(false, info.hasShortCastleOption(Piece.WHITE));
    }

    @Test
    public void givenShortCastleOptionsForBlack() {
        String hasIt = "k";
        info.setCastleOptionsFromFen(hasIt);
        assertEquals(true, info.hasShortCastleOption(Piece.BLACK));
        
        String doesntHaveIt = "KQq";
        info.setCastleOptionsFromFen(doesntHaveIt);
        assertEquals(false, info.hasShortCastleOption(Piece.BLACK));
    }

    @Test
    public void givenLongCastleOptionsForWhite() {
        String hasIt = "Q";
        info.setCastleOptionsFromFen(hasIt);
        assertEquals(true, info.hasLongCastleOption(Piece.WHITE));
        
        String doesntHaveIt = "Kkq";
        info.setCastleOptionsFromFen(doesntHaveIt);
        assertEquals(false, info.hasLongCastleOption(Piece.WHITE));
    }

    @Test
    public void givenLongCastleOptionsForBlack() {
        String hasIt = "q";
        info.setCastleOptionsFromFen(hasIt);
        assertEquals(true, info.hasLongCastleOption(Piece.BLACK));
        
        String doesntHaveIt = "KQk";
        info.setCastleOptionsFromFen(doesntHaveIt);
        assertEquals(false, info.hasLongCastleOption(Piece.BLACK));
    }
    
    @Test
    public void givenCastlingOptionBits()
    {
        int options = GameState.W_SHORT_CASTLE|GameState.W_LONG_CASTLE
                     |GameState.B_SHORT_CASTLE|GameState.B_LONG_CASTLE;
        assertEquals(options, info.getCastleOptions());
        assertTrue(info.hasShortCastleOption(Piece.WHITE));
        assertTrue(info.hasLongCastleOption(Piece.WHITE));
        assertTrue(info.hasShortCastleOption(Piece.BLACK));
        assertTrue(info.hasLongCastleOption(Piece.BLACK));
        
        options &= ~(GameState.W_LONG_CASTLE|GameState.B_SHORT_CASTLE);
        info.removeLongCastleOption(Piece.WHITE);
        info.removeShortCastleOption(Piece.BLACK);
        assertEquals(options, info.getCastleOptions());
        assertTrue(info.hasShortCastleOption(Piece.WHITE));
        assertFalse(info.hasLongCastleOption(Piece.WHITE));
        assertFalse(info.hasShortCastleOption(Piece.BLACK));
        assertTrue(info.hasLongCastleOption(Piece.BLACK));
    }
    
    @Test
    public void givenEveryCombinationOfCastlingOptions() {
        int expectedCastlingOptions = 0;
        for(String option: CASTLING_OPTIONS)
        {
            info.setCastleOptionsFromFen(option);
            assertEquals(expectedCastlingOptions, info.getCastleOptions());
            assertEquals(option, info.getCastleOptionsAsFen());
            expectedCastlingOptions++;
        }
    }

    @Test
    public void testInvalidCastlingCharacters()
    {
        String bad = "KQxx";
        String expectedError = "KQxx is an invalid castle options string";
        try {
            info.setCastleOptionsFromFen(bad);
        } catch (IllegalArgumentException e) {
            assertEquals(expectedError, e.getMessage());
        }
    }

    @Test
    public void givenGoodEnPassantSquare() {
        int[] goodSquares = new int[]{Bitmap.A3,Bitmap.B3,Bitmap.H3,
                                      Bitmap.A6,Bitmap.B6,Bitmap.H6,
                                      Bitmap.NOSQUARE};
        for(int goodSquare: goodSquares)
        {
            info.setEnPassantSquare(goodSquare);
            assertEquals(goodSquare, info.getEnPassantSquare());
        }
    }
    
    @Test @Ignore
    public void givenBadEnPassantSquare() {
        int[] badSquares = new int[]{Bitmap.H2,Bitmap.A4,Bitmap.E4,
                Bitmap.H5,Bitmap.A7,Bitmap.D5};
        for(int badSquare: badSquares)
        {
            try {
                info.setEnPassantSquare(badSquare);
                fail(badSquare + " is not a valid ep square; should throw exception");
            } catch (Exception e) {
                assertEquals(badSquare + " is an invalid EP target square; must be between 16 and 23 or between 40 and 47", e.getMessage());
            }
        }
    }
    
    private void assertMoveNumberEquals(int expectedMoveNumber)
    {
        assertEquals(expectedMoveNumber, info.getMoveNumber());
        assertEquals(511, info.getReversiblePlies());
        assertEquals(true, info.hasShortCastleOption(Piece.WHITE));
        assertEquals(true, info.hasShortCastleOption(Piece.BLACK));
        assertEquals(true, info.hasLongCastleOption(Piece.WHITE));
        assertEquals(true, info.hasLongCastleOption(Piece.BLACK));
        assertEquals(42, info.getEnPassantSquare());
    }

    private void assertReversiblePliesEquals(int expectedRevPlies)
    {
        assertEquals(511, info.getMoveNumber());
        assertEquals(expectedRevPlies, info.getReversiblePlies());
        assertEquals(true, info.hasShortCastleOption(Piece.WHITE));
        assertEquals(true, info.hasShortCastleOption(Piece.BLACK));
        assertEquals(true, info.hasLongCastleOption(Piece.WHITE));
        assertEquals(true, info.hasLongCastleOption(Piece.BLACK));
        assertEquals(42, info.getEnPassantSquare());
    }

}
