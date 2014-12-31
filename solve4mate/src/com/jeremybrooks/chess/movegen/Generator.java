package com.jeremybrooks.chess.movegen;

import java.util.List;

import com.jeremybrooks.chess.base.GameState;

/**
 * Implement this to define your own move generator.
 * 
 * @author jeremy
 *
 */
public interface Generator {

    /**
     * Generate moves, populating the given move list
     * 
     * @param moves the move list that will be populated with generated moves
     * @param side the side to move
     * @param depth the depth at which these moves will be generated
     */
    public void generate(List<Integer> moves, int side, int depth);

    public boolean isAttacked(GameState g, int side, int sq);
    
    // Returns a bitbrd of the pieces (excluding the king) attacking 
    // "square".  "side" represents the color/side whose pieces we want to
    // see that are under attack.
    // To see all the black pieces attacking e4 do this:
    //
    //        attacks = Attackers(g, Color.WHITE, E4);
    //
    // To see all the white pieces attacking g8 do this;
    //
    //        attacks = Attackers(g, Color.BLACK, G8);
    //
    //NOTE: the king is not included in the attackers
    public long attackers(GameState g, int sideUnderAttack, int squareUnderAttack);

    public boolean canWhiteShortCastle(GameState g, int side);

    public boolean canWhiteLongCastle(GameState g, int side);

    public boolean canBlackShortCastle(GameState g, int side);

    public boolean canBlackLongCastle(GameState g, int side);

    public void setGameState(GameState gameState);
}