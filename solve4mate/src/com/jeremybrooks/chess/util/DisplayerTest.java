package com.jeremybrooks.chess.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.FenParser;
import com.jeremybrooks.chess.base.Position;

public class DisplayerTest {

    Displayer displayer;
    
    @Before
    public void init() {
        displayer = new Displayer();
    }

    @Test
    public void testStartingPosition()
    {
        String initialBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        Position p = FenParser.parsePieceBoard(initialBoard);
        String expectedBoard = 
                        "   -----------------\n" +
                        "8 | r n b q k b n r |\n" +
                        "7 | p p p p p p p p |\n" +
                        "6 | - - - - - - - - |\n" +
                        "5 | - - - - - - - - |\n" +
                        "4 | - - - - - - - - |\n" +
                        "3 | - - - - - - - - |\n" +
                        "2 | P P P P P P P P |\n" +
                        "1 | R N B Q K B N R |\n" +
                        "   -----------------\n" +
                        "    a b c d e f g h\n";
        assertEquals(expectedBoard, displayer.formatBoard(p));
    }

    @Test
    public void testQueensGambitAccepted()
    {
        String pieceBoard = "rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR";
        Position p = FenParser.parsePieceBoard(pieceBoard);

        String expectedBoard = 
                "   -----------------\n" +
                "8 | r n b q k b n r |\n" +
                "7 | p p p - p p p p |\n" +
                "6 | - - - - - - - - |\n" +
                "5 | - - - P - - - - |\n" +
                "4 | - - - - - - - - |\n" +
                "3 | - - - - - - - - |\n" +
                "2 | P P P P - P P P |\n" +
                "1 | R N B Q K B N R |\n" +
                "   -----------------\n" +
                "    a b c d e f g h\n";
        Assert.assertEquals(expectedBoard, displayer.formatBoard(p));
    }
    
    @Test
    public void testEndgamePosition()
    {
        String pieceBoard = "q1n5/1P3p2/2P5/8/1K6/5b2/k6P/7R";
        Position position = FenParser.parsePieceBoard(pieceBoard);

        String expectedBoard = 
                "   -----------------\n" +
                "8 | q - n - - - - - |\n" +
                "7 | - P - - - p - - |\n" +
                "6 | - - P - - - - - |\n" +
                "5 | - - - - - - - - |\n" +
                "4 | - K - - - - - - |\n" +
                "3 | - - - - - b - - |\n" +
                "2 | k - - - - - - P |\n" +
                "1 | - - - - - - - R |\n" +
                "   -----------------\n" +
                "    a b c d e f g h\n";
        Assert.assertEquals(expectedBoard, displayer.formatBoard(position));
    }
}
