package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.BOARD_PIECE;

public class Displayer extends AbstractDisplayer {

	void appendPiece(Position position, int currentSquare) {
		switch(position.getBoard(currentSquare)){
		    //white pieces: pawns, knights, bishops, rooks, queens, king
		case 1: display.append(BOARD_PIECE[0]);
		    break;
		case 2: display.append(BOARD_PIECE[1]);
		    break;
		case 5: display.append(BOARD_PIECE[2]);
		    break;
		case 6: display.append(BOARD_PIECE[3]);
		    break;
		case 7: display.append(BOARD_PIECE[4]);
		    break;
		case 3: display.append(BOARD_PIECE[5]);
		    break;
		    
		    //black pieces: pawns, knights, bishops, rooks, queens, king
		case -1: display.append(BOARD_PIECE[6]);
		    break;
		case -2: display.append(BOARD_PIECE[7]);
		    break;
		case -5: display.append(BOARD_PIECE[8]);
		    break;
		case -6: display.append(BOARD_PIECE[9]);
			break;
		case -7: display.append(BOARD_PIECE[10]);
		    break;
		case -3: display.append(BOARD_PIECE[11]);
		    break;
		default: display.append("-");
		    break;
		}
	}

}
