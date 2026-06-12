package com.jeremybrooks.util;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;

import com.jeremybrooks.chess.util.ZobristKey;
import org.junit.Test;
import org.junit.Ignore;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.PieceFactory;
import com.jeremybrooks.chess.base.Square;

public class ZobristKeyTest extends KeyTestBase {

    @Ignore
    @Test
    public void givenAllKeys()
    {
    	//assert they are all unique (no overlaps or collisions between hash keys)
    	long blackToMoveKey = ZobristKey.forBlackToMove();
    	assertKeyIsUnique(blackToMoveKey);
    	addKey(blackToMoveKey);

    	//piece-square keys
    	for(int color=0; color < 2; color ++)
    	{
    		for(int pieceIndex=0; pieceIndex < 6; pieceIndex++)
    		{
    			for(int square=0; square < 64; square++)
    			{
    				Piece piece = PieceFactory.create(Piece.asCharacter(color, pieceIndex));
    				boolean isFirstOrEighthRank = Bitmap.rankNumber(square) == 0 || Bitmap.rankNumber(square) == 7;
					if(piece.isPawn() && isFirstOrEighthRank)
    				{
    					try {
    						ZobristKey.forPieceOnSquare(piece, square);
    					} catch (IllegalArgumentException e) {
    						assertEquals(
    								"no hash values allowed for pawns outside the second to seventh ranks: "+Square.named(square),
    								e.getMessage());
    					}
    				} else {
    					long key = ZobristKey.forPieceOnSquare(piece, square);
    					assertKeyIsUnique(key);
    					addKey(key);
    				}
    			}
    		}
    	}
    	for(int castlingOptions=0; castlingOptions<16; castlingOptions++)
    	{
    		long key = ZobristKey.forCastlingOptions(castlingOptions);
    		assertKeyIsUnique(key);
			addKey(key);
    	}
    	for(int enPassantFileNumber=0; enPassantFileNumber<8; enPassantFileNumber++)
    	{
    		long key = ZobristKey.forEnPassantTargetFile(enPassantFileNumber);
    		assertKeyIsUnique(key);
    		addKey(key);
    	}
    	int totalKeyCount = 0;
    	totalKeyCount += 1;      //black to move
    	totalKeyCount += 2*5*64; //2 colors * 5 pieces * 64 squares
    	totalKeyCount += 2*48;   //2 colors * 1 pawns * 48 squares
    	totalKeyCount += 16;     //2 colors * 8 castling states/color
    	totalKeyCount += 8;      //8 en passant files
    	assertEquals(totalKeyCount, keyMapper.size());
    }
    
    @Test
    public void givenInvalidCastlingOptions()
    {
    	int[] invalidOptions = new int[]{-1,16};
    	for(int invalidOption: invalidOptions)
    	{
    		try {
    			ZobristKey.forCastlingOptions(invalidOption);
    		} catch (IllegalArgumentException e) {
    			assertEquals(
    					"options "+invalidOption+" must be in range 0-15",
    					e.getMessage());
    		}
    	}
    }

    @Test
    public void givenInvalidEnPassantFiles()
    {
    	int[] invalidFiles = new int[]{-1,8};
    	for(int invalidFile: invalidFiles)
    	{
    		try {
    			ZobristKey.forEnPassantTargetFile(invalidFile);
    		} catch (IllegalArgumentException e) {
    			assertEquals(
    					"zeroBasedFileNumber "+invalidFile+" must be in range 0-7",
    					e.getMessage());
    		}
    	}
    }
}
