package com.jeremybrooks.chess.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.jeremybrooks.chess.base.Bishop;
import com.jeremybrooks.chess.base.Knight;
import com.jeremybrooks.chess.base.Piece;

public class ZobristKeyTest {

    @Test
    public void testZobristKey() {
        Piece blackKnight = new Knight(Piece.Color.B);
        Piece whiteBishop = new Bishop(Piece.Color.W);
        long key1 = ZobristKey.get(blackKnight, 13);
        long key2 = ZobristKey.get(whiteBishop, 56);
        long hash = key1;// zk = new ZobristKey();
        assertEquals(key1, hash);
        hash ^= key2;
        assertNotEquals(key1, hash);
        hash ^= key2;
        assertEquals(key1, hash);
    }
}
