package com.jeremybrooks.chess;

/**
 * Holds the results of a search
 * @author jeremy
 */
public class SearchInfo {
	private int score;
	private String solutionMoves;
	private boolean mate;
	private long nodeCount;
	private double solveTimeMillis;

	private String scoredRootMoves;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return a human readable list of moves
	 */
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

	public long getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(long nodeCount) {
		this.nodeCount = nodeCount;
	}

	public int getNodesPerSecond() {
		return (int) (nodeCount / (solveTimeMillis * 1000)); //int division is okay;
	}

	public String getScoredRootMoves() {
		return scoredRootMoves;
	}

	public void setScoredRootMoves(String scoredRootMoves) {
		this.scoredRootMoves = scoredRootMoves;
	}
}

