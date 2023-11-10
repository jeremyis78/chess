package com.jeremybrooks.chess.util;

import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;

public class Displayer extends AbstractDisplayer {

	private Position pos;
	
	public Displayer() {
		super();
	}

	public Displayer(Position position){
		pos = position;
	}
	
    public void setPosition(Position position) {
		pos = position;
	}

	void appendPiece(int currentSquare) {
        Piece piece = pos.get(currentSquare);
        if(piece.exists())
        {
            display.append(piece.toString());
        } else {
            display.append("-");
        }
    }

}
