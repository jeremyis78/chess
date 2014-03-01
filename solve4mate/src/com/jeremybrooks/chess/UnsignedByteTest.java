package com.jeremybrooks.chess;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnsignedByteTest {

	@Test
	public void testUnsignedByteClass(){
		UnsignedByte ub = new UnsignedByte(1);
		int i = ub.get() << 2;
		assertEquals(4, i);
	}
}
