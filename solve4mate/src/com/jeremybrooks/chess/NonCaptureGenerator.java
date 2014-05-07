package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

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
	    Position position = g.getPosition();
		long allPiecesByRank = position.getAllPieces(0);
		empty = ~allPiecesByRank;

	    switch (side) {
	        case Bitmap.WHITE:
	        	long whitePawns = position.getPawns(Bitmap.WHITE);
	            pMoves = (whitePawns << 8) & empty & ~EIGHTHRANK;
	            // 'pMoves' is all moves except those to the eighth rank
	            promoters = (whitePawns << 8) & empty & EIGHTHRANK;
	            // 'promoters' is only the moves to the eighth rank
	            advanceTwo = whitePawns & SECONDRANK;
	            advanceTwo = (advanceTwo << 8) & empty;
	            advanceTwo = (advanceTwo << 8) & empty;
	            break;
	        case Bitmap.BLACK:
	        	long blackPawns = position.getPawns(Bitmap.BLACK);
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
	            pMoves = piece.advances(from, position) & empty;
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
