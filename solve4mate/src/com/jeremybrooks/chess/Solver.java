package com.jeremybrooks.chess;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.eval.Evaluator;

public class Solver {
	private static final Logger log = Logger.getLogger(Solver.class);
	private DefaultGenerator moveGenerator;
	private Evaluator evaluator;
	private Search search = new Search();

	
	public Solver() {
		//TODO: use dependency injection instead
		DefaultGenerator mg = new DefaultGenerator();
		Evaluator eval = new Evaluator();
		eval.setMoveGenerator(mg);
		setMoveGenerator(mg);
		setEvaluator(eval);
		search = new IterativeDeepeningSearch();
		search.setEvaluator(evaluator);
		search.setMoveGenerator(moveGenerator);
	}

	public SearchInfo search(GameState g, int maxDepth)
	{
		assert(maxDepth <= GameState.MAX_NUM_MOVES_MADE);
		
    	search.setStackSize(maxDepth + 1);
		search.setGameState(g);

		log.debug("Searching...");
		long start = System.nanoTime();
		int score = search.search(g.isWhiteToMove()?0:1);

		double elapsedTimeMillis = (System.nanoTime() - start)/1000000.0;
		long nodeCount = search.getNodeCount();
		boolean mate = Math.abs(score) > Evaluator.CHECKMATE / 2;
		String solutionMoves = search.getPVMoveLine();
		String scoredRootMoves = getScoredRootMoves();
		SearchInfo info = new SearchInfo();
		info.setScore(score);
		info.setSolutionMoves(solutionMoves);
		info.setMate(mate);
		info.setNodeCount(nodeCount);
		info.setSolveTimeMillis(elapsedTimeMillis);
		info.setScoredRootMoves(scoredRootMoves);
		return info;
	}

	public SearchInfo solve(Puzzle puzzle)
	{
		int movesToMate = puzzle.getMovesToMate();
		int pliesForMovesToMate = getPliesToMate(movesToMate);
    	int stackSize = getStackSize(pliesForMovesToMate);
    	search.setStackSize(stackSize);
		GameState g = new GameState(pliesForMovesToMate);
		search.setGameState(g);
//		FenParser parser = new FenParser();
//		parser.init(puzzle.getFen());
//		parser.parse();
//		g.setWhiteToMove(parser.isWhiteToMove());
//		g.setMoveNumber(parser.getCurrentMoveNumber());
		g.set(puzzle.getFen());

		log.debug("Searching for mate in "+movesToMate+"...");
		long start = System.nanoTime();
		int score = search.search(g.isWhiteToMove()?0:1);
		double solveTimeMillis = (System.nanoTime() - start)/1000000.0;
		long nodeCount = search.getNodeCount(); //g.nodes;
		boolean mate = Math.abs(score) > Evaluator.CHECKMATE / 2;
		String solutionMoves = search.getPVMoveLine();
		String scoredRootMoves = getScoredRootMoves();
		SearchInfo solveInfo = new SearchInfo();
		solveInfo.setScore(score);
		solveInfo.setSolutionMoves(solutionMoves);
		solveInfo.setMate(mate);
		solveInfo.setNodeCount(nodeCount);
		solveInfo.setSolveTimeMillis(solveTimeMillis);
		solveInfo.setScoredRootMoves(scoredRootMoves);
		return solveInfo;
	}

	private String getScoredRootMoves() {
		StringBuilder sb = new StringBuilder("\n");
		for(ScoredMove sm: search.getRootMove())
		{
			if(sm == null) break;
			sb.append(Util.displayMoveStr(sm.getMove(),false,false));
			sb.append("("+sm.getScore()+")\n");
		}
		return sb.toString();
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
			depth = 6;
			break;
		case 4: 
			depth = 7;
			break;
		case 5: 
			depth = 10;
			break;
		default: 
			depth = (2 * mateInN) - 1;
			break;
		}
		log.debug("mate in "+mateInN+" requires stack depth of "+depth);
		return depth;
	}

	private static int getStackSize(int depthForMovesToMate) {
		return depthForMovesToMate-1; //-1 so we can eval and set flags and such for the next level in the graph/tree.
	}


	public void setMoveGenerator(DefaultGenerator moveGenerator) {
		this.moveGenerator = moveGenerator;
		search.setMoveGenerator(moveGenerator);
	}

	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
		search.setEvaluator(evaluator);
	}

	private void setMaxSearchDepth(int maxSearchDepth) {
		search.setStackSize(maxSearchDepth);
	}
	

}