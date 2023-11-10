package com.jeremybrooks.chess;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PuzzleTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testParsingPuzzleWithNotes() {
        String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1 ";
        String puzzleLine = "3 " + fen + "#some notes";
        Puzzle puzzle = Puzzle.parse(puzzleLine);
        assertEquals(3, puzzle.getMovesToMate());
        assertEquals(fen, puzzle.getFen());
        assertEquals("some notes", puzzle.getNotes());
    }

    @Test
    public void testParsingPuzzle() {
        String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1";
        String puzzleLine = "3 " + fen;
        Puzzle puzzle = Puzzle.parse(puzzleLine);
        assertEquals(3, puzzle.getMovesToMate());
        assertEquals(fen, puzzle.getFen());
        assertEquals("", puzzle.getNotes());
    }

}
