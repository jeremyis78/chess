package com.jeremybrooks.chess;

import com.jeremybrooks.chess.Piece.Color;

public abstract class SlidingPiece extends Piece {

	public SlidingPiece(Color color, int pieceIndex, char displayCharacter)
	{
		super(color, pieceIndex, displayCharacter);
	}
	
	@Override 
	public long nonCaptures(int fromSquare, Position position)
	{
		long allPiecesByRank = position.getAllPieces(0);
		long emptySquares = ~allPiecesByRank;

		long pMoves = 0;
        if (slidesOnDiagonals())
        {
            long allPieces45Left = position.getAllPieces(-45);
			long allPieces45Right = position.getAllPieces(45);
			pMoves |= MoveGenerator.bishopAttacks(fromSquare, allPieces45Left, allPieces45Right) & emptySquares;
        }
        if (slidesLaterally())
        {
            long allPiecesByFile = position.getAllPieces(90);
			pMoves |= MoveGenerator.rookAttacks(fromSquare, allPiecesByRank, allPiecesByFile) & emptySquares;
        }
        return pMoves;
	}

	private boolean slidesLaterally() {
		return Util.bool(encoded() & MoveGenerator.ROOK_OR_QUEEN);
	}

	private boolean slidesOnDiagonals() {
		return Util.bool(encoded() & MoveGenerator.BISHOP_OR_QUEEN);
	}
}