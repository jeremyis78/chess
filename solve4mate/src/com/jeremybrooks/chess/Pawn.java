package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

public class Pawn extends Piece {
	
	public Pawn(Color color){ 
		super(color, PAWN, 'P');
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public long advances(int fromSquare, Position position) {
		int side = (color==Color.W?0:1);
		long allPiecesByRank = position.getAllPieces(0);
		long emptySquares = ~allPiecesByRank;

		long advanceOneButIsNotPromoter = 0;
		long promoters = 0;
		long advanceTwo = 0;
		
		if (side == Bitmap.WHITE) {
			long whitePawns = position.getPawns(Bitmap.WHITE);
			advanceOneButIsNotPromoter = (whitePawns << 8) & emptySquares & ~EIGHTHRANK;
			// 'pMoves' is all moves except those to the eighth rank
			promoters = (whitePawns << 8) & emptySquares & EIGHTHRANK;
			// 'promoters' is only the moves to the eighth rank
			advanceTwo = whitePawns & SECONDRANK;
			advanceTwo = (advanceTwo << 8) & emptySquares;
			advanceTwo = (advanceTwo << 8) & emptySquares;
		} else {
			long blackPawns = position.getPawns(Bitmap.BLACK);
			advanceOneButIsNotPromoter = (blackPawns >> 8) & emptySquares & ~FIRSTRANK;
			// 'pMoves' is all moves except those to the first rank
			promoters = (blackPawns >> 8) & emptySquares & FIRSTRANK;
			// 'promoters' is only the moves to the first rank
			advanceTwo = blackPawns & SEVENTHRANK;
			advanceTwo = (advanceTwo >> 8) & emptySquares;
			advanceTwo = (advanceTwo >> 8) & emptySquares;
		}
		
		// bitwise-OR them all together for now; we'll take care of the special cases in MoveGenerator
		return advanceOneButIsNotPromoter | advanceTwo | promoters;
	}

	public long pushes(Position position) {
		int side = (color==Color.W?0:1);
		long allPiecesByRank = position.getAllPieces(0);
		long emptySquares = ~allPiecesByRank;
		long pawnsMoveOneExcludingPromotions = 0;
		if (side == Bitmap.WHITE) {
			long whitePawns = position.getPawns(Bitmap.WHITE);
			pawnsMoveOneExcludingPromotions = (whitePawns << 8) & emptySquares & ~EIGHTHRANK;
		} else {
			long blackPawns = position.getPawns(Bitmap.BLACK);
			pawnsMoveOneExcludingPromotions = (blackPawns >> 8) & emptySquares & ~FIRSTRANK;
		}
		return pawnsMoveOneExcludingPromotions;
	}

	public long pushesTwo(Position position) {
		int side = (color==Color.W?0:1);
		long allPiecesByRank = position.getAllPieces(0);
		long emptySquares = ~allPiecesByRank;
		long pawnsMoveTwo = 0;
		if (side == Bitmap.WHITE) {
			long whitePawns = position.getPawns(Bitmap.WHITE);
			pawnsMoveTwo = whitePawns & SECONDRANK;
			pawnsMoveTwo = (pawnsMoveTwo << 8) & emptySquares;
			pawnsMoveTwo = (pawnsMoveTwo << 8) & emptySquares;
		} else {
			long blackPawns = position.getPawns(Bitmap.BLACK);
			pawnsMoveTwo = blackPawns & SEVENTHRANK;
			pawnsMoveTwo = (pawnsMoveTwo >> 8) & emptySquares;
			pawnsMoveTwo = (pawnsMoveTwo >> 8) & emptySquares;
		}
		return pawnsMoveTwo;
	}

	public long promotions(Position position) {
		int side = (color==Color.W?0:1);
		long allPiecesByRank = position.getAllPieces(0);
		long emptySquares = ~allPiecesByRank;
		long pawnPromotions = 0;
		if (side == Bitmap.WHITE) {
			long whitePawns = position.getPawns(Bitmap.WHITE);
			pawnPromotions = (whitePawns << 8) & emptySquares & EIGHTHRANK;
		} else {
			long blackPawns = position.getPawns(Bitmap.BLACK);
			pawnPromotions = (blackPawns >> 8) & emptySquares & FIRSTRANK;
		}
		return pawnPromotions;
	}

	@Override
	public long attacks(int fromSquare, Position position) {
		long attacks = 0L;
		int side = (color==Color.W?0:1);
		attacks = AbstractGenerator.att.pawn[side][fromSquare];
		return attacks;
	}
	
}