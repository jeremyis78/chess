package com.jeremybrooks.chess.eval;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.util.Util;

public class EvaluatorTest {

    private static int queen() { return Evaluator.PIECE_VALUE[Piece.QUEEN ]; }
    private static int rook()  { return Evaluator.PIECE_VALUE[Piece.ROOK  ]; }
    private static int bishop(){ return Evaluator.PIECE_VALUE[Piece.BISHOP]; }
    private static int knight(){ return Evaluator.PIECE_VALUE[Piece.KNIGHT]; }
    private static int pawn()  { return Evaluator.PIECE_VALUE[Piece.PAWN  ]; }

    private Evaluator eval;
    private GameState gameState;
    
    @Before
    public void setUp()
    {
        eval = getEvaluator();
        gameState = new GameState(2);
        
    }

    @Test
    public void givenStartPositionEvaluation() {
        String initialPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        gameState.set(initialPosition);
        int scoreAtStartOfGame = eval.evaluate(gameState, Piece.WHITE, 0, false, true);
        assertFalse("white should have no advantage", scoreAtStartOfGame > 0);
        assertFalse("black should have no advantage", scoreAtStartOfGame < 0);
        assertEquals(0, scoreAtStartOfGame);
    }
    
    @Test
    public void givenWhiteMatesBlack()
    {
        String queenRookMate = "k6Q/5R2/8/8/8/8/8/6K1 b - - 0 1";
        boolean isWhiteToMove = Util.setupState(gameState, queenRookMate);
        int searchDepth = 0;
        int materialAdvantage = 1475 + (13 * 5); //white is up a queen and a rook (plus rook bonus for no pawns)
//        int whiteMatesBlackScore = Evaluator.CHECKMATE - searchDepth + materialAdvantage;
        try {
            int actualScore = evaluate(gameState, isWhiteToMove, searchDepth);
            fail("should not allow eval'ing a check or mate situation");
        } catch (IllegalStateException e) {
            //pass
        }
//        assertEquals(whiteMatesBlackScore, actualScore);
//        assertTrue("white mates black should be a negative score", actualScore > 0);
    }
    
    @Test
    public void givenBlackMatesWhite()
    {
        String backRankMate = "k7/8/8/8/8/8/5PPP/q5K1 w - - 0 1";
        boolean isWhiteToMove = Util.setupState(gameState, backRankMate);
        int searchDepth = 0;
        try {
            evaluate(gameState, isWhiteToMove, searchDepth);
            fail("should not allow eval'ing a check or mate situation");
        } catch (IllegalStateException e) {
            //pass
        }
    }

    @Test
    public void givenCheckWithFlightSquares()
    {
        String fen = "8/8/8/8/5K2/pP6/8/Q4k2 b - - 0 1"; //     #Polgar #307"
        boolean isWhiteToMove = Util.setupState(gameState, fen);
        int searchDepth = 0;
        try {
            evaluate(gameState, isWhiteToMove, searchDepth);
            fail("should not allow eval'ing a check or mate situation");
        } catch (IllegalStateException e) {
            //pass
        }
    }

    @Test
    public void givenBishopPair()
    {
        String whiteHasBishopPair = "7k/8/8/8/8/8/8/BB5K w - - 0 1";
        boolean isWhiteToMove = Util.setupState(gameState, whiteHasBishopPair);
        int searchDepth = 0;
        int materialAdvantage = (2 * bishop()) + 50; //bishop pair + bonus
        int expectedScore = materialAdvantage;
        int actualScore = evaluate(gameState, isWhiteToMove, searchDepth);
        assertEquals("white has advantage with bishop pair", expectedScore, actualScore);
    }

    @Test
    public void givenTwoBlackKnightsEightPawns() {
        String initialPosition = "1n2k1n1/pppppppp/8/8/8/8/8/4K3 b kq - 0 1";
        gameState.set(initialPosition);
        int score = eval.evaluate(gameState, Piece.BLACK, 0, false, false);
        int startingScore = (knight() * 2) + (pawn() * 8);
        int knightAdjustmentGivenEightPawns = (6 * 3) * 2;
        int startingBlackScore = -1 * (startingScore + knightAdjustmentGivenEightPawns);
        assertEquals(startingBlackScore, score);
    }

    @Test
    public void givenOneBlackKnightFourPawns() {
        String initialPosition = "1n2k3/pppp4/8/8/8/8/8/4K3 b - - 0 1";
        gameState.set(initialPosition);
        int score = eval.evaluate(gameState, Piece.BLACK, 0, false, false);
        int knightFourPawnsScore = knight() + (pawn() * 4);
        int knightAdjustmentGivenFourPawns = -6;
        int startingBlackScore = -1 * (knightFourPawnsScore + knightAdjustmentGivenFourPawns);
        assertEquals(startingBlackScore, score);
    }

    @Test
    public void givenTwoWhiteKnightEightPawns() {
        String initialPosition = "4k3/8/8/8/8/8/PPPPPPPP/1N2K1N1 w - - 0 1";
        gameState.set(initialPosition);
        int score = eval.evaluate(gameState, Piece.WHITE, 0, false, false);
        int twoKnightsEightPawnsScore = (knight() * 2) + (pawn() * 8);
        int knightAdjustmentGivenAllPawns = 6 * 3 * 2; //bonus * 3 pawns * 2 knights
        int startingBlackScore = twoKnightsEightPawnsScore + knightAdjustmentGivenAllPawns;
        assertEquals(startingBlackScore, score);
    }
    
    @Test
    public void givenOneBlackKnightFivePawns() {
        //Boundary condition for knight adjustment is at 5 pawns
        String initialPosition = "1n2k3/ppppp3/8/8/8/8/8/4K3 b - - 0 1";
        gameState.set(initialPosition);
        int score = eval.evaluate(gameState, Piece.BLACK, 0, false, false);
        int knightFivePawnsScore = knight() + (pawn() * 5);
        int knightAdjustmentGivenFivePawns = 0;
        int expectedScore = -1 * (knightFivePawnsScore + knightAdjustmentGivenFivePawns);
        assertEquals(expectedScore, score);
    }
    
    @Test
    public void givenOneBlackRookFourPawns() {
        String initialPosition = "r3k3/pppp4/8/8/8/8/8/4K3 b - - 0 1";
        gameState.set(initialPosition);
        int score = eval.evaluate(gameState, Piece.BLACK, 0, false, false);
        int rookFourPawnsScore = rook() + (pawn() * 4);
        int rookAdjustmentGivenFourPawns = 13;
        int startingBlackScore = -1 * (rookFourPawnsScore + rookAdjustmentGivenFourPawns);
        assertEquals(startingBlackScore, score);
    }

    @Test
    public void givenTwoWhiteRooksSevenPawns() {
        String initialPosition = "4k3/8/8/8/8/8/1PPPPPPP/R3K2R w - - 0 1";
        gameState.set(initialPosition);
        int score = eval.evaluate(gameState, Piece.WHITE, 0, false, false);
        int twoRooksSevenPawnsScore = (rook() * 2) + (pawn() * 7);
        int rookAdjustmentGivenSevenPawns = 2 * (-13 * 2); //2 rooks * adjustment * pawns over 5
        int startingBlackScore = twoRooksSevenPawnsScore + rookAdjustmentGivenSevenPawns;
        assertEquals(startingBlackScore, score);
    }

    @Test
    public void givenWhiteRookFivePawns() {
        String initialPosition = "4k3/8/8/8/8/8/3PPPPP/R3K3 w - - 0 1";
        gameState.set(initialPosition);
        int score = eval.evaluate(gameState, Piece.WHITE, 0, false, false);
        int twoRooksSevenPawnsScore = (rook() * 1) + (pawn() * 5);
        int rookAdjustmentGivenSevenPawns = 0;
        int startingBlackScore = twoRooksSevenPawnsScore + rookAdjustmentGivenSevenPawns;
        assertEquals(startingBlackScore, score);
     }
    
    //
    // SEE tests
    //
    
    @Test
    public void givenSeeScoreForRookTakesEnPrisePawn()
    {
    	String rookTakesPawnWins = "1k1r4/8/8/4p3/8/8/8/2K1R3 w - - 0 1";
    	Util.setupState(gameState, rookTakesPawnWins);
    	int rookTakesPawn = Util.EncodeMove(Bitmap.E1, Bitmap.E5, Piece.ENCODED[Piece.ROOK], Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.NONE]);
    	int expectedScore = pawn();
    	int actualScore   = eval.scoreFromSEE(rookTakesPawn, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    @Test
    public void givenSeeScoreForPawnExchangeWhereKingRecaptures()
    {
    	String fen = "6k1/6p1/7P/8/8/8/8/2K5 w - - 0 1";
    	Util.setupState(gameState, fen);
    	int captureMove = Util.EncodeMove(Bitmap.H6, Bitmap.G7, Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.NONE]);
    	int expectedScore = pawn() - pawn();
    	int actualScore   = eval.scoreFromSEE(captureMove, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    @Test
    public void givenSeeScoreForBishopWinsKnightBecauseKingCannotRecapture()
    {
    	String fen = "6k1/6n1/5K1B/8/8/8/8/8 w - - 0 1";
    	Util.setupState(gameState, fen);
    	int captureMove = Util.EncodeMove(Bitmap.H6, Bitmap.G7, Piece.ENCODED[Piece.BISHOP], Piece.ENCODED[Piece.KNIGHT], Piece.ENCODED[Piece.NONE]);
    	int expectedScore = knight();
    	int actualScore   = eval.scoreFromSEE(captureMove, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    @Test
    public void givenSeeScoreForPawnForBishopExchange()
    {
    	String fen = "6k1/6b1/7P/8/8/8/8/2K5 w - - 0 1";
    	Util.setupState(gameState, fen);
    	int captureMove = Util.EncodeMove(Bitmap.H6, Bitmap.G7, Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.BISHOP], Piece.ENCODED[Piece.NONE]);
    	int expectedScore = bishop() - pawn();
    	int actualScore   = eval.scoreFromSEE(captureMove, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    @Test
   public void givenSeeScoreForPawnTakesRookAndPromotesAndLoses()
    {
    	String fen = "8/8/8/8/8/4k3/K4p2/R3R3 b - - 0 1";
    	Util.setupState(gameState, fen);
    	int captureMove = Util.EncodeMove(Bitmap.F2, Bitmap.E1, Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.ROOK], Piece.ENCODED[Piece.QUEEN]);
    	int expectedScore = rook() - queen();
    	int actualScore   = eval.scoreFromSEE(captureMove, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    @Test
    public void givenSeeScoreForPawnTakesRookAndPromotesAndWins()
    {
    	String fen = "8/8/8/8/8/4k3/K2b1p2/R3R3 b - - 0 1";
    	Util.setupState(gameState, fen);
    	int captureMove = Util.EncodeMove(Bitmap.F2, Bitmap.E1, Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.ROOK], Piece.ENCODED[Piece.QUEEN]);
    	int expectedScore = 2*rook() - queen();
    	int actualScore   = eval.scoreFromSEE(captureMove, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    @Test
    public void givenSeeScoreForBlackPawnCapturesEnPassantAndWins()
    {
    	String fen = "8/8/8/8/4pP2/8/8/k6K b - f3 0 1";
    	Util.setupState(gameState, fen);
    	int captureMove = Util.EncodeMove(Bitmap.E4, Bitmap.F3, Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.NONE]);
    	int expectedScore = pawn();
    	int actualScore   = eval.scoreFromSEE(captureMove, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    @Test
    public void givenSeeScoreForWhitePawnCapturesEnPassantNoGain()
    {
    	String fen = "8/3p4/8/2pP4/8/8/8/k6K w - c6 0 1";
    	Util.setupState(gameState, fen);
    	int captureMove = Util.EncodeMove(Bitmap.D5, Bitmap.C6, Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.NONE]);
    	int expectedScore = pawn() - pawn();
    	int actualScore   = eval.scoreFromSEE(captureMove, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    @Test
    public void givenSeeScoreForLosingCaptureWithMultipleXraysAttacks()
    {
    	String fen = "1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - - 0 1";
    	Util.setupState(gameState, fen);
    	int knightTakePawnAtE5 = Util.EncodeMove(Bitmap.D3, Bitmap.E5, Piece.ENCODED[Piece.KNIGHT], Piece.ENCODED[Piece.PAWN], Piece.ENCODED[Piece.NONE]);
    	int expectedScore = pawn() - knight(); //-1 * (Evaluator.PIECE_VALUE[Piece.KNIGHT] - Evaluator.PIECE_VALUE[Piece.PAWN]);
    	int actualScore   = eval.scoreFromSEE(knightTakePawnAtE5, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    @Test
    public void givenSeeScoreForStraightUpExchangeBlackKnightTakesBishop()
    {
    	String fen = "1k2r1q1/8/4r3/6P1/3n2Q1/5B2/4R3/2K5 b - - 0 1";
    	Util.setupState(gameState, fen);
    	int captureMove = Util.EncodeMove(Bitmap.D4, Bitmap.F3, Piece.ENCODED[Piece.KNIGHT], Piece.ENCODED[Piece.BISHOP], Piece.ENCODED[Piece.NONE]);
    	int expectedScore = knight() - bishop();
    	int actualScore   = eval.scoreFromSEE(captureMove, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    @Test
    public void givenSeeScoreForBlackKnightTakesRook()
    {
    	String fen = "1k2r1q1/8/4r3/6P1/3n2Q1/5B2/4R3/2K5 b - - 0 1";
    	Util.setupState(gameState, fen);
    	int captureMove = Util.EncodeMove(Bitmap.D4, Bitmap.E2, Piece.ENCODED[Piece.KNIGHT], Piece.ENCODED[Piece.ROOK], Piece.ENCODED[Piece.NONE]);
    	int expectedScore = rook();
    	int actualScore   = eval.scoreFromSEE(captureMove, gameState);
    	assertEquals(expectedScore, actualScore);
    }

    public static Evaluator getEvaluator()
    {
        Evaluator e = new Evaluator();
        return e;

    }
    
    private int evaluate(GameState g, boolean isWhiteToMove, int searchDepth) {
        return eval.evaluate(g, isWhiteToMove?0:1, searchDepth, false, false);
    }

}
