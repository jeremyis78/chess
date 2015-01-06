package com.jeremybrooks.chess.movegen;

import java.util.ArrayList;
import java.util.List;

import com.jeremybrooks.chess.base.GameState;

public class DefaultGenerator {

    public static ArrayList<Integer> newMoveList() {
        return new ArrayList<>(AbstractGenerator.MAX_NUM_GENERATED_MOVES);
    }

    private Generator capturesGenerator;
    private Generator nonCapturesGenerator;
    private Generator escapeGenerator;
    private GameState g;

    
    public DefaultGenerator()
    {
        capturesGenerator = new CaptureGenerator();
        nonCapturesGenerator = new NonCaptureGenerator();
        escapeGenerator = new EscapeGenerator();
    }
    
    public void setGameState(GameState g)
    {
        this.g = g;
        capturesGenerator.setGameState(g);
        nonCapturesGenerator.setGameState(g);
        escapeGenerator.setGameState(g);
    }

    public Generator getBaseMoveGenerator()
    {
        return capturesGenerator;
    }
    
    public List<Integer> generateMoves(int side, boolean dangerousMovesOnly) {
        // Generate legal moves from this position
        List<Integer> moves = DefaultGenerator.newMoveList();
        if (!getBaseMoveGenerator().isAttacked(g, side, g.getPosition().getKingSquare(side))){
            generateCaptures(moves, side);
            if(!dangerousMovesOnly) 
                generateNonCaptures(moves, side);
        } else {
            generateKingEscapes(moves, side);
        }
        return moves;
    }
    
    public void generateCaptures(List<Integer> moves, int side)
    {
        capturesGenerator.generate(moves, side);
    }

    public void generateNonCaptures(List<Integer> moves, int side)
    {
        nonCapturesGenerator.generate(moves, side);
    }
    
    public void generateKingEscapes(List<Integer> moves, int side)
    {
        escapeGenerator.generate(moves, side);
    }

    public boolean isAttacked(GameState g2, int side, int square) {
        return getBaseMoveGenerator().isAttacked(g2, side, square);
    }

}
