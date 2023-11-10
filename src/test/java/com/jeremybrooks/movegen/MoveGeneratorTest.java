package com.jeremybrooks.movegen;

import static com.jeremybrooks.chess.base.Bitmap.B7;
import static com.jeremybrooks.chess.base.Bitmap.C8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jeremybrooks.chess.movegen.DefaultGenerator;
import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.util.Displayer;
import com.jeremybrooks.chess.util.Util;

public class MoveGeneratorTest {

    private DefaultGenerator mg = new DefaultGenerator();
    private GameState g; 
    
    @Before
    public void setUp() throws Exception {
        g = new GameState(GameState.MAX_NUM_MOVES_MADE);
        mg.setGameState(g);
    }

    @Test
    public void testEncodeMove()
    {
        int fromSquare = B7;
        int toSquare = C8;
        int movingPiece = Piece.ENCODED[Piece.TO_PIECE[Piece.PAWN]];
        int capturedPiece = Piece.ENCODED[Piece.TO_PIECE[Piece.BISHOP]];
        int promotedPiece = Piece.ENCODED[Piece.TO_PIECE[Piece.QUEEN]];
        int encodedMove = Util.EncodeMove(fromSquare, toSquare, movingPiece, capturedPiece, promotedPiece);
        
        assertEquals(fromSquare, encodedMove & 0x3F);
        assertEquals(toSquare, (encodedMove >> 6) & 0x3F);
        assertEquals(movingPiece, (encodedMove >> 12) & 0x3);
        assertEquals(capturedPiece, (encodedMove >> 15) & 0x3);
        assertEquals(promotedPiece, (encodedMove >> 18) & 0x3);
    }
    
    @Test
    public void testGenerateCapturesFromStartingPositionWhiteToMove() {
        String startFenWhiteToMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Set<String> expectedMoves = toSet("");
        Set<String> actualCaptures = generateCapturesInFan(startFenWhiteToMove);
        assertMovesAreEqual(expectedMoves, actualCaptures);
    }

    @Test
    public void testGenerateCapturesFromStartingPositionBlackToMove() {
        String startFenBlackToMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1";
        Set<String> expectedMoves = toSet("");
        Set<String> actualCaptures = generateCapturesInFan(startFenBlackToMove);
        assertMovesAreEqual(expectedMoves, actualCaptures);
    }

    @Test
    public void testGenerateNonCapturesFromStartingPositionWhiteToMove() {
        String startFenWhiteToMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Set<String> expectedMoves = toSet("Pa2-a4,Pb2-b4,Pc2-c4,Pd2-d4,Pe2-e4,Pf2-f4,Pg2-g4,Ph2-h4,"
                + "Pa2-a3,Pb2-b3,Pc2-c3,Pd2-d3,Pe2-e3,Pf2-f3,Pg2-g3,Ph2-h3,"
                + "Nb1-a3,Nb1-c3,Ng1-f3,Ng1-h3");
        Set<String> actualMoves = generateNonCapturesInFan(startFenWhiteToMove);
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateNonCapturesFromStartingPositionBlackToMove() {
        String startFenBlackToMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1";
        Set<String> expectedMoves = toSet("Pa7-a5,Pb7-b5,Pc7-c5,Pd7-d5,Pe7-e5,Pf7-f5,Pg7-g5,Ph7-h5,"
                + "Pa7-a6,Pb7-b6,Pc7-c6,Pd7-d6,Pe7-e6,Pf7-f6,Pg7-g6,Ph7-h6,"
                + "Nb8-a6,Nb8-c6,Ng8-f6,Ng8-h6");
        Set<String> actualMoves = generateNonCapturesInFan(startFenBlackToMove);
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateCapturesPinnedWhitePawnCantCaptureToPromote() {
        String positionFen = "2rn4/2P5/8/8/8/8/8/2K4k w - - 0 1";
        Set<String> expectedMoves = toSet("Pc7xd8Q,Pc7xd8R,Pc7xd8B,Pc7xd8N");
        Set<String> actualMoves = generateCapturesInFan(positionFen);
        assertTrue("c7 pawn is pinned but still psuedo-legal", actualMoves.contains("Pc7xd8Q"));
        assertTrue("c7 pawn is pinned but still psuedo-legal", actualMoves.contains("Pc7xd8R"));
        assertTrue("c7 pawn is pinned but still psuedo-legal", actualMoves.contains("Pc7xd8B"));
        assertTrue("c7 pawn is pinned but still psuedo-legal", actualMoves.contains("Pc7xd8N"));
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateNonCapturesPinnedWhitePawnCanAdvance() {
        String positionFen = "k7/8/8/8/8/8/2K1P2r/8 w - - 0 1";
        Set<String> expectedMoves = toSet(
                "Kc2-b1,Kc2-c1,Kc2-d1,"
                + "Kc2-b2,Kc2-d2,"
                + "Kc2-b3,Kc2-c3,Kc2-d3,"
                + "Pe2-e3,Pe2-e4"); //e2 is pinned but it's still a good psuedo-legal move
        Set<String> actualMoves = generateNonCapturesInFan(positionFen);
        assertTrue(actualMoves.contains("Pe2-e3"));
        assertTrue(actualMoves.contains("Pe2-e4"));
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateNonCapturesPosition1() {
        String positionFen = "R1Q5/1p3p2/1k1qpb2/8/P2p4/P2P2P1/4rPK1/8 w - - 0 1";
        Set<String> expectedMoves = toSet("Kg2-f1,Kg2-g1,Kg2-h1,Kg2-h2,Kg2-f3,Kg2-h3,"
                + "Pf2-f3,Pf2-f4," /*psuedo-legal*/
                + "Pg3-g4,Pa4-a5,"
                + "Ra8-a5,Ra8-a6,Ra8-a7,Ra8-b8,"
                + "Qc8-c1,Qc8-c2,Qc8-c3,Qc8-c4,Qc8-c5,Qc8-c6,Qc8-c7,"
                + "Qc8-d7,Qc8-b8,Qc8-d8,Qc8-e8,Qc8-f8,Qc8-g8,Qc8-h8");
        Set<String> actualMoves = generateNonCapturesInFan(positionFen);
        assertTrue("pinned f2 pawn push is still psuedo-legal", actualMoves.contains("Pf2-f3"));
        assertTrue("pinned f2 pawn push is still psuedo-legal", actualMoves.contains("Pf2-f4"));
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void givenPsuedoLegalMoveKingCapturesAndIsInCheck() {
        String positionFen = "R1Q5/1p3p2/1k1qpb2/8/P2p4/P2P1np1/6K1/8 w - - 0 1";
        Set<String> expectedMoves = toSet("Qc8xb7,Qc8xe6,Kg2xf3");
        Set<String> actualMoves = generateCapturesInFan(positionFen);
        assertMovesAreEqual(expectedMoves, actualMoves);
    }
    
    @Test
    public void givenKingNeedsToEvadeCheck()
    {
        String positionFen = "8/8/4k3/2pp1q2/4K3/8/8/8 w - - 0 1";
        Set<String> actualMoves = generateCapturesInFan(positionFen);
        assertTrue(actualMoves.isEmpty());
        
        Set<String> expectedMoves = toSet("Ke4-e3");
        actualMoves = generateNonCapturesInFan(positionFen);
        assertMovesAreEqual(expectedMoves, actualMoves);

        actualMoves = generateKingEscapesInFan(positionFen);
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testWhitePawnPromotions()
    {
        String blockedAndCanPromoteAndCanCaptureToPromote = "1r1n2k1/2PP4/8/8/8/8/2q5/K7 w - - 0 1";
        Set<String> expectedMoves = toSet("Pc7-c8B,Pc7-c8N,Pc7-c8Q,Pc7-c8R,Pc7xb8B,Pc7xb8N,"
                + "Pc7xb8Q,Pc7xb8R,Pc7xd8B,Pc7xd8N,Pc7xd8Q,Pc7xd8R"); 
        Set<String> actualMoves = generateMovesInFan(blockedAndCanPromoteAndCanCaptureToPromote);
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testBlackPawnPromotions()
    {
        Set<String> actualMoves = generateMovesInFan("k7/2Q5/8/8/8/8/7p/K5NR b - - 0 1");
        Set<String> expectedMoves = toSet("Ph2xg1Q,Ph2xg1R,Ph2xg1B,Ph2xg1N"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testWhiteCastling()
    {
        String positionFen = "k7/8/8/8/8/p6p/P6P/R3K2R w KQ - 0 1";
        Set<String> expectedMoves = toSet("Ke1-c1,Ke1-d1,Ke1-d2,Ke1-e2,Ke1-f1,Ke1-f2,Ke1-g1,"
                + "Ra1-b1,Ra1-c1,Ra1-d1,Rh1-f1,Rh1-g1"); 
        Set<String> actualMoves = generateMovesInFan(positionFen);
        assertMovesAreEqual(expectedMoves, actualMoves);
        actualMoves.contains("Ke1-c1 0-0-0");
        actualMoves.contains("Ke1-g1 0-0");
    }

    @Test
    public void testWhiteCannotCastleDueToKingOrRookHavingMovedAndMovedBack()
    {
        String initialPositionsButCastlingIsUnavailable = "k7/8/8/8/8/p6p/P6P/R3K2R w - - 0 1";
        Set<String> actualMoves = generateMovesInFan(initialPositionsButCastlingIsUnavailable);
        assertFalse(actualMoves.contains("Ke1-c1"));
        assertFalse(actualMoves.contains("Ke1-g1"));
    }

    @Test
    public void testWhiteCannotCastleThroughCheck()
    {
        String bishopAttackingB1andF1 = "k7/8/8/8/8/p2b3p/P6P/R3K2R w KQ - 0 1";
        Set<String> actualMoves = generateMovesInFan(bishopAttackingB1andF1);
        assertTrue (actualMoves.contains("Ke1-c1"));
        assertFalse(actualMoves.contains("Ke1-g1"));
    }

    @Test
    public void testWhiteCannotCastleToAvoidCheck()
    {
        String rookCheckingKing = "k7/8/8/4r3/8/p6p/P6P/R3K2R w KQ - 0 1";
        Set<String> actualMoves = generateMovesInFan(rookCheckingKing);
        assertFalse(actualMoves.contains("Ke1-c1"));
        assertFalse(actualMoves.contains("Ke1-g1"));
    }

    @Test
    public void testBlackCastling()
    {
        String initialPositions = "r3k2r/p6p/P6P/8/8/8/8/7K b kq - 0 1";
        Set<String> actualMoves = generateMovesInFan(initialPositions);
        assertTrue(actualMoves.contains("Ke8-c8"));
        assertTrue(actualMoves.contains("Ke8-g8"));
    }

    @Test
    public void testBlackCanCastleKingSideButNotQueenside()
    {   
        String initialPositions = "r3k2r/p1ppqNb1/bn2pnp1/3P4/1p2P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq - 0 1";
        Set<String> actualMoves = generateMovesInFan(initialPositions);
        //TODO: can reduce this position to its basic elements, kings, black rooks and white knight and castling privileges
        //TODO: mirror this test for the white to move
        assertTrue("king-side castling is allowed when rook is attacked",
                actualMoves.contains("Ke8-g8"));
        assertFalse("queen-side castling is prevented due to king having to move through check",
                actualMoves.contains("Ke8-c8"));
    }

    @Test
    public void testBlackCannotCastle()
    {
        String initialPositionsButCastlingIsUnavailable = "r3k2r/p6p/P6P/8/8/8/8/7K b - - 0 1";
        Set<String> actualMoves = generateMovesInFan(initialPositionsButCastlingIsUnavailable);
        assertFalse(actualMoves.contains("Ke8-c8 0-0-0"));
        assertFalse(actualMoves.contains("Ke8-g8 0-0"));
    }

    @Test
    public void testBlackCannotCastleBug()
    {
        String longCastleUnavailable = "4k2r/rppp1ppp/1b3nbN/nP6/B1PPP3/B4N2/Pp4PP/R2Q1RK1 b k - 0 2";
        Set<String> actualMoves = generateMovesInFan(longCastleUnavailable);
        assertFalse(actualMoves.contains("Ke8-c8"));
        assertFalse(actualMoves.contains("Ke8-g8")); //bishop attacks f8
    }
    
    @Test
    public void testPromotionsIncludePromotionPieceBug()
    {
        String longCastleUnavailable = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 b kq - 0 1";
        Set<String> actualMoves = generateMovesInFan(longCastleUnavailable);
        assertFalse(actualMoves.contains("Pb2-b1"));
        assertTrue (actualMoves.contains("Pb2-b1Q"));
        assertTrue (actualMoves.contains("Pb2-b1R"));
        assertTrue (actualMoves.contains("Pb2-b1B"));
        assertTrue (actualMoves.contains("Pb2-b1N"));

        assertFalse(actualMoves.contains("Pb2xa1"));
        assertTrue (actualMoves.contains("Pb2xa1Q"));
        assertTrue (actualMoves.contains("Pb2xa1R"));
        assertTrue (actualMoves.contains("Pb2xa1B"));
        assertTrue (actualMoves.contains("Pb2xa1N"));
    }
    
    /*
     * Kf1xf2 (f1f2) is not generated
		position fen r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1 moves f1f2 b6f2 g1f1 a3d3
		
		 
		d
   		   -----------------
		8 | r - - - k - - r |
		7 | P p p p - p p p |
		6 | - - - - - n b N |
		5 | n P - - - - - - |
		4 | B B P - P - - - |
		3 | - - - q - N - - |
		2 | P p - P - b P P |
		1 | R - - Q - K - - |
   		   -----------------
    		a b c d e f g h

		State: w - - 2 3
		"r3k2r/Pppp1ppp/5nbN/nP6/BBP1P3/3q1N2/Pp1P1bPP/R2Q1K2 w kq - 2 3"
     */
    @Test
    public void testKingCanEscapeByCapturingUnprotectedPieceBug()
    {
        String fen = "r3k2r/Pppp1ppp/5nbN/nP6/BBP1P3/3q1N2/Pp1P1bPP/R2Q1K2 w kq - 2 3";
        Set<String> actualMoves = generateMovesInFan(fen);
        assertTrue(actualMoves.contains("Qd1-e2"));
        assertTrue(actualMoves.contains("Kf1xf2"));

        actualMoves = generateKingEscapesInFan(fen);
        assertTrue(actualMoves.contains("Qd1-e2"));
        assertTrue(actualMoves.contains("Kf1xf2"));
    }
    
    //duplicate move generated g1f2 (Kg1xf2): r3k2r/Pppp1ppp/5nbN/nP6/BBP1P3/q4N2/Pp1P1bPP/R2Q2K1 w KQkq - 0 2
    @Test
    public void testKingEscapesDontGenerateSameMoveMoreThanOnceBug()
    {
        String fen = "r3k2r/Pppp1ppp/5nbN/nP6/BBP1P3/q4N2/Pp1P1bPP/R2Q2K1 w KQkq - 0 2";
        Set<String> actualMoves = generateMovesInFan(fen);
        Set<String> expectedMoves = toSet("Kg1-f1,Kg1-h1,Kg1xf2");
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    // The following test cases check for castling legality when the king
    // attempts to castle out of, through, into check or even near attacked squares.
    // The test cases use a simple test position that begins with just the following pieces ...
    // 
    //   * both kings in starting position and
    //   * both rooks (on both sides) in starting position and
    //   * all castling rights available
    // 
    // Each test then adds an opposing queen to be placed along the sideToMove's 5th
    // rank (from the d-file to the g-file, or vice versa for black) so it always attacks 
    // a pair of the sideToMove's first rank squares (with the exception of the e-file).
    // With the opposing and attacking rooks, every bank-rank square is attacked at some
    // point. At every queen placement we have a test case and a check for a legal
    // long or short castle move.
    //
    // Black to move
    //                                       Queen on    Kside   Qside     Scenario
    // r3k2r/8/8/8/3Q4/8/8/R3K2R b KQkq - 0 1    d4         y       n       castle thru check on qside
    // r3k2r/8/8/8/4Q3/8/8/R3K2R b KQkq - 0 1    e4         n       n       castle out of check
    // r3k2r/8/8/8/5Q2/8/8/R3K2R b KQkq - 0 1    f4         n       y       castle thru check on kside
    // r3k2r/8/8/8/6Q1/8/8/R3K2R b KQkq - 0 1    g4         n       n       castle into check both sides
    //
    // White to move
    //
    // r3k2r/8/8/3q4/8/8/8/R3K2R w KQkq - 0 1    d5         y       n       
    // r3k2r/8/8/4q3/8/8/8/R3K2R w KQkq - 0 1    e5         n       n
    // r3k2r/8/8/5q2/8/8/8/R3K2R w KQkq - 0 1    f5         n       y
    // r3k2r/8/8/6q1/8/8/8/R3K2R w KQkq - 0 1    g5         n       n
    //
    // Additionally, a second test position is used--the same as the first but with a bishop
    // instead of a queen. Tests check for legality after the bishop captures one of the 
    // involved rooks. We also verify that even with a castling right specified that isn't 
    // legal given the initial rook position (or existence) that the given castle right is revoked.

    
    @Test
    public void givenWhiteCastlingLegalityWithQueenOnD5()
    {
        String fen = "r3k2r/8/8/3q4/8/8/8/R3K2R w KQkq - 0 1";
        Set<String> actualMoves = generateMovesInFan(fen);
        assertFalse("because king passes through check",        actualMoves.contains("Ke1-c1"));
        assertTrue ("because king does not pass through check", actualMoves.contains("Ke1-g1"));
    }

    @Test
    public void givenWhiteCastlingLegalityWithQueenOnE5()
    {
        String fen = "r3k2r/8/8/4q3/8/8/8/R3K2R w KQkq - 0 1";
        Set<String> actualMoves = generateMovesInFan(fen);
        assertFalse("because king can't castle to avoid check", actualMoves.contains("Ke1-c1"));
        assertFalse("because king can't castle to avoid check", actualMoves.contains("Ke1-g1"));
    }

    @Test
    public void givenWhiteCastlingLegalityWithQueenOnF5()
    {
        String fen = "r3k2r/8/8/5q2/8/8/8/R3K2R w KQkq - 0 1";
        Set<String> actualMoves = generateMovesInFan(fen);
        assertTrue ("because b1 attack doesn't prevent long castling", actualMoves.contains("Ke1-c1"));
        assertFalse("because king passes through check",               actualMoves.contains("Ke1-g1"));
    }
    
    @Test
    public void givenWhiteCastlingLegalityWithQueenOnG5()
    {
        String fen = "r3k2r/8/8/6q1/8/8/8/R3K2R w KQkq - 0 1";
        Set<String> actualMoves = generateMovesInFan(fen);
        assertFalse("because king will end up in check", actualMoves.contains("Ke1-c1"));
        assertFalse("because king will end up in check", actualMoves.contains("Ke1-g1"));
    }

    @Test
    public void givenWhiteCastlingLegalityNoQueenButBishopJustTookKingSideRook()
    {
    	String correctCastlingRights = "r3k2r/8/8/8/8/8/8/R3K2b w Qkq - 0 1";
		String invalidCastlingRights = "r3k2r/8/8/8/8/8/8/R3K2b w KQkq - 0 1";
		String fens[] = new String[]{correctCastlingRights,
    							     invalidCastlingRights};
    	//Correct moves should be generated in both cases
    	for(String fen: fens)
    	{
    		Set<String> actualMoves = generateMovesInFan(fen);
    		assertTrue ("because king is safe",                 actualMoves.contains("Ke1-c1"));
    		assertFalse("because of capture of king side rook", actualMoves.contains("Ke1-g1"));
    	}
    }

    @Test
    public void givenWhiteCastlingLegalityNoQueenButBishopJustTookQueenSideRook()
    {
    	String correctCastlingRights = "r3k2r/8/8/8/8/8/8/b3K2R w Kkq - 0 1";
		String invalidCastlingRights = "r3k2r/8/8/8/8/8/8/b3K2R w KQkq - 0 1";
		String fens[] = new String[]{correctCastlingRights,
    								 invalidCastlingRights};
    	//Correct moves should be generated in both cases
    	for(String fen: fens)
    	{
    		Set<String> actualMoves = generateMovesInFan(fen);
    		assertFalse("because of capture of queen side rook", actualMoves.contains("Ke1-c1"));
    		assertTrue ("because king is safe",                  actualMoves.contains("Ke1-g1"));
    	}
    }

    @Test
    public void givenBlackCastlingLegalityWithQueenOnG4()
    {
        String destinationSquaresC8andG8AreAttacked = "r3k2r/8/8/8/6Q1/8/8/R3K2R b KQkq - 0 1";
        Set<String> actualMoves = generateMovesInFan(destinationSquaresC8andG8AreAttacked);
        assertFalse("because king will end up in check",        actualMoves.contains("Ke8-c8"));
        assertFalse("because king will end up in check",        actualMoves.contains("Ke8-g8"));
    }

    @Test
    public void givenBlackCastlingLegalityWithQueenOnF4()
    {
        String b8andF8AreAttacked = "r3k2r/8/8/8/5Q2/8/8/R3K2R b KQkq - 0 1";
        Set<String> actualMoves = generateMovesInFan(b8andF8AreAttacked);
        assertTrue ("because b8 attack doesn't preclude long castling", actualMoves.contains("Ke8-c8"));
        assertFalse("because king passes through check",                actualMoves.contains("Ke8-g8"));
    }

    @Test
    public void givenBlackCastlingLegalityWithQueenOnE4()
    {
        String kingInCheck = "r3k2r/8/8/8/4Q3/8/8/R3K2R b KQkq - 0 1";
        Set<String> actualMoves = generateMovesInFan(kingInCheck);
        assertFalse("because king is in check",                 actualMoves.contains("Ke8-c8"));
        assertFalse("because king is in check",                 actualMoves.contains("Ke8-g8"));
    }

    @Test
    public void givenBlackCastlingLegalityWithQueenOnD4()
    {
        String d8andH8AreAttacked = "r3k2r/8/8/8/3Q4/8/8/R3K2R b KQkq - 0 1";
        Set<String> actualMoves = generateMovesInFan(d8andH8AreAttacked);
        assertFalse("because king passes through check",        actualMoves.contains("Ke8-c8"));
        assertTrue ("because king does not pass through check", actualMoves.contains("Ke8-g8"));
    }
    
    @Test
    public void givenBlackCastlingLegalityNoQueenButBishopJustTookKingSideRook()
    {
    	String correctCastlingRights = "r3k2B/8/8/8/8/8/8/R3K2R b KQq - 0 1";
		String invalidCastlingRights = "r3k2B/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
		String fens[] = new String[]{correctCastlingRights,
      		   						 invalidCastlingRights};
		//Correct moves should be generated in both cases
    	for(String fen: fens)
    	{
    		Set<String> actualMoves = generateMovesInFan(fen);
    		assertTrue ("because king is safe",                 actualMoves.contains("Ke8-c8"));
    		assertFalse("because of capture of king side rook", actualMoves.contains("Ke8-g8"));
    	}
    }

    @Test
    public void givenBlackCastlingLegalityNoQueenButBishopJustTookQueenSideRook()
    {
    	String correctCastleRights = "B3k2r/8/8/8/8/8/8/R3K2R b KQk - 0 1";
		String invalidCastleRights = "B3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
		String fens[] = new String[]{correctCastleRights,
    			                     invalidCastleRights};
		//Correct moves should be generated in both cases
    	for(String fen: fens)
    	{
    		Set<String> actualMoves = generateMovesInFan(fen);
    		assertFalse("because of capture of king side rook", actualMoves.contains("Ke8-c8"));
    		assertTrue ("because king is safe",                 actualMoves.contains("Ke8-g8"));
    	}
    }

    @Test
    public void testBlackCastlingGivenAttackedSquares()
    {
        String queenAttackingB8andD8andF8 = "r3k2r/p6p/P2Q3P/8/8/8/8/7K b kq - 0 1";
        Set<String> actualMoves = generateMovesInFan(queenAttackingB8andD8andF8);
        assertFalse("because king does not pass through check", actualMoves.contains("Ke8-c8"));
        assertFalse("because king passes through check",       actualMoves.contains("Ke8-g8"));
    }

    @Test
    public void testBlackCannotCastleToAvoidCheck()
    {
        String rookCheckingKing = "r3k2r/p6p/P6P/8/4R3/8/8/7K b kq - 0 1";
        Set<String> actualMoves = generateMovesInFan(rookCheckingKing);
        assertFalse(actualMoves.contains("Ke8-c8"));
        assertFalse(actualMoves.contains("Ke8-g8"));
    }

    @Test
    public void testBlackCanCastleWhenRookIsAttacked()
    {
        String bishopAttackingQueenSideRook = "r3k2r/p6p/P6P/8/4B3/8/8/7K b kq - 0 1";
        Set<String> actualMoves = generateMovesInFan(bishopAttackingQueenSideRook);
        assertTrue(actualMoves.contains("Ke8-c8"));
        assertTrue(actualMoves.contains("Ke8-g8"));
    }

    @Test
    public void givenPinnedBPawn()
    {
        String pinnedPawn = "rnb1kbnr/pp1ppppp/8/q1p5/1P1P4/8/P1P1PPPP/RNBQKBNR w KQkq - 1 3";
        Set<String> actualMoves = generateMovesInFan(pinnedPawn);
        assertTrue("pinned but still psuedo-legal", actualMoves.contains("Pb4xc5"));
        assertTrue("pinned but still psuedo-legal", actualMoves.contains("Pb4-b5"));
        assertTrue(actualMoves.contains("Pb4xa5"));
    }
    
//    Nd2-b1 for rnb1kbnr/pp1ppppp/8/q1p5/3P4/8/PPPNPPPP/R1BQKBNR w KQkq - 2 3
    @Test
    public void givenPinnedKnight()
    {
        String pinnedKnightOnD2 = "rnb1kbnr/pp1ppppp/8/q1p5/3P4/8/PPPNPPPP/R1BQKBNR w KQkq - 2 3";
        Set<String> actualMoves = generateMovesInFan(pinnedKnightOnD2);
        assertFalse(actualMoves.toString().contains("Nd2"));
    }

    @Test
    public void givenPinnedBishop()
    {   //illegal: Bd2-c1 for rnb1kbnr/pp1ppppp/8/q1p5/3P4/8/PPPBPPPP/RN1QKBNR w KQkq - 2 3
        String pinnedBishopOnD2 = "rnb1kbnr/pp1ppppp/8/q1p5/3P4/8/PPPBPPPP/RN1QKBNR w KQkq - 2 3";
        Set<String> actualMoves = generateMovesInFan(pinnedBishopOnD2);
        assertFalse(actualMoves.contains("Bd2-c1"));
        assertFalse(actualMoves.contains("Bd2-e3"));
        assertFalse(actualMoves.contains("Bd2-f4"));
        assertFalse(actualMoves.contains("Bd2-g5"));
        assertFalse(actualMoves.contains("Bd2-h6"));
        assertTrue(actualMoves.contains("Bd2-c3"));
        assertTrue(actualMoves.contains("Bd2-b4"));
        assertTrue(actualMoves.contains("Bd2xa5"));
    }
    
    @Test
    public void givenPinnedPawnCapturesEnPassant()
    {
        //illegal: Pf5xg6 for rnb1kbnr/pppp1p1p/5q2/4pPp1/8/8/PPPPPKPP/RNBQ1BNR w kq g6 0 4
        String pinnedPawn = "rnb1kbnr/pppp1p1p/5q2/4pPp1/8/8/PPPPPKPP/RNBQ1BNR w kq g6 0 4";
        Set<String> actualMoves = generateMovesInFan(pinnedPawn);
        System.out.println(actualMoves.toString());
        assertTrue("pinned piece moves are psuedo legal", actualMoves.contains("Pf5xg6"));
    }
    
    @Test
    public void givenBlackCanCaptureEnPassantCreatesDiscoveredCheck()
    {
        //8/2p5/3p4/KP5r/1R3pPk/8/4P3/8 b - g3  f4g3 should be in there (18 total LEGAL moves)
        String blackEnPassantPossible = "8/2p5/3p4/KP5r/1R3pPk/8/4P3/8 b - g3 0 1";
        Set<String> actualMoves = generateMovesInFan(blackEnPassantPossible);
        System.out.println(actualMoves);
        assertTrue(actualMoves.contains("Pf4xg3"));
        int f4xg3 = 38301;
        boolean isLegal = mg.isLegalMove(g, f4xg3);
        assertFalse(isLegal);
    }

    //@Test  - TODO: this fails currently and it IS a bug and needs fixing
    public void givenWhiteCanCaptureEnPassantCreatesDiscoveredCheck()
    {
        //8/8/3p4/KPp4r/1R3p1k/8/4P1P1/8 w - c6 0 1     b5xc6 e.p. 
        //8/2p5/3p4/KP5r/1R3pPk/8/4P3/8 b - g3  f4g3 should be in there (18 total LEGAL moves)
        String blackEnPassantPossible = "8/8/3p4/KPp4r/1R3p1k/8/4P1P1/8 w - c6 0 1";
        Set<String> actualMoves = generateMovesInFan(blackEnPassantPossible);
        displayBoardAndSideToMove();
        System.out.println(actualMoves);
        assertTrue(actualMoves.contains("Pb5xc6"));
        int f4xg3 = 38301;
        boolean isLegal = mg.isLegalMove(g, f4xg3);
        assertFalse(isLegal); //fails here
    }

    @Test
    public void givenKingAttemptsCaptureOfDefendedPiece()
    {
        String defendedPawn = "8/8/8/8/8/2k5/3pn3/4K3 w - - 0 1";
        Set<String> actualMoves = generateMovesInFan(defendedPawn);
        assertFalse("because piece is defended",   actualMoves.contains("Ke1xd2"));
        assertTrue ("because piece is undefended", actualMoves.contains("Ke1xe2"));
    }

    @Test
    public void givenKingAttemptsMovingIntoCheck()
    {
        String defendedPawn = "8/8/8/8/8/2k2r2/3p4/4K3 w - - 0 1";
        Set<String> actualMoves = generateMovesInFan(defendedPawn);
        assertFalse("because piece is defended",      actualMoves.contains("Ke1xd2"));
        assertFalse("because king moves into check",  actualMoves.contains("Ke1-f1"));
        assertFalse("because king moves into check",  actualMoves.contains("Ke1-f2"));
        assertTrue ("because square is not attacked", actualMoves.contains("Ke1-e2"));
        assertTrue ("because square is not attacked", actualMoves.contains("Ke1-d1"));
    }

    @Test
    public void testMixOfSpecialCasesPosition()
    {
        String enPassantCastlingCapturesAndPromotions =
                "r1b2k1r/1pQn2pp/pP2p3/2p1Pp1N/B2p2q1/B2P4/P1P2PPP/R3K2R w KQ f6 0 1";
        Set<String> actualMoves = generateMovesInFan(enPassantCastlingCapturesAndPromotions);
        Set<String> expectedMoves = toSet("Pc2-c3,Pc2-c4,Pe5xf6,Pf2-f3,Pf2-f4,Pg2-g3,Ph2-h3,Ph2-h4," +
                "Ba3-b2,Ba3-b4,Ba3-c1,Ba3xc5,Ba4-b3,Ba4-b5,Ba4-c6,Ba4xd7," +
                "Nh5-f4,Nh5-f6,Nh5-g3,Nh5xg7," +
                "Ra1-b1,Ra1-c1,Ra1-d1,Rh1-f1,Rh1-g1," +
                "Qc7-b8,Qc7-c6,Qc7-d6,Qc7-d8,Qc7xb7,Qc7xc5,Qc7xc8,Qc7xd7," +
                "Ke1-d2,Ke1-f1,Ke1-g1"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }
    
//    @Test
//    public void testGenerateInterpositions()
//    {
//        String position = "R6k/7b/6n1/4q3/3r4/8/8/K7 b - - 0 1";
//        Set<String> actualMoves = generateKingEscapes(position);
//        Set<String> expectedMoves = toSet("Kh8-g7,Bh7-g8,Ng6-f8,Qe5-e8,Qe5-b8,Rd4-d8"); 
//        assertMovesAreEqual(expectedMoves, actualMoves);
//        
//    }
    
    //***************************************************************************
    //*                                                                         *
    //* Single checker:                                                         *
    //*    Generate captures to checker's square                                *
    //*    If that checker is not a knight, generate interposing moves.         *
    //*                                                                         *
    //* Two checkers:                                                           *
    //*    Generate king captures to either checker's square who is left en     *
    //*    prise (unprotected).                                                 *
    //*                                                                         *
    //* Always:                                                                 *
    //*    Generate king moves to flight squares                                *
    //*                                                                         *
    //***************************************************************************
    @Test
    public void testEscapeCheckFromOneCheckingPiece()
    {
        String kingCanTakeChecker =    "6Rk/R7/8/8/8/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapesInFan(kingCanTakeChecker);
        Set<String> expectedMoves = toSet("Kh8xg8"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckWithoutEnPassantOption()
    {
        String enPassantCapture = "R7/8/8/1k6/2Pp4/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapesInFan(enPassantCapture);
        Set<String> expectedMoves = toSet("Kb5xc4,Kb5-b4,Kb5-c5,Kb5-b6,Kb5-c6"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }
    
    @Test
    public void testEscapeCheckByEnPassantCaptureOnSixthRank()
    {
    	String whiteCaptureEPLeft = "8/5p2/8/3pP3/4K3/8/8/7k w - d6 0 1";
    	Set<String> actualMoves = generateKingEscapesInFan(whiteCaptureEPLeft);
    	Set<String> expectedMoves = toSet("Pe5xd6,Ke4xd5,Ke4-d3,Ke4-e3,Ke4-f3,Ke4-d4,Ke4-f4,Ke4-f5"); 
    	assertMovesAreEqual(expectedMoves, actualMoves);
    	
    	String whiteCaptureEPRight = "8/3p4/8/4Pp2/4K3/8/8/7k w - f6 0 1";
    	actualMoves = generateKingEscapesInFan(whiteCaptureEPRight);
    	expectedMoves = toSet("Pe5xf6,Ke4xf5,Ke4-d3,Ke4-e3,Ke4-f3,Ke4-d4,Ke4-f4,Ke4-d5"); 
    	assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckByEnPassantCaptureOnThirdRank()
    {
    	String blackCaptureEPLeft = "8/8/8/4k3/3Pp3/8/5P2/7K b - d3 0 1";
    	Set<String> actualMoves = generateKingEscapesInFan(blackCaptureEPLeft);
    	Set<String> expectedMoves = toSet("Pe4xd3,Ke5xd4,Ke5-f4,Ke5-d5,Ke5-f5,Ke5-d6,Ke5-e6,Ke5-f6"); 
    	assertMovesAreEqual(expectedMoves, actualMoves);
    	
    	String blackCaptureEPRight = "8/8/8/4k3/4pP2/8/3P4/7K b - f3 0 1";
    	actualMoves = generateKingEscapesInFan(blackCaptureEPRight);
    	expectedMoves = toSet("Pe4xf3,Ke5xf4,Ke5-d4,Ke5-d5,Ke5-f5,Ke5-d6,Ke5-e6,Ke5-f6"); 
    	assertMovesAreEqual(expectedMoves, actualMoves);

    	String blackWithoutEPCaptureOption = "8/8/8/3k4/2P1p3/8/8/7K b - c3 0 1";
    	actualMoves = generateKingEscapesInFan(blackWithoutEPCaptureOption);
    	expectedMoves = toSet("Kd5xc4,Kd5-d4,Kd5-c5,Kd5-e5,Kd5-c6,Kd5-d6,Kd5-e6"); 
    	assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckByPawnCapturePromotionOnEighthRank()
    {
    	String whiteCanPromote = "K6r/6P1/8/8/3k4/8/8/8 w - - 0 1";
    	Set<String> actualMoves = generateKingEscapesInFan(whiteCanPromote);
    	Set<String> expectedMoves = toSet("Ka8-a7,Ka8-b7,Pg7xh8Q,Pg7xh8R,Pg7xh8B,Pg7xh8N,Pg7-g8Q,Pg7-g8R,Pg7-g8B,Pg7-g8N"); 
    	assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckByPawnCapturePromotionOnFirstRank()
    {
        String blackCanPromote = "8/8/8/8/3K4/8/6p1/k6R b - - 0 1";
        Set<String> actualMoves = generateKingEscapesInFan(blackCanPromote);
        Set<String> expectedMoves = toSet("Ka1-a2,Ka1-b2,Pg2xh1Q,Pg2xh1R,Pg2xh1B,Pg2xh1N,Pg2-g1Q,Pg2-g1R,Pg2-g1B,Pg2-g1N"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckViaCaptureAndFlightSquare()
    {
        String bishopCanTakeCheckerAndKingHasFlightSquare = "5RRk/5b2/8/8/8/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapesInFan(bishopCanTakeCheckerAndKingHasFlightSquare);
        Set<String> expectedMoves = toSet("Bf7xg8,Kh8-h7"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateInterpositions()
    {
        String position = "R6k/7b/6n1/4q3/3r4/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapesInFan(position);
        Set<String> expectedMoves = toSet("Kh8-g7,Bh7-g8,Ng6-f8,Qe5-e8,Qe5-b8,Rd4-d8"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckmateFromTwoCheckingPieces()
    {
        String checkmated = "2RRQ3/8/1N6/3k4/8/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapesInFan(checkmated);
        Set<String> expectedMoves = toSet(""); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckFromBothKnightAndRook()
    {
        String knightAndRookChecking = "3RQ3/8/1N6/3k4/8/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapesInFan(knightAndRookChecking);
        Set<String> expectedMoves = toSet("Kd5-c5"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckFromBishopAndRook()
    {
        String bishopAndRookChecking = "8/3R4/8/2B5/3k4/8/4Q3/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapesInFan(bishopAndRookChecking);
        Set<String> expectedMoves = toSet("Kd4xc5,Kd4-c3"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckmateFromBishopDiscoveredCheck()
    {
        String bishopAndRookChecking = "8/3R4/8/8/1B1k4/8/4Q3/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapesInFan(bishopAndRookChecking);
        Set<String> expectedMoves = toSet(""); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }


    
//    @Test
//    public void testKingFlightSquaresFromCheckingPiece()
//    {
//        fail("not implemented yet");
//    }

    public void tryPrintingMoves() {
        
        String[] positions = new String[]{
                "r1b2k1r/1pQn2pp/pP2p3/2p1Pp1N/B2p2q1/B2P4/P1P2PPP/R3K2R w KQ f6 0 1"
                /*
                "r4r1k/1R1R2p1/7p/8/8/3Q1Ppq/P7/6K1 w - - 0 1",
                "6k1/ppp2pp1/1q4b1/5rQp/8/1P6/PBP2PPP/3R2K1 w - - 0 1",
                "8/6k1/8/3b3Q/pP4P1/1P6/KP3r2/N4r2 b - - 0 1",
                "3rr1k1/pp3ppp/3b4/2p5/2Q5/6qP/PPP1B1P1/R1B2K1R b - - 0 1",
                "5r1k/p4rpp/1p1b1pQ1/q2p4/P2N1PPN/1P2P2R/7P/6RK w - - 0 1"
                */
        };
        for(String position: positions)
        {
            g.set(position);
            displayBoardAndSideToMove();
            List<Integer> moves = DefaultGenerator.newMoveList();
            mg.generateCaptures(moves, g.isWhiteToMove()?0:1);
            mg.generateNonCaptures(moves, g.isWhiteToMove()?0:1);
            Set<String> generatedMoves = toCoordinateMoveSet(moves);
            
            String allMoves = "blah"; 
            assertMovesAreEqual(toSet(allMoves), generatedMoves);
        }
    }

    private List<Integer> generateMoves(String positionFen) {
        g.set(positionFen);
        int side = g.isWhiteToMove()?0:1;
        List<Integer> moves = DefaultGenerator.newMoveList();
        displayBoardAndSideToMove();
        mg.generateCaptures(moves, side);
        mg.generateNonCaptures(moves, side);
        return moves;
    }

    private Set<String> generateMovesInFan(String positionFen) {
        g.set(positionFen);
        int side = g.isWhiteToMove()?0:1;
//        List<Integer> moves = DefaultGenerator.newMoveList();
        displayBoardAndSideToMove();
//        mg.generateCaptures(moves, side);
//        mg.generateNonCaptures(moves, side);
        List<Integer> moves = mg.generateMoves(side, false);
        return toCoordinateMoveSet(moves);
    }

    private Set<String> generateCapturesInFan(String positionFen) {
        g.set(positionFen);
        int side = g.isWhiteToMove()?0:1;
        List<Integer> moves = DefaultGenerator.newMoveList();
        displayBoardAndSideToMove();
        mg.generateCaptures(moves, side);
        return toCoordinateMoveSet(moves);
    }
    
    private Set<String> generateNonCapturesInFan(String positionFen) {
        g.set(positionFen);
        int side = g.isWhiteToMove()?0:1;
        List<Integer> moves = DefaultGenerator.newMoveList();
        mg.generateNonCaptures(moves, side);
        return toCoordinateMoveSet(moves);
    }
    
    private Set<String> generateKingEscapesInFan(String positionFen) {
        g.set(positionFen);
        int side = g.isWhiteToMove()?0:1;
        List<Integer> moves = DefaultGenerator.newMoveList();
        mg.generateKingEscapes(moves, side);
        return toCoordinateMoveSet(moves);
    }

    private void displayBoardAndSideToMove() {
        System.out.println(new Displayer(g.getPosition()).formatBoard());
        System.out.println("To Move: " + (g.isWhiteToMove() ? "White" : "Black") + "\n");
    }

//    private Set<String> generateInterpositions(String positionFen) {
//        g.set(positionFen);
//        System.out.println(new Displayer().formatBoard(g.pos));
//        System.out.println("To Move: " + (g.sideToMove == 0 ? "White" : "Black") + "\n");
//        int depth = 0;
//        mg.GenerateInterpositions(g, g.moves, g.sideToMove, depth);
//        return formatGameStatesCoordinateMoveSet(g.numberOfLegalMoves[depth]);
//    }

    private Set<String> toCoordinateMoveSet(List<Integer> moveList) {
        Set<String> moveSet = new HashSet<>();
        for(int move: moveList)
        {
            String s = Util.displayMoveStr(move, false, false); 
            moveSet.add(s);
        }
        return moveSet;
    }
    
    private static Set<String> toSet(String movesInCSV)
    {
        String[] moveArray = (movesInCSV != null && !movesInCSV.isEmpty()) ? movesInCSV.split(",") : new String[]{};
        int numberOfMovesGiven = moveArray.length;
        int numberOfMovesInSet = 0;
        Set<String> moveSet = new HashSet<>();
        for(String move: moveArray)
        {
            moveSet.add(move);
            numberOfMovesInSet++;
        }
        assertEquals("cannot convert move list to set because it contains a duplicate move",
                numberOfMovesInSet, numberOfMovesGiven);
        return moveSet;
    }

    private static void assertMovesAreEqual(Set<String> expectedSet, Set<String> actualSet)
    {
        if(setsAreDifferent(expectedSet, actualSet));
        {
            sortMovesAndShowDifferences(expectedSet, actualSet);
        }
    }

    private static void sortMovesAndShowDifferences(Set<String> expectedSet,
            Set<String> actualSet) {
        List<String> expectedSorted = new ArrayList<>();
        List<String> actualSorted = new ArrayList<>();
        expectedSorted.addAll(expectedSet);
        actualSorted.addAll(actualSet);
        Collections.sort(expectedSorted);
        Collections.sort(actualSorted);
        assertEquals(oneMovePerLine(expectedSorted), oneMovePerLine(actualSorted));
    }
    
    private static boolean setsAreDifferent(Set<String> expected,
            Set<String> actual) {
        return !expected.toString().equals(actual.toString());
    }
    
    private static String oneMovePerLine(List<String> moves) {
        StringBuilder sb = new StringBuilder();
        for(String move: moves)
        {
            sb.append(move);
            sb.append("\n");
        }
        return sb.toString();
    }
}
