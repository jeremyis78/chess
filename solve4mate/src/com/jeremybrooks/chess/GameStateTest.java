package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class GameStateTest {

	private static final int NONE = 0;

	@Test
	public void testMakeAndUndoWhitePieceMoves() {
		String position = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		GameState g = new GameState();
		g.set(position);
		
		String initialState = g.get();
		assertEquals(position, initialState);
		//Make move
		int e4 = encodeMove(E2, E4, PIECE[PAWN]);
		g.makeMove(e4, Bitmap.WHITE);
		String afterE5 = g.get();
		assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", afterE5);
		
		//Undo move
		g.undoMove(e4, Bitmap.WHITE);
		String afterUndoingE5 = g.get();
		assertEquals(initialState, afterUndoingE5);
	}

	@Test
	public void testMakeAndUndoBlackPieceCaptures() {
		String position = "rnbqkb1r/pppppppp/5n2/8/4P3/5N2/PPPPPPPP/RNBQKB1R b KQkq - 1 2";
		GameState g = new GameState();
		g.set(position);
		
		String stateBeforeMove = g.get();
		assertEquals(position, stateBeforeMove);
		//Make move
		int knightTakeE4 = encodeMove(F6, E4, PIECE[KNIGHT], PIECE[PAWN]);
		g.makeMove(knightTakeE4, BLACK);
		String stateAfterMakeMove = g.get();
		assertEquals("rnbqkb1r/pppppppp/8/8/4n3/5N2/PPPPPPPP/RNBQKB1R w KQkq - 0 3", stateAfterMakeMove);
		
		//Undo move
		g.undoMove(knightTakeE4, BLACK);
		String stateAfterUndoMove = g.get();
		assertEquals(stateBeforeMove, stateAfterUndoMove);
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


}
