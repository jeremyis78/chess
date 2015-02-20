package com.jeremybrooks.chess.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jeremybrooks.chess.base.Position;

public class DisplayerTest {

    Displayer displayer;
    
    @Test
    public void testStartingPosition()
    {
        String initialBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

        OutputBuilder expectedBoard = new OutputBuilder();
        expectedBoard.append("   -----------------");
        expectedBoard.append("8 | r n b q k b n r |");
        expectedBoard.append("7 | p p p p p p p p |");
        expectedBoard.append("6 | - - - - - - - - |");
        expectedBoard.append("5 | - - - - - - - - |");
        expectedBoard.append("4 | - - - - - - - - |");
        expectedBoard.append("3 | - - - - - - - - |");
        expectedBoard.append("2 | P P P P P P P P |");
        expectedBoard.append("1 | R N B Q K B N R |");
        expectedBoard.append("   -----------------");
        expectedBoard.append("    a b c d e f g h");
        
        Position p = FenParser.parsePieceBoard(initialBoard);
        assertEquals(expectedBoard.toString(), format(p));
    }

    @Test
    public void testQueensGambitAccepted()
    {
        String pieceBoard = "rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR";

        OutputBuilder expectedBoard = new OutputBuilder();
        expectedBoard.append("   -----------------");
        expectedBoard.append("8 | r n b q k b n r |");
        expectedBoard.append("7 | p p p - p p p p |");
        expectedBoard.append("6 | - - - - - - - - |");
        expectedBoard.append("5 | - - - P - - - - |");
        expectedBoard.append("4 | - - - - - - - - |");
        expectedBoard.append("3 | - - - - - - - - |");
        expectedBoard.append("2 | P P P P - P P P |");
        expectedBoard.append("1 | R N B Q K B N R |");
        expectedBoard.append("   -----------------");
        expectedBoard.append("    a b c d e f g h");
        
        Position p = FenParser.parsePieceBoard(pieceBoard);
        Assert.assertEquals(expectedBoard.toString(), format(p));
    }
    
    @Test
    public void testEndgamePosition()
    {
        String pieceBoard = "q1n5/1P3p2/2P5/8/1K6/5b2/k6P/7R";

        OutputBuilder expectedBoard = new OutputBuilder();
        expectedBoard.append("   -----------------");
        expectedBoard.append("8 | q - n - - - - - |");
        expectedBoard.append("7 | - P - - - p - - |");
        expectedBoard.append("6 | - - P - - - - - |");
        expectedBoard.append("5 | - - - - - - - - |");
        expectedBoard.append("4 | - K - - - - - - |");
        expectedBoard.append("3 | - - - - - b - - |");
        expectedBoard.append("2 | k - - - - - - P |");
        expectedBoard.append("1 | - - - - - - - R |");
        expectedBoard.append("   -----------------");
        expectedBoard.append("    a b c d e f g h");

        Position position = FenParser.parsePieceBoard(pieceBoard);
        Assert.assertEquals(expectedBoard.toString(), format(position));
    }
    
    private String format(Position position)
    {
    	displayer = new Displayer();
    	displayer.setPosition(position);
    	Displayer constructedDisplayer = new Displayer(position);
    	assertEquals(displayer.formatBoard(), constructedDisplayer.formatBoard());
    	return displayer.formatBoard();
    }
}
