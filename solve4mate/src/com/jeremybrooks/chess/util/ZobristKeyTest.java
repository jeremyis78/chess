package com.jeremybrooks.chess.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.PieceFactory;
import com.jeremybrooks.chess.base.Square;

public class ZobristKeyTest {

    @Test
    public void givenAllKeys()
    {
    	//assert they are all unique (no overlaps or collisions between hash keys)
    	HashMap<Long,Integer> keyMapper = new HashMap<>(1000); //bigger than 2*6*64
    	
    	long blackToMoveKey = ZobristKey.forBlackToMove();
    	assertKeyIsUnique(blackToMoveKey, keyMapper);
    	addKey(blackToMoveKey, keyMapper);

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
    					assertKeyIsUnique(key, keyMapper);
    					addKey(key, keyMapper);
    				}
    			}
    		}
    	}
    	for(int castlingOptions=0; castlingOptions<16; castlingOptions++)
    	{
    		long key = ZobristKey.forCastlingOptions(castlingOptions);
    		assertKeyIsUnique(key, keyMapper);
			addKey(key, keyMapper);
    	}
    	for(int enPassantFileNumber=0; enPassantFileNumber<8; enPassantFileNumber++)
    	{
    		long key = ZobristKey.forEnPassantTargetFile(enPassantFileNumber);
    		assertKeyIsUnique(key, keyMapper);
    		addKey(key, keyMapper);
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

	private static void addKey(long key, HashMap<Long, Integer> keyMapper) {
		keyMapper.put(key, 1);
	}

	private static void assertKeyIsUnique(long key, HashMap<Long, Integer> keyMapper) {
		boolean keyAlreadyUsed = keyMapper.get(key) != null;
		if(keyAlreadyUsed)
		{
			fail("each hash key must be unique; the key "+key+" was used twice");
		}
	}
}
