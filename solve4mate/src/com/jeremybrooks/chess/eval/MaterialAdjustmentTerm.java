package com.jeremybrooks.chess.eval;

import static com.jeremybrooks.chess.base.Bitmap.*;

import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.util.Util;

public class MaterialAdjustmentTerm extends EvalTerm {

    static final int PAWN_COUNT_BOUNDARY = 5;
    static final int KNIGHT_ADJUSTMENT_PER_PAWN = 6; //ideally 6.25
    static final int ROOK_ADJUSTMENT_PER_PAWN = 13;  //ideally 12.5
    private static final String DESC = "knights/rooks material adjustment given either many or few pawns";
    
    public MaterialAdjustmentTerm() {
        setDescription(DESC);
    }

    @Override
    public int evaluate(GameState gameState) {
        // Count pieces
        Position position = gameState.getPosition();
        int[][] count = new int[2][5];
        int[][] pieceValue = new int[2][5];
        for(int color = 0; color<2; color++){
            for (int piece = Piece.PAWN; piece <= Piece.QUEEN; piece++){
                int numPieces = Util.bitCount(position.getPieces(color,piece));
                int pcScore = Evaluator.PIECE_VALUE[piece] * numPieces;
                count[color][piece] = numPieces;
                pieceValue[color][piece] = pcScore;
            }
        }
        int score = 0;
        int wKnightAdjustment = knightAdjustment(count[Piece.WHITE][Piece.KNIGHT], count[Piece.WHITE][Piece.PAWN]);
        int bKnightAdjustment = knightAdjustment(count[Piece.BLACK][Piece.KNIGHT], count[Piece.BLACK][Piece.PAWN]);
        score += wKnightAdjustment - bKnightAdjustment;
        int wRookAdjustment = rookAdjustment(count[Piece.WHITE][Piece.ROOK], count[Piece.WHITE][Piece.PAWN]);
        int bRookAdjustment = rookAdjustment(count[Piece.BLACK][Piece.ROOK], count[Piece.BLACK][Piece.PAWN]);
        score += wRookAdjustment - bRookAdjustment;
        return score;
    }

    
    private int knightAdjustment(int numKnights, int numPawns)
    {
        //knights can jump so they are worth more with more pawns around; worth less with fewer
        int adjustment = 0;
        int pawnCountBoundary = PAWN_COUNT_BOUNDARY;
        if(numKnights > 0)
        {
            if(numPawns > pawnCountBoundary)
            {
                adjustment += numKnights * (numPawns - pawnCountBoundary) * KNIGHT_ADJUSTMENT_PER_PAWN; //+6 for every pawn over the boundary
            } else {
                //when at the boundary, adjustment is zero
                adjustment -= numKnights * (pawnCountBoundary - numPawns) * KNIGHT_ADJUSTMENT_PER_PAWN; //-6 for every pawn under the boundary
            }
        }
        return adjustment;
    }

    private int rookAdjustment(int numRooks, int numPawns)
    {
        //rooks need open files so they are worth more with fewer pawns around; less with more
        int adjustment = 0;
        int pawnCountBoundary = 5;
        if(numRooks > 0)
        {
            if(numPawns > pawnCountBoundary)
            {
                adjustment -= numRooks * (numPawns - pawnCountBoundary) * ROOK_ADJUSTMENT_PER_PAWN;
            } else {
                //when at the boundary, adjustment is zero
                adjustment += numRooks * (pawnCountBoundary - numPawns) * ROOK_ADJUSTMENT_PER_PAWN;
            }
        }
        return adjustment;
    }

}
