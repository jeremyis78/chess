package com.jeremybrooks.chess;

public class DefaultGenerator {

	private static final int MAX_NUM_GENERATED_MOVES = 70;
	
	private Generator capturesGenerator;
	private Generator nonCapturesGenerator;
	private Generator escapeGenerator;
	private GameState g;

	
	public DefaultGenerator()
	{
		capturesGenerator = new CaptureGenerator();
		nonCapturesGenerator = new NonCaptureGenerator();
		escapeGenerator = new EscapeGenerator();
	}
	
	public void setGameState(GameState g)
	{
		this.g = g;
		capturesGenerator.setGameState(g);
		nonCapturesGenerator.setGameState(g);
		escapeGenerator.setGameState(g);
	}

	public Generator getBaseMoveGenerator()
	{
		return capturesGenerator;
	}
	
	public int[] generateMoves(int side, int depth) {
		// Generate legal moves from this position
		int[] moves = new int[MAX_NUM_GENERATED_MOVES]; //how many moves are there actually? fails with 50
	    if (!getBaseMoveGenerator().isAttacked(g, side, g.getPosition().getKingSquare(side))){
	        generateCaptures(moves, side, depth);
	        generateNonCaptures(moves, side, depth);
	    } else {
	        generateKingEscapes(moves, side, depth);
	    }
	    return moves;
	}
	
	// Side effect: g.legalMoves[depth] has the number of moves
	// found in this function added to it after this function completes
	public int generateCaptures(int moves[], int side, int depth)
	{
		return capturesGenerator.generate(moves, side, depth);
	}

	// Side effect: g.legalMoves[depth] has the number of moves
	// found in this function added to it after this function completes
	public int generateNonCaptures(int moves[], int side, int depth)
	{
		return nonCapturesGenerator.generate(moves, side, depth);
	}
	
	public int generateKingEscapes(int moves[], int side, int depth)
	{
		return escapeGenerator.generate(moves, side, depth);
	}

	public boolean isAttacked(GameState g2, int side, int square) {
		return getBaseMoveGenerator().isAttacked(g2, side, square);
	}

}
