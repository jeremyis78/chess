package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

public class CaptureGenerator extends AbstractGenerator {

	@Override
	public int generate(int[] moves, int side, int depth) {
	    // This includes pawns that capture to promote to a Q,R,B,N

	    int to = 0;
	    int from = 0;
	    int cap = 0;
	    int pro = 0;
	    int n;       //move index counter
	    int mover;
	    long pieces;
	    long pieceAttacks = 0;    //must be zeroed
	    long attackedPieces;      //as in "the enemy pieces that are attacked"

	    n = g.numberOfLegalMoves[depth];

	    for (int p = PAWN; p <= KING; p++) {
	        mover = PIECE[p];
	        Position position = g.getPosition();
			pieces = position.getPieces (side, p);
			Piece piece = PieceFactory.fromBoardPiece((side==0?1:-1)*mover);
	        while (morePieces(pieces)) {
	            from = lowestBitNumber(pieces);
	            pro = 0;
	            pieceAttacks = 0;
	            switch (p) {
	            case PAWN:
	                pieceAttacks = piece.attacks(from, position);
	                pro = isPawnPromotion(side, from);

	                //EnPassant captures
					if (g.hasEnPassantOption())
	                {
						int enPassantSquare = g.getEnPassantSquare();
	                    if (Util.bool(pieceAttacks & (1L << enPassantSquare)))
	                    {
	                        to = enPassantSquare;
	                        cap = PIECE[PAWN];
	                        moves[n++] = EncodeMove (from, to, PIECE[PAWN], cap, 0);
	                    }
	                }
	                break;
	            case KNIGHT:   //fall through
	            case BISHOP:   //fall through
	            case ROOK:     //fall through
	            case QUEEN:   //fall through
	            case KING:
	                pieceAttacks = piece.advances(from, position);
	                break;
	            }

	            attackedPieces = pieceAttacks & position.getOpponentPiecesExceptKing(side);
	            while (morePieces(attackedPieces)) {
	                to = lowestBitNumber(attackedPieces);
	                cap = Math.abs(position.getBoard(to));
	                if (!Util.bool(pro)) {     //Capture only
	                	//TODO: make sure king does not move into check!!!!!
	                	int move = EncodeMove(from, to, PIECE[p], cap, 0);  
	                	//if((piece)p == KING && !isAttacked(g, side, to)){
	                	if(p == KING){
	                		if(isLegal(g, move, side)){
	                			moves[n++] = move;
	                		}
	                	} else {
	                		moves[n++] = move;
	                	}
	                } else {        //Capture and promotion
	                	int move = EncodeMove (from, to, PIECE[p], cap, PIECE[QUEEN]);
                		if(isLegal(g, move, side)) //can't move if pinned
                		{
                			moves[n++] = move;
                			//If the queen promotion is legal the other promotion choices will be too
                			for (int i = ROOK; i >= KNIGHT; i--) {
    	                		move = EncodeMove (from, to, PIECE[p], cap, PIECE[i]);
                    			moves[n++] = move;
    	                	}
                		}
	                }
	                attackedPieces = clearBit(attackedPieces, to);
	            }
	            pieces = clearBit(pieces, from);
	        }
	    }
	    g.numberOfLegalMoves[depth] = n;
	    return g.numberOfLegalMoves[depth];
	}

}
