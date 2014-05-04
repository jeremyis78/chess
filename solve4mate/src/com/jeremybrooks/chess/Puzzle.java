package com.jeremybrooks.chess;

public class Puzzle {
	private static FenParser parser;
	private String fen;
	private int movesToMate;
	
	public Puzzle() {}
	public Puzzle(String fen, int mateInN) {
		setFen(fen);
		setMovesToMate(mateInN);
	}
	public String getFen() { return fen; }
	public void setFen(String fen) { this.fen = fen; }
	public int getMovesToMate() { return movesToMate; }
	public void setMovesToMate(int movesToMate) { this.movesToMate = movesToMate; }
}
