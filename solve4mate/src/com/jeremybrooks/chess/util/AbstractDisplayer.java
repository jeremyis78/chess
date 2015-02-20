package com.jeremybrooks.chess.util;

import static com.jeremybrooks.chess.base.Bitmap.A1;
import static com.jeremybrooks.chess.base.Bitmap.A8;

import com.jeremybrooks.chess.base.Bitmap;

public abstract class AbstractDisplayer {

    public static final String EOL = System.lineSeparator();
    protected StringBuilder display;

    public AbstractDisplayer() {
        super();
    }

    public String formatBoard() {
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
                appendPiece(currentSquare);
                appendDelimiter();
            }
            appendRankSuffix();
        }
        appendBorder();
        appendFileLabels();
        return display.toString();
    }


    protected void appendBorder() {
        display.append("   -----------------"+EOL);
    }

    protected void appendRankLabel(int firstSquareOnRank) {
        display.append(firstSquareOnRank/8 + 1 + " | "); //Print the rank number
    }

    protected void appendRankSuffix() {
        display.append("|"+EOL);
    }

    private void appendDelimiter() {
        display.append(" ");
    }

    protected void appendFileLabels() {
        display.append("    a b c d e f g h"+EOL);
    }

    abstract void appendPiece(int currentSquare);
}