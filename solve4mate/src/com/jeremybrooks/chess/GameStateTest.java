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
		String beforeMove = "4k3/8/8/3pP3/8/8/8/4K3 w - d6 0 23";
		int movePawnOnE5CapturesOnD6 = encodeMove(E5, D6, PIECE[PAWN], PIECE[PAWN]);
		String afterMove = "4k3/8/3P4/8/8/8/8/4K3 b - - 0 23";
		int sideToMove = setupState(beforeMove);
		assertEquals(afterMove, makeMove(sideToMove, movePawnOnE5CapturesOnD6));
		assertEquals(beforeMove, undoMove(sideToMove, movePawnOnE5CapturesOnD6));
	}

	@Test @Ignore
	public void testMakeAndUndoWhitePawnPromotes() {
		fail("Not yet implemented");
	}
	
	@Test @Ignore
	public void testMakeAndUndoWhitePawnCapturesAndPromotes() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testMakeAndUndoWhiteShortCastles() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testMakeAndUndoWhiteLongCastles() {
		fail("Not yet implemented");
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

	private void assertMoveIsMadeAndUndone(int sideToMove, int moveE4, String beforeMove, String afterMove) {
		String position = beforeMove;
		gameState.set(position);
		
		String initialState = gameState.get();
		assertEquals(position, initialState);
		//Make move
		gameState.makeMove(moveE4, sideToMove);
		String afterE5 = gameState.get();
		assertEquals(afterMove, afterE5);
		
		//Undo move
		gameState.undoMove(moveE4, sideToMove);
		String afterUndoingE5 = gameState.get();
		assertEquals(initialState, afterUndoingE5);
	}

}
