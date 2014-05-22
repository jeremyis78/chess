package com.jeremybrooks.chess;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.Bitmap.*;

import java.util.Arrays;

import org.junit.Test;

import com.jeremybrooks.chess.Piece.Color;

/**
 * Includes encodeMove() function and a test that encodes moves such that
 * sorting them will yield a most valuable victim taken by least valuable attacker
 * (aka, MVV/LVA) move ordering:
 * 1) captures (MVV/LVA) and promotions (to Queen, then Rook, Bishop, Knight)
 * 2) promotions (to Queen, then Rook, Bishop, Knight)
 * 3) captures (most valuable victim taken by least valuable attacker, aka, MVV/LVA)
 * 4) Major piece moves
 * 5) Minor piece moves
 * 6) Pawn moves
 * 
 * @author jeremy
 *
 */
public class MoveOrderingTest {

	private static final String[] EXPECTED_ORDERING = new String[] {
			"PxQq",
			"PxRq",
			"PxBq",
			"PxNq",
			"PxQr",
			"PxRr",
			"PxBr",
			"PxNr",
			"PxQb",
			"PxRb",
			"PxBb",
			"PxNb",
			"PxQn",
			"PxRn",
			"PxBn",
			"PxNn",
			"PxQ",
			"NxQ",
			"KxQ",
			"BxQ",
			"RxQ",
			"QxQ",
			"PxR",
			"NxR",
			"KxR",
			"BxR",
			"RxR",
			"QxR",
			"PxB",
			"NxB",
			"KxB",
			"BxB",
			"RxB",
			"QxB",
			"PxN",
			"NxN",
			"KxN",
			"BxN",
			"RxN",
			"QxN",
			"PxP",
			"NxP",
			"KxP",
			"BxP",
			"RxP",
			"QxP",
			"Q",
			"R",
			"B",
			"K",
			"N",
			"P",
			" ",
			" ",
			" "
	};

	@Test
	public void generateMoveCombos()
	{
		int[] moves = generateAllPieceMoves();
		Arrays.sort(moves);
		reverse(moves);
//		System.out.println(String.format("%11s", "encodedMove") +"\tSummary\tpro\tcap\tadj\tmov\tActual");
		StringBuilder actualOrder = new StringBuilder();
		StringBuilder expectedOrder = new StringBuilder();
		for(String s: EXPECTED_ORDERING)
		{
			expectedOrder.append(s).append(System.lineSeparator());
		}
		for(int m: moves)
		{
		    int from = m & 0x3F;         //first 6 bits
		    int to = (m >> 6) & 0x3F;    //next 6
		    int mov = (m >> 12) & 0x7;   //next 3
		    int adj = (m >> 15) & 0x7;  //next 3
		    int cap = (m >> 18) & 0x7;   //next 3
		    int pro = (m >> 21) & 0x7;   //next 3
		    
		    Piece attacker = PieceFactory.fromBoardPiece(mov);
		    Piece victim = PieceFactory.fromBoardPiece(cap);
		    Piece promoter = pro != 0 ? PieceFactory.fromBoardPiece(-pro) : new Empty();
		    String moveStr = ""+attacker.toChar();
		    if(victim.exists()) moveStr += "x"+victim.toChar();
		    if(promoter.exists()) moveStr += promoter.toChar();
		    actualOrder.append(moveStr + System.lineSeparator());
//			System.out.println(String.format("%11d", m) + "\t" + moveStr + "\t" + pro + "\t" + cap + "\t" + adj + "\t" + mov + "\t" + formatMove(m, false, false) );
		}
		assertEquals(expectedOrder.toString(), actualOrder.toString());
	}

	private int[] generateAllPieceMoves() {
		int[] moves = new int[55];
		int index = 0;
		Piece[] attackers = new Piece[]{new Pawn(Color.W), new Knight(Color.W), new Bishop(Color.W), new Rook(Color.W), new Queen(Color.W), new King(Color.W)};
		Piece[] victims = new Piece[]{new Queen(Color.W), new Rook(Color.W), new Bishop(Color.W), new Knight(Color.W), new Pawn(Color.W)};
		Piece[] promoters = new Piece[]{new Queen(Color.B), new Rook(Color.B), new Bishop(Color.B), new Knight(Color.B)};
		int move = 0;
		for(Piece attacker: attackers)
		{
			for(Piece captured: victims)
			{
				if(attacker.index() == PAWN && captured.index() != PAWN) 
				{
					for(Piece promotion: promoters)
					{
						move = myEncodeMove(A1,A1,PIECE[attacker.index()],PIECE[captured.index()],PIECE[promotion.index()]);
						moves[index++] = move;
					}
				}
				move = myEncodeMove(A1,A1,PIECE[attacker.index()],PIECE[captured.index()]);
				moves[index++] = move;
			}
			move = myEncodeMove(A1,A1,PIECE[attacker.index()]);
			moves[index++] = move;
		}
		return moves;
	}
	
	private int myEncodeMove(int from, int to, int piece) {
		return myEncodeMove(from, to, piece, 0, 0);
	}

	private int myEncodeMove(int from, int to, int piece, int capturedPiece) {
		return myEncodeMove(from, to, piece, capturedPiece, 0);
	}

	/**
	 * Encodes a single move such that the returned encoding accommodates sorting.
	 * 
	 * Accommodates the following ordering:
	 * 1) pawn captures to promotes
	 * 2) pawn promotes
	 * 3) captures
	 * 4) Major piece moves
	 * 5) Minor piece moves
	 * 6) Pawn moves,
	 * 
	 * where promotions are in (Q,R,B,N) order and captures are in MVV/LVA order.
	 *
	 * @param from  The from square
	 * @param to  The to square
	 * @param piece  The attacker
	 * @param capturedPiece  The victim, if any (0 for empty)
	 * @param promotionPiece  The promotion piece, if any (0 for empty)
	 * @return an encoded move (int) suitable for sorting
	 */
	private int myEncodeMove(int from, int to, int piece, int capturedPiece, int promotionPiece) {
		//adj and its placement in the encoded move insures capturing moves are ordered with least
		//valuable attacker first (moves capturing a knight would be sorted like: PxN,NxN,BxN,RxN,QxN)
		int adj = (capturedPiece == 0 ? 0 : PIECE[QUEEN]-piece); 
		return (promotionPiece << 21) | 
				(capturedPiece << 18) | 
				(adj << 15) | 
				(piece << 12) | 
				(to << 6) | 
				from;
	}
	
    private static String formatMove(int m, boolean check, boolean mate){
	    int from, to, mov, cap, pro, gain;
	    from = m & 0x3F;          //first 6 bits
	    to = (m >> 6) & 0x3F;     //next 6
	    mov = (m >> 12) & 0x7;    //next 3
	    gain = (m >> 15) & 0x7;   //next 3
	    cap = (m >> 18) & 0x7;    //next 3
	    pro = (m >> 21) & 0x7;    //next 3

	    char pieceChar[] = {' ','P','N','K',' ','B','R','Q'};
	    StringBuilder coordStr = new StringBuilder(); 
	    StringBuilder SANStr = new StringBuilder();

	    //Add the moving piece
	    coordStr.append(pieceChar[mov]);
	    coordStr.append(Util.SqToStr(from));
	    if (pieceChar[mov] == 'P' && cap != 0){ //if pawn capture
	    	SANStr.append(coordStr.toString().charAt(1)); //the file of the pawn
	    } else {
	    	SANStr.append(pieceChar[mov]);  //the type of piece
	    }
	    
	    //Add 'x' or '-' for capture or noncapture	
	    if (cap != 0){
	        //coordStr[i++] = SANStr[j++] = 'x';
	    	coordStr.append("x");
	    	SANStr.append("x");
	    } else {
	        coordStr.append('-');
	    }

	    //Add the 'to' square
	    coordStr.append(Util.SqToStr(to));
	    SANStr.append(Util.SqToStr(to));
	    
	    //Add the promotion piece
	    if(pro != 0){
	        if (pieceChar[mov] == 'P'){
	            coordStr.append(pieceChar[pro]);
	            SANStr.append('=');
	            SANStr.append(pieceChar[pro]);
	        } else {
	        	System.err.println("can't promote a piece other than a pawn");
	        }
	    }

	    if (mate || (check && mate)){
	    	coordStr.append("#");
        	SANStr.append("#");
	    } else if (check){
	    	coordStr.append("+");
	    	SANStr.append("+");
	    }
	    
	    //Print the moves in coordinate notation and SAN (TODO: SAN doesn't account for ambiguous moves yet!)
	    if ('K' == pieceChar[mov]){
		    if ((from == Bitmap.E1 && to == Bitmap.G1) || (from == Bitmap.E8 && to == Bitmap.G8)){
	            //printf("%s 0-0", coordStr);
		    	coordStr.append(" 0-0");
		    } else if ((from == Bitmap.E1 && to == Bitmap.C1) || (from == Bitmap.E8 && to == Bitmap.C8)){
		        //printf("%s 0-0-0", coordStr);
		    	coordStr.append(" 0-0-0");
		    } 
	    }
	    //out.printf("0x%06X %s %s ", m, coordStr, SANStr);
	    return coordStr.toString();
	}

    /**
     * Reverses the order of the elements in the specified array.<p>
     *
     * This method runs in linear time.
     *
     * @param  array The array whose elements are to be reversed.
     */
    public static void reverse(int[] array) {
        int size = array.length;
        for (int i=0, mid=size>>1, j=size-1; i<mid; i++, j--)
        	swap(array, i, j);
    }

	private static void swap(int[] array, int i, int j) {
		int tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}

    
}
