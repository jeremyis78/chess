package com.jeremybrooks.chess.eval;

import static com.jeremybrooks.chess.base.Bitmap.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.movegen.DefaultGenerator;
import com.jeremybrooks.chess.util.FenBuilder;
import com.jeremybrooks.chess.util.Util;

public class Evaluator {
    private static final Logger log = Logger.getLogger(Evaluator.class);
    public static final int CHECKMATE = 100000;    //value for checkmate
    public static final int PIECE_VALUE[] = 
    {
        100, // White Pawn
        325, // White Knight
        325, // White Bishop
        500, // White Rook
        975  // White Queen
    };

    private final static int BISHOP_PAIR_VALUE = 50;

    //These boards represent the Positional Value (in centipawns)
    //on the board for the named piece (a1=0, b1=1,...,h8=63).
    //These are from white's perspective
    private static int knightPV[][] = new int[][]{
        //White
        {   5,  5, 12, 12, 12, 12,  5,  5, // first rank (a1-h1)
            10, 15, 20, 20, 20, 20, 15, 10, // second rank  
            10, 20, 30, 20, 20, 30, 20, 10, // 
            10, 20, 30, 20, 20, 30, 20, 10, //    .
            10, 40, 45, 20, 20, 45, 40, 10, //    .
            10, 20, 30, 20, 20, 30, 20, 10, //    .
            10, 15, 20, 20, 20, 20, 15, 10, //
            5, 10, 12, 12, 12, 12, 10,  5  // eighth rank
        },
        //Black
        {   
            5, 10, 12, 12, 12, 12, 10,  5, // first rank
            10, 15, 20, 20, 20, 20, 15, 10, //
            10, 20, 30, 20, 20, 30, 20, 10, //    .
            10, 40, 45, 20, 20, 45, 40, 10, //    .
            10, 20, 30, 20, 20, 30, 20, 10, //    .
            10, 20, 30, 20, 20, 30, 20, 10, // 
            10, 15, 20, 20, 20, 20, 15, 10, // seventh rank  
            5,  5, 12, 12, 12, 12,  5,  5  // eighth rank
        }
    };

    private static int bishopPV[][] = new int[][]{
        //White
        {    10, 10,  8, 15, 15,  8, 10, 10, // first rank (a1-h1)
            10, 50, 10, 10, 10, 10, 50, 10, // second rank
            10, 20, 20, 25, 25, 20, 20, 10, //
            10, 20, 45, 20, 20, 45, 20, 10, //    .
            15, 45, 40, 30, 30, 40, 45, 15, //    .
            15, 20, 20, 20, 20, 20, 20, 15, //    .
            15, 20, 20, 20, 20, 20, 20, 15, //
            10, 15, 20, 20, 20, 20, 15, 10  // eighth rank
        },
        //Black
        {
            10, 15, 20, 20, 20, 20, 15, 10, // first rank
            15, 20, 20, 20, 20, 20, 20, 15, //
            15, 20, 20, 20, 20, 20, 20, 15, //    .
            15, 45, 40, 30, 30, 40, 45, 15, //    .
            10, 20, 45, 20, 20, 45, 20, 10, //    .
            10, 20, 20, 25, 25, 20, 20, 10, //        
            10, 50, 10, 10, 10, 10, 50, 10, // seventh rank
            10, 10,  8, 15, 15,  8, 10, 10  // eighth rank
        }
    };

    private static int rookPV[][] = new int[][]{
        //White
        {    20,  5,  5, 45, 45, 45,  5, 20, // first rank (a1-h1)
            5,  5,  5, 18, 20, 10,  5,  5, // second rank
            10, 10, 10, 13, 15, 10, 10, 10, //
            10, 10, 10, 10, 12, 10, 10, 10, //    .
            10, 10, 10, 10, 10, 10, 10, 10, //    .
            10, 10, 10, 10, 10, 10, 10, 10, //    .
            10, 10, 10, 10, 10, 10, 10, 10, //
            10, 10, 10, 10, 10, 10, 10, 10 // eighth rank
        },
        //Black
        {    10, 10, 10, 10, 10, 10, 10, 10, // first rank
            10, 10, 10, 10, 10, 10, 10, 10, //
            10, 10, 10, 10, 10, 10, 10, 10, //    .
            10, 10, 10, 10, 10, 10, 10, 10, //    .
            10, 10, 10, 10, 12, 10, 10, 10, //    .
            10, 10, 10, 13, 15, 10, 10, 10, //
            5,  5,  5, 18, 20, 10,  5,  5, // seventh rank
            20,  5,  5, 45, 45, 45,  5, 20  // eighth rank        
        }
    };


    private DefaultGenerator mg;


    public DefaultGenerator getMoveGenerator() {
        return mg;
    }

    public void setMoveGenerator(DefaultGenerator mg) {
        this.mg = mg;
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
        int mateScore = 0;
        mg.setGameState(g); //FIXME: works for now but needs fixing (F1): gross!
        
        Position position = g.getPosition();
        if (isCheckMated(g, side, depth))
        {
                //Mate-in-1 > Mate-in-2 > Mate-in-3 > ... etc  (white is mated is negative, black is 
                mateScore = (CHECKMATE - depth) * (side==WHITE?-1:+1);  
        }

        List<Term> whiteTerms = new ArrayList<>();
        List<Term> blackTerms = new ArrayList<>();
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
//                System.out.println(color + " - " + piece + ": " + pcScore + " (#" + numPieces+")");
            }
            if(color==WHITE) wMaterialScore += pieceScore;
            else             bMaterialScore += pieceScore;
        }
        whiteTerms.add(new Term(wMaterialScore, "white material"));
        blackTerms.add(new Term(bMaterialScore, "black material"));
        
        int materialAdjustment = new MaterialAdjustmentTerm().evaluate(g);
        
        int finalScore = sum(whiteTerms) - sum(blackTerms) + materialAdjustment + mateScore;
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

//        if(isEval){
//            log.debug("white score: "+ wTotalScore + " = " + wMaterialScore + " + " + wPositionalScore);
//            log.debug("black score: "+ bTotalScore + " = " + bMaterialScore + " + " + bPositionalScore);
//            log.debug("mate score : "+ mateScore);
//        }
        return finalScore;
    }

    private int sum(List<Term> terms) {
        int sum = 0;
        for(Term term: terms)
        {
            sum += term.getScore();
        }
        return sum;
    }

    boolean isCheckMated(GameState g, int side, int depth)
    {
        //Does king have legal moves?
        int moves[] = new int[70];
        int numMoves = mg.generateKingEscapes(moves, side, depth);
        //number of moves may be helpful for evaluation tuning because 1 or 2 moves
        //limits the branching factor so that could be use to feed into the overall evaluation score
        Position position = g.getPosition();
        if (numMoves == 0 &&   mg.isAttacked(g, side, position.getKingSquare(side)))
        {
            return true;
        }
        return false;
    }

}
