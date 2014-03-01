package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.A1;
import static com.jeremybrooks.chess.Bitmap.A8;
import static com.jeremybrooks.chess.Bitmap.BOARD_PIECE;
import static com.jeremybrooks.chess.Bitmap.PIECE_STR;

public class Displayer {

	public String formatBoard(Position position){
		StringBuilder display = new StringBuilder();
		display.append("   -----------------\n");
	    for (int i = A8; i >= A1; i-=8){
	        display.append(i/8 + 1 + " | "); //Print the rank number
	        for (int j = i; j < i+8; j++){
	            switch(position.getBoard(j)){
	                //white pieces: pawns, knights, bishops, rooks, queens, king
	            case 1: display.append(BOARD_PIECE[0] + " ");
	                break;
	            case 2: display.append(BOARD_PIECE[1] + " ");
	                break;
	            case 5: display.append(BOARD_PIECE[2] + " ");
	                break;
	            case 6: display.append(BOARD_PIECE[3] + " ");
	                break;
	            case 7: display.append(BOARD_PIECE[4] + " ");
	                break;
	            case 3: display.append(BOARD_PIECE[5] + " ");
	                break;
	                
	                //black pieces: pawns, knights, bishops, rooks, queens, king
	            case -1: display.append(BOARD_PIECE[6] + " ");
	                break;
	            case -2: display.append(BOARD_PIECE[7] + " ");
	                break;
	            case -5: display.append(BOARD_PIECE[8] + " ");
	                break;
	            case -6: display.append(BOARD_PIECE[9] + " ");
	            	break;
	            case -7: display.append(BOARD_PIECE[10] + " ");
	                break;
	            case -3: display.append(BOARD_PIECE[11] + " ");
	                break;
	            default: display.append("- ");
	                break;
	            }
			}
	        display.append("|\n");
	    }
	    display.append("   -----------------\n");
	    display.append("    a b c d e f g h\n");
	    return display.toString();
	}

	public String formatAllBitboards(Position position){
		StringBuilder display = new StringBuilder();
	    //Displays ASCII chessboard position
	    
	    //This prints the board so that a1 is in the lower
	    //left hand corner and h8 is in the upper right hand
	    //corner--the normal chessboard view.
	    
	    long mask = 1; //, m = 1;
	    
	    int  num_of_sq_to_display = 64; //must be multiple of 8 and <= 64
	    
	    //Above value should be 64 to display the entire chessboard
	    //To display only the first rank (a1-h1) it should be 8.
		//To display 2nd and 1st rank (a2-h2 and a1-h1) it should be 16, & so on.
	    
	    display.append("   -----------------\n");
	    
	    int i, j;
	    for(i = num_of_sq_to_display - 8; i >=0; i-=8){
	        mask = 1L << i;
	        
	        int k = i + 8;  //set upper bound on next for-loop
	        //		display.append(i/8 + 1 << ' '; //Print the rank number
	        display.append(i/8 + 1 + " | "); //Print the rank number
	        
	        for(j = i; j < k; ++j, mask <<= 1){
	            //If there's a piece at that square
	            //Print PIECE[c][p] otherwise print "-"
	            boolean nopiece = true;
	            for (int c = Color.WHITE; c <= Color.BLACK; c++){
	                for (int p = 0; p <= Pieces.QUEENS; p++){ 
	                    if (Util.bool(mask & position.getPieces(c, p))){
	                        display.append(PIECE_STR[c][p] + " ");  
	                        nopiece = false;
	                            }
	                }//end for p
	            }//end for c
	            if (nopiece){
	                display.append("- "); //print "- " for empty square
	            }
	        }
	        display.append("|\n");
	    }
	    display.append("   -----------------\n");
	    display.append("    a b c d e f g h\n");
	    return display.toString();
	}

}
