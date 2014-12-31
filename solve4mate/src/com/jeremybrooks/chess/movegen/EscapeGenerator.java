package com.jeremybrooks.chess.movegen;

import static com.jeremybrooks.chess.base.Bitmap.*;

import java.util.List;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.util.Util;

public class EscapeGenerator extends AbstractGenerator {

    @Override
    public int generate(List<Integer> moves, int side, int depth) {
        int checker;
        int n;
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

        n = g.numberOfLegalMoves[depth];
        //n = 0;
        Position position = g.getPosition();
        kingSq = position.getKingSquare(side);
        checkers = attackers(g, side, position.getKingSquare(side));
        switch (side) {
            case Bitmap.WHITE:
                promoteRank = EIGHTHRANK;
                enPassantRank = FIFTHRANK;
                break;
            case Bitmap.BLACK:
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


        if (Util.bitCount(checkers) == 1) {
            checker = lowestBitNumber(checkers);
            cap = Math.abs(position.getBoard(checker));

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

            while (morePieces(capturers)) {
                from = lowestBitNumber(capturers);
                mover = Math.abs(position.getBoard(from));

                if (TO_PIECE[mover] == PAWN && Util.bool(checkers & promoteRank)) {
                    // Pawn promotion
                    //if (!isPinned(g, from, checker, mover, cap)){
                    move = Util.EncodeMove(from,checker,mover,cap,0);
                    if(isLegal(g, move, side)){
//                        System.err.print("Adding pawn captures and promotes to any piece: ");
//                        Util.displayMove(move, false, false);
                        for (pro = QUEEN; pro >= KNIGHT; pro--) {
                            moves.add(Util.EncodeMove(from,checker, mover,cap,PIECE[pro])); n++;
                            //g.legalMoves[depth]++;
                        }
                    }
                } else if (TO_PIECE[mover] == PAWN && g.hasEnPassantOption()){
                    attackers = att.pawn[side][from];
                    if (Util.bool(attackers & (1L << enPassantSquare))){
                        // Pawn captures en Passant
                        to = enPassantSquare;
                        cap = PIECE[PAWN];
                        move = Util.EncodeMove(from,to,PIECE[PAWN],cap,0);
                        //if (!isPinned(g, from, to, PIECE[PAWN], cap)){
                        if (isLegal(g, move, side)){
//                            System.err.print("Added pawn captures via en-passant: ");
//                            Util.displayMove(move, false, false);
                            moves.add(move); n++;
                            //g.legalMoves[depth]++;
                        }
                    }                
                } else {
                    move = Util.EncodeMove(from,checker,mover,cap,pro);
                    //if (!isPinned(g, from, checker, mover, cap)){
                    if (isLegal(g, move, side)){
//                        System.err.print("Added capture the checking piece: ");
//                        Util.displayMove(move, false, false);
                        moves.add(move); n++;
                        //g.legalMoves[depth]++;
                        //g.addMove (move);
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
                g.numberOfLegalMoves[depth] = n;  //required for call to GenInter() below
                //DisplayBoard(interpose);
                n += GenerateInterpositions (g, moves, side, depth, interpose);
            }
        } else if (Util.bitCount (checkers) == 2) {  //Two pieces checking the king
        // Add king moves that would capture either checking piece
            kingMoves = att.king[kingSq] & checkers;
            while (morePieces(kingMoves)){
                to = lowestBitNumber(kingMoves);
                cap = Math.abs(position.getBoard(to));
                move = Util.EncodeMove(kingSq, to, PIECE[KING], cap, 0);
                if (isLegal (g, move, side)) {
                    //cap = abs (g.pos.board[to]);
//                    System.err.print("Adding king captures one of two checking pieces: ");
//                    Util.displayMove(move, false, false);
                    moves.add(move); n++;//EncodeMove(kingSq, to, PIECE[KING], cap, NONE);
                }

                kingMoves = clearBit(kingMoves, to);
            }
        } 

        // Add king moves to flight squares (and captures)
        kingMoves = att.king[kingSq] & ~position.getAllPieces(0);
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
            move = Util.EncodeMove(kingSq, to, PIECE[KING], cap, 0);
            if ( isLegal(g, move, side)){
//                System.err.print("Adding king escapes via flight square: ");
//                Util.displayMove(move, false, false);
                moves.add(move); n++;
            }
            kingMoves = clearBit(kingMoves, to);
        }

        //cout << "moves added: " << n << endl;
        g.numberOfLegalMoves[depth] = n;
        //cout << "legalMoves: " << g.legalMoves[depth] << endl;
        return g.numberOfLegalMoves[depth];
    }

}
