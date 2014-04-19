package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.ALL;
import static com.jeremybrooks.chess.Bitmap.BISHOP;
import static com.jeremybrooks.chess.Bitmap.C1;
import static com.jeremybrooks.chess.Bitmap.C8;
import static com.jeremybrooks.chess.Bitmap.E1;
import static com.jeremybrooks.chess.Bitmap.E8;
import static com.jeremybrooks.chess.Bitmap.EIGHTHRANK;
import static com.jeremybrooks.chess.Bitmap.FIRSTRANK;
import static com.jeremybrooks.chess.Bitmap.G1;
import static com.jeremybrooks.chess.Bitmap.G8;
import static com.jeremybrooks.chess.Bitmap.KING;
import static com.jeremybrooks.chess.Bitmap.KNIGHT;
import static com.jeremybrooks.chess.Bitmap.PAWN;
import static com.jeremybrooks.chess.Bitmap.PIECE;
import static com.jeremybrooks.chess.Bitmap.QUEEN;
import static com.jeremybrooks.chess.Bitmap.ROOK;
import static com.jeremybrooks.chess.Bitmap.SECONDRANK;
import static com.jeremybrooks.chess.Bitmap.SEVENTHRANK;

public class NonCaptureGenerator extends MoveGenerator implements Generator {

	@Override
	public int generate(GameState g, int[] moves, int side, int depth) {
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
	    empty = ~g.pos.getAllPieces(0);

	    switch (side) {
	        case Bitmap.WHITE:
	        	long whitePawns = g.pos.getPawns(Bitmap.WHITE);
	            pMoves = (whitePawns << 8) & empty & ~EIGHTHRANK;
	            // 'pMoves' is all moves except those to the eighth rank
	            promoters = (whitePawns << 8) & empty & EIGHTHRANK;
	            // 'promoters' is only the moves to the eighth rank
	            advanceTwo = whitePawns & SECONDRANK;
	            advanceTwo = (advanceTwo << 8) & empty;
	            advanceTwo = (advanceTwo << 8) & empty;
	            break;
	        case Bitmap.BLACK:
	        	long blackPawns = g.pos.getPawns(Bitmap.BLACK);
	            pMoves = (blackPawns >> 8) & empty & ~FIRSTRANK;
	            // 'pMoves' is all moves except those to the first rank
	            promoters = (blackPawns >> 8) & empty & FIRSTRANK;
	            // 'promoters' is only the moves to the first rank
	            advanceTwo = blackPawns & SEVENTHRANK;
	            advanceTwo = (advanceTwo >> 8) & empty;
	            advanceTwo = (advanceTwo >> 8) & empty;
	            break;
	    }
	    // Pawn promotions
	    while (morePieces(promoters)) {
	        to = FirstPiece (promoters);
	        from = minusOneRank(side, to);
	        for (int i = QUEEN; i >= KNIGHT; i--) {
	            moves[n++] = EncodeMove (from, to, PIECE[PAWN], 0, PIECE[i]);
	            //g.legalMoves[depth]++;
	            //g.addMove (move);
	        }
	        promoters = ClearPiece(promoters, to);
	    }
	    // Pawns advance two squares
	    while (morePieces(advanceTwo)) {
	        to = FirstPiece (advanceTwo);
	        from = minusTwoRank(side, to);
	        int move = EncodeMove (from, to, PIECE[PAWN], 0, 0);
	        if(isLegal(g, move, side)) //can't move if pinned
	        {
	        	moves[n++] = move;
	        }
	        advanceTwo = ClearPiece(advanceTwo, to);
	    }
	    // Pawns advance one square
	    while (morePieces(pMoves)) {
	        to = FirstPiece (pMoves);
	        from = minusOneRank(side, to);
	        int move = EncodeMove (from, to, PIECE[PAWN], 0, 0);
	        if(isLegal(g, move, side)) //can't move if pinned
	        {
	        	moves[n++] = move;
	        }
	        pMoves = ClearPiece (pMoves, to);
	    }

	    //***************************************************************************
	    //*                                                                         *
	    //* Add all non-capturing knight, bishop, rook, queen, king moves           *
	    //*                                                                         *
	    //***************************************************************************

	    for (int p = KNIGHT; p <= KING; p++) {
	        pieces = g.pos.getPieces (side, p);
	        while (morePieces(pieces)) {
	            from = FirstPiece (pieces);
	            switch (p) {
	                case KNIGHT:
	                    pMoves = att.knight[from] & empty;
	                    break;
	                case BISHOP:   //fall through
	                case ROOK:     //fall through
	                case QUEEN:
	                    if (Util.bool(PIECE[p] & BISHOP_OR_QUEEN))
	                    {
	                        pMoves |= BishopAttacks (g, from) & empty;
	                    }
	                    if (Util.bool(PIECE[p] & ROOK_OR_QUEEN))
	                    {
	                        pMoves |= RookAttacks (g, from) & empty;
	                    }
	                    break;
	                case KING:
	                	//exclude moves that are attacked by opponent's king 
	                    pMoves = att.king[from] & empty & ~att.king[g.pos.getKingSquare(Util.opp(side))];
	                    break;
	            }
	            while (morePieces(pMoves)) {
	                to = FirstPiece (pMoves);
	                int move = EncodeMove(from,to,PIECE[p],0,0);
	                if (p == KING){
	                	if (!isAttacked(g, side, to)){
	                    //if(isLegal(g, move, side)){
	                        moves[n++] = move;
	                    }
	                } else {
	                    moves[n++] = move;
	                }
	                pMoves = ClearPiece (pMoves, to);
	            }
	            pieces = ClearPiece (pieces, from);
	        }
	    }

	    //***************************************************************************
	    //*                                                                         *
	    //* Add castling moves, if any                                              *
	    //*                                                                         *
	    //***************************************************************************
	    switch (side) {
	    case Bitmap.WHITE:
	        if(canWhiteShortCastle(g, side, depth)){
	            moves[n++] = EncodeMove(E1,G1,PIECE[KING],0,0);
	        }
	        if(canWhiteLongCastle(g, side, depth)){
	            moves[n++] = EncodeMove(E1,C1,PIECE[KING],0,0);
	        }
	        break;
	    case Bitmap.BLACK:
	        if(canBlackShortCastle(g, side, depth)){
	            moves[n++] = EncodeMove(E8,G8,PIECE[KING],0,0);
	        }
	        if(canBlackLongCastle(g, side, depth)){
	            moves[n++] = EncodeMove(E8,C8,PIECE[KING],0,0);
	        }
	        break;
	    }

	    g.numberOfLegalMoves[depth] = n;
	    return g.numberOfLegalMoves[depth];
	}

}
