package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.A1;
import static com.jeremybrooks.chess.Bitmap.A8;

public abstract class AbstractDisplayer {

	protected StringBuilder display;

	public AbstractDisplayer() {
		super();
	}

	public String formatBoard(Position position) {
		display = new StringBuilder();
		appendBorder();
	    for (int firstSquareOnRank = A8;
	    		firstSquareOnRank >= A1; 
	    		firstSquareOnRank-=8)
	    {
	        int numberOfFilesOnBoard = Bitmap.File.values().length;
	        appendRankLabel(firstSquareOnRank);
	        int lastSquareOnRank = firstSquareOnRank + numberOfFilesOnBoard;
	        for (int currentSquare = firstSquareOnRank;
	        		currentSquare < lastSquareOnRank;
	        		currentSquare++)
	        {
	            appendPiece(position, currentSquare);
	            appendDelimiter();
			}
	        appendRankSuffix();
	    }
	    appendBorder();
	    appendFileLabels();
	    return display.toString();
	}


	protected void appendBorder() {
		display.append("   -----------------\n");
	}

	protected void appendRankLabel(int firstSquareOnRank) {
		display.append(firstSquareOnRank/8 + 1 + " | "); //Print the rank number
	}

	protected void appendRankSuffix() {
		display.append("|\n");
	}

	private void appendDelimiter() {
		display.append(" ");
	}

	protected void appendFileLabels() {
		display.append("    a b c d e f g h\n");
	}

	abstract void appendPiece(Position position, int currentSquare);
}