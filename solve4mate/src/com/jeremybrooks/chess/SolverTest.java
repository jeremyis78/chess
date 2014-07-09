package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.search.SearchInfo;

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
		SearchInfo info = solver.solve(puzzle);
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
		SearchInfo info = solver.solve(puzzle);
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
		SearchInfo info = solver.solve(puzzle);
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
		SearchInfo info = solver.solve(puzzle);
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
		SearchInfo info = solver.solve(puzzle);
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
		SearchInfo info = solver.solve(puzzle);
		boolean isMate = info.isMate();
		String solution = info.getSolutionMoves();
		System.out.println(solution);
		assertTrue(isMate);
		assertTrue(solution.startsWith(expectedSolution));
	}
	
	//@Test -- FIXME: currently fails to find this double sacrifice mate 3 moves out
	//This will probably be easily fixed once move ordering is implemented and checks 
	//are higher priority to search.
	public void givenMateInThreeWithTrickyDoubleSacrifice () {
		String fen = "r1b2rk1/p5pp/1pp3q1/3p1p2/1B1PnPnR/3BPK2/PP2N1P1/R4NQ1 b - - 0 1 #Polgar No. 4190";
		String expectedSolution = "Ng4-e5 f4xe5 Qg6-g4 Rh4xg4 f5xg4";  //double sacrifice!! (knight, then queen)
		puzzle.setFen(fen);
		puzzle.setMovesToMate(3);
		SearchInfo info = solver.solve(puzzle);
		boolean isMate = info.isMate();
		String solution = info.getSolutionMoves();
		System.out.println(solution);
		assertTrue(isMate);
		assertEquals(expectedSolution, solution);
	}
	
	
}
