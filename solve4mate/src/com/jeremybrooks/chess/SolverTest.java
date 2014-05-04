package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SolverTest {

	private Solver solver;
	private Puzzle puzzle;
	
	@Before
	public void setUp()
	{
		solver = new Solver();
		puzzle = new Puzzle();
	}
	
	@Test
	public void testSolveMateInOne() {
		String fen = "3q1rk1/5pbp/5Qp1/8/8/2B5/5PPP/6K1 w - - 0 1  #\"Chess\" Lazlo Polgar #1";
		String expectedSolution = "Qf6xg7";
		puzzle.setFen(fen);
		puzzle.setMovesToMate(1);
		Solver.Info info = solver.solve(puzzle);
		boolean isMate = info.isMate();
		String solution = info.getSolutionMoves();
		assertTrue(isMate);
		assertTrue(solution.startsWith(expectedSolution));
	}

	@Test
	public void testSolveForWhiteWhereBlackMatesIfWhiteMovesOffBackRank() {
		String fen = "6k1/4rppp/8/8/8/8/5PPP/3R2K1 w - - 0 1  #Burgess #1";
		String expectedSolution = "Rd1-d8 Re7-e8 Rd8xe8";
		puzzle.setFen(fen);
		puzzle.setMovesToMate(2);
		Solver.Info info = solver.solve(puzzle);
		boolean isMate = info.isMate();
		String solution = info.getSolutionMoves();
		System.out.println(solution);
		assertTrue(isMate);
		assertTrue(solution.startsWith(expectedSolution));
	}

	@Test
	public void testSolveForMate1() {
		String fen = "4K1k1/6pp/2NN4/8/8/8/8/8 w - - 0 1 #Mammoth Book of Chess  Graham Burgess #4";
		String expectedSolution = "Nc6-e7 Kg8-h8 Nd6-f7";
		puzzle.setFen(fen);
		puzzle.setMovesToMate(2);
		Solver.Info info = solver.solve(puzzle);
		boolean isMate = info.isMate();
		String solution = info.getSolutionMoves();
		System.out.println(solution);
		assertTrue(isMate);
		assertTrue(solution.startsWith(expectedSolution));
	}

	@Test
	public void testSolveForMate2MultiplePathsLeadToMate() {
		String fen = "8/8/8/8/5K2/pP6/Q7/5k2 w - - 0 1     #Polgar #307";
		String expectedSolution = "Kf4-f3 Kf1-e1 Qa2-e2";
		puzzle.setFen(fen);
		puzzle.setMovesToMate(2);
		Solver.Info info = solver.solve(puzzle);
		boolean isMate = info.isMate();
		String solution = info.getSolutionMoves();
		System.out.println(solution);
		assertTrue(isMate);
		assertTrue(solution.startsWith(expectedSolution));
	}
	
	
	@Test
	public void testSolveForMate3MorePiecesOnTheBoard() {
		String fen = "r2qkbnr/ppp2ppp/n2p4/4N3/2B1P3/2N5/PPPP1PPP/R1BbK2R w - - 0 1  #Burgess 2";
		String expectedSolution = "Bc4xf7 Ke8-e7 Nc3-d5";
		puzzle.setFen(fen);
		puzzle.setMovesToMate(2);
		Solver.Info info = solver.solve(puzzle);
		boolean isMate = info.isMate();
		String solution = info.getSolutionMoves();
		System.out.println(solution);
		assertTrue(isMate);
		assertTrue(solution.startsWith(expectedSolution));
	}

	@Test
	public void testSolveForMate4 () {
		String fen = "r2qkbnr/ppp2ppp/n2p4/4N3/2B1P3/2N5/PPPP1PPP/R1BbK2R w - - 0 1  #Burgess 2";
		String expectedSolution = "Bc4xf7 Ke8-e7 Nc3-d5";
		puzzle.setFen(fen);
		puzzle.setMovesToMate(2);
		Solver.Info info = solver.solve(puzzle);
		boolean isMate = info.isMate();
		String solution = info.getSolutionMoves();
		System.out.println(solution);
		assertTrue(isMate);
		assertTrue(solution.startsWith(expectedSolution));
	}
}
