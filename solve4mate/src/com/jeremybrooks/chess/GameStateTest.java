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
		int moveE4 = encodeMove(E2, E4, PIECE[PAWN]);
		String beforeMove = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		String afterMove = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
		setupState(beforeMove);
		assertEquals(afterMove, makeMove(WHITE, moveE4));
		assertEquals(beforeMove, undoMove(WHITE, moveE4));
	}

	@Test
	public void testMakeAndUndoBlackPieceCaptures() {
		int moveKnightTakeE4 = encodeMove(F6, E4, PIECE[KNIGHT], PIECE[PAWN]);
		String beforeMove = "rnbqkb1r/pppppppp/5n2/8/4P3/5N2/PPPPPPPP/RNBQKB1R b KQkq - 1 2";
		String afterMove = "rnbqkb1r/pppppppp/8/8/4n3/5N2/PPPPPPPP/RNBQKB1R w KQkq - 0 3";
		setupState(beforeMove);
		assertEquals(afterMove, makeMove(BLACK, moveKnightTakeE4));
		assertEquals(beforeMove, undoMove(BLACK, moveKnightTakeE4));
	}

	@Test @Ignore
	public void testMakeAndUndoWhitePawnCapturesEnPassant() {
		fail("Not yet implemented");
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

	private String setupState(String startState) {
		String position = startState;
		gameState.set(startState);
		String initialState = gameState.get();
		assertEquals(position, initialState);
		return initialState;
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
