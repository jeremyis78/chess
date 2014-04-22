package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class GameStateTest {

	private static final int NONE = 0;
	private GameState gameState;

	@Before
	public void setup()
	{
		gameState = new GameState();
	}
	
	@Test
	public void testConstructor()
	{
		String expectedState = "8/8/8/8/8/8/8/8 w KQkq - 0 0";
		String actualState = gameState.get();
		assertEquals(expectedState, actualState);
	}
	
	@Test
	public void testMakeAndUndoWhitePieceMoves() {
		String beforeMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		int moveE4 = encodeMove(E2, E4, PIECE[PAWN]);
		String afterMove = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, moveE4));
		assertEquals(beforeMove, undoMove(sideToMove, moveE4));
	}

	@Test
	public void testMakeAndUndoBlackPieceCaptures() {
		String beforeMove = "rnbqkb1r/pppppppp/5n2/8/4P3/5N2/PPPPPPPP/RNBQKB1R b KQkq - 1 2";
		int moveKnightTakeE4 = encodeMove(F6, E4, PIECE[KNIGHT], PIECE[PAWN]);
		String afterMove = "rnbqkb1r/pppppppp/8/8/4n3/5N2/PPPPPPPP/RNBQKB1R w KQkq - 0 3";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, moveKnightTakeE4));
		assertEquals(beforeMove, undoMove(sideToMove, moveKnightTakeE4));
	}

	@Test
	public void testMakeAndUndoWhitePawnCapturesEnPassant() {
		String beforeMove = "4k3/8/8/3pP3/8/8/8/4K3 w - d6 2 23";
		int movePawnOnE5CapturesOnD6 = encodeMove(E5, D6, PIECE[PAWN], PIECE[PAWN]);
		String afterMove = "4k3/8/3P4/8/8/8/8/4K3 b - - 0 23";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, movePawnOnE5CapturesOnD6));
		assertEquals(beforeMove, undoMove(sideToMove, movePawnOnE5CapturesOnD6));
	}

	@Test
	public void testMakeAndUndoBlackPawnCapturesEnPassant() {
		String beforeMove = "4k3/8/8/8/4pP2/8/8/4K3 b - f3 0 23";
		int pawnOnE4CapturesOnF3 = encodeMove(E4, F3, PIECE[PAWN], PIECE[PAWN]);
		String afterMove = "4k3/8/8/8/8/5p2/8/4K3 w - - 0 24";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, pawnOnE4CapturesOnF3));
		assertEquals(beforeMove, undoMove(sideToMove, pawnOnE4CapturesOnF3));
	}
	
	@Test
	public void testMakeAndUndoWhitePawnPromotes() {
		String beforeMove = "8/4P3/8/8/8/8/8/k6K w - - 5 30";
		int movePawnPromotesOnE8 = encodeMove(E7, E8, PIECE[PAWN], NONE, PIECE[QUEEN]);
		String afterMove = "4Q3/8/8/8/8/8/8/k6K b - - 0 30";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, movePawnPromotesOnE8));
		assertEquals(beforeMove, undoMove(sideToMove, movePawnPromotesOnE8));
	}

	@Test
	public void testMakeAndUndoBlackPawnPromotes() {
		String beforeMove = "k6K/8/8/8/8/8/7p/8 b - - 2 30";
		int pawnPromotesOnH1 = encodeMove(H2, H1, PIECE[PAWN], NONE, PIECE[ROOK]);
		String afterMove = "k6K/8/8/8/8/8/8/7r w - - 0 31";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, pawnPromotesOnH1));
		assertEquals(beforeMove, undoMove(sideToMove, pawnPromotesOnH1));
	}

	@Test
	public void testMakeAndUndoWhitePawnCapturesAndPromotes() {
		String beforeMove = "3r4/4P3/8/8/8/8/8/k6K w - - 5 30";
		int movePawnCapturesAndPromotesOnD8 = encodeMove(E7, D8, PIECE[PAWN], PIECE[ROOK], PIECE[KNIGHT]);
		String afterMove = "3N4/8/8/8/8/8/8/k6K b - - 0 30";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, movePawnCapturesAndPromotesOnD8));
		assertEquals("captured piece must be put back", 
				beforeMove, undoMove(sideToMove, movePawnCapturesAndPromotesOnD8));
	}

	@Test
	public void testMakeAndUndoBlackPawnCapturesAndPromotes() {
		String beforeMove = "kK6/8/8/8/8/8/p7/1Q6 b - - 5 30";
		int pawnCapturesAndPromotesOnB1 = encodeMove(A2, B1, PIECE[PAWN], PIECE[QUEEN], PIECE[BISHOP]);
		String afterMove = "kK6/8/8/8/8/8/8/1b6 w - - 0 31";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, pawnCapturesAndPromotesOnB1));
		assertEquals("captured piece must be put back", 
				beforeMove, undoMove(sideToMove, pawnCapturesAndPromotesOnB1));
	}

	@Test
	public void testMakeAndUndoWhiteShortCastles() {
		String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
		int move = encodeMove(E1, G1, PIECE[KING]);
		String afterMove = "r3k2r/8/8/8/8/8/8/R4RK1 b kq - 1 1";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, move));
		assertEquals(beforeMove, undoMove(sideToMove, move));
	}

	@Test
	public void testMakeAndUndoBlackShortCastles() {
		String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
		int move = encodeMove(E8, G8, PIECE[KING]);
		String afterMove = "r4rk1/8/8/8/8/8/8/R3K2R w KQ - 1 2";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, move));
		assertEquals(beforeMove, undoMove(sideToMove, move));
	}

	@Test
	public void testMakeAndUndoWhiteLosesCastlePrivilegeWhenKingMoves() {
		String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
		int move = encodeMove(E1, F1, PIECE[KING]);
		String afterMove = "r3k2r/8/8/8/8/8/8/R4K1R b kq - 1 1";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, move));
		assertEquals(beforeMove, undoMove(sideToMove, move));
	}

	@Test
	public void testMakeAndUndoBlackLosesCastlePrivilegeWhenKingMoves() {
		String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
		int move = encodeMove(E8, D8, PIECE[KING]);
		String afterMove = "r2k3r/8/8/8/8/8/8/R3K2R w KQ - 1 2";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, move));
		assertEquals(beforeMove, undoMove(sideToMove, move));
	}

	@Test
	public void testMakeAndUndoWhiteLosesShortCastlePrivilegeWhenRookMoves() {
		String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
		int move = encodeMove(H1, G1, PIECE[ROOK]);
		String afterMove = "r3k2r/8/8/8/8/8/8/R3K1R1 b Qkq - 1 1";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, move));
		assertEquals(beforeMove, undoMove(sideToMove, move));
	}

	@Test
	public void testMakeAndUndoBlackLosesShortCastlePrivilegeWhenRookMoves() {
		String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
		int move = encodeMove(H8, F8, PIECE[ROOK]);
		String afterMove = "r3kr2/8/8/8/8/8/8/R3K2R w KQq - 1 2";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, move));
		assertEquals(beforeMove, undoMove(sideToMove, move));
	}

	@Test
	public void testMakeAndUndoWhiteLongCastles() {
		String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
		int move = encodeMove(E1, C1, PIECE[KING]);
		String afterMove = "r3k2r/8/8/8/8/8/8/2KR3R b kq - 1 1";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, move));
		assertEquals(beforeMove, undoMove(sideToMove, move));
	}

	@Test
	public void testMakeAndUndoWhiteLosesLongCastlePrivilegeWhenRookMoves() {
		String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
		int move = encodeMove(A1, A2, PIECE[ROOK]);
		String afterMove = "r3k2r/8/8/8/8/8/R7/4K2R b Kkq - 1 1";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, move));
		assertEquals(beforeMove, undoMove(sideToMove, move));
	}

	@Test
	public void testMakeAndUndoBlackLongCastles() {
		String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
		int move = encodeMove(E8, C8, PIECE[KING]);
		String afterMove = "2kr3r/8/8/8/8/8/8/R3K2R w KQ - 1 2";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, move));
		assertEquals(beforeMove, undoMove(sideToMove, move));
	}

	@Test
	public void testMakeAndUndoBlackLosesLongCastlePrivilegeWhenRookMoves() {
		String beforeMove = "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1";
		int move = encodeMove(A8, A1, PIECE[ROOK], PIECE[ROOK]);
		String afterMove = "4k2r/8/8/8/8/8/8/r3K2R w KQk - 0 2";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, move));
		assertEquals(beforeMove, undoMove(sideToMove, move));
	}

	private int encodeMove(int from, int to, int piece) {
		return MoveGenerator.EncodeMove(from, to, piece, NONE, NONE);
	}

	private int encodeMove(int from, int to, int piece, int capturedPiece) {
		return MoveGenerator.EncodeMove(from, to, piece, capturedPiece, NONE);
	}

	private int encodeMove(int from, int to, int piece, int capturedPiece, int promotionPiece) {
		return MoveGenerator.EncodeMove(from, to, piece, capturedPiece, promotionPiece);
	}

	private int setupState(String startState) {
		String position = startState;
		gameState.set(startState);
		String initialState = gameState.get();
		assertEquals(position, initialState);
		return gameState.sideToMove;
	}
	
	private String makeMove(int sideToMove, int move) {
		gameState.makeMove(move, sideToMove);
		String stateAfterMove = gameState.get();
		return stateAfterMove;
	}

	private String undoMove(int sideToMove, int move) {
		gameState.undoMove(move, sideToMove);
		String stateAfterUndo = gameState.get();
		return stateAfterUndo;
	}

}
