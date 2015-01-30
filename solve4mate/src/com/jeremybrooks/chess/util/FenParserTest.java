package com.jeremybrooks.chess.util;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.base.Bitmap.*;

import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Position;

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
        assertEquals(new Integer(5), parser.getOperandInt(FenParser.OPCODE_HMVC));
        assertEquals(new Integer(20), parser.getOperandInt(FenParser.OPCODE_FMVN));
    }

    @Test
    public void givenBlackCastlingEnPassant() {
        String fen = "8/4B3/8/8/8/8/4P3/k6K b KQq d3 3 22";
        String epd = "8/4B3/8/8/8/8/4P3/k6K b KQq d3 fmvn 22; hmvc 3;";
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
        int hmvc = parser.getOperandInt(FenParser.OPCODE_HMVC);
        assertEquals(3, hmvc);
        int fmvn = parser.getOperandInt(FenParser.OPCODE_FMVN);
        assertEquals(22, fmvn);
        assertEquals(epd, parser.toEpd());
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
        
        int hmvc = (int) parser.getOperandInt(FenParser.OPCODE_HMVC);
        assertEquals(0, hmvc);
        int fmvn = (int) parser.getOperandInt(FenParser.OPCODE_FMVN);
        assertEquals(1, fmvn);

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
        
        int hmvc = (int) parser.getOperandInt(FenParser.OPCODE_HMVC);
        assertEquals(0, hmvc);
        int fmvn = (int) parser.getOperandInt(FenParser.OPCODE_FMVN);
        assertEquals(1, fmvn);

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
        
        int hmvc = (int) parser.getOperandInt(FenParser.OPCODE_HMVC);
        assertEquals(0, hmvc);
        int fmvn = (int) parser.getOperandInt(FenParser.OPCODE_FMVN);
        assertEquals(1, fmvn);

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
        int hmvc = parser.getOperandInt(FenParser.OPCODE_HMVC);
        assertEquals(0, hmvc);
    }

    @Test
    public void testMoveNumberIsZero()
    {
        String badFen = "k6K/8/8/8/8/8/8/8 b KQkq - 1 0";
        //expected    = "k6K/8/8/8/8/8/8/8 b KQkq - 1 1";
        parser.init(badFen);
        parser.parse();
        int fmvn = (int) parser.getOperandInt(FenParser.OPCODE_FMVN);
        assertEquals(1, fmvn);
        
        String badFen2 = "k6K/8/8/8/8/8/8/8 b KQkq - 1 -9";
        //expected     = "k6K/8/8/8/8/8/8/8 b KQkq - 1 1";
        parser.init(badFen2);
        parser.parse();
        fmvn = parser.getOperandInt(FenParser.OPCODE_FMVN);
        assertEquals(1, fmvn);
    }
    
    @Test
    public void givenEPD()
    {
        String e = "3r1k2/4npp1/1ppr3p/p6P/P2PPPP1/1NR5/4K3/R7 w Q e6 id DummyPosition;";
        parser.init(e);
        parser.parseEpd();
        assertNotNull(                parser.getPosition());
        assertEquals(true,            parser.isWhiteToMove());
        assertEquals("Q",             parser.getCastlingOptions());
        assertEquals(Bitmap.E6,       parser.getEnPassantSquare());
        assertEquals(null,            parser.getOperand(FenParser.OPCODE_HMVC));
        assertEquals(null,            parser.getOperand(FenParser.OPCODE_FMVN));
        assertEquals("DummyPosition", parser.getOperand("id"));
        assertEquals(e, parser.toEpd());
    }

    @Test
    public void givenEPDwithNoOperations()
    {
        String e = "3r1k2/4npp1/1ppr3p/p6P/P2PPPP1/1NR5/4K3/R7 w Q e6";
        parser.init(e);
        parser.parseEpd();
        assertNotNull(                parser.getPosition());
        assertEquals(true,            parser.isWhiteToMove());
        assertEquals("Q",             parser.getCastlingOptions());
        assertEquals(Bitmap.E6,       parser.getEnPassantSquare());
        assertEquals(null,            parser.getOperand(FenParser.OPCODE_HMVC));
        assertEquals(null,            parser.getOperand(FenParser.OPCODE_FMVN));
        assertEquals(e,               parser.toEpd());
    }

    @Test
    public void givenFenCompleteParseEPD()
    {
        String e = "3r1k2/4npp1/1ppr3p/p6P/P2PPPP1/1NR5/4K3/R7 w Q e6 2 45";
        String expectedEpd = 
                    "3r1k2/4npp1/1ppr3p/p6P/P2PPPP1/1NR5/4K3/R7 w Q e6 fmvn 45; hmvc 2;";
        parser.init(e);
        parser.parseEpd();
        assertNotNull(                parser.getPosition());
        assertEquals(true,            parser.isWhiteToMove());
        assertEquals("Q",             parser.getCastlingOptions());
        assertEquals(Bitmap.E6,       parser.getEnPassantSquare());
        assertEquals(new Integer(2),  parser.getOperandInt(FenParser.OPCODE_HMVC));
        assertEquals(new Integer(45), parser.getOperandInt(FenParser.OPCODE_FMVN));
        assertEquals(expectedEpd,     parser.toEpd());
    }

    @Test
    public void givenEPDwithIncorrectlySortedOpcodes()
    {
        String epd = "2r2rk1/1bqnbpp1/1p1ppn1p/pP6/N1P1P3/P2B1N1P/1B2QPP1/R2R2K1 b - - "
                 + "fmvn 89; hmvc 22; bm Bxe4;";
        String expectedEpd = "2r2rk1/1bqnbpp1/1p1ppn1p/pP6/N1P1P3/P2B1N1P/1B2QPP1/R2R2K1 b - - "
                + "bm Bxe4; fmvn 89; hmvc 22;";
        parser.init(epd);
        parser.parseEpd();
        assertNotNull(                parser.getPosition());
        assertEquals(false,           parser.isWhiteToMove());
        assertEquals("-",             parser.getCastlingOptions());
        assertEquals(Bitmap.NOSQUARE, parser.getEnPassantSquare());
        int hmvc = (int) parser.getOperandInt(FenParser.OPCODE_HMVC);
        assertEquals(22,              hmvc);
        int fmvn = (int) parser.getOperandInt(FenParser.OPCODE_FMVN);
        assertEquals(89,              fmvn);
        assertEquals("Bxe4",          parser.getOperand("bm"));
        assertEquals(expectedEpd, parser.toEpd());
    }

    @Test
    public void callingToEpdWithoutParsingFirst()
    {
        String epd = "2r2rk1/1bqnbpp1/1p1ppn1p/pP6/N1P1P3/P2B1N1P/1B2QPP1/R2R2K1 b - - "
                 + "multipleOperands a1 a2 a3;";
        parser.init(epd);
        try {
            parser.toEpd();
        } catch (IllegalStateException e) {
            assertEquals("need to call parse() first",
                    e.getMessage());
        }
    }

    @Test
    public void givenEPDwithMultipleOperands()
    {
        String epd = "2r2rk1/1bqnbpp1/1p1ppn1p/pP6/N1P1P3/P2B1N1P/1B2QPP1/R2R2K1 b - - "
                 + "multipleOperands a1 a2 a3;";
        parser.init(epd);
        try {
            parser.parseEpd();
        } catch (UnsupportedOperationException e) {
            assertEquals("multiple operands not supported: multipleOperands a1 a2 a3",
                    e.getMessage());
        }
    }

    @Test
    public void givenEPDwithQuotedArgument()
    {
        String epd = "2r2rk1/1bqnbpp1/1p1ppn1p/pP6/N1P1P3/P2B1N1P/1B2QPP1/R2R2K1 b - - "
                 + "id \"someText\";";
        parser.init(epd);
        try {
            parser.parseEpd();
        } catch (UnsupportedOperationException e) {
            assertEquals("quoted operands not supported: id \"someText\"",
                    e.getMessage());
        }
    }
    
    @Test
    public void givenEPDwithNoClosingDelimiter()
    {
        String e = "8/5k2/2R1b1pp/5p2/5Pn1/4qBKP/4R1P1/8 b - - bm someMove; dm 3; hmvc 4";
        String expectedEpd = e + ";";
        parser.init(e);
        parser.parseEpd();
        assertNotNull(                parser.getPosition());
        assertEquals(false,           parser.isWhiteToMove());
        assertEquals("-",             parser.getCastlingOptions());
        assertEquals(Bitmap.NOSQUARE, parser.getEnPassantSquare());
        int hmvc = (int) parser.getOperandInt(FenParser.OPCODE_HMVC);
        assertEquals(4,               hmvc);
        assertEquals(null,            parser.getOperand(FenParser.OPCODE_FMVN));
        assertEquals("3",             parser.getOperand("dm"));
        assertEquals("someMove",      parser.getOperand("bm"));
        assertEquals(expectedEpd,     parser.toEpd());
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
