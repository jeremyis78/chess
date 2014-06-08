package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

import com.jeremybrooks.chess.Piece.Color;

public class NonCaptureGenerator extends AbstractGenerator {

	@Override
	public int  generate(int[] moves, int side, int depth) {
	    long pieces;
	    long pMoves = 0;
	    long advanceTwo = 0;
	    long promoters = 0;
	    long empty;
	    int n;
	    int to, from;

	    //***************************************************************************
	    //*                                                                         *
	    //* Add all non-capturing pawn moves (promotions, advance-two, advance-one) *
	    //*                                                                         *
	    //***************************************************************************

	    n = g.numberOfLegalMoves[depth];
	    Position position = g.getPosition();
		long allPiecesByRank = position.getAllPieces(0);
		empty = ~allPiecesByRank;

	    Pawn pawn = new Pawn(side==WHITE?Color.W:Color.B);
	    long advancesOne = pawn.advances(NOSQUARE, position);
	    pMoves = advancesOne & ~EIGHTHRANK;
	    advanceTwo = side==WHITE
	    		? ((advancesOne & THIRDRANK) << 8) & empty
	    		: ((advancesOne & SIXTHRANK) >> 8) & empty;
	    promoters = (side==WHITE
	    		? advancesOne & EIGHTHRANK
	    		: advancesOne & FIRSTRANK)
	    		& empty;
	    // Pawn promotions
	    while (morePieces(promoters)) {
	        to = lowestBitNumber(promoters);
	        from = Util.squareBehind(to, side);
	        for (int i = QUEEN; i >= KNIGHT; i--) {
	            moves[n++] = EncodeMove (from, to, PIECE[PAWN], 0, PIECE[i]);
	            //g.legalMoves[depth]++;
	            //g.addMove (move);
	        }
	        promoters = clearBit(promoters, to);
	    }
	    // Pawns advance two squares
	    while (morePieces(advanceTwo)) {
	        to = lowestBitNumber(advanceTwo);
	        from = Util.twoSquaresBehind(to, side);
	        int move = EncodeMove (from, to, PIECE[PAWN], 0, 0);
	        if(isLegal(g, move, side)) //can't move if pinned
	        {
	        	moves[n++] = move;
	        }
	        advanceTwo = clearBit(advanceTwo, to);
	    }
	    // Pawns advance one square
	    while (morePieces(pMoves)) {
	        to = lowestBitNumber(pMoves);
	        from = Util.squareBehind(to, side);
	        int move = EncodeMove (from, to, PIECE[PAWN], 0, 0);
	        if(isLegal(g, move, side)) //can't move if pinned
	        {
	        	moves[n++] = move;
	        }
	        pMoves = clearBit(pMoves, to);
	    }

	    //***************************************************************************
	    //*                                                                         *
	    //* Add all non-capturing knight, bishop, rook, queen, king moves           *
	    //*                                                                         *
	    //***************************************************************************

	    for (int p = KNIGHT; p <= KING; p++) {
	        pieces = position.getPieces (side, p);
	        Piece piece = PieceFactory.fromBoardPiece((side==0?1:-1)*PIECE[p]);
	        while (morePieces(pieces)) {
	            from = lowestBitNumber(pieces);
	            long advances = piece.advances(from, position);
				pMoves = advances & empty;
	            while (morePieces(pMoves)) {
	                to = lowestBitNumber(pMoves);
	                int move = EncodeMove(from,to,PIECE[p],0,0);
	                if (p == KING){
	                	if (!isAttacked(g, side, to)){
	                    //if(isLegal(g, move, side)){
	                        moves[n++] = move;
	                    }
	                } else {
	                    moves[n++] = move;
	                }
	                pMoves = clearBit(pMoves, to);
	            }
	            pieces = clearBit(pieces, from);
	        }
	    }

	    //***************************************************************************
	    //*                                                                         *
	    //* Add castling moves, if any                                              *
	    //*                                                                         *
	    //***************************************************************************
	    switch (side) {
	    case Bitmap.WHITE:
	        if(canWhiteShortCastle(g, side)){
	            moves[n++] = EncodeMove(E1,G1,PIECE[KING],0,0);
	        }
	        if(canWhiteLongCastle(g, side)){
	            moves[n++] = EncodeMove(E1,C1,PIECE[KING],0,0);
	        }
	        break;
	    case Bitmap.BLACK:
	        if(canBlackShortCastle(g, side)){
	            moves[n++] = EncodeMove(E8,G8,PIECE[KING],0,0);
	        }
	        if(canBlackLongCastle(g, side)){
	            moves[n++] = EncodeMove(E8,C8,PIECE[KING],0,0);
	        }
	        break;
	    }

	    g.numberOfLegalMoves[depth] = n;
	    return g.numberOfLegalMoves[depth];
	}

}
