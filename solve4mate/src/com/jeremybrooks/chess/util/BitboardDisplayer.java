package com.jeremybrooks.chess.util;

import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;

public class BitboardDisplayer extends AbstractDisplayer {

    private static final char EMPTY_CHARACTER = '-';

    public void appendPiece(Position position, int currentSquare) {
        long maskForCurrentSquare = 1L << currentSquare;
        //If there's a piece at that square
        //print its character, otherwise print "-"
        boolean nopiece = true;
        for (int c = Piece.WHITE; c <= Piece.BLACK; c++){
            for (int p = 0; p <= Piece.QUEEN; p++){ 
                if (Util.bool(maskForCurrentSquare & position.getPieces(c, p))){
                    display.append(Piece.asCharacter(c, p));  
                    nopiece = false;
                        }
            }
        }
        if (nopiece){
            display.append(EMPTY_CHARACTER);
        }
    }
}
