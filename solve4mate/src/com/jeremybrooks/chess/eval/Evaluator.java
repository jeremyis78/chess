package com.jeremybrooks.chess.eval;

import static com.jeremybrooks.chess.base.Piece.*;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.base.Square;
import com.jeremybrooks.chess.movegen.Attacks;
import com.jeremybrooks.chess.util.FenBuilder;
import com.jeremybrooks.chess.util.Util;

public class Evaluator {
    private static final Logger log = Logger.getLogger(Evaluator.class);
    public static final int PIECE_VALUE[] = 
    {
        100, // Pawn
        325, // Knight
        325, // Bishop
        500, // Rook
        975, // Queen
        9999 // King
    };
    
    public static final int INCLUDE_PIECE_LOCATION_BONUSES = 1;

    private static final int BISHOP_PAIR_VALUE = 50;
    /*
     * 
    	Useful Masks  (maybe????)
	-------------------------------------
	Basic geographies on the board:
	-territory[w]    = (ranks 1-4)
	-territory[b]    = (ranks 5-8)
	-left            = (files 1-4)
	-right           = (files 5-8)
	-dark squares    = (a1,c1,e1,...,h8)  0xAA55AA55AA55AA55L;
	-light squares   = (b1,d1,f1,...,g7)  DARK_SQUARES << 1;
	-mainDiagonals   = (a1,b2,c3,...,h8; h1,g2,f3,...,a8) 
        
	These form concentric circles starting from the center:
	-center          = (d4,e4,d5,e5)                         (2x2)
	-adjacentCenter  = (c3-f3,c4,f4,c5,f5,c6-f6)             (4x4 minus middle 4 squares)
	-adjacentEdges   = (b2-g2,b3 up to b6,g3 up to g6,b7-g7) (6x6 minus middle 16 squares)
	-edges           = (ranks 1,8; files 1,8)                (8x8 minus middle 32 squares)

	    Computed masks
	-----------------------------------
	-not edges        = NOT edges                  (6x6) 
	-corners          = edges AND mainDiagonals
	-bankRank[w]      = territory[w] AND edges
	-bankRank[b]      = territory[b] AND edges

     */
    
    private int evaluationTerms;
    
    

    public Evaluator() {
		super();
	}

	public Evaluator(int evaluationTerms) {
		super();
		this.evaluationTerms = evaluationTerms;
	}

	/**
     * 
     * Returns an evaluation score for the side to move 'side'
     * Scores advantageous for white are positive.
     * Scores advantageous for black are negative.
     * Zero indicates no advantage for either side
     * TODO: there's no code or score for a draw yet.
     * 
     * A more positive number is better for white.
     * A more negative number is better for black.
     */
    public int evaluate(GameState g, int side, int depth, boolean searchDebug, boolean eval) {
        return evaluate(g, side, depth, null, searchDebug, eval);
    }
    
    public int evaluate(GameState g, int side, int depth, int[] currentMove, boolean isSearchDebug, boolean isEval){
        int wMaterialScore = 0, bMaterialScore = 0;  //score for white and black
        assert g.inCheck() == false;
        if(g.inCheck()) 
        {
            throw new IllegalStateException("can't statically evaluate the check or mate position: " + g.get());
        }
        
        Position position = g.getPosition();

        // Compute material value
        int[][] count = new int[2][5];
        int[][] pieceValue = new int[2][5];
        for(int color = 0; color<2; color++){
            int pieceScore = 0;
            for (int piece = PAWN; piece <= QUEEN; piece++){
                int numPieces = Util.bitCount(position.getPieces(color,piece));
                int pcScore = PIECE_VALUE[piece] * numPieces;
                pieceScore += pcScore;
                count[color][piece] = numPieces;
                pieceValue[color][piece] = pcScore;
                if(piece==BISHOP && numPieces >= 2) 
                {
                    //TODO: The above condition should ensure bishops are of opposite color which is the whole point
                    //As it is, it's possible to get the bonus if you had only one bishop and then 
                    //promoted a pawn to a bishop on a square of the same color of the existing bishop.
                    //This should NOT count as the bishop pair bonus
                    pcScore += BISHOP_PAIR_VALUE;
                    pieceScore += BISHOP_PAIR_VALUE;
                }
            }
            if(color==Piece.WHITE) wMaterialScore += pieceScore;
            else                   bMaterialScore += pieceScore;
        }

        int materialScore = wMaterialScore - bMaterialScore;
        int materialAdjustment = new MaterialAdjustmentTerm().evaluate(g);
        int finalScore = materialScore + materialAdjustment;
        if(Util.bool(evaluationTerms & INCLUDE_PIECE_LOCATION_BONUSES))
        {
        	int pieceLocationScore = new PieceLocationsTerm().evaluate(g);
        	finalScore += pieceLocationScore;
        }
        
        if(isSearchDebug)
        {
            String currentLine = "";
            if(currentMove != null && currentMove.length != 0)
            {
                for(int i=0;i<depth;i++){currentLine += Util.displayMoveStr(currentMove[i], false, false)+" ";}
            }
            FenBuilder fb = new FenBuilder();
            fb.appendPieceBoard(g.getPosition());
            log.debug("("+finalScore+") " + currentLine + "  " +fb.toString());
        }
        return finalScore;
    }

	public int scoreFromSEE(int move, GameState state)
	{
	    int fromSquare = move & 0x3F;                            //first 6 bits
	    int toSquare = (move >> 6) & 0x3F;                       //next 6
	    int capturingPiece = TO_PIECE[(move >> 12) & 0x7];       //next 3
	    int capturedPieceInMove  = TO_PIECE[(move >> 15) & 0x7]; //next 3
	    int gain[] = new int[32];
	    int depth = 0;
	    int sideToMove = state.isWhiteToMove()?Piece.WHITE:Piece.BLACK;
	    long xrayCandidates = state.getPosition().getAllPiecesExceptKing(Piece.WHITE)
	    		            | state.getPosition().getAllPiecesExceptKing(Piece.BLACK);
	    long attackedBy = 1L << fromSquare;
	    long occupied = state.getPosition().getOccupied();
	    long attdef = Attacks.attackers(state, Piece.WHITE, toSquare)
	                | Attacks.attackers(state, Piece.BLACK, toSquare);

	    gain[depth] = PIECE_VALUE[capturedPieceInMove];
	    if(log.isDebugEnabled())
	    {
	    	log.debug("captureSquare: "+Square.named(toSquare));
	    	log.debug(String.format("attackFrom(ply%d): %s", depth, Square.named(fromSquare)));
	    	log.debug(String.format("init gain[0]=%5d if %s@%s is en-prise", gain[depth], state.getPosition().get(toSquare).toChar(), Square.named(toSquare)));
	    }
	    do {
	    	Piece nextAttacker = state.getPosition().get(fromSquare);
	    	if(depth > 0)
	    	{
	    		assert(fromSquare != Bitmap.NOSQUARE);
	    		capturingPiece = nextAttacker.index();	//the next capturing piece in the exchange
	    		attackedBy = 1L << fromSquare;
	    	}
	    	depth++;
	    	int capturingPieceValue = PIECE_VALUE[capturingPiece];
	    	boolean isPromotion = isPromotion(capturingPiece, toSquare, sideToMove);
    		if(isPromotion)	
    			capturingPieceValue = PIECE_VALUE[Piece.QUEEN]; //use best possible promotion
	    	sideToMove = Util.opposing(sideToMove);
	    	gain[depth] = capturingPieceValue - gain[depth - 1];
	    	if(log.isDebugEnabled())
	    	{
	    		log.debug(String.format("init gain[%d]=%5d = (%-5d-%5d) if %s@%s is en-prise%s", 
	    			depth, gain[depth], capturingPieceValue, gain[depth - 1],
	    			state.getPosition().get(fromSquare).toChar(), Square.named(fromSquare), (isPromotion?" (promotion=Q)":"")));
	    	}
	    	attdef   ^= attackedBy;
	    	occupied ^= attackedBy;
	    	if(Util.bool(attackedBy & xrayCandidates))
	    		attdef |= Attacks.xrayAttackers(toSquare, xrayCandidates, attackedBy, occupied);
	    	fromSquare = state.getPosition().getSquareOfLeastValuablePiece(attdef, sideToMove);
	    } while (fromSquare != Bitmap.NOSQUARE ); //&& depth < gain.length - 1);// Util.bool(attackedBy));
	    return computeGain(gain, depth);
	}

	private static int computeGain(int[] gain, int depth) {
		if(log.isDebugEnabled()) 
	    	for(int d=0; d<depth; d++) log.debug(String.format("gain[%d]=%5d", d, gain[d]));
	    while (--depth != 0)
	    {
	    	int previousGainInverted = -gain[depth - 1];
	    	int currentGain = gain[depth];
			int newGain = -Math.max(previousGainInverted, currentGain);
	    	if(log.isDebugEnabled())
	    	{
	    		Object[] args = new Object[]{depth-1, newGain, previousGainInverted, currentGain};
	    		log.debug(String.format("gain[%d]=%5d = -max(%5d,%5d)", args));
	    	}
			gain[depth - 1] = newGain;
	    }
	    return gain[0];
	}

	private static boolean isPromotion(int capturingPiece, int toSq, int sideToMove) {
		if(PAWN == capturingPiece)
			return (sideToMove==0) ? Bitmap.rankNumber(toSq)==7 : Bitmap.rankNumber(toSq)==0; 
		return false;
	}

}
