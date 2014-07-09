package com.jeremybrooks.chess.util;

import static com.jeremybrooks.chess.base.Bitmap.*;

/**
 * An iterator defining the squares within the diagonals
 * that are read (from left to right) from a chess board
 * rotated 45 degrees right.  Square H1 (7) at the bottom, 
 * A8 (56) at the top. The main (longest) diagonal goes from
 * A1 (0) to H8 (63).
 * 
 * Diagonal #
 *                      /\
 *    14               /56\
 *                    /----\
 *    13             /48  57\
 *                  /--------\
 *    12           / 40 49 58 \
 *    .           /------------\
 *    .           Middle of  the
 *    7     a1, b2, c3, d4, e5, f6, g7, h8  (main diagonal)
 *    .          board  goes here
 *    .          \---------------/
 *    3           \4  13  22  31/
 *                 \-----------/
 *    2             \5  14  23/
 *                   \-------/
 *    1               \6  15/
 *                     \---/
 *    0                 \7/
 *                       -
 */
public class RightDiagonalIterator extends DiagonalIterator {

	private static final int START_SQUARE[] = {H1,G1,F1,E1,D1,C1,B1,A1,A2,A3,A4,A5,A6,A7,A8};
	
	public RightDiagonalIterator(int diagonalIndex) {
		super(diagonalIndex);
	}

	@Override
	public int nextSquareOffset() {
		return 9;
	}

	@Override
	public int startSquare() {
		return START_SQUARE[diagonal];
	}

}
