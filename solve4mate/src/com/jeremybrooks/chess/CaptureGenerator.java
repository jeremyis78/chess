package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.BISHOP;
import static com.jeremybrooks.chess.Bitmap.KING;
import static com.jeremybrooks.chess.Bitmap.KNIGHT;
import static com.jeremybrooks.chess.Bitmap.NOSQUARE;
import static com.jeremybrooks.chess.Bitmap.PAWN;
import static com.jeremybrooks.chess.Bitmap.PIECE;
import static com.jeremybrooks.chess.Bitmap.QUEEN;
import static com.jeremybrooks.chess.Bitmap.ROOK;

public class CaptureGenerator extends MoveGenerator implements Generator {

	@Override
	public int generate(GameState g, int[] moves, int side, int depth) {
	    // This includes pawns that capture to promote to a Q,R,B,N

	    int to = 0;
	    int from = 0;
	    int cap = 0;
	    int pro = 0;
	    int n;       //move index counter
	    int mover;
	    int move;
	    long pieces;
	    long pieceAttacks = 0;    //must be zeroed
	    long attackedPieces;      //as in "the enemy pieces that are attacked"

	    n = g.numberOfLegalMoves[depth];

	    for (int p = PAWN; p <= KING; p++) {
	        mover = PIECE[p];
	        pieces = g.pos.getPieces (side, p);
	        while (morePieces(pieces)) {
	            from = FirstPiece (pieces);
	            pro = 0;
	            pieceAttacks = 0;
	            switch (p) {
	            case PAWN:
	                pieceAttacks = att.pawn[side][from];
	                pro = isPawnPromotion(side, from);

	                //EnPassant captures
	                if (g.enPassantSq[depth] != NOSQUARE)
	                {
	                    if (Util.bool(pieceAttacks & (1L << g.enPassantSq[depth])))
	                    {
	                        to = g.enPassantSq[depth];
	                        cap = PIECE[PAWN];
	                        moves[n++] = EncodeMove (from, to, PIECE[PAWN], cap, 0);
	                    }
	                }
	                break;
	            case KNIGHT:
	                pieceAttacks = att.knight[from];
	                break;
	            case BISHOP:   //fall through
	            case ROOK:     //fall through
	            case QUEEN:
	                if (Util.bool(mover & ROOK_OR_QUEEN)) {
	                    pieceAttacks |= RookAttacks (g, from);
	                }
	                if (Util.bool(mover & BISHOP_OR_QUEEN)) {
	                    pieceAttacks |= BishopAttacks (g, from);
	                }
	                break;
	            case KING:
	                pieceAttacks = att.king[from];
	                break;
	            }

	            //Add or update which squares are currently attacked
	            //Following line doesn't do anythign right now!
	            g.attacked[depth] |= pieceAttacks;

	            attackedPieces = pieceAttacks & g.pos.getOpponentPiecesExceptKing(side);
	            while (morePieces(attackedPieces)) {
	                to = FirstPiece (attackedPieces);
	                cap = Math.abs(g.pos.getBoard(to));
	                if (!Util.bool(pro)) {     //Capture only
	                	//TODO: make sure king does not move into check!!!!!
	                	move = EncodeMove(from, to, PIECE[p], cap, 0);  
	                	//if((piece)p == KING && !isAttacked(g, side, to)){
	                	if(p == KING && isLegal(g, move, side)){
	                		moves[n++] = move;
	                	} else {
	                		moves[n++] = move;
	                	}
	                } else {        //Capture and promotion
	                	for (int i = QUEEN; i >= KNIGHT; i--) {
	                		moves[n++] = EncodeMove (from, to, PIECE[p], cap, PIECE[i]);
	                	}
	                }
	                attackedPieces = ClearPiece (attackedPieces, to);
	            }
	            pieces = ClearPiece (pieces, from);
	        }
	    }
	    g.numberOfLegalMoves[depth] = n;
	    return g.numberOfLegalMoves[depth];
	}

}
