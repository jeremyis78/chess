package com.jeremybrooks.chess.util;

import static org.junit.Assert.*;

import org.junit.Test;


public class OutputBuilderTest {

    @Test
    public void givenNewInstance() throws Exception {
        OutputBuilder builder = new OutputBuilder();
        assertEquals("", builder.toString());
    }
    
    @Test
    public void givenMultipleAppendCalls() throws Exception {
        OutputBuilder builder = new OutputBuilder();
        builder.append("position startpos");
        builder.append("go mate moves 30");
        String expected = 
                "position startpos" + System.lineSeparator() + 
                "go mate moves 30"  + System.lineSeparator();
        assertEquals(expected, builder.toString());
    }

}
