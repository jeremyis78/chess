package com.jeremybrooks.chess.util;

import static com.jeremybrooks.chess.base.Bitmap.BLACK;
import static com.jeremybrooks.chess.base.Bitmap.WHITE;

import java.security.SecureRandom;

import com.jeremybrooks.chess.base.Piece;


public class ZobristKey {

    private static final long[][][] zobristKey = new long[2][6][64];
    private static final byte[] seed = toByteArray("GaMe-0F_pAwNs!");
    private static final SecureRandom random = new SecureRandom(seed);

    /*
     * Don't allow instantiations
     */
    private ZobristKey(){}
    
    static
    {
        for(int color=0; color<2; color++)
            for(int piece=0; piece<6; piece++)
                for(int square=0; square<64; square++)
                    zobristKey[color][piece][square] = random.nextLong();
    }

    private static byte[] toByteArray(String s)
    {
        byte[] bytes = new byte[s.length()];
        int index = 0;
        for(char c: s.toCharArray())
        {
            bytes[index++] = (byte) c;
        }
        return bytes;
    }
    
    public static long get(Piece piece, int square)
    {
        int color = (piece.encodedByColor() > 0) ? WHITE : BLACK;
        return zobristKey[color][piece.index()][square];
    }
}
