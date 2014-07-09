package com.jeremybrooks.chess.util;

import static com.jeremybrooks.chess.base.Bitmap.*;

import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.base.PositionValidator;
import com.jeremybrooks.chess.base.Square;

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
	    if(areKingsAdjacent(position))
	    {
	        throw new IllegalArgumentException("board cannot have adjacent kings");
	    }
	}

	private static boolean areKingsAdjacent(Position position) {
		return Square.adjacentSquares(position.getKingSquare(WHITE), position.getKingSquare(BLACK));
	}

	private static boolean eitherKingIsMissing(Position position) {
		boolean isWhiteKingMissing = !position.isKingPlaced(WHITE);
		boolean isBlackKingMissing = !position.isKingPlaced(BLACK);
		return isWhiteKingMissing || isBlackKingMissing;
	}
}
