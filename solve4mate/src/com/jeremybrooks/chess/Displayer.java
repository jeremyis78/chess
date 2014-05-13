package com.jeremybrooks.chess;

public class Displayer extends AbstractDisplayer {

	void appendPiece(Position position, int currentSquare) {
		int boardPiece = position.getBoard(currentSquare);
		Piece piece = PieceFactory.fromBoardPiece(boardPiece);
		if(piece.exists())
		{
			display.append(piece.toString());
		} else {
			display.append("-");
		}
	}

}
