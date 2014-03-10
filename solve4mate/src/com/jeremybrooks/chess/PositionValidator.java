/**
 * 
 */
package com.jeremybrooks.chess;

/**
 * Implementations will determine whether the position
 * is valid for chess or some variant of chess.
 * 
 * @author jeremy
 *
 */
public interface PositionValidator {

	public void validateOrThrow(Position position);
}
