package com.jeremybrooks.chess;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.util.OutputBuilder;

public class UciDriverTest {

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

    @Test
    public void givenPositionStartPosAndMovesIncludesCastling() throws Exception {
        
        /*
         * Fischer-Myagmarsuren, Sousse Interzonal 1967
         * (King's Indian Attack)
         * 
         * 1. e4 e6
         * 2. d3 d5
         * 3. Nd2 Nf6
         * 4. g3 c5
         * 5. Bg2 Nc6
         * 6. Ngf3 Be7
         * 7. 0-0 0-0
         */
        String input = "position startpos";
        input       += " moves e2e4 e7e6 d2d3 d7d5 b1d2 g8f6 g2g3 c7c5 f1g2 b8c6 g1f3 f8e7 e1g1 e8g8";
        driver.execute(input);
        assertNoOutputOrErr();
        driver.execute("diagram");
        OutputBuilder expected = new OutputBuilder();
        expected.append("   -----------------");
        expected.append("8 | r - b q - r k - |");
        expected.append("7 | p p - - b p p p |");
        expected.append("6 | - - n - p n - - |");
        expected.append("5 | - - p p - - - - |");
        expected.append("4 | - - - - P - - - |");
        expected.append("3 | - - - P - N P - |");
        expected.append("2 | P P P N - P B P |");
        expected.append("1 | R - B Q - R K - |");
        expected.append("   -----------------");
        expected.append("    a b c d e f g h");
        expected.append("");
        Assert.assertEquals(expected.toString(),out.toString());
    }

    @Test
    public void givenPositionFenAndMovesIncludesCaptures() throws Exception {
        
        /*
         * Fischer-Myagmarsuren, Sousse Interzonal 1967
         * (King's Indian Attack)
         * 
         * 1. e4 e6
         * 2. d3 d5
         * 3. Nd2 Nf6
         * 4. g3 c5
         * 5. Bg2 Nc6
         * 6. Ngf3 Be7
         * 7. 0-0 0-0 (initial fen)
         * 
         * 8.  e5 Nd7-
         * 9.  Re1 b5-
         * 10. Nf1 b4-
         * 11. h4 a5
         * 12. Bf4 a4
         * 13. a3 bxa3
         * 14. bxa3 Na5
         * 
         */
        String input = "position fen r1bq1rk1/pp2bppp/2n1pn2/2pp4/4P3/3P1NP1/PPPN1PBP/R1BQ1RK1 w - - 0 8";
        input       += " moves e4e5 f6d7 f1e1 b7b5 d2f1 b5b4 h2h4 a7a5 c1f4 a5a4 a2a3 b4a3 b2a3 c6a5";
        driver.execute(input);
        assertNoOutputOrErr();
        driver.execute("diagram");
        OutputBuilder expected = new OutputBuilder();
        expected.append("   -----------------");
        expected.append("8 | r - b q - r k - |");
        expected.append("7 | - - - n b p p p |");
        expected.append("6 | - - - - p - - - |");
        expected.append("5 | n - p p P - - - |");
        expected.append("4 | p - - - - B - P |");
        expected.append("3 | P - - P - N P - |");
        expected.append("2 | - - P - - P B - |");
        expected.append("1 | R - - Q R N K - |");
        expected.append("   -----------------");
        expected.append("    a b c d e f g h");
        expected.append("");
        Assert.assertEquals(expected.toString(),out.toString());
    }

    @Test
    public void givenPositionInvalidToken() throws Exception {
        String input = "position invalid";
        try 
        {
            driver.execute(input);
            fail("an invalid token should throw");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            //assertEquals("must be good", e.getMessage());
        }
    }

    
    
    public void testGo() throws Exception {
        fail("fix this so it passes without having to know the specific numbers for time, nodes, etc");
        String input = "position startpos";
        driver.execute(input);
        assertNoOutputOrErr();
        driver.execute("go wtime 1000 winc 0 btime 1000 binc 0 depth 4");
        OutputBuilder expected = new OutputBuilder();
        expected.append("info depth \\d+ time \\d+ nodes \\d+ nps \\d+ pv .*");
        expected.append("info bestmove g2g3");
        expected.append("");
        //Assert.assertEquals(expected.toString(),out.toString());
        Assert.assertTrue(out.toString().matches(expected.toString()));
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
