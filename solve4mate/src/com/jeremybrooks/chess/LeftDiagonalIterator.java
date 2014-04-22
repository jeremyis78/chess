package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

/**
 * An iterator defining the order of squares within the diagonals
 * that are read (from left to right) from a chess board
 * rotated 45 degrees left.  Square A1 (0) is at the bottom, 
 * H8 (63) at the top. The main (longest) diagonal goes from
 * H1 (7) to A8 (56).
 * 
 * Diagonal #
 *                      /\
 *    14               /63\
 *                    /----\
 *    13             /55  62\
 *                  /--------\
 *    12           / 47 54 61 \
 *    .           /------------\
 *    .           Middle of  the
 *    7     h1, g2, f3, e4, d5, c6, b7, a8  (main diagonal)
 *    .          board  goes here
 *    .          \---------------/
 *    3           \3  10  17  24/
 *                 \-----------/
 *    2             \2   9  16/
 *                   \-------/
 *    1               \1  8 /
 *                     \---/
 *    0                 \0/
 *                       -
 */
public class LeftDiagonalIterator extends DiagonalIterator {

	private static final int START_SQUARE[] = {A1,B1,C1,D1,E1,F1,G1,H1,H2,H3,H4,H5,H6,H7,H8};

	public LeftDiagonalIterator(int diagonalIndex) {
		super(diagonalIndex);
	}

	@Override
	public int nextSquareOffset() {
		return 7;
	}

	@Override
	public int startSquare() {
		return START_SQUARE[diagonal];
	}

}
