package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.BLACK;
import static com.jeremybrooks.chess.Bitmap.WHITE;
import static com.jeremybrooks.chess.MaterialAdjustmentTerm.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MaterialAdjustmentTermTest {

	private static final int GAMESTATE_MAX_MOVES = 2;
	private MaterialAdjustmentTerm term = new MaterialAdjustmentTerm();
	
	
	@Test
	public void givenTwoBlackKnightsEightPawns() {
		String fen = "1n2k1n1/pppppppp/8/8/8/8/8/4K3 b kq - 0 1";
		int pawns = pieceCount('p', fen);
		int knights = pieceCount('n', fen);
		int expectedScore = blackScore(forMorePawns(knights, pawns, KNIGHT_ADJUSTMENT_PER_PAWN));
		int score = evaluate(fen);
		assertEquals(expectedScore, score);
	}

	@Test
	public void givenOneBlackKnightFourPawns() {
		String fen = "1n2k3/pppp4/8/8/8/8/8/4K3 b - - 0 1";
		int pawns = pieceCount('p', fen);
		int knights = pieceCount('n', fen);
		int expectedScore = blackScore(forLessPawns(knights, pawns, -KNIGHT_ADJUSTMENT_PER_PAWN));
		int score = evaluate(fen);
		assertEquals(expectedScore, score);
	}

	@Test
	public void givenTwoWhiteKnightEightPawns() {
		String fen = "4k3/8/8/8/8/8/PPPPPPPP/1N2K1N1 w - - 0 1";
		int pawns = pieceCount('P', fen);
		int knights = pieceCount('N', fen);
		int expectedScore = whiteScore(forMorePawns(knights, pawns, KNIGHT_ADJUSTMENT_PER_PAWN));
		int score = evaluate(fen);
		assertEquals(expectedScore, score);
	}
	
	@Test
	public void givenOneBlackKnightAtFivePawnBoundary() {
		String fen = "1n2k3/ppppp3/8/8/8/8/8/4K3 b - - 0 1";
		int expectedScore = 0;
		int score = evaluate(fen);
		assertEquals(expectedScore, score);
	}
	
	@Test
	public void givenOneBlackRookFourPawns() {
		String fen = "r3k3/pppp4/8/8/8/8/8/4K3 b - - 0 1";
		int pawns = pieceCount('p', fen);
		int rooks = pieceCount('r', fen);
		System.out.println("pawns: " + pawns + "  rooks: " + rooks);
		int expectedScore = blackScore(forLessPawns(rooks, pawns, ROOK_ADJUSTMENT_PER_PAWN));
		int score = evaluate(fen);
		assertEquals(expectedScore, score);
	}

	@Test
	public void givenTwoWhiteRooksSevenPawns() {
		String fen = "4k3/8/8/8/8/8/1PPPPPPP/R3K2R w - - 0 1";
		int pawns = pieceCount('P', fen);
		int rooks = pieceCount('R', fen);
		int expectedScore = whiteScore(forMorePawns(rooks, pawns, -ROOK_ADJUSTMENT_PER_PAWN));
		int score = evaluate(fen);
		assertEquals(expectedScore, score);
	}
	
	@Test
	public void givenWhiteRookAtFivePawnBoundary() {
		String fen = "4k3/8/8/8/8/8/3PPPPP/R3K3 w - - 0 1";
		int expectedScore = 0;
		int score = evaluate(fen);
		assertEquals(expectedScore, score);
	}

	public int evaluate(String fen) {
		GameState g = new GameState(GAMESTATE_MAX_MOVES);
		g.set(fen);
		int score = term.evaluate(g);
		return score;
	}
	
	private static int whiteScore(int score) { return score; }
	private static int blackScore(int score) { return -1 * score; }

	private static int forMorePawns(int pieces, int pawns, int adjustmentPerPawn)
	{
		return pieces * (adjustmentPerPawn * pawnsOverBoundary(pawns));
	}

	private static int forLessPawns(int pieces, int pawns, int adjustmentPerPawn)
	{
		return pieces * (adjustmentPerPawn * pawnsUnderBoundary(pawns));
	}

	private static int pawnsUnderBoundary(int pawnCount)
	{
		return PAWN_COUNT_BOUNDARY - pawnCount; 
	}

	private static int pawnsOverBoundary(int pawnCount)
	{
		return Math.abs(pawnsUnderBoundary(pawnCount));
	}

	private static int pieceCount(char pieceChar, String fen) {
		int count = 0;
		for(char c: fen.toCharArray())
		{
			if(pieceChar == c) count++;
		}
		return count;
	}

}
