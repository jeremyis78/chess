package com.jeremybrooks.chess.movegen;

import static com.jeremybrooks.chess.base.Bitmap.*;

import java.util.List;

import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.PieceFactory;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.util.Util;

public class CaptureGenerator extends AbstractGenerator {

    @Override
    public void generate(List<Integer> moves, int side) {
        // This includes pawns that capture to promote to a Q,R,B,N

        int to = 0;
        int from = 0;
        int cap = 0;
        int pro = 0;
        int mover;
        long pieces;
        long pieceAttacks = 0;    //must be zeroed
        long attackedPieces;      //as in "the enemy pieces that are attacked"

        for (int p = Piece.PAWN; p <= Piece.KING; p++) {
            mover = Piece.ENCODED[p];
            Position position = g.getPosition();
            pieces = position.getPieces (side, p);
            Piece piece = PieceFactory.fromBoardPiece((side==0?1:-1)*mover);
            while (morePieces(pieces)) {
                from = lowestBitNumber(pieces);
                pro = 0;
                pieceAttacks = 0;
                switch (p) {
                case Piece.PAWN:
                    pieceAttacks = piece.attacks(from, position);
                    pro = isPawnPromotion(side, from);

                    //EnPassant captures
                    if (g.hasEnPassantOption())
                    {
                        int enPassantSquare = g.getEnPassantSquare();
                        if (Util.bool(pieceAttacks & (1L << enPassantSquare)))
                        {
                            to = enPassantSquare;
                            cap = Piece.ENCODED[Piece.PAWN];
                            int move = Util.EncodeMove (from, to, Piece.ENCODED[Piece.PAWN], cap, 0);
//                            if(isLegal(g, move))
                            {
                                moves.add(move);
                            }
                        }
                    }
                    break;
                case Piece.KNIGHT:   //fall through
                case Piece.BISHOP:   //fall through
                case Piece.ROOK:     //fall through
                case Piece.QUEEN:   //fall through
                case Piece.KING:
                    pieceAttacks = piece.advances(from, position);
                    break;
                }

                attackedPieces = pieceAttacks & position.getOpponentPiecesExceptKing(side);
                while (morePieces(attackedPieces)) {
                    to = lowestBitNumber(attackedPieces);
                    cap = Math.abs(position.getBoard(to));
                    if (Util.bool(pro)) {  //Capture and promotion
                    	int move = Util.EncodeMove (from, to, Piece.ENCODED[p], cap, Piece.ENCODED[Piece.QUEEN]);
                    	//                      if(isLegal(g, move)) //can't move if pinned
                    	{
                    		moves.add(move);
                    		//If the queen promotion is legal the other promotion choices will be too
                    		for (int i = Piece.ROOK; i >= Piece.KNIGHT; i--) {
                    			move = Util.EncodeMove (from, to, Piece.ENCODED[p], cap, Piece.ENCODED[i]);
                    			moves.add(move);
                    		}
                    	}
                    } else {     //Capture only
                    	//TODO: make sure king does not move into check!!!!!
                        int move = Util.EncodeMove(from, to, Piece.ENCODED[p], cap, 0);  
                        if(p != Piece.KING || (p == Piece.KING && isNotAttacked(to, g)))
                        {
                            moves.add(move);
                        }
                    }
                    attackedPieces = clearBit(attackedPieces, to);
                }
                pieces = clearBit(pieces, from);
            }
        }
    }

}
