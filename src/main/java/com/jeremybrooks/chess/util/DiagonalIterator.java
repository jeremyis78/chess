package com.jeremybrooks.chess.util;

import java.util.Iterator;

/**
 * <p>Implementors will define how to iterate over the squares found
 * within a diagonal on the chess board.
 * 
 * <p>Thirty diagonals are found on the board.  Half of the digaonals
 * are associated with diagonals found on a bitboard rotated 45
 * degrees left and the other half on a bitboard rotated 45 degrees
 * right. Implementors of this class will define those left and
 * right iterators by implementing {@code startSquare} and
 * {@code nextSquareOffset}
 * 
 * <p>For example to iterate over the squares (that is, the bit indexes) of a bitboard's
 * third diagonal (zero-indexed) you would do the following:
 * 
 * <pre><code>
 * DiagonalIterator di =  new ConcreteImplementation(2)
 * while(di.hasNext())
 * {
 *      int currentIndex  = di.nextIndex(); // value is within [0..lengthOfDiagonal-1]
 *      int currentSquare = di.next();     // value is within [0..63]
 *      //use currentSquare to access that bit within the bitboard
 *      //for proper usage of nextIndex() the call must occur BEFORE the call to next();
 * }
 * </code>
 * </pre>
 * 
 * @author jeremy
 *
 */
/**
 * @author jeremy
 *
 */
public abstract class DiagonalIterator implements Iterator<Integer>
{
    private static final int LENGTH[] = {1,2,3,4,5,6,7,8,7,6,5,4,3,2,1};
    protected int diagonal;  //identifies the diagonal number (zero-based, [0..14])
    protected int index;     //identifies the index into the diagonal (zero-based, [0..diagLen-1])

    public DiagonalIterator(int diagonalIndex)
    {
        if(diagonalIndex > LENGTH.length)
            throw new IllegalArgumentException("index '"+diagonalIndex+"' must be in range 0-14");
        diagonal = diagonalIndex;
        index = 0;
    }

    public int diagonalLength()
    {
        return LENGTH[diagonal];
    }

    @Override
    public boolean hasNext() {
        return index < diagonalLength();
    }

    @Override
    public Integer next() {
        return new Integer(startSquare() + (numberOfNextSquares() * nextSquareOffset())); //G34 and/or G6
    }
    
    /**
     * Gets the internal zero-based index within the diagonal (NOT the value of next()!), where
     * the first index in a diagonal is zero, the next is one, and so forth until, the length of
     * the diagonal-1 is reached.
     * When next() is called this index will determine how many offsets by which to multiply
     * to get the correct next square.
     * @see #next()
     */
    public int nextIndex() {
        return index;
    }

    private int numberOfNextSquares() {  //G20
        return index++;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the square (the index) of the first square on this diagonal
     */
    public abstract int startSquare();
    
    /**
     * Gets the offset to proceed to the next square in the diagonal
     */
    public abstract int nextSquareOffset();
    
}
