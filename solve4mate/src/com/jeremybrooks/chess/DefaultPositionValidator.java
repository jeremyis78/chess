package com.jeremybrooks.chess;

/**
 * Validates that both kings are present and not adjacent to each other.
 * @author jeremy
 *
 */
public class DefaultPositionValidator implements PositionValidator {

	@Override
	public void validateOrThrow(Position position) {

		if(eitherKingIsMissing(position) )
		{
			throw new IllegalArgumentException("board is missing one or both kings");
		}
	    if(areKingsAdjacent(position)){
	        throw new IllegalArgumentException("board cannot have adjacent kings");
	    }
	}

	private static boolean areKingsAdjacent(Position position) {
		return Util.adjacentSquares(position.getWhiteKingSquare(),
				position.getBlackKingSquare());
	}

	private static boolean eitherKingIsMissing(Position position) {
		boolean isWhiteKingMissing = !position.isKingPlaced(Color.WHITE);
		boolean isBlackKingMissing = !position.isKingPlaced(Color.BLACK);
		return isWhiteKingMissing || isBlackKingMissing;
	}
}
