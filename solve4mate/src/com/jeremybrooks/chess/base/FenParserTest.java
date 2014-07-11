package com.jeremybrooks.chess.base;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.base.Bitmap.*;

import org.junit.Before;
import org.junit.Test;

public class FenParserTest {

    //use these to compare to what is returned from Position.getBoard(int)
    private static final int WHITE_PAWN   = PIECE[PAWN];
    private static final int WHITE_KNIGHT = PIECE[KNIGHT];
    private static final int WHITE_BISHOP = PIECE[BISHOP];
    private static final int WHITE_ROOK   = PIECE[ROOK];
    private static final int WHITE_QUEEN  = PIECE[QUEEN];
    private static final int WHITE_KING   = PIECE[KING];
    private static final int BLACK_PAWN   = -WHITE_PAWN;
    private static final int BLACK_KNIGHT = -WHITE_KNIGHT;
    private static final int BLACK_BISHOP = -WHITE_BISHOP;
    private static final int BLACK_ROOK   = -WHITE_ROOK;
    private static final int BLACK_QUEEN  = -WHITE_QUEEN;
    private static final int BLACK_KING   = -WHITE_KING;
    
    private FenParser parser;
    
    @Before
    public void setUp()
    {
        parser = new FenParser();
    }

    @Test
    public void givenNothingToParse() {
        String pieceBoard = "";
        parser.init(pieceBoard);
        try {
            parser.parse();
        } catch (IllegalStateException expected) {
            assertEquals("need something to parse; call init(String) or use constructor before calling parse()",
                    expected.getMessage());
        }
    }

    @Test
    public void givenNothingToParseForStaticParse() {
        String pieceBoard = "";
        try {
            FenParser.parsePieceBoard(pieceBoard);
        } catch (IllegalStateException expected) {
            assertEquals("need something to parse; call init(String) or use constructor before calling parse()",
                    expected.getMessage());
        }
    }

    @Test
    public void givenBadBoardToParse() {
        String pieceBoard = "-";
        parser.init(pieceBoard);
        try {
            parser.parse();
        } catch (IllegalArgumentException expected) {
            assertEquals("board must contain eight ranks",
                    expected.getMessage());
        }
    }

    @Test
    public void givenBoardWithZeroesOnRanks() {
        String pieceBoard = "0/0/0/0/0/0/0/0";
        parser.init(pieceBoard);
        try {
            parser.parse();
        } catch (IllegalArgumentException expected) {
            assertEquals("board pieces and empty squares on rank #8 do not fit on eight files: 0",
                    expected.getMessage());
        }
    }

    @Test
    public void givenNineSquaresOnARank() {
        String pieceBoard = "8/8/8/PPPPPPPPPPPP/8/8/8/8 w - - 0 1";
        parser.init(pieceBoard);
        try {
            parser.parse();
        } catch (IllegalArgumentException expected) {
            assertEquals("board pieces and empty squares on rank #5 do not "
                    + "fit on eight files: PPPPPPPPPPPP",
                    expected.getMessage());
        }
    }

    @Test
    public void givenAllSixFenFields() {
        String pieceBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 5 20";
        parser.init(pieceBoard);
        parser.parse();
        Position position = parser.getPosition();
        assertNotNull(position);
        assertEquals(BLACK_ROOK, position.getBoard(A8));
        assertEquals(BLACK_KNIGHT, position.getBoard(B8));
        assertEquals(BLACK_BISHOP, position.getBoard(C8));
        assertEquals(BLACK_QUEEN, position.getBoard(D8));
        assertEquals(BLACK_KING, position.getBoard(E8));
        assertEquals(BLACK_BISHOP, position.getBoard(F8));
        assertEquals(BLACK_KNIGHT, position.getBoard(G8));
        assertEquals(BLACK_ROOK, position.getBoard(H8));
        for(int square = A7; square >= H7; square--)
        {
            assertEquals(BLACK_PAWN, position.getBoard(square));
        }
        for(int square = A6; square >= H3; square--)
        {
            assertEquals(BOARD_EMPTY_SQUARE, position.getBoard(square));
        }
        for(int square = A2; square >= H2; square--)
        {
            assertEquals(WHITE_PAWN, position.getBoard(square));
        }
        assertEquals(WHITE_ROOK, position.getBoard(A1));
        assertEquals(WHITE_KNIGHT, position.getBoard(B1));
        assertEquals(WHITE_BISHOP, position.getBoard(C1));
        assertEquals(WHITE_QUEEN, position.getBoard(D1));
        assertEquals(WHITE_KING, position.getBoard(E1));
        assertEquals(WHITE_BISHOP, position.getBoard(F1));
        assertEquals(WHITE_KNIGHT, position.getBoard(G1));
        assertEquals(WHITE_ROOK, position.getBoard(H1));

        assertTrue(parser.isWhiteToMove());
        assertEquals("KQkq", parser.getCastlingOptions());
        assertEquals(NOSQUARE, parser.getEnPassantSquare());
        assertEquals(5, parser.getHalfMoveNumber());
        assertEquals(20, parser.getCurrentMoveNumber());
    }

    @Test
    public void givenBlackCastlingEnPassant() {
        String fen = "8/4B3/8/8/8/8/4P3/k6K b KQq d3 3 22";
        parser.init(fen);
        parser.parse();
        Position position = parser.getPosition();
        assertEquals(WHITE_BISHOP, position.getBoard(E7));
        assertEquals(WHITE_PAWN, position.getBoard(E2)); // 1
        assertEquals(BLACK_KING, position.getBoard(A1)); // -3
        assertEquals(WHITE_KING, position.getBoard(H1)); //  3
        assertFalse(parser.isWhiteToMove());
        assertEquals("KQq", parser.getCastlingOptions());
        assertEquals(D3, parser.getEnPassantSquare());
        assertEquals(3, parser.getHalfMoveNumber());
        assertEquals(22, parser.getCurrentMoveNumber());
    }

    @Test
    public void testParsePieceBoard() {
        String pieceBoard = "8/4P3/8/8/8/8/4P3/k6K";
        Position position = FenParser.parsePieceBoard(pieceBoard);
        FenBuilder builder = new FenBuilder();
        builder.appendPieceBoard(position);
        assertEquals(pieceBoard, builder.toString().split(" ")[0]);
        
        assertEquals(WHITE_PAWN, position.getBoard(E7));
        assertEquals(WHITE_PAWN, position.getBoard(E2));
        assertEquals(BLACK_KING, position.getBoard(A1));
        assertEquals(WHITE_KING, position.getBoard(H1));
    }

    @Test
    public void givenIntConvertToCharEquivalent()
    { 
        int number = 6;
//        Character c = '0' + number;
        assertEquals('6', Character.forDigit(number, 10));
        assertEquals('6', (char)('0' + number));
        
    }
    
    @Test
    public void givenFirstFieldBoard()
    {
        String first = "k6K/8/8/8/8/8/8/8";
        parser.init(first);
        parser.parse();
        Position position = parser.getPosition();
        assertNotNull(position);
        assertEquals(BLACK_KING, position.getBoard(A8));
        assertEquals(WHITE_KING, position.getBoard(H8));
        
        //Defaults from here on
        assertTrue(parser.isWhiteToMove());
        assertEquals("-", parser.getCastlingOptions());
        assertEquals(NOSQUARE, parser.getEnPassantSquare());
        assertEquals(0, parser.getHalfMoveNumber());
        assertEquals(1, parser.getCurrentMoveNumber());
    }

    @Test
    public void givenSecondFieldOnMove()
    {
        String first = "k6K/8/8/8/8/8/8/8 b";
        parser.init(first);
        parser.parse();
        Position position = parser.getPosition();
        assertNotNull(position);
        assertEquals(BLACK_KING, position.getBoard(A8));
        assertEquals(WHITE_KING, position.getBoard(H8));
        assertFalse(parser.isWhiteToMove());
        
        //Defaults from here on
        assertEquals("-", parser.getCastlingOptions());
        assertEquals(NOSQUARE, parser.getEnPassantSquare());
        assertEquals(0, parser.getHalfMoveNumber());
        assertEquals(1, parser.getCurrentMoveNumber());
    }

    @Test
    public void givenThirdFieldOnMove()
    {
        String first = "k6K/8/8/8/8/8/8/8 b KQkq";
        parser.init(first);
        parser.parse();
        Position position = parser.getPosition();
        assertNotNull(position);
        assertEquals(BLACK_KING, position.getBoard(A8));
        assertEquals(WHITE_KING, position.getBoard(H8));
        assertFalse(parser.isWhiteToMove());
        assertEquals("KQkq", parser.getCastlingOptions());
        
        //Defaults from here on
        assertEquals(NOSQUARE, parser.getEnPassantSquare());
        assertEquals(0, parser.getHalfMoveNumber());
        assertEquals(1, parser.getCurrentMoveNumber());
    }

    @Test
    public void givenTooManyCastlingOptions()
    {
        String invalidPiece = "k6K/8/8/8/8/8/8/7q w KQkqabc - 0 1";
        assertInvalid(invalidPiece, "castling options 'KQkqabc' must not be empty or "
                + "exceed four characters; use only characters from KQkq");
    }

    @Test
    public void testSetTooManyWhiteKings()
    {
        String tooManyWhiteKingsOnDifferentRanks = "2K5/8/8/8/8/8/8/7K w - - 0 1";
        assertInvalid(tooManyWhiteKingsOnDifferentRanks, "board has too many white kings; wking='K'");
    }

    @Test
    public void testSetTooManyBlackKings()
    {
        String tooManyBlackKingsOnSameRank = "8/k6k/8/8/8/8/8/8 w - - 0 1";
        assertInvalid(tooManyBlackKingsOnSameRank, "board has too many black kings; bking='k'");
    }

    @Test
    public void testSetTooManyRanksOnBoard()
    {
        String tooManyRanks = "8/8/8/8/8/8/8/8/8 w - - 0 1";
        assertInvalid(tooManyRanks, "board must contain eight ranks");
    }

    @Test
    public void testSetTooManyFilesOnBoard()
    {
        String tooManyFiles = "k8/8/8/8/8/8/8/8 w - - 0 1";
        assertInvalid(tooManyFiles, "board pieces and empty squares on rank #8 "
                + "do not fit on eight files: k8");
        
        String tooManyPawnsOnRank= "k7/8/8/PPPPPPPPP/8/8/8/8 w - - 0 1";
        assertInvalid(tooManyPawnsOnRank, "board pieces and empty squares on rank #5 "
                + "do not fit on eight files: PPPPPPPPP");
    }
    
    @Test
    public void testSetUnknownPiece()
    {
        String invalidPiece = "k6K/8/8/8/8/8/8/7z w - - 0 1";
        assertInvalid(invalidPiece, "board contains invalid piece 'z'; allowed piece characters are: KkQqRrBbNnPp");
    }
    
    @Test
    public void testInvalidOnMove()
    {
        String badFen = "k6K/8/8/8/8/8/8/8 xx - - 0 1";
        String expectedError = "onMove 'xx' is invalid; use 'w' for white or 'b' for black";
        assertInvalid(badFen, expectedError);
    }

    @Test
    public void testTooManyCastlingCharacters()
    {
        String badFen = "k6K/8/8/8/8/8/8/8 b KQkqK - 0 2";
        String expectedError = "castling options 'KQkqK' must not be empty or exceed four characters; use only characters from KQkq";
        assertInvalid(badFen, expectedError);
    }

    @Test
    public void testBlankCastlingOptions()
    {
        String badFen = "k6K/8/8/8/8/8/8/8 b  - 0 2";
        String expectedError = "castling options '' must not be empty or exceed four characters; use only characters from KQkq";
        assertInvalid(badFen, expectedError);
    }

    @Test
    public void testEnPassantSquareConflictsWithPlayerOnMove()
    {
        String badFen = "k6K/8/8/8/8/8/8/8 w - e3 0 1";
        //expected    = "k6K/8/8/8/8/8/8/8 w - - 0 1";;
        parser.init(badFen);
        parser.parse();
        assertEquals("en passant square e3 is invalid when white is moving",
                NOSQUARE, parser.getEnPassantSquare());
        
        badFen   = "k6K/8/8/8/8/8/8/8 b - h6 0 1";
        //expect = "k6K/8/8/8/8/8/8/8 b - - 0 1";
        parser.init(badFen);
        parser.parse();
        assertEquals("en passant square h6 is invalid when black is moving",
                NOSQUARE, parser.getEnPassantSquare());
    }

    @Test
    public void givenEnPassantSquareNotOnThirdOrSixthRank()
    {
        String badFen = "k6K/8/8/8/8/8/8/8 w - d7 0 1";
        //expected    = "k6K/8/8/8/8/8/8/8 w - - 0 1";;
        parser.init(badFen);
        parser.parse();
        assertEquals("en passant square d7 is not on 3rd or sixth rank",
                NOSQUARE, parser.getEnPassantSquare());
    }

    @Test
    public void testHalfMoveNumberIsNotNegative()
    {
        String badFen = "k6K/8/8/8/8/8/8/8 b KQkq - -1 2";
        //expected    = "k6K/8/8/8/8/8/8/8 b KQkq - 0 2";
        parser.init(badFen);
        parser.parse();
        assertEquals(0, parser.getHalfMoveNumber());
    }

    @Test
    public void testMoveNumberIsZero()
    {
        String badFen = "k6K/8/8/8/8/8/8/8 b KQkq - 1 0";
        //expected    = "k6K/8/8/8/8/8/8/8 b KQkq - 1 1";
        parser.init(badFen);
        parser.parse();
        assertEquals(1, parser.getCurrentMoveNumber());

        String badFen2 = "k6K/8/8/8/8/8/8/8 b KQkq - 1 -9";
        //expected     = "k6K/8/8/8/8/8/8/8 b KQkq - 1 1";
        parser.init(badFen2);
        parser.parse();
        assertEquals(1, parser.getCurrentMoveNumber());
    }

    private void assertInvalid(String position, String expectedError) {
        try {
            parser.init(position);
            parser.parse();
            fail(position+" did not throw '"+expectedError+"'");
        } catch (IllegalArgumentException e) {
            assertEquals(expectedError, e.getMessage());
        }    
    }

}
