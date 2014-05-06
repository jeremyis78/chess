package com.jeremybrooks.chess;

import com.jeremybrooks.chess.Piece.Color;

public abstract class SlidingPiece extends Piece {

	public SlidingPiece(Color color, int pieceIndex, char displayCharacter)
	{
		super(color, pieceIndex, displayCharacter);
	}
	
	@Override 
	public long advances(int fromSquare, Position position)
	{
		long advances = 0;
        if (slidesOnDiagonals())
        {
            long allPieces45Left = position.getAllPieces(-45);
			long allPieces45Right = position.getAllPieces(45);
			advances |= MoveGenerator.bishopAttacks(fromSquare, allPieces45Left, allPieces45Right);
        }
        if (slidesLaterally())
        {
        	long allPiecesByRank = position.getAllPieces(0);
            long allPiecesByFile = position.getAllPieces(90);
			advances |= MoveGenerator.rookAttacks(fromSquare, allPiecesByRank, allPiecesByFile);
        }
        return advances;
	}

	private boolean slidesLaterally() {
		return Util.bool(encoded() & MoveGenerator.ROOK_OR_QUEEN);
	}

	private boolean slidesOnDiagonals() {
		return Util.bool(encoded() & MoveGenerator.BISHOP_OR_QUEEN);
	}
}