package com.jeremybrooks.chess;

public class Puzzle {
	private String fen;
	private int movesToMate;
	private String notes;
	
	public Puzzle() {}

	public static Puzzle parse(String line) {
		Puzzle p = new Puzzle();
		int firstSpace = line.indexOf(' ');
		int firstHash = line.indexOf('#');
		int movesToMateIndex = 0;
		int fenIndex = firstSpace != -1 ? firstSpace : line.length()-1;
		int notesIndex = firstHash != -1 ? firstHash : line.length();
		int mateInN = Integer.parseInt(line.substring(movesToMateIndex, fenIndex));
		String fen = line.substring(fenIndex+1, notesIndex);
		String notes = notesIndex < line.length() ? line.substring(notesIndex+1) : "";
		p.setFen(fen);
		p.setMovesToMate(mateInN);
		p.setNotes(notes);
		return p;
	}
	public String getFen() { return fen; }
	public void setFen(String fen) { this.fen = fen; }
	public int getMovesToMate() { return movesToMate; }
	public void setMovesToMate(int movesToMate) { this.movesToMate = movesToMate; }
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
}
