package com.jeremybrooks.chess.util;

import java.security.SecureRandom;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Square;


public class ZobristKey {

    private static final long[][][] pieceSquareKey = new long[2][6][64];
    private static final long[] castleKey = new long[16];
    private static final byte[] seed = toByteArray("GaMe-0F_pAwNs!");
    private static final SecureRandom random = new SecureRandom(seed);
    
    static
    {
        for(int color=0; color<2; color++)
            for(int piece=0; piece<6; piece++)
                for(int square=0; square<64; square++)
                    pieceSquareKey[color][piece][square] = random.nextLong();
        for(int option=0; option<16; option++)
        	castleKey[option] = random.nextLong();
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

    private ZobristKey(){}
    
    public static long forPieceOnSquare(Piece piece, int square)
    {
        int color = (piece.encodedByColor() > 0) ? Piece.WHITE : Piece.BLACK;
        if(piece.index() == Piece.PAWN && 
        		(Bitmap.rankNumber(square) <= 0 || Bitmap.rankNumber(square) > 6)) //TODO: ideally this could be an assert statement
        	throw new IllegalArgumentException(
        			"no hash values allowed for pawns outside the second to seventh ranks: " + Square.named(square));
        return pieceSquareKey[color][piece.index()][square];
    }
    
    public static long forBlackToMove()
    {
    	//Use the unusable key "white pawn on a1" as black to move key
        return pieceSquareKey[Piece.WHITE][Piece.PAWN][Bitmap.A1];
    }

    public static long forEnPassantTargetFile(int zeroBasedFileNumber)
    {
    	//Use the unusable keys "black pawns on first rank" as target square keys
    	if(zeroBasedFileNumber < 0 || zeroBasedFileNumber > 7) //TODO: ideally this could be an assert statement
    		throw new IllegalArgumentException("zeroBasedFileNumber "+zeroBasedFileNumber+" must be in range 0-7");
    	return pieceSquareKey[Piece.BLACK][Piece.PAWN][zeroBasedFileNumber];
    }

    public static long forCastlingOptions(int options)
    {
    	if(options < 0 || options > castleKey.length-1)
    		throw new IllegalArgumentException("options "+options+" must be in range 0-15");
    	return castleKey[options];
    }
}
