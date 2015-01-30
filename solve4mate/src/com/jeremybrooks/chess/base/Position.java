package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static com.jeremybrooks.chess.base.Square.*;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.Piece.Color;
import com.jeremybrooks.chess.util.Util;

/**                                                                 
 *  A position represents the physical locations of all pieces on
 *  the chessboard.
 *
 *  TODO: Pieces!!!!! (that's the best name for this class which would
 *  be used within a Board (formerly GameState) class
 * @author jeremy
 *
 */

public class Position
{
    private static final Logger log = Logger.getLogger(Position.class);
    
    private static final int PAWNS = 0;
    private static final int KNIGHTS = 1;
    private static final int BISHOPS = 2;
    private static final int ROOKS = 3;
    private static final int QUEENS = 4;
    private static final int ALLPIECES = 5;

    private static final int KING_NOT_PLACED = -1;

    private long pieces[][] = new long[2][6];
    private long all[] = new long[MAXALL];
    private Square board[] = new Square[64];
    private int kingSq[] = new int[]{KING_NOT_PLACED, KING_NOT_PLACED};

    public Position(){
        clear();
    }
    
    public long getPawns(int side)
    {
        return pieces[side][PAWNS];        
    }

    public long getOpponentPawns(int side)
    {
        return pieces[Util.opposing(side)][PAWNS];        
    }
    
    public long getKnights(int side)
    {
        return pieces[side][KNIGHTS];        
    }
    
    public long getOpponentKnights(int side)
    {
        return pieces[Util.opposing(side)][KNIGHTS];        
    }

    public long getBishops(int side)
    {
        return pieces[side][BISHOPS];        
    }

    public long getOpponentBishops(int side)
    {
        return pieces[Util.opposing(side)][BISHOPS];        
    }

    public long getRooks(int side)
    {
        return pieces[side][ROOKS];        
    }

    public long getOpponentRooks(int side)
    {
        return pieces[Util.opposing(side)][ROOKS];        
    }

    public long getQueens(int side)
    {
        return pieces[side][QUEENS];        
    }

    public long getOpponentQueens(int side)
    {
        return pieces[Util.opposing(side)][QUEENS];        
    }
    
    public long getKing(int side)
    {
        if(isKingPlaced(side)) {
            return 1L << kingSq[side];
        }
        return 0L;
    }

    public long getOpponentKing(int side)
    {
        int opponentSide = Util.opposing(side); 
        if(isKingPlaced(opponentSide)) {
            return 1L << kingSq[opponentSide];
        }
        return 0L;
    }

    public int getKingSquare(int side) {
        return kingSq[side];
    }

    public long getPieces(int side, int piece){
        if (isNotTheKing(piece)) {
            return pieces[side][piece];
        } else if(isKingPlaced(side)) {
            return 1L << kingSq[side];
        }
        return 0L;
    }

    public void setPieces(int side, int piece, long bitmap) {
        int squareOfPiece = 0;
        while(bitmap != 0)
        {
            squareOfPiece = Bitmap.lowestBitNumber(bitmap);
            placePiece(side, piece, squareOfPiece);
            bitmap = Bitmap.clearBit(bitmap, squareOfPiece);
        }
    }

    public long getPieces(int side){
        long oneSidesPieces = 0L;
        for(int piece=PAWN; piece<=QUEEN; piece++)
        {
            oneSidesPieces |= pieces[side][piece];
        }
        if(isKingPlaced(side)) {
            oneSidesPieces |= 1L << kingSq[side];
        }
        return oneSidesPieces;
    }

    public long getOpponentPiecesExceptKing(int color)
    {
        int opponentColor = (color == Bitmap.WHITE) ? Bitmap.BLACK : Bitmap.WHITE;
        return pieces[opponentColor][ALLPIECES];
    }

    public long getAllPieces(int rotationInDegrees)
    {
        switch(rotationInDegrees)
        {
        case -45:
            return all[ALL45L];
        case 0:
            return all[ALL];
        case 45:
            return all[ALL45R];
        case 90:
            return all[ALL90];
        default:
            throw new IllegalArgumentException("invalid rotation "+rotationInDegrees+"; rotation must be -45, 0, 45 or 90");
        }
    }

    private boolean isNotTheKing(int p) {
        return p <= QUEEN;
    }

    public boolean isKingPlaced(int side) {
        return kingSq[side] != KING_NOT_PLACED;
    }
    
    public int getBoard(int square)
    {
        return get(square).encodedByColor();
    }
    
    public Piece get(int square)
    {
        return board[square].get();
    }

    public boolean isNotEmpty(int square)
    {
        return board[square].isOccupied();
    }
    
    public boolean isEmpty(int square)
    {
        return !isNotEmpty(square);
    }

    public void clear(){
        kingSq[Bitmap.WHITE] = KING_NOT_PLACED;
        kingSq[Bitmap.BLACK] = KING_NOT_PLACED;
        for (int i = Bitmap.WHITE; i <= Bitmap.BLACK; i++){
            pieces[i][PAWNS] = 0L;
            pieces[i][KNIGHTS] = 0L;
            pieces[i][BISHOPS] = 0L;
            pieces[i][ROOKS] = 0L;
            pieces[i][QUEENS] = 0L;
            pieces[i][ALLPIECES] = 0L;
        }
        all[ALL] = 0L;
        all[ALL90] = 0L;
        all[ALL45L] = 0L;
        all[ALL45R] = 0L;
        
        for (int i = A1; i <= H8; i++)
        {
            board[i] = new Square(); 
        }
    }    

    /**
     * Places a piece on the board and in the bitmaps
     * 
     * @param c the color of the piece as in Color.WHITE/BLACK
     * @param p the index into the PIECE[] array Chess.PAWN, etc
     * @param sq the square to place the piece on
     */
    public void placePiece(int c, int p, int sq){
        if(p == KING && kingSq[c] != KING_NOT_PLACED)
        {
            throw new IllegalStateException("cannot place " + (c==WHITE?"white":"black") +
                    " king on "+ named(sq)+"; already placed on " +
                    named(kingSq[c])+ "; use erasePiece()");
        }
        if(isNotEmpty(sq))
        {
            System.err.println(toString());
            throw new IllegalStateException(named(sq)+ " is already occupied");
        }
        if(log.isTraceEnabled()) 
            log.trace("placing "+(c==WHITE?"white":"black")+" "+p+" on " +named(sq));
        long mask = 1L << sq;
        all[ALL] |= mask;
        all[ALL90] |= 1L << SQ2BIT90R[sq];
        all[ALL45L] |= 1L << SQ2BIT45L[sq];
        all[ALL45R] |= 1L << SQ2BIT45R[sq];
        Color color = c==Bitmap.WHITE?Color.W:Color.B;
        board[sq].set(PieceFactory.fromIndex(color, p));
    
        if (p == KING){
                kingSq[c] = sq;
        } else {
            pieces[c][p] |= mask;
            pieces[c][ALLPIECES] |= mask;
        }
    }
    
    /**
     * Erases/removes the piece on the given square
     * 
     * @param sq the square of the piece to remove
     */
    public void erasePiece(int square){
        int boardPiece = getBoard(square);
        if(boardPiece == BOARD_EMPTY_SQUARE)
        {
            return; //already empty
        }
        int color = (boardPiece > 0) ? WHITE : BLACK;
        int piece = TO_PIECE[Math.abs(boardPiece)];
        if(log.isTraceEnabled()) 
            log.trace("erasing "+(color==WHITE?"white":"black")+" piece on " + named(square));
        long mask = 1L << square;
        all[Bitmap.ALL] ^= mask;
        all[ALL90] ^= 1L << SQ2BIT90R[square];
        all[ALL45L] ^= 1L << SQ2BIT45L[square];
        all[ALL45R] ^= 1L << SQ2BIT45R[square];
        board[square].clear();
        if (piece == KING)
        {
            kingSq[color] = KING_NOT_PLACED;
        }  else {
            pieces[color][piece] ^= mask;
            pieces[color][ALLPIECES] ^= mask;
        }
    }

	// Validate the invariants of the position.
	// Specifically, that the bitboards, king square locations, board array
    // and occupied bitboards all stay in agreement. 
    public static void validate(Position p) {
    	//
    	// 1. Verify the piece bitboards as well as king squares do not overlap
    	//
    	long bitboardPiecesOR = 0;
    	long bitboardPiecesAND = 0;
    	long bitboardAllPiecesOR[] = new long[2];
    	int bitboardPieceCount[][] = new int[2][6];
    	for(int side=0; side < 2; side++)
    	{
    		for(int piece=PAWNS; piece<=QUEENS; piece++)
    		{
    			long pcs = p.getPieces(side, piece);
				bitboardPiecesAND         &= pcs;
    			bitboardPiecesOR          |= pcs;
    			bitboardAllPiecesOR[side] |= pcs;
    			bitboardPieceCount[side][piece] = Long.bitCount(pcs);
    		}
    	}
    	long kingsAND = p.getKing(WHITE) & p.getKing(BLACK);
    	long bitboardPiecesANDkings = bitboardPiecesAND & kingsAND;
    	assertValid(0 == bitboardPiecesAND); 
		assertValid(0 == bitboardPiecesANDkings);
    	
		//
    	// 2. Verify that the summary bitboard of each side's pieces is accurate
		//
		System.out.println("computed: " + Util.displaySquaresStr(bitboardAllPiecesOR[WHITE]));
		System.out.println("actual  : " + Util.displaySquaresStr(p.getPieces(WHITE,ALLPIECES)));
    	assertValid(p.getPieces(WHITE,ALLPIECES) == bitboardAllPiecesOR[WHITE]);
    	assertValid(p.getPieces(BLACK,ALLPIECES) == bitboardAllPiecesOR[BLACK]);

    	//
    	// 3. Verify the occupied bitboards (and rotations) are in sync with the piece bitboards
    	// representing all pieces for each side.
    	// 
    	// For the rotated bitboards we simply validate the bit counts against the non-rotated board.
    	// This will catch simple things like a piece being added (or removed) to the non-rotated
    	// occupied bitboard but not to the rotated boards.  Errors where the rotated bitboard has 
    	// the same number of bits--but bits in the wrong spot (wrong value)--will NOT be caught here.
    	//
    	long kingsOR = p.getKing(WHITE) | p.getKing(BLACK);
    	long allPiecesWithKings = p.getPieces(WHITE,ALLPIECES) | p.getPieces(BLACK,ALLPIECES) | kingsOR;
    	bitboardPiecesOR |= kingsOR;
    	assertValid(p.getAllPieces(0)  == bitboardPiecesOR);
    	assertValid(allPiecesWithKings == bitboardPiecesOR);
    	int occupiedBitCount     = Long.bitCount(p.getAllPieces(0));
    	assertValid(occupiedBitCount == Long.bitCount(p.getAllPieces(90)));
    	assertValid(occupiedBitCount == Long.bitCount(p.getAllPieces(-45)));
    	assertValid(occupiedBitCount == Long.bitCount(p.getAllPieces(45)));
    	
    	//
    	// 4. Verify the array of board squares/pieces (of what piece is on each square) 
    	// matches the bitboards.
    	//
    	// Again for simplicity we only validate the piece
    	// counts match, not their positions, so this will catch a missing piece on the board
    	// and not in the bitboards or vice versa, but it will not catch a set of the same number
    	// of pieces but on different squares in the array versus on the bitboard.
    	//
    	int boardArrayKing[] = new int[]{KING_NOT_PLACED, KING_NOT_PLACED};
    	int boardArrayPieceCount[][] = new int[2][6];
    	for(int squareNo=0; squareNo<64; squareNo++)
    	{
    		Piece piece = p.get(squareNo);
    		boolean existsInOccupiedBitboard = Util.bool(1L<<squareNo & p.getAllPieces(0));
    		if(piece.exists())
    		{
    			//Piece piece = sq.get();
    			int side = Character.isUpperCase(piece.toChar())?WHITE:BLACK;
    			if('K' == Character.toUpperCase(piece.toChar()))
    			{
    				boardArrayKing[side] = squareNo;
    			} else {
    				int index = piece.index();
    				boardArrayPieceCount[side][index]++;
    			}
//    			System.out.println("all : "+Util.formatSquares(all[ALL]));
//    			System.out.println("mask: "+Util.formatSquares(1L<<squareNo));
//    			System.out.println("and : "+Util.formatSquares((1L<<squareNo)&all[ALL]));
//    			System.out.println("cond: "+existsOnBitboard);
    			assertValid(existsInOccupiedBitboard); //bitboard piece must exist
    		} else {
				assertValid(!existsInOccupiedBitboard); //bitboard piece must NOT exist
    		}
    	}
    	int side = WHITE;
    	if(p.isKingPlaced(side))
    		assertValid(p.getKing(side)               == boardArrayKing[side]);
    	assertValid(bitboardPieceCount[side][PAWNS  ] == boardArrayPieceCount[side][PAWNS]);
    	assertValid(bitboardPieceCount[side][KNIGHTS] == boardArrayPieceCount[side][KNIGHTS]);
    	assertValid(bitboardPieceCount[side][BISHOPS] == boardArrayPieceCount[side][BISHOPS]);
    	assertValid(bitboardPieceCount[side][ROOKS  ] == boardArrayPieceCount[side][ROOKS]);
    	assertValid(bitboardPieceCount[side][QUEENS ] == boardArrayPieceCount[side][QUEENS]);
    	side = BLACK;
    	if(p.isKingPlaced(side))
    		assertValid(p.getKing(side)               == boardArrayKing[side]);
    	assertValid(bitboardPieceCount[side][PAWNS  ] == boardArrayPieceCount[side][PAWNS]);
    	assertValid(bitboardPieceCount[side][KNIGHTS] == boardArrayPieceCount[side][KNIGHTS]);
    	assertValid(bitboardPieceCount[side][BISHOPS] == boardArrayPieceCount[side][BISHOPS]);
    	assertValid(bitboardPieceCount[side][ROOKS  ] == boardArrayPieceCount[side][ROOKS]);
    	assertValid(bitboardPieceCount[side][QUEENS ] == boardArrayPieceCount[side][QUEENS]);
	}

	private static void assertValid(boolean condition)
	{
		if(!condition) throw new IllegalStateException("my assertion failed; see Position.validate() line number below");
		//TODO: in the end (production) we want the keyword assertion: assert(condition);
		//so it can be optimized away when -ea is not enabled.
	}

	public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("wking: "+Square.named(kingSq[Bitmap.WHITE])).append("\n");
        sb.append("bking: "+Square.named(kingSq[Bitmap.BLACK])).append("\n");
        sb.append("piece bitboards:\n");
        for (int i = Bitmap.WHITE; i <= Bitmap.BLACK; i++){
            String color = (i==Bitmap.WHITE?"w":"b");
            sb.append(color + "p  : " + Util.displaySquaresStr(pieces[i][PAWNS])).append("\n");
            sb.append(color + "n  : " + Util.displaySquaresStr(pieces[i][KNIGHTS])).append("\n");
            sb.append(color + "b  : " + Util.displaySquaresStr(pieces[i][BISHOPS])).append("\n");
            sb.append(color + "r  : " + Util.displaySquaresStr(pieces[i][ROOKS])).append("\n");
            sb.append(color + "q  : " + Util.displaySquaresStr(pieces[i][QUEENS])).append("\n");
            sb.append(color + "All: " + Util.displaySquaresStr(pieces[i][ALLPIECES])).append("\n");
        }
        sb.append("occupied bitboards:\n");
        sb.append("all00x: " + Util.displaySquaresStr(all[ALL])).append("\n");
        sb.append("all90x: " + Util.displaySquaresStr(all[ALL90])).append("\n");
        sb.append("all45L: " + Util.displaySquaresStr(all[ALL45L])).append("\n");
        sb.append("all45R: " + Util.displaySquaresStr(all[ALL45R])).append("\n");
        sb.append("board array:\n");
        for (int i = A1; i <= H8; i++)
        {
            if(isNotEmpty(i))
            {
                //Prepend the black lowercase pieces with a "-" so they stand out more 
                //when vertically aligned with the white uppercase pieces
                Piece piece = board[i].get();
                String signedChar = (piece.encodedByColor() < 0 ? "-":"") + piece.toChar();
                sb.append(Square.named(i) + ": " + signedChar).append("\n");
            }
        }
        return sb.toString();
    }

    
    public static boolean isSameColor(int c, int p)
    {
        if ( (p > 0 && c == Bitmap.WHITE) || (p < 0 && c == Bitmap.BLACK) )
                return true;
        return false;
    }
    
}
