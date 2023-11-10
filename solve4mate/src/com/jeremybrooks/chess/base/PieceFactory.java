package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static com.jeremybrooks.chess.base.Piece.*;

import com.jeremybrooks.chess.base.Piece.Color;

public class PieceFactory {
    
    private static final Piece WHITE_PAWN   = new Pawn  (Color.W);
    private static final Piece BLACK_PAWN   = new Pawn  (Color.B);
    private static final Piece WHITE_KNIGHT = new Knight(Color.W);
    private static final Piece BLACK_KNIGHT = new Knight(Color.B);
    private static final Piece WHITE_BISHOP = new Bishop(Color.W);
    private static final Piece BLACK_BISHOP = new Bishop(Color.B);
    private static final Piece WHITE_ROOK   = new Rook  (Color.W);
    private static final Piece BLACK_ROOK   = new Rook  (Color.B);
    private static final Piece WHITE_QUEEN  = new Queen (Color.W);
    private static final Piece BLACK_QUEEN  = new Queen (Color.B);
    private static final Piece WHITE_KING   = new King  (Color.W);
    private static final Piece BLACK_KING   = new King  (Color.B);
    private static final Piece EMPTY        = new Empty();
    
    /**
     * Given a piece returned from Position.getBoard(int) 
     * construct and return the appropriate Piece object
     * 
     * @param boardPiece value returned from Position.getBoard(int)
     * @return a corresponding Piece object
     */
    public static Piece fromBoardPiece(int boardPiece)
    {
        //positive boardPiece = white
        //negative boardPiece = black
        Color pieceColor = boardPiece > 0 ? Color.W : Color.B;
        if(BOARD_EMPTY_SQUARE == boardPiece)
        {
            return PieceFactory.fromIndex(pieceColor, NONE);
        }
        return PieceFactory.fromIndex(pieceColor, TO_PIECE[Math.abs(boardPiece)]);
    }

    public static Piece fromIndex(Color color, int pieceIndex)
    {
        switch(pieceIndex)
        {
        case PAWN  : return color==Color.W ? WHITE_PAWN   : BLACK_PAWN;
        case KNIGHT: return color==Color.W ? WHITE_KNIGHT : BLACK_KNIGHT;
        case BISHOP: return color==Color.W ? WHITE_BISHOP : BLACK_BISHOP;
        case ROOK  : return color==Color.W ? WHITE_ROOK   : BLACK_ROOK;
        case QUEEN : return color==Color.W ? WHITE_QUEEN  : BLACK_QUEEN;
        case KING  : return color==Color.W ? WHITE_KING   : BLACK_KING;
        case NONE  : return EMPTY;   
        default: throw new IllegalArgumentException("invalid pieceIndex: " + pieceIndex);
        }
    }

    public static Piece create(char pieceChar) {
        Piece piece = new Empty();
        Color color = Character.isUpperCase(pieceChar) ? Color.W : Color.B;
        char upperPieceChar = Character.toUpperCase(pieceChar);
        if(upperPieceChar == 'P') piece = color==Color.W ? WHITE_PAWN   : BLACK_PAWN;
        if(upperPieceChar == 'N') piece = color==Color.W ? WHITE_KNIGHT : BLACK_KNIGHT;
        if(upperPieceChar == 'B') piece = color==Color.W ? WHITE_BISHOP : BLACK_BISHOP;
        if(upperPieceChar == 'R') piece = color==Color.W ? WHITE_ROOK   : BLACK_ROOK;
        if(upperPieceChar == 'Q') piece = color==Color.W ? WHITE_QUEEN  : BLACK_QUEEN;
        if(upperPieceChar == 'K') piece = color==Color.W ? WHITE_KING   : BLACK_KING;
        if(!piece.exists())
            throw new IllegalArgumentException("piece is invalid '"+pieceChar+"'; "
                    + "allowed piece characters are: KkQqRrBbNnPp");
        return piece;
    }

    public static Piece toPromotePiece(char pieceChar) {
        Piece piece = new Empty();
        char upperPieceChar = Character.toUpperCase(pieceChar);
        if     (upperPieceChar == 'Q') piece = WHITE_QUEEN;
        else if(upperPieceChar == 'R') piece = WHITE_ROOK;
        else if(upperPieceChar == 'B') piece = WHITE_BISHOP;
        else if(upperPieceChar == 'N') piece = WHITE_KNIGHT;
        else 
            throw new IllegalArgumentException("promotion piece is invalid '"+pieceChar+"'; "
                    + "allowed piece characters are: QqRrBbNn");
        return piece;
    }

}
