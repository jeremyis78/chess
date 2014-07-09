package com.jeremybrooks.chess.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Square;

public class LeftDiagonalIteratorTest {
	private static final String EOL = "\n";
	private static final String DIAGONAL_SQUARES = 
			"a1" + EOL + 
			"b1 a2" + EOL + 
			"c1 b2 a3" + EOL + 
			"d1 c2 b3 a4" + EOL + 
			"e1 d2 c3 b4 a5" + EOL + 
			"f1 e2 d3 c4 b5 a6" + EOL + 
			"g1 f2 e3 d4 c5 b6 a7" + EOL + 
			"h1 g2 f3 e4 d5 c6 b7 a8" + EOL + 
			"h2 g3 f4 e5 d6 c7 b8" + EOL + 
			"h3 g4 f5 e6 d7 c8" + EOL + 
			"h4 g5 f6 e7 d8" + EOL + 
			"h5 g6 f7 e8" + EOL + 
			"h6 g7 f8" + EOL + 
			"h7 g8" + EOL + 
			"h8";
	
	@Test
	public void testInvalidConstructionWithAnInvalidDiagonalIndex()
	{
		int invalidDiagonalIndex = Bitmap.H8 + 1;
		try {
			@SuppressWarnings("unused")
			DiagonalIterator it = new LeftDiagonalIterator(invalidDiagonalIndex);
		} catch (Exception e) {
			assertEquals("index '" + invalidDiagonalIndex + "' must be in range 0-14", e.getMessage());
		}
	}
	
	@Test
	public void testRemoveMethodIsUnsupported()
	{
		try {
			DiagonalIterator it = new LeftDiagonalIterator(6);
			it.remove();
		} catch (UnsupportedOperationException expected) {
			//expected
		}
	}

	@Test
	public void testIteration() {
		StringBuilder sb = new StringBuilder();
		boolean notFirst = false;
		for(int diagonal=0; diagonal<15; diagonal++)
		{
			LeftDiagonalIterator it = new LeftDiagonalIterator(diagonal);
			if(notFirst) sb.append("\n");
			else notFirst = true;
			
			while(it.hasNext())
			{
				sb.append(Square.named(it.next()) + " ");
			}
			sb.deleteCharAt(sb.length()-1);
		}
		assertEquals(DIAGONAL_SQUARES, sb.toString().trim());
	}
}
