package com.jeremybrooks.chess.movegen;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.base.Bitmap.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.GameState;
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
        int movingPiece = PIECE[TO_PIECE[PAWN]];
        int capturedPiece = PIECE[TO_PIECE[BISHOP]];
        int promotedPiece = PIECE[TO_PIECE[QUEEN]];
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
        Set<String> actualCaptures = generateCaptures(startFenWhiteToMove);
        assertMovesAreEqual(expectedMoves, actualCaptures);
    }

    @Test
    public void testGenerateCapturesFromStartingPositionBlackToMove() {
        String startFenBlackToMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1";
        Set<String> expectedMoves = toSet("");
        Set<String> actualCaptures = generateCaptures(startFenBlackToMove);
        assertMovesAreEqual(expectedMoves, actualCaptures);
    }

    @Test
    public void testGenerateNonCapturesFromStartingPositionWhiteToMove() {
        String startFenWhiteToMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Set<String> expectedMoves = toSet("Pa2-a4,Pb2-b4,Pc2-c4,Pd2-d4,Pe2-e4,Pf2-f4,Pg2-g4,Ph2-h4,"
                + "Pa2-a3,Pb2-b3,Pc2-c3,Pd2-d3,Pe2-e3,Pf2-f3,Pg2-g3,Ph2-h3,"
                + "Nb1-a3,Nb1-c3,Ng1-f3,Ng1-h3");
        Set<String> actualMoves = generateNonCaptures(startFenWhiteToMove);
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateNonCapturesFromStartingPositionBlackToMove() {
        String startFenBlackToMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1";
        Set<String> expectedMoves = toSet("Pa7-a5,Pb7-b5,Pc7-c5,Pd7-d5,Pe7-e5,Pf7-f5,Pg7-g5,Ph7-h5,"
                + "Pa7-a6,Pb7-b6,Pc7-c6,Pd7-d6,Pe7-e6,Pf7-f6,Pg7-g6,Ph7-h6,"
                + "Nb8-a6,Nb8-c6,Ng8-f6,Ng8-h6");
        Set<String> actualMoves = generateNonCaptures(startFenBlackToMove);
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateCapturesPinnedWhitePawnCantCaptureToPromote() {
        String positionFen = "2rn4/2P5/8/8/8/8/8/2K4k w - - 0 1";
        Set<String> expectedMoves = toSet("");
        Set<String> actualMoves = generateCaptures(positionFen);
        assertFalse("invalid move: c7 pawn is pinned", actualMoves.contains("Pc7xd8Q"));
        assertFalse("invalid move: c7 pawn is pinned", actualMoves.contains("Pc7xd8R"));
        assertFalse("invalid move: c7 pawn is pinned", actualMoves.contains("Pc7xd8B"));
        assertFalse("invalid move: c7 pawn is pinned", actualMoves.contains("Pc7xd8N"));
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateNonCapturesPinnedWhitePawnCantAdvance() {
        String positionFen = "k7/8/8/8/8/8/2K1P2r/8 w - - 0 1";
        Set<String> expectedMoves = toSet(
                "Kc2-b1,Kc2-c1,Kc2-d1,"
                + "Kc2-b2,Kc2-d2,"
                + "Kc2-b3,Kc2-c3,Kc2-d3");
        Set<String> actualMoves = generateNonCaptures(positionFen);
        assertFalse("invalid move: e2 pawn is pinned", actualMoves.contains("e3"));
        assertFalse("invalid move: e2 pawn is pinned", actualMoves.contains("e4"));
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateNonCapturesPosition1() {
        String positionFen = "R1Q5/1p3p2/1k1qpb2/8/P2p4/P2P2P1/4rPK1/8 w - - 0 1";
        Set<String> expectedMoves = toSet("Kg2-f1,Kg2-g1,Kg2-h1,Kg2-h2,Kg2-f3,Kg2-h3,"
                + "Pg3-g4,Pa4-a5,"
                + "Ra8-a5,Ra8-a6,Ra8-a7,Ra8-b8,"
                + "Qc8-c1,Qc8-c2,Qc8-c3,Qc8-c4,Qc8-c5,Qc8-c6,Qc8-c7,"
                + "Qc8-d7,Qc8-b8,Qc8-d8,Qc8-e8,Qc8-f8,Qc8-g8,Qc8-h8");
        Set<String> actualMoves = generateNonCaptures(positionFen);
        assertFalse("invalid move: f2 pawn is pinned", actualMoves.contains("Pf2-f3"));
        assertFalse("invalid move: f2 pawn is pinned", actualMoves.contains("Pf2-f4"));
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateCapturesPosition1() {
        String positionFen = "R1Q5/1p3p2/1k1qpb2/8/P2p4/P2P1np1/6K1/8 w - - 0 1";
        Set<String> expectedMoves = toSet("Qc8xb7,Qc8xe6,Kg2xf3");
        Set<String> actualMoves = generateCaptures(positionFen);
        assertMovesAreEqual(expectedMoves, actualMoves);
    }
    
    @Test
    public void testWhitePawnPromotions()
    {
        String blockedAndCanPromoteAndCanCaptureToPromote = "1r1n2k1/2PP4/8/8/8/8/2q5/K7 w - - 0 1";
        Set<String> expectedMoves = toSet("Pc7-c8B,Pc7-c8N,Pc7-c8Q,Pc7-c8R,Pc7xb8B,Pc7xb8N,"
                + "Pc7xb8Q,Pc7xb8R,Pc7xd8B,Pc7xd8N,Pc7xd8Q,Pc7xd8R"); 
        Set<String> actualMoves = generateMoves(blockedAndCanPromoteAndCanCaptureToPromote);
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testBlackPawnPromotions()
    {
        Set<String> actualMoves = generateMoves("k7/2Q5/8/8/8/8/7p/K5NR b - - 0 1");
        Set<String> expectedMoves = toSet("Ph2xg1Q,Ph2xg1R,Ph2xg1B,Ph2xg1N"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testWhiteCastling()
    {
        String positionFen = "k7/8/8/8/8/p6p/P6P/R3K2R w KQ - 0 1";
        Set<String> expectedMoves = toSet("Ke1-c1,Ke1-d1,Ke1-d2,Ke1-e2,Ke1-f1,Ke1-f2,Ke1-g1,"
                + "Ra1-b1,Ra1-c1,Ra1-d1,Rh1-f1,Rh1-g1"); 
        Set<String> actualMoves = generateMoves(positionFen);
        assertMovesAreEqual(expectedMoves, actualMoves);
        actualMoves.contains("Ke1-c1 0-0-0");
        actualMoves.contains("Ke1-g1 0-0");
    }

    @Test
    public void testWhiteCannotCastleDueToKingOrRookHavingMovedAndMovedBack()
    {
        String initialPositionsButCastlingIsUnavailable = "k7/8/8/8/8/p6p/P6P/R3K2R w - - 0 1";
        Set<String> actualMoves = generateMoves(initialPositionsButCastlingIsUnavailable);
        assertFalse(actualMoves.contains("Ke1-c1 0-0-0"));
        assertFalse(actualMoves.contains("Ke1-g1 0-0"));
    }

    @Test
    public void testWhiteCannotCastleThroughCheck()
    {
        String bishopAttackingB1andF1 = "k7/8/8/8/8/p2b3p/P6P/R3K2R w KQ - 0 1";
        Set<String> actualMoves = generateMoves(bishopAttackingB1andF1);
        assertFalse(actualMoves.contains("Ke1-c1 0-0-0"));
        assertFalse(actualMoves.contains("Ke1-g1 0-0"));
    }

    @Test
    public void testWhiteCannotCastleToAvoidCheck()
    {
        String rookCheckingKing = "k7/8/8/4r3/8/p6p/P6P/R3K2R w KQ - 0 1";
        Set<String> actualMoves = generateMoves(rookCheckingKing);
        assertFalse(actualMoves.contains("Ke1-c1 0-0-0"));
        assertFalse(actualMoves.contains("Ke1-g1 0-0"));
    }
    
     @Test
    public void testBlackCastling()
    {
        String initialPositions = "r3k2r/p6p/P6P/8/8/8/8/7K b kq - 0 1";
        Set<String> actualMoves = generateMoves(initialPositions);
        assertTrue(actualMoves.contains("Ke8-c8"));
        assertTrue(actualMoves.contains("Ke8-g8"));
    }

    @Test
    public void testBlackCannotCastle()
    {
        String initialPositionsButCastlingIsUnavailable = "r3k2r/p6p/P6P/8/8/8/8/7K b - - 0 1";
        Set<String> actualMoves = generateMoves(initialPositionsButCastlingIsUnavailable);
        assertFalse(actualMoves.contains("Ke8-c8 0-0-0"));
        assertFalse(actualMoves.contains("Ke8-g8 0-0"));
    }

    @Test
    public void testBlackCannotCastleThroughCheck()
    {
        String queenAttackingB8andF8 = "r3k2r/p6p/P2Q3P/8/8/8/8/7K b kq - 0 1";
        Set<String> actualMoves = generateMoves(queenAttackingB8andF8);
        assertFalse(actualMoves.contains("Ke8-c8 0-0-0"));
        assertFalse(actualMoves.contains("Ke8-g8 0-0"));
    }

    @Test
    public void testBlackCannotCastleToAvoidCheck()
    {
        String rookCheckingKing = "r3k2r/p6p/P6P/8/4R3/8/8/7K b kq - 0 1";
        Set<String> actualMoves = generateMoves(rookCheckingKing);
        assertFalse(actualMoves.contains("Ke8-c8 0-0-0"));
        assertFalse(actualMoves.contains("Ke8-g8 0-0"));
    }

    @Test
    public void testBlackCannotCastleWhenRookIsAttacked()
    {
        String bishopAttackingQueenSideRook = "r3k2r/p6p/P6P/8/4B3/8/8/7K b kq - 0 1";
        Set<String> actualMoves = generateMoves(bishopAttackingQueenSideRook);
        assertFalse(actualMoves.contains("Ke8-c8"));
        assertTrue(actualMoves.contains("Ke8-g8"));
    }

    @Test
    public void testMixOfSpecialCasesPosition()
    {
        String enPassantCastlingCapturesAndPromotions =
                "r1b2k1r/1pQn2pp/pP2p3/2p1Pp1N/B2p2q1/B2P4/P1P2PPP/R3K2R w KQ f6 0 1";
        Set<String> actualMoves = generateMoves(enPassantCastlingCapturesAndPromotions);
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
        Set<String> actualMoves = generateKingEscapes(kingCanTakeChecker);
        Set<String> expectedMoves = toSet("Kh8xg8"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeAPawnCheckByCapturingEnPassant()
    {
        String enPassantCapture = "R7/8/8/1k6/2Pp4/8/8/K7 b - c3 0 1";
        Set<String> actualMoves = generateKingEscapes(enPassantCapture);
        Set<String> expectedMoves = toSet("Pd4xc3,Kb5xc4,Kb5-b4,Kb5-c5,Kb5-b6,Kb5-c6"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeAPawnCheckNoEnPassant()
    {
        String enPassantCapture = "R7/8/8/1k6/2Pp4/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapes(enPassantCapture);
        Set<String> expectedMoves = toSet("Kb5xc4,Kb5-b4,Kb5-c5,Kb5-b6,Kb5-c6"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckViaCaptureAndFlightSquare()
    {
        String bishopCanTakeCheckerAndKingHasFlightSquare = "5RRk/5b2/8/8/8/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapes(bishopCanTakeCheckerAndKingHasFlightSquare);
        Set<String> expectedMoves = toSet("Bf7xg8,Kh8-h7"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testGenerateInterpositions()
    {
        String position = "R6k/7b/6n1/4q3/3r4/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapes(position);
        Set<String> expectedMoves = toSet("Kh8-g7,Bh7-g8,Ng6-f8,Qe5-e8,Qe5-b8,Rd4-d8"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckmateFromTwoCheckingPieces()
    {
        String checkmated = "2RRQ3/8/1N6/3k4/8/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapes(checkmated);
        Set<String> expectedMoves = toSet(""); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckFromBothKnightAndRook()
    {
        String knightAndRookChecking = "3RQ3/8/1N6/3k4/8/8/8/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapes(knightAndRookChecking);
        Set<String> expectedMoves = toSet("Kd5-c5"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckFromBishopAndRook()
    {
        String bishopAndRookChecking = "8/3R4/8/2B5/3k4/8/4Q3/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapes(bishopAndRookChecking);
        Set<String> expectedMoves = toSet("Kd4xc5,Kd4-c3"); 
        assertMovesAreEqual(expectedMoves, actualMoves);
    }

    @Test
    public void testEscapeCheckmateFromBishopDiscoveredCheck()
    {
        String bishopAndRookChecking = "8/3R4/8/8/1B1k4/8/4Q3/K7 b - - 0 1";
        Set<String> actualMoves = generateKingEscapes(bishopAndRookChecking);
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

    private Set<String> generateMoves(String positionFen) {
        g.set(positionFen);
        int side = g.isWhiteToMove()?0:1;
        List<Integer> moves = DefaultGenerator.newMoveList();
        mg.generateCaptures(moves, side);
        mg.generateNonCaptures(moves, side);
        return toCoordinateMoveSet(moves);
    }

    private Set<String> generateCaptures(String positionFen) {
        g.set(positionFen);
        int side = g.isWhiteToMove()?0:1;
        List<Integer> moves = DefaultGenerator.newMoveList();
        mg.generateCaptures(moves, side);
        return toCoordinateMoveSet(moves);
    }
    
    private Set<String> generateNonCaptures(String positionFen) {
        g.set(positionFen);
        int side = g.isWhiteToMove()?0:1;
        List<Integer> moves = DefaultGenerator.newMoveList();
        mg.generateNonCaptures(moves, side);
        return toCoordinateMoveSet(moves);
    }
    
    private Set<String> generateKingEscapes(String positionFen) {
        g.set(positionFen);
        int side = g.isWhiteToMove()?0:1;
        List<Integer> moves = DefaultGenerator.newMoveList();
        mg.generateKingEscapes(moves, side);
        return toCoordinateMoveSet(moves);
    }

    private void displayBoardAndSideToMove() {
        System.out.println(new Displayer().formatBoard(g.getPosition()));
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
