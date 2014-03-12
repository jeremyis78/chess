package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

public class BitboardDisplayer extends AbstractDisplayer {

	private static final char EMPTY_CHARACTER = '-';
	private static final char PIECE_CHARACTER[][] = {
	    {'P','N','B','R','Q','K',},
	    {'p','n','b','r','q','k',}
	};

	public void appendPiece(Position position, int currentSquare) {
		long maskForCurrentSquare = 1L << currentSquare;
		//If there's a piece at that square
		//Print PIECE[c][p] otherwise print "-"
		boolean nopiece = true;
		for (int c = Bitmap.WHITE; c <= Bitmap.BLACK; c++){
		    for (int p = 0; p <= QUEEN; p++){ 
		        if (Util.bool(maskForCurrentSquare & position.getPieces(c, p))){
		            display.append(PIECE_CHARACTER[c][p]);  
		            nopiece = false;
		                }
		    }
		}
		if (nopiece){
		    display.append(EMPTY_CHARACTER);
		}
	}
}
