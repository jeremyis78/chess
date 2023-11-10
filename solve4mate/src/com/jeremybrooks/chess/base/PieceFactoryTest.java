package com.jeremybrooks.chess.base;

import static org.junit.Assert.*;
import static com.jeremybrooks.chess.base.Piece.*;

import org.junit.Test;

public class PieceFactoryTest {

    @Test
    public void givenValidPromotionPiece() {
        String validPromoteChars = "QqRrBbNn";
        for(int index=0; index<validPromoteChars.length(); index++)
        {
            char pieceChar = validPromoteChars.charAt(index);
            Piece piece = PieceFactory.create(pieceChar);
            Piece promotePiece = PieceFactory.toPromotePiece(pieceChar);
            //The color of the promotion piece doesn't matter
            //because color isn't encoded into the move so we're not testing that
            assertEquals("promoted pieces are always white",
                    Character.toUpperCase(piece.toChar()), promotePiece.toChar());
            assertEquals("the encoded value of promoted piece should match both black and white regular piece", 
                    piece.encoded(), promotePiece.encoded());
        }
    }

    @Test
    public void givenInvalidPromotionPiece() {
        String invalidPromoteChars = "PpKk1234567890X";
        for(int i=0; i<invalidPromoteChars.length(); i++)
        {
            char promoteChar = invalidPromoteChars.charAt(i);
            try {
                PieceFactory.toPromotePiece(promoteChar);
            } catch (IllegalArgumentException e) {
                assertEquals("promotion piece is invalid '"+promoteChar+"'; allowed piece characters are: QqRrBbNn", e.getMessage());
            }
        }
    }
    
    @Test
    public void givenEncodedPieces()
    {
        assertEquals(PAWN,   TO_PIECE[ENCODED[PAWN]]);
        assertEquals(KNIGHT, TO_PIECE[ENCODED[KNIGHT]]);
        assertEquals(BISHOP, TO_PIECE[ENCODED[BISHOP]]);
        assertEquals(ROOK,   TO_PIECE[ENCODED[ROOK]]);
        assertEquals(QUEEN,  TO_PIECE[ENCODED[QUEEN]]);
        assertEquals(KING,   TO_PIECE[ENCODED[KING]]);
        assertEquals(NONE,   TO_PIECE[ENCODED[NONE]]);
        
        assertEquals('P', Piece.uppercase(ENCODED[PAWN]));
        assertEquals('N', Piece.uppercase(ENCODED[KNIGHT]));
        assertEquals('B', Piece.uppercase(ENCODED[BISHOP]));
        assertEquals('R', Piece.uppercase(ENCODED[ROOK]));
        assertEquals('Q', Piece.uppercase(ENCODED[QUEEN]));
        assertEquals('K', Piece.uppercase(ENCODED[KING]));
        assertEquals(' ', Piece.uppercase(ENCODED[NONE]));
        
        assertEquals('p', Piece.lowercase(ENCODED[PAWN]));
        assertEquals('n', Piece.lowercase(ENCODED[KNIGHT]));
        assertEquals('b', Piece.lowercase(ENCODED[BISHOP]));
        assertEquals('r', Piece.lowercase(ENCODED[ROOK]));
        assertEquals('q', Piece.lowercase(ENCODED[QUEEN]));
        assertEquals('k', Piece.lowercase(ENCODED[KING]));
        assertEquals(' ', Piece.lowercase(ENCODED[NONE]));
    }

}
