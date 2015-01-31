package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static com.jeremybrooks.chess.base.Piece.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.util.FenBuilder;
import com.jeremybrooks.chess.util.Util;

public class GameStateTest {

    private static final int NONE = 0;
    private GameState gameState;

    @Before
    public void setup()
    {
        gameState = new GameState(GameState.MAX_NUM_MOVES_MADE);
    }
    
    @Test
    public void testConstructorWhiteToMove()
    {
        String expectedState = "8/8/8/8/8/8/8/8 w KQkq - 0 1";
        String actualState = gameState.get();
        assertEquals(expectedState, actualState);
        assertWhiteToMove();
    }

    @Test
    public void testConstructorBlackToMove()
    {
        String expectedState = "k7/8/8/8/8/8/8/7K b - - 0 1";
        gameState.set(expectedState);
        String actualState = gameState.get();
        assertEquals(expectedState, actualState);
        assertBlackToMove();
    }

    @Test
    public void testConstructionWithTwoMoveLimit()
    {
        int maxNumberOfMovesToSupport = 2;
        gameState = new GameState(maxNumberOfMovesToSupport);
        String beforeMove = "8/4P3/8/8/8/8/4P3/k6K w - - 5 30";
        int firstMove = encodeMove(E7, E8, ENCODED[PAWN], NONE, ENCODED[QUEEN]);
        String afterFirstMove = "4Q3/8/8/8/8/8/4P3/k6K b - - 0 30";
        boolean isWhitesMove = setupState(beforeMove);
        assertWhiteToMove();
        assertEquals(afterFirstMove, makeMove(isWhitesMove, firstMove));
        assertEquals(1, gameState.getNumberOfMovesMade());
        assertEquals(1, gameState.currentLine().size());
        assertBlackToMove();
        
        int secondMove  = encodeMove(A1, A2, ENCODED[KING]);
        String afterSecondMove = "4Q3/8/8/8/8/8/k3P3/7K w - - 1 31";
        assertEquals(afterSecondMove , makeMove(!isWhitesMove, secondMove));
        assertEquals(maxNumberOfMovesToSupport, gameState.getNumberOfMovesMade());
        assertEquals(afterFirstMove, undoMove(!isWhitesMove, secondMove));
        assertEquals(1, gameState.getNumberOfMovesMade());
        assertEquals(afterSecondMove , makeMove(!isWhitesMove, secondMove));
        
        int thirdMove  = encodeMove(E2, E3, ENCODED[PAWN]);
        try {
            makeMove(isWhitesMove, thirdMove);
        } catch (IllegalStateException e) {
            assertEquals("max number of moves have been made: 2", e.getMessage());
        }
        undoMove(!isWhitesMove, secondMove);
        undoMove(isWhitesMove, firstMove);
        assertEquals(0, gameState.getNumberOfMovesMade());
        try {
            undoMove(isWhitesMove, thirdMove);
        } catch (IllegalStateException e) {
            assertEquals("no moves to undo; call makeMove() first", e.getMessage());
        }
        assertEquals(0, gameState.getNumberOfMovesMade());
    }

    @Test
    public void testUndoMoveWithNoMovesToUndo()
    {
        int move  = encodeMove(E2, E3, ENCODED[PAWN]);
        boolean isWhitesMove = setupState("8/4P3/8/8/8/8/4P3/k6K w - - 5 30");
        try {
            undoMove(isWhitesMove, move);
        } catch (IllegalStateException e) {
            assertEquals("no moves to undo; call makeMove() first", e.getMessage());
        }
    }

    @Test
    public void testConstructorWithNotEnoughFields()
    {
        String invalidFEN = "k6K/8/8/8/8/8/8/8 w KQkq";;
        String errorMessage = "The FEN string 'k6K/8/8/8/8/8/8/8 w KQkq' "
                + "needs six space-delimited fields: "
                + "board onMove castlingFlags enPassantSquare halfMoveClock moveNumber";
        assertConstructionFailsWith(errorMessage, invalidFEN);
    }

    @Test
    public void testConstructorWithWhiteToMoveEnPassantSquareIsInvalid()
    {
        String invalidFEN = "k6K/8/8/8/8/8/8/8 w - e3 0 1";;
        String errorMessage = "Given 'w' to move, the en passant square 'e3' "
                + "ought to be on the 6th rank";
        assertConstructionFailsWith(errorMessage, invalidFEN);
    }

    @Test
    public void testConstructorWithBlackToMoveEnPassantSquareIsInvalid()
    {
        String invalidFEN = "k6K/8/8/8/8/8/8/8 b - h6 0 1";;
        String errorMessage = "Given 'b' to move, the en passant square 'h6' "
                + "ought to be on the 3rd rank";
        assertConstructionFailsWith(errorMessage, invalidFEN);
    }

    @Test
    public void testConstructorWithNegativeHalfMoveClock()
    {
        String invalidFEN = "k6K/8/8/8/8/8/8/8 b - - -1 2";;
        String errorMessage = "Half move clock '-1' must be zero or greater";
        assertConstructionFailsWith(errorMessage, invalidFEN);
    }

    @Test
    public void testConstructorWithZeroMoveNumber()
    {
        String invalidFEN = "k6K/8/8/8/8/8/8/8 b - - 1 0";;
        String errorMessage = "Move number '0' must be greater than zero";
        assertConstructionFailsWith(errorMessage, invalidFEN);
    }

    private void assertConstructionFailsWith(String errorMessage,
            String invalidFEN) {
        try {
            gameState.set(invalidFEN);
        } catch (IllegalArgumentException e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }

    @Test
    public void testMakeAndUndoWhitePieceMoves() {
        String beforeMove = GameState.FEN_START;
        int moveE4 = encodeMove(E2, E4, ENCODED[PAWN]);
        String afterMove = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, moveE4));
        assertEquals(1, gameState.getNumberOfMovesMade());
        assertEquals(1, gameState.getMoveNumber());
        assertEquals(beforeMove, undoMove(isWhitesMove, moveE4));
        assertEquals(0, gameState.getNumberOfMovesMade());
        assertEquals(1, gameState.getMoveNumber());
    }

    @Test
    public void testMakeAndUndoBlackPieceCaptures() {
        String beforeMove = "rnbqkb1r/pppppppp/5n2/8/4P3/5N2/PPPPPPPP/RNBQKB1R b KQkq - 1 2";
        int moveKnightTakeE4 = encodeMove(F6, E4, ENCODED[KNIGHT], ENCODED[PAWN]);
        String afterMove = "rnbqkb1r/pppppppp/8/8/4n3/5N2/PPPPPPPP/RNBQKB1R w KQkq - 0 3";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, moveKnightTakeE4));
        assertEquals(beforeMove, undoMove(isWhitesMove, moveKnightTakeE4));
    }

    @Test
    public void testMakeAndUndoWhitePawnCapturesNoEnPassant() {
        String beforeMove = "4k3/8/4p3/3P4/8/8/8/4K3 w - d6 2 23";
        int move = encodeMove(D5, E6, ENCODED[PAWN], ENCODED[PAWN]);
        String afterMove = "4k3/8/4P3/8/8/8/8/4K3 b - - 0 23";
        boolean isWhitesMove = setupState(beforeMove);
        assertTrue(gameState.hasEnPassantOption());
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
        assertTrue(gameState.hasEnPassantOption());
    }

    /*
     * Test to ensure en-passant square is properly reset
     * Found bug when searching for mate in 5 with the position below;
     * Extracted out the moves to duplicate the failure
     */
    @Test
    public void testMakeAndUndoBlackPawnAdvanceTwoUndoesEnpassantSquare() {
        String mateIn5 = "1rqn3k/6pp/1pn5/4P3/1P2bNP1/1Q2N3/4BR1P/6K1 w - - 0 1";
        boolean isWhitesMove = setupState(mateIn5);
        int whiteMove = encodeMove(H2, H4, ENCODED[PAWN]);
        int blackG5 = encodeMove(G7, G5, ENCODED[PAWN]);
        int blackH5 = encodeMove(H7, H5, ENCODED[PAWN]);
        int blackB5 = encodeMove(B6, B5, ENCODED[PAWN]);

        makeMove(isWhitesMove, whiteMove);
        assertEquals(H3, gameState.getEnPassantSquare());
        assertEquals("Ph2-h4", movesMadeInFAN());
        assertBlackToMove();
        
        makeMove(!isWhitesMove, blackG5);
        assertEquals(G6, gameState.getEnPassantSquare());
        assertEquals("Ph2-h4 Pg7-g5", movesMadeInFAN());
        assertWhiteToMove();
        undoMove(!isWhitesMove, blackG5);
        assertEquals(H3, gameState.getEnPassantSquare());
        assertEquals("Ph2-h4", movesMadeInFAN());
        assertBlackToMove();
        
        makeMove(!isWhitesMove, blackH5);
        assertEquals(H6, gameState.getEnPassantSquare());
        assertEquals("Ph2-h4 Ph7-h5", movesMadeInFAN());
        assertWhiteToMove();
        undoMove(!isWhitesMove, blackH5);
        assertEquals(H3, gameState.getEnPassantSquare());
        assertEquals("Ph2-h4", movesMadeInFAN());
        assertBlackToMove();

        makeMove(!isWhitesMove, blackB5);
        assertEquals(NOSQUARE, gameState.getEnPassantSquare());
        assertEquals("Ph2-h4 Pb6-b5", movesMadeInFAN());
        assertWhiteToMove();
        undoMove(!isWhitesMove, blackB5);
        assertEquals(H3, gameState.getEnPassantSquare());
        assertEquals("Ph2-h4", movesMadeInFAN());
        assertBlackToMove();
        
        undoMove(isWhitesMove, whiteMove);
        assertEquals("", movesMadeInFAN());
        assertWhiteToMove();
    }

	private void assertWhiteToMove() {
		assertEquals(true, gameState.isWhiteToMove());
	}

	private void assertBlackToMove() {
		assertEquals(false, gameState.isWhiteToMove());
	}

	private String movesMadeInFAN() {
		StringBuilder line = new StringBuilder();
		for(int move: gameState.currentLine())
		{
			line.append(Util.displayMoveStr(move, false, false)).append(" ");
		}
		if(line.length()>0) line.deleteCharAt(line.length()-1);
		return line.toString();
	}
    
    @Test
    public void testMakeAndUndoWhitePawnCapturesEnPassant() {
        String beforeMove = "4k3/8/8/3pP3/8/8/8/4K3 w - d6 2 23";
        int movePawnOnE5CapturesOnD6 = encodeMove(E5, D6, ENCODED[PAWN], ENCODED[PAWN]);
        String afterMove = "4k3/8/3P4/8/8/8/8/4K3 b - - 0 23";
        boolean isWhitesMove = setupState(beforeMove);
        assertTrue(gameState.hasEnPassantOption());
        assertEquals(D6, gameState.getEnPassantSquare());
        assertEquals(afterMove, makeMove(isWhitesMove, movePawnOnE5CapturesOnD6));
        assertFalse(gameState.hasEnPassantOption());
        assertEquals(beforeMove, undoMove(isWhitesMove, movePawnOnE5CapturesOnD6));
    }

    @Test
    public void testMakeAndUndoBlackPawnCapturesEnPassant() {
        String beforeMove = "4k3/8/8/8/4pP2/8/8/4K3 b - f3 0 23";
        int pawnOnE4CapturesOnF3 = encodeMove(E4, F3, ENCODED[PAWN], ENCODED[PAWN]);
        String afterMove = "4k3/8/8/8/8/5p2/8/4K3 w - - 0 24";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, pawnOnE4CapturesOnF3));
        assertEquals(beforeMove, undoMove(isWhitesMove, pawnOnE4CapturesOnF3));
    }
    
    @Test
    public void testMakeAndUndoWhitePawnPromotes() {
        String beforeMove = "8/4P3/8/8/8/8/8/k6K w - - 5 30";
        int movePawnPromotesOnE8 = encodeMove(E7, E8, ENCODED[PAWN], NONE, ENCODED[QUEEN]);
        String afterMove = "4Q3/8/8/8/8/8/8/k6K b - - 0 30";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, movePawnPromotesOnE8));
        assertEquals(beforeMove, undoMove(isWhitesMove, movePawnPromotesOnE8));
    }

    @Test
    public void testMakeAndUndoBlackPawnPromotes() {
        String beforeMove = "k6K/8/8/8/8/8/7p/8 b - - 2 30";
        int pawnPromotesOnH1 = encodeMove(H2, H1, ENCODED[PAWN], NONE, ENCODED[ROOK]);
        String afterMove = "k6K/8/8/8/8/8/8/7r w - - 0 31";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, pawnPromotesOnH1));
        assertEquals(beforeMove, undoMove(isWhitesMove, pawnPromotesOnH1));
    }

    @Test
    public void testMakeAndUndoWhitePawnCapturesAndPromotes() {
        String beforeMove = "3r4/4P3/8/8/8/8/8/k6K w - - 5 30";
        int movePawnCapturesAndPromotesOnD8 = encodeMove(E7, D8, ENCODED[PAWN], ENCODED[ROOK], ENCODED[KNIGHT]);
        String afterMove = "3N4/8/8/8/8/8/8/k6K b - - 0 30";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, movePawnCapturesAndPromotesOnD8));
        assertEquals("captured piece must be put back", 
                beforeMove, undoMove(isWhitesMove, movePawnCapturesAndPromotesOnD8));
    }

    @Test
    public void testMakeAndUndoBlackPawnCapturesAndPromotes() {
        String beforeMove = "kK6/8/8/8/8/8/p7/1Q6 b - - 5 30";
        int pawnCapturesAndPromotesOnB1 = encodeMove(A2, B1, ENCODED[PAWN], ENCODED[QUEEN], ENCODED[BISHOP]);
        String afterMove = "kK6/8/8/8/8/8/8/1b6 w - - 0 31";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, pawnCapturesAndPromotesOnB1));
        assertEquals("captured piece must be put back", 
                beforeMove, undoMove(isWhitesMove, pawnCapturesAndPromotesOnB1));
    }

    @Test
    public void testMakeAndUndoWhiteShortCastles() {
        String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        int move = encodeMove(E1, G1, ENCODED[KING]);
        String afterMove = "r3k2r/8/8/8/8/8/8/R4RK1 b kq - 1 1";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
    }

    @Test
    public void testMakeAndUndoBlackShortCastles() {
        String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
        int move = encodeMove(E8, G8, ENCODED[KING]);
        String afterMove = "r4rk1/8/8/8/8/8/8/R3K2R w KQ - 1 2";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
    }

    @Test
    public void testMakeAndUndoWhiteLosesCastlePrivilegeWhenKingMoves() {
        String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        int move = encodeMove(E1, F1, ENCODED[KING]);
        String afterMove = "r3k2r/8/8/8/8/8/8/R4K1R b kq - 1 1";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
    }

    @Test
    public void testMakeAndUndoBlackLosesCastlePrivilegeWhenKingMoves() {
        String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
        int move = encodeMove(E8, D8, ENCODED[KING]);
        String afterMove = "r2k3r/8/8/8/8/8/8/R3K2R w KQ - 1 2";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
    }

    @Test
    public void testMakeAndUndoWhiteLosesShortCastlePrivilegeWhenRookMoves() {
        String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        int move = encodeMove(H1, G1, ENCODED[ROOK]);
        String afterMove = "r3k2r/8/8/8/8/8/8/R3K1R1 b Qkq - 1 1";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
    }

    @Test
    public void testMakeAndUndoBlackLosesShortCastlePrivilegeWhenRookMoves() {
        String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
        int move = encodeMove(H8, F8, ENCODED[ROOK]);
        String afterMove = "r3kr2/8/8/8/8/8/8/R3K2R w KQq - 1 2";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
    }

    @Test
    public void testMakeAndUndoWhiteLongCastles() {
        String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        int move = encodeMove(E1, C1, ENCODED[KING]);
        String afterMove = "r3k2r/8/8/8/8/8/8/2KR3R b kq - 1 1";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
    }

    @Test
    public void testMakeAndUndoWhiteLosesLongCastlePrivilegeWhenRookMoves() {
        String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        int move = encodeMove(A1, A2, ENCODED[ROOK]);
        String afterMove = "r3k2r/8/8/8/8/8/R7/4K2R b Kkq - 1 1";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
    }

    @Test
    public void testMakeAndUndoBlackLongCastles() {
        String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
        int move = encodeMove(E8, C8, ENCODED[KING]);
        String afterMove = "2kr3r/8/8/8/8/8/8/R3K2R w KQ - 1 2";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
    }

    @Test
    public void testMakeAndUndoBlackLosesLongCastlePrivilegeWhenRookMoves() {
        String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
        int move = encodeMove(A8, A1, ENCODED[ROOK], ENCODED[ROOK]);
        String afterMove = "4k2r/8/8/8/8/8/8/r3K2R w KQk - 0 2";
        boolean isWhitesMove = setupState(beforeMove);
        assertEquals(afterMove, makeMove(isWhitesMove, move));
        assertEquals("Ra8xa1", movesMadeInFAN());
        assertEquals(beforeMove, undoMove(isWhitesMove, move));
        assertTrue(gameState.currentLine().isEmpty());
    }
     
    @Test
    public void testZobristHash()
    {
        
    }

    @Test
    public void testForA8AlreadyOccupiedBug()
    {   // Test attempts to verify a bug found when doing a deep search.
        // When running the same series of moves however this test
        // fails to duplicate the issue.  There may have been some
        // corrupted GameState prior to actually executing this series of
        // moves and that makes this hard to test/fix.
        /*
         * bug!!!!!
            When making 13...Qd8-a8 after 
             1. Pa2-a4 Pa7-a5 
             2. Pb2-b4 Pa5xb4
             3. Pc2-c4 Pb4xc3
             4. Pd2xc3 Ra8xa4  <-- maybe the rook was never recognized as moving from a8?? 
             5. Ra1xa4 Pb7-b5
             6. Bf1xb5 Pc7-c5
             7. Bb5xd7 Nb8xd7
             8. Qd1xd7 Bc8xd7
             9. Pf2-f4 Pe5xf4
            10. Bc1xf4 Bd7xa4
            11. Pg2-g4 Pf7-f5
            12. Pe4xf5 Pg7-g5
            13. Pf5xg6   
            We get IllegalStateException from Position.placePiece() "a8 is already occupied"
            final fen: r3kbnr/7p/6P1/2p5/b4BP1/2P5/7P/1N2K1NR  (rook is still on a8?????)
         */
        String beforeMove = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1";
        //when making 32315 after 
        boolean isWhiteToMove = setupState(beforeMove);
        assertTrue("white to move", isWhiteToMove);
        int[] m = new int[25]; //contains the moves listed above
        m[0]=5640; m[1]=6192; m[2]=5705; m[3]=38496; m[4]=5770; m[5]=38041; m[6]=38027; m[7]=58936; m[8]=222720; m[9]=6257; m[10]=55365; m[11]=6322; m[12]=56545; m[13]=175353; m[14]=97475; m[15]=253178; m[16]=5965; m[17]=38756; m[18]=55106; m[19]=218675; m[20]=6030; m[21]=6517; m[22]=39260; m[23]=6582; m[24]=39845;  // got a8 is already occupied
        int i=0;
        while (i<24) { //right before 13...Qd8-a8
            makeUndoMakeMove(i%2==0?true:false, m[i]);
            i++;
        }
        FenBuilder fb = new FenBuilder();
//        Displayer d = new Displayer();
//        System.out.println(d.formatBoard(gameState.getPosition()));
        fb.appendPieceBoard(gameState.getPosition());
//        System.out.println(fb.toString());
        //Create 13...Qd8-a8
        int move = 0;
        move = Bitmap.D8;
        move |= (Bitmap.A8 << 6);
        move |= (ENCODED[QUEEN] << 12);
        makeUndoMakeMove(i%2==0?true:false, move);  //doesn't throw, ugggh.
    }
    
    public void testA8AlreadyOccupiedBug()
    {
    	//r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1
        String before = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1";
        int move1 = encodeMove(D2, D4, ENCODED[PAWN]);
        int move2 = encodeMove(A8, A7, ENCODED[ROOK], ENCODED[PAWN]);

        String expectedAfter = "4k2r/Rppp1ppp/1b3nbN/nP6/BBPPP3/q4N2/Pp4PP/R2Q1RK1 w kq - 0 1";
        boolean isWhitesMove = setupState(before);
        makeMove(isWhitesMove, move1);
        String afterMove = makeMove(!isWhitesMove, move2);
        assertEquals(expectedAfter, afterMove);
        String afterUndoRookMove = undoMove(!isWhitesMove, move2);
        String expectedAfterUndo = "r3k2r/1ppp1ppp/1b3nbN/nP6/BBPPP3/q4N2/Pp4PP/R2Q1RK1 w kq - 0 1";
        assertEquals(expectedAfterUndo, afterUndoRookMove);
    }

    private void makeUndoMakeMove(boolean isWhiteToMove, int move) {
        makeMove(isWhiteToMove, move);
        undoMove(isWhiteToMove, move);
        makeMove(isWhiteToMove, move);
    }

    private boolean setupState(String startState) {
        String position = startState;
        gameState.set(startState);
        String initialState = gameState.get();
        assertEquals(position, initialState);
        return gameState.isWhiteToMove();
    }
    
    private String makeMove(boolean isWhitesMove, int move) {
        gameState.makeMove(move);
        String stateAfterMove = gameState.get();
        return stateAfterMove;
    }

    private String undoMove(boolean isWhitesMove, int move) {
        gameState.undoMove();
        String stateAfterUndo = gameState.get();
        return stateAfterUndo;
    }

    private int encodeMove(int from, int to, int piece) {
        return Util.EncodeMove(from, to, piece, NONE, NONE);
    }
    
    private int encodeMove(int from, int to, int piece, int capturedPiece) {
        return Util.EncodeMove(from, to, piece, capturedPiece, NONE);
    }
    
    private int encodeMove(int from, int to, int piece, int capturedPiece, int promotionPiece) {
        return Util.EncodeMove(from, to, piece, capturedPiece, promotionPiece);
    }
}
