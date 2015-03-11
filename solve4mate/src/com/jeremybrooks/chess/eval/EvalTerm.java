package com.jeremybrooks.chess.eval;

import com.jeremybrooks.chess.base.GameState;

public abstract class EvalTerm {
    private String description;
    
    /**
     * Returns the score for this term's evaluation.
     * 
     * The returned score is from white's perspective.
     * That is, the more positive the score the better the
     * evaluation is for white and conversely the more negative
     * the better for black.
     *  
     * @param position The position to score
     * @return a signed integer representing the evaluation of Position
     */
    public abstract int evaluate(GameState state);
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
