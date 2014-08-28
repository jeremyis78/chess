package com.jeremybrooks.chess;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jeremybrooks.chess.util.OutputBuilder;

public class UciDriverTest {

    private static PrintStream oldOut = System.out;
    private static PrintStream oldErr = System.err;
    private static final String NO_OUTPUT = "";
    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    
    private UciDriver driver;
    
    @Before
    public void setUp()
    {
        out = new ByteArrayOutputStream();
        err = new ByteArrayOutputStream();
        driver = new UciDriver(System.in, new PrintStream(out), new PrintStream(err));
    }

    @Test
    public void givenUci() throws Exception {
        String input = "uci";
        driver.execute(input);

        OutputBuilder expected = new OutputBuilder();
        expected.append("id name Breaker 0.1 by Jeremy Brooks (c) 2004-2014");
        expected.append("uciok");
        assertOutputAndNoErr(expected);
    }

    @Test
    public void givenIsReady() throws Exception {
        String input = "isready";
        driver.execute(input);
        
        OutputBuilder expected = new OutputBuilder();
        expected.append("readyok");
        assertOutputAndNoErr(expected);
    }
    
    @Test
    public void givenPositionStartPos() throws Exception {
        String input = "position startpos";
        driver.execute(input);
        assertNoOutputOrErr();
        driver.execute("diagram");
        OutputBuilder expected = new OutputBuilder();
        expected.append("   -----------------");
        expected.append("8 | r n b q k b n r |");
        expected.append("7 | p p p p p p p p |");
        expected.append("6 | - - - - - - - - |");
        expected.append("5 | - - - - - - - - |");
        expected.append("4 | - - - - - - - - |");
        expected.append("3 | - - - - - - - - |");
        expected.append("2 | P P P P P P P P |");
        expected.append("1 | R N B Q K B N R |");
        expected.append("   -----------------");
        expected.append("    a b c d e f g h");
        expected.append("");
        Assert.assertEquals(expected.toString(),out.toString());
    }

    
    public void testStartSearch() {
        fail("Not yet implemented");
    }

    
    public void testEncodeMove() {
        fail("Not yet implemented");
    }

    
    public void testRespond() {
        fail("Not yet implemented");
    }

    
    public void testSendResponse() {
        fail("Not yet implemented");
    }

    public void assertOutputAndNoErr(OutputBuilder expected) {
        Assert.assertEquals(expected.toString(),out.toString());
        Assert.assertEquals(NO_OUTPUT, err.toString());
    }

    public void assertNoOutputOrErr() {
        Assert.assertEquals(NO_OUTPUT, out.toString());
        Assert.assertEquals(NO_OUTPUT, err.toString());
    }

}
