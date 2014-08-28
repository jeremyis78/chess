package com.jeremybrooks.chess.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.Position;

public class BitboardDisplayerTest {

    private BitboardDisplayer bitboardDisplayer;

    @Before
    public void init() {
        bitboardDisplayer = new BitboardDisplayer();
    }

    @Test
    public void testStartingPosition()
    {
        
        String initialBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        Position p = FenParser.parsePieceBoard(initialBoard);
        OutputBuilder expected = new OutputBuilder();
        expected.append("   -----------------");
        expected.append("8 | r n b q - b n r |");
        expected.append("7 | p p p p p p p p |");
        expected.append("6 | - - - - - - - - |");
        expected.append("5 | - - - - - - - - |");
        expected.append("4 | - - - - - - - - |");
        expected.append("3 | - - - - - - - - |");
        expected.append("2 | P P P P P P P P |");
        expected.append("1 | R N B Q - B N R |");
        expected.append("   -----------------");
        expected.append("    a b c d e f g h");
        assertEquals(expected.toString(), bitboardDisplayer.formatBoard(p));
    }

    @Test
    public void testQueensGambitAccepted()
    {
        String board = "rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR";
        Position p = FenParser.parsePieceBoard(board);
        OutputBuilder expected = new OutputBuilder();
        expected.append("   -----------------");
        expected.append("8 | r n b q - b n r |");
        expected.append("7 | p p p - p p p p |");
        expected.append("6 | - - - - - - - - |");
        expected.append("5 | - - - P - - - - |");
        expected.append("4 | - - - - - - - - |");
        expected.append("3 | - - - - - - - - |");
        expected.append("2 | P P P P - P P P |");
        expected.append("1 | R N B Q - B N R |");
        expected.append("   -----------------");
        expected.append("    a b c d e f g h");
        Assert.assertEquals(expected.toString(), bitboardDisplayer.formatBoard(p));
    }

    @Test
    public void testEndgamePosition()
    {
        String board = "q1n5/1P3p2/2P5/8/1K6/5b2/k6P/7R";
        Position p = FenParser.parsePieceBoard(board);
        OutputBuilder expected = new OutputBuilder();
        expected.append("   -----------------");
        expected.append("8 | q - n - - - - - |");
        expected.append("7 | - P - - - p - - |");
        expected.append("6 | - - P - - - - - |");
        expected.append("5 | - - - - - - - - |");
        expected.append("4 | - - - - - - - - |");
        expected.append("3 | - - - - - b - - |");
        expected.append("2 | - - - - - - - P |");
        expected.append("1 | - - - - - - - R |");
        expected.append("   -----------------");
        expected.append("    a b c d e f g h");
        Assert.assertEquals(expected.toString(), bitboardDisplayer.formatBoard(p));
    }
}
