package com.jeremybrooks.chess.movegen;

import static com.jeremybrooks.chess.base.Bitmap.EIGHTHRANK;
import static com.jeremybrooks.chess.base.Bitmap.FIFTHRANK;
import static com.jeremybrooks.chess.base.Bitmap.FIRSTRANK;
import static com.jeremybrooks.chess.base.Bitmap.FOURTHRANK;
import static com.jeremybrooks.chess.base.Bitmap.clearBit;
import static com.jeremybrooks.chess.base.Bitmap.lowestBitNumber;
import static com.jeremybrooks.chess.base.Piece.ENCODED;
import static com.jeremybrooks.chess.base.Piece.KNIGHT;
import static com.jeremybrooks.chess.base.Piece.PAWN;
import static com.jeremybrooks.chess.base.Piece.QUEEN;
import static com.jeremybrooks.chess.base.Piece.TO_PIECE;

import java.util.List;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.base.Square;
import com.jeremybrooks.chess.util.Util;

public class EscapeGenerator extends AbstractGenerator {
	private static final Logger log = Logger.getLogger(EscapeGenerator.class);

    @Override
    public void generate(List<Integer> moves, int side) {
        int checker;
        int from = 0;
        int to = 0;
        int mover = 0;
        int cap = 0;
        int pro = 0;
        int kingSq = -1;
        int move;
        long checkers = 0;            // pieces checking 'side's king
        long capturers;           // pieces that can caputre the checker
        long attackers;
        long promoteRank = 0;
        long enPassantRank = 0;
        long interpose = 0;
        long kingMoves;

        Position position = g.getPosition();
        kingSq = position.getKingSquare(side);
        checkers = attackers(g, side, position.getKingSquare(side));
        if(log.isDebugEnabled())
        {
        	log.debug("gen escapes for "+Piece.asString(side, Piece.KING)+" on " + Square.named(kingSq));
        }
        switch (side) {
            case Piece.WHITE:
                promoteRank = EIGHTHRANK;
                enPassantRank = FIFTHRANK;
                break;
            case Piece.BLACK:
                promoteRank = FIRSTRANK;
                enPassantRank = FOURTHRANK;
                break;
        }

        //***************************************************************************
        //*                                                                         *
        //* Single checker:                                                         *
        //*    Generate captures to checker's square                                *
        //*    If that checker is not a knight, generate interposing moves.         *
        //*                                                                         *
        //* Two checkers:                                                           *
        //*    Generate king captures to either checker's square who is left en     *
        //*    prise (unprotected).                                                 *
        //*                                                                         *
        //* Always:                                                                 *
        //*    Generate king moves to flight squares                                *
        //*                                                                         *
        //***************************************************************************


        int numCheckers = Util.bitCount(checkers);
		if (numCheckers == 1) {
            checker = lowestBitNumber(checkers);
            cap = Math.abs(position.getBoard(checker));
            if(log.isDebugEnabled())
            {
            	log.debug("checker: "+Piece.asString(Util.opposing(side), TO_PIECE[cap])+" on " + Square.named(checker));
            }

            //Generate captures to checker's square
            capturers = attackers (g, Util.opposing(side), checker);

            //Add to 'capturers' pawns that would capture enpassant
            int enPassantSquare = g.getEnPassantSquare();
            if(TO_PIECE[cap] == PAWN && g.hasEnPassantOption()){
                attackers = position.getPieces(side, PAWN);
                if (Util.bool((attackers & enPassantRank) & att.mask[checker-1]))
                {
                    capturers = capturers | att.mask[checker-1];
                }
                if (Util.bool((attackers & enPassantRank) & att.mask[checker+1]))
                {
                    capturers = capturers | att.mask[checker+1];
                }
            }
            if(log.isDebugEnabled())
            {
            	log.debug("capturers on: "+Util.formatSquares(capturers));
            }

            while (morePieces(capturers)) {
                from = lowestBitNumber(capturers);
                mover = Math.abs(position.getBoard(from));

                if (TO_PIECE[mover] == PAWN && Util.bool(checkers & promoteRank)) {
                    // Pawn promotion
                    //if (!isPinned(g, from, checker, mover, cap)){
                    move = Util.EncodeMove(from,checker,mover,cap,0);
                    if(isLegal(g, move)){
                        if(log.isDebugEnabled())
                        {
                        	log.debug("add pawn captures checker and promotes to QRBN: "+Util.formatMoveInFan(move, g));
                        }
                        for (pro = QUEEN; pro >= KNIGHT; pro--) {
                            moves.add(Util.EncodeMove(from,checker, mover,cap,ENCODED[pro]));
                        }
                    }
                } else if (TO_PIECE[mover] == PAWN && g.hasEnPassantOption()){
                    attackers = att.pawn[side][from];
                    if (Util.bool(attackers & (1L << enPassantSquare))){
                        // Pawn captures en Passant
                        to = enPassantSquare;
                        cap = ENCODED[PAWN];
                        move = Util.EncodeMove(from,to,ENCODED[PAWN],cap,0);
                        //if (!isPinned(g, from, to, PIECE[PAWN], cap)){
                        if (isLegal(g, move)){
                            if(log.isDebugEnabled())
                            {
                            	log.debug("add e.p. capture of checker: "+Util.formatMoveInFan(move, g));
                            }
                            moves.add(move);
                        }
                    }                
                } else {
                    move = Util.EncodeMove(from,checker,mover,cap,pro);
                    //if (!isPinned(g, from, checker, mover, cap)){
                    if (isLegal(g, move)){
                        if(log.isDebugEnabled())
                        {
                        	log.debug("add capture checker: "+Util.formatMoveInFan(move, g));
                        }
                        moves.add(move);
                    }
                }
                capturers = clearBit(capturers, from);
            }

            if(cap != KNIGHT){
                //Generate interpositions (still a single checking piece)
                //compute squares between king and the checking piece
                
                //interpose = getInterposingSquares(kingSq, checker);
                //
                // TODO: Replace the code below with the code above by
                //       pulling out the code below into 
                //       getInterposingSquares(int sq1, int sq2)

                if (Util.bool(att.plus8[kingSq] & checkers))
                {
                    //attack from north
                    interpose = att.plus8[kingSq] & att.minus8[checker];
                }
                else if (Util.bool(att.plus9[kingSq] & checkers))
                {
                    //attack from north-east
                    interpose = att.plus9[kingSq] & att.minus9[checker];
                }
                else if (Util.bool(att.plus1[kingSq] & checkers))
                {
                    //attack from east
                    interpose = att.plus1[kingSq] & att.minus1[checker];
                }
                else if (Util.bool(att.minus7[kingSq] & checkers))
                {
                    //attack from south-east
                    interpose = att.minus7[kingSq] & att.plus7[checker];
                }
                else if (Util.bool(att.minus8[kingSq] & checkers))
                {
                    //attack from south
                    interpose = att.minus8[kingSq] & att.plus8[checker];
                }
                else if (Util.bool(att.minus9[kingSq] & checkers))
                {
                    //attack from south-west
                    interpose = att.minus9[kingSq] & att.plus9[checker];
                }
                else if (Util.bool(att.minus1[kingSq] & checkers))
                {
                    //attack from west
                    interpose = att.minus1[kingSq] & att.plus1[checker];
                }
                else if (Util.bool(att.plus7[kingSq] & checkers))
                {
                    //attack from northwest
                    interpose = att.plus7[kingSq] & att.minus7[checker];
                }
                //DisplayBoard(interpose);
                if(log.isDebugEnabled())
                {
                	log.debug("interposing candidate squares: "+Util.formatSquares(interpose));
                }
                generateInterpositions (moves, side, interpose);
            }
        } 
        if (numCheckers == 2) {  //Two pieces checking the king
        // Add king moves that would capture either checking piece
            kingMoves = att.king[kingSq] & checkers;
            while (morePieces(kingMoves)){
                to = lowestBitNumber(kingMoves);
                cap = Math.abs(position.getBoard(to));
                move = Util.EncodeMove(kingSq, to, ENCODED[Piece.KING], cap, 0);
                if (isLegal (g, move)) {
                    if(log.isDebugEnabled())
                    {
                    	log.debug("add king captures checker: "+Util.formatMoveInFan(move, g));
                    }
                    moves.add(move);
                }
                kingMoves = clearBit(kingMoves, to);
            }
        } 

        // Add king moves to flight squares (and captures)
        //Candidates include empty squares or any opposing piece that's NOT a checker
        //Moves capturing the checker have already been added above (we don't want to generate the same move twice
        //so we exclude them here)
        long emptySquares = ~position.getOccupied(0);
        long opposingPiecesNotCheckingUs = ~checkers & position.getAllPiecesExceptKing(Util.opposing(side));
		long candidateSquares = emptySquares | opposingPiecesNotCheckingUs;
        kingMoves = att.king[kingSq] & candidateSquares;
        if(log.isDebugEnabled())
        {
        	log.debug("king candidate flight/capture squares: "+Util.formatSquares(kingMoves));
        }
        while (morePieces(kingMoves)){
            to = lowestBitNumber(kingMoves);
            //Same reason as above...hafta make sure the king doesn't just
            //move away from the sliding checking piece.
            //
            if (position.isNotEmpty(to)) {
                cap = Math.abs(position.getBoard(to));
            } else {
                cap = 0;
            }
            move = Util.EncodeMove(kingSq, to, ENCODED[Piece.KING], cap, 0);
            if ( isLegal(g, move)){
                if(log.isDebugEnabled())
                {
                	log.debug("add king flight/capture: "+Util.formatMoveInFan(move, g));
                }
                moves.add(move);
            }
            kingMoves = clearBit(kingMoves, to);
        }
    }

}
