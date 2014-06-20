package com.jeremybrooks.chess;

public class Displayer extends AbstractDisplayer {

	void appendPiece(Position position, int currentSquare) {
		Piece piece = position.get(currentSquare);
		if(piece.exists())
		{
			display.append(piece.toString());
		} else {
			display.append("-");
		}
	}

}
