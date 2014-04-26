package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.BOARD_EMPTY_SQUARE;

public class Displayer extends AbstractDisplayer {

	void appendPiece(Position position, int currentSquare) {
		int boardPiece = position.getBoard(currentSquare);
		Piece piece = Piece.fromBoardPiece(boardPiece);
		if(piece.exists())
		{
			display.append(piece.toString());
		} else {
			display.append("-");
		}
	}

}
