package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import org.junit.Test;

public class RightDiagonalIteratorTest {
	private static final String EOL = "\n";
	private static final String DIAGONAL_SQUARES = 
			"h1" + EOL + 
			"g1 h2" + EOL + 
			"f1 g2 h3" + EOL + 
			"e1 f2 g3 h4" + EOL + 
			"d1 e2 f3 g4 h5" + EOL + 
			"c1 d2 e3 f4 g5 h6" + EOL + 
			"b1 c2 d3 e4 f5 g6 h7" + EOL + 
			"a1 b2 c3 d4 e5 f6 g7 h8" + EOL + 
			"a2 b3 c4 d5 e6 f7 g8" + EOL + 
			"a3 b4 c5 d6 e7 f8" + EOL + 
			"a4 b5 c6 d7 e8" + EOL + 
			"a5 b6 c7 d8" + EOL + 
			"a6 b7 c8" + EOL + 
			"a7 b8" + EOL + 
			"a8";
	
	@Test
	public void testInvalidConstructionWithAnInvalidDiagonalIndex()
	{
		int invalidDiagonalIndex = Bitmap.A8 + 1;
		try {
			@SuppressWarnings("unused")
			DiagonalIterator it = new RightDiagonalIterator(invalidDiagonalIndex);
		} catch (Exception e) {
			assertEquals("index '" + invalidDiagonalIndex + "' must be in range 0-14", e.getMessage());
		}
	}

	@Test
	public void testRemoveMethodIsUnsupported()
	{
		try {
			DiagonalIterator it = new RightDiagonalIterator(6);
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
			RightDiagonalIterator it = new RightDiagonalIterator(diagonal);
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
