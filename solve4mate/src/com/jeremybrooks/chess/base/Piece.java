package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
/**
 * <pre>
 * Represents a piece on the chess board.
 * 
 * TODO: Work to eventually replace Position's "int board[64]" to be an array of these
 * instead (which would eliminate the static {@link #fromBoardPiece(int)} method.
 * 
 * Eventually (and ideally) we should be able massage this class into something that
 * can be used in the move generators (for even more complexity reduction)
 * </pre>
 * @author jeremy
 *
 */
/*
 * Of the 37 classes as of this commit, these are the only classes remaining
 * with a complexity of 4 or greater.
 * 
 * class                  Line coverage  Branch coverage   Complexity
 * ---------------------- -------------  ---------------   ----------
    EscapeGenerator            85% 79/92      66% 42/63        33
    NonCaptureGenerator       100% 81/81      93% 41/44        27
    CaptureGenerator       100% 54/54      96% 28/29        19
    Search++                 0% 0/83        0% 0/38         6.5
    SolveForMate++             0% 0/46        0% 0/22         5.5
    MoveGenerator            85% 206/242    72% 112/154      4.483
    Evaluator                79%    54/68      70% 21/30        4.4
     ++ = currently has NO unit tests
 *
 */
public abstract class Piece {
    public static enum Color{W, B}
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public static final char[][] CHAR = new char[2][];
    public static final String[] NAME =new String[]{"pawn","knight","bishop","rook","queen","king","none"};
    static {
    	CHAR[WHITE] = new char[]{' ','P','N','K',' ','B','R','Q'};
    	CHAR[BLACK] = new char[]{' ','p','n','k',' ','b','r','q'};
    }
    public static char lowercase(int encodedPiece){ return CHAR[BLACK][encodedPiece]; }
    public static char uppercase(int encodedPiece){ return CHAR[WHITE][encodedPiece]; }
    public static char asCharacter(int side, int pieceIndex)
    {
    	return CHAR[side][ENCODED[pieceIndex]];
    }

    public static String asString(int side, int pieceIndex)
    {
    	return (side == WHITE ? "white " : "black ") + NAME[pieceIndex];
    }

    public static int asIndex(char pieceCharacter)
    {
    	switch(pieceCharacter)
    	{
    	case 'P':
    	case 'p':
    		return PAWN;
    	case 'N':
    	case 'n':
    		return KNIGHT;
    	case 'B':
    	case 'b':
    		return BISHOP;
    	case 'R':
    	case 'r':
    		return ROOK;
    	case 'Q':
    	case 'q':
    		return QUEEN;
    	case 'K':
    	case 'k':
    		return KING;
    	default:
    		return NONE;
    	}
    }

//    public static int encode(int pieceIndex)      { return ENCODED[pieceIndex]; }
//    public static int unencode(int encoded)       { return TO_PIECE[encoded]; }

    //Piece indexes
    public static final int PAWN   = 0;
    public static final int KNIGHT = 1;
    public static final int BISHOP = 2;
    public static final int ROOK   = 3;
    public static final int QUEEN  = 4;
    public static final int KING   = 5;
    public static final int NONE   = 6;

    /**
	 * Returns the encoded value of the piece as it is encoded into 
	 * the "move" integer (as well as the Position.board array). 
	 * Indexed by Piece.PAWN, Piece.KNIGHT, etc.
	 * @see Position.getBoard(int) 
	 */
	public static final int ENCODED[] = {
	    1,  // ENCODED[PAWN  ] = 0x001
	    2,  // ENCODED[KNIGHT] = 0x010
	    5,  // ENCODED[BISHOP] = 0x101
	    6,  // ENCODED[ROOK  ] = 0x110
	    7,  // ENCODED[QUEEN ] = 0x111
	    3,  // ENCODED[KING  ] = 0x011 
	    0   // ENCODED[NONE  ] = 0x000
	};

	//takes an int index (a board character) and maps it to a corresponding piece
	
	/**
	 * Reverses the operation of ENCODED[] 
	 */
	public static final int TO_PIECE[] = {
	    NONE,       // 0 (no piece)
	    PAWN,       // 1
	    KNIGHT,     // 2
	    KING,       // 3
	    NONE,       // 4 (no piece)
	    BISHOP,     // 5
	    ROOK,       // 6
	    QUEEN
	};


    protected Color color;
    protected int index;
    protected char displayCh;


    public Piece(Color pieceColor, int pieceIndex, char displayCharacter)
    {
        super();
        this.color = pieceColor;
        this.index = pieceIndex;
        this.displayCh = displayCharacter;
    }

    public char toChar()
    { 
        return color == Color.W 
            ? displayCh 
            : Character.toLowerCase(displayCh);
    }

    @Override
    public String toString() { return ""+toChar(); }

    /**
     * Get the value used for indexing into the bitboard arrays in 
     * Position.getPieces(int color, int piece)
     * @return the index of this piece 
     */
    public int index() { return index; }
    
    public boolean isPawn()  { return index == PAWN;   }
    public boolean isKnight(){ return index == KNIGHT; }
    public boolean isBishop(){ return index == BISHOP; }
    public boolean isRook()  { return index == ROOK;   }
    public boolean isQueen() { return index == QUEEN;  }
    public boolean isKing()  { return index == KING;   }
    public boolean isNone()  { return index == NONE;   }
    
    public int side() { return (color == Color.W ? WHITE : BLACK); }
    
    /**
     * Gets the piece encoded for or retrieved from a 'move' int.
     * The encoded value only represents the type of piece; the color is not encoded.
     * 
     * @return the encoded piece
     */
    public int encoded() { return ENCODED[index()]; }
    
    /**
     * Gets the piece as would be returned from {@link #encoded()} but 
     * if the piece is Black it will return {@code -1 * moveEncoded()}
     * If the piece has no color or there is no piece then 
     * {@code Absent.toChar()} is returned.
     * @return the piece's color encoded value or Absent.toChar()
     */
    public int encodedByColor() 
    {
        if(!exists())
            return BOARD_EMPTY_SQUARE;
        return (color == Color.W ? encoded() : -1 * encoded());
    }
    
    /**
     * Return the value in centi-pawns for this piece placed on 
     * the given square.
     * @param square the square where the piece is located
     * @return the centi-pawn bonus value for this piece on this square
     */
    public abstract int centipawnValueOnSquare(int square);

    /**
     * Should return false ONLY when this piece represents
     * an absent piece (ie, no piece at all)
     * 
     * @return true if this refers to a piece, false if empty/absent
     */
    public abstract boolean exists();
    
    /**
     * Get a bitboard containing all squares where this piece can advance to
     * excluding captures.
     * All moves that indicate a pseudo legal move for this piece (e.g. pawn 
     * advance.
     * 
     * @param fromSquare  square on which the piece resides, or NOSQUARE if moves are generated en masse 
     * @param position  the current position on the board
     * @return
     */
    public abstract long advances(int fromSquare, Position position);
    
    /**
     * Get a bitboard containing all squares attacked given the piece sits on fromSquare.
     * Attacked squares include those occupied by friendly (non-enemy) pieces, a.k.a. squares
     * of defended pieces. 
     * 
     * @param fromSquare The square on which the piece sits.
     * @param position The current board position
     * @return
     */
    public abstract long attacks(int fromSquare, Position position);
    
}