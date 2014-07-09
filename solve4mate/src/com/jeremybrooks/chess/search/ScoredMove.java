package com.jeremybrooks.chess.search;

import com.jeremybrooks.chess.util.Util;

/**
 * Holds a score (ie, the results of a search) for a given move.
 * 
 * Includes the precision of the score as well as the
 * depth of the search that computed the score (or bound
 * on the score).
 * @author jeremy
 *
 */
public class ScoredMove 
{
	/**
	 * Indicates the precision of the move's score.
	 * The score is either an exact score or simply a bound
	 * on the score.
	 *
	 */
	public enum Precision {EXACT, LOWER_BOUND, UPPER_BOUND};
	
	private int move;
	private int score;
	private Precision precision;
	private int depthOfSearch;

	public ScoredMove() { }

	public ScoredMove(int move, int score) {
		super();
		this.move = move;
		this.score = score;
	}
	
	public ScoredMove(int move, int score, Precision precision,
			int depthOfSearch) {
		super();
		this.move = move;
		this.score = score;
		this.precision = precision;
		this.depthOfSearch = depthOfSearch;
	}

	public int getMove() { return move; }
	public void setMove(int move) { this.move = move; }
	public int getScore() {	return score; }
	public void setScore(int score) { this.score = score; }
	public Precision getPrecision() { return precision; }
	public void setPrecision(Precision metaData) { this.precision = metaData; }
	public int getDepthOfSearch() {	return depthOfSearch; }
	public void setDepthOfSearch(int depth) { this.depthOfSearch = depth; }
	
	@Override
	public String toString()
	{
		return "" + score + ": " + precision + ", " +
				Util.displayMoveStr(move, false, false) + ", " + depthOfSearch;
	}
}
