package com.jeremybrooks.chess;

import org.apache.log4j.Logger;

public class Solver {
	private static final Logger log = Logger.getLogger(Solver.class);
	private MoveGenerator moveGenerator;
	private Evaluator evaluator;
	private Search search = new Search();

	public class Info {
		private double solveTimeMillis;
		private boolean mate;
		private String solutionMoves;
		private int totalNodes;
		public String getSolutionMoves() {
			return solutionMoves;
		}

		public void setSolutionMoves(String solutionMoves) {
			this.solutionMoves = solutionMoves;
		}

		public boolean isMate() {
			return mate;
		}

		public void setMate(boolean mate) {
			this.mate = mate;
		}

		public double getSolveTimeMillis() {
			return solveTimeMillis;
		}

		public void setSolveTimeMillis(double solveTimeMillis) {
			this.solveTimeMillis = solveTimeMillis;
		}

		public int getTotalNodes() {
			return totalNodes;
		}

		public void setTotalNodes(int totalNodes) {
			this.totalNodes = totalNodes;
		}
	}
	
	public Solver() {
		//TODO: use dependency injection instead
		MoveGenerator mg = new MoveGenerator();
		Evaluator eval = new Evaluator();
		eval.setMoveGenerator(mg);
		setMoveGenerator(mg);
		setEvaluator(eval);
		search = new Search();
		search.setEvaluator(evaluator);
		search.setMoveGenerator(moveGenerator);
	}
	
	public Info solve(Puzzle puzzle)
	{
		int movesToMate = puzzle.getMovesToMate();
		int pliesForMovesToMate = getPliesToMate(movesToMate);
    	int maxSearchDepth = getMaxSearchDepth(pliesForMovesToMate);
    	search.setMaxSearchDepth(maxSearchDepth);
		GameState g = new GameState(pliesForMovesToMate);
//		FenParser parser = new FenParser();
//		parser.init(puzzle.getFen());
//		parser.parse();
//		g.setWhiteToMove(parser.isWhiteToMove());
//		g.setMoveNumber(parser.getCurrentMoveNumber());
		g.set(puzzle.getFen());

		log.debug("Searching for mate in "+movesToMate+"...");
		long start = System.nanoTime();
		search.getBestMove(g, g.isWhiteToMove()?0:1);
		double solveTimeMillis = (System.nanoTime() - start)/1000.0;
		int totalNodes = g.nodes;
		boolean mate = g.numberOfLinesToMate > 0;
		String solutionMoves = search.getPVMoveLine();
		Info solveInfo = new Info();
		solveInfo.setSolutionMoves(solutionMoves);
		solveInfo.setMate(mate);
		solveInfo.setTotalNodes(totalNodes);
		solveInfo.setSolveTimeMillis(solveTimeMillis);
		return solveInfo;
	}

	// So given a position in which white mates in 2,
	// depth = getDepthToMate(2) = 2 + (2-1) = 3
	// 
	private static int getPliesToMate(int mateInN){
		/*
		 * mateIn	depth
		 * 1		1			1+1-1 = 1
		 * 2		3           2+2-1 = 3
		 * 3		5           3+3-1 = 5
		 * 4		7
		 * 5		9
		 */
		int depth=0;
		switch(mateInN)
		{
		case 1: 
			depth = 2;
			break;
		case 2: 
			depth = 4;
			break;
		case 3: 
			depth = 5;
			break;
		case 4: 
			depth = 7;
			break;
		case 5: 
			depth = 9;
			break;
		default: 
			depth = (2 * mateInN) - 1;
			break;
		}
		log.debug("mate in "+mateInN+" requires stack depth of "+depth);
		return depth;
	}

	private static int getMaxSearchDepth(int depthForMovesToMate) {
		return depthForMovesToMate-1; //-1 so we can eval and set flags and such for the next level in the graph/tree.
	}


	public void setMoveGenerator(MoveGenerator moveGenerator) {
		this.moveGenerator = moveGenerator;
		search.setMoveGenerator(moveGenerator);
	}

	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
		search.setEvaluator(evaluator);
	}

	private void setMaxSearchDepth(int maxSearchDepth) {
		search.setMaxSearchDepth(maxSearchDepth);
	}
	

}