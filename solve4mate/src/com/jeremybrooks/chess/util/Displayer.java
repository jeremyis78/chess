package com.jeremybrooks.chess.util;

import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;

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
