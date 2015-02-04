package com.jeremybrooks.chess.movegen;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static com.jeremybrooks.chess.base.Square.*;

import java.util.List;

import com.jeremybrooks.chess.base.Pawn;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.PieceFactory;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.base.Piece.Color;
import com.jeremybrooks.chess.base.Square;
import com.jeremybrooks.chess.util.Util;

public class NonCaptureGenerator extends AbstractGenerator {

    @Override
    public void  generate(List<Integer> moves, int side) {
        long pieces;
        long pMoves = 0;
        long advanceTwo = 0;
        long promoters = 0;
        long empty;
        int to, from;

        //***************************************************************************
        //*                                                                         *
        //* Add all non-capturing pawn moves (promotions, advance-two, advance-one) *
        //*                                                                         *
        //***************************************************************************

        Position position = g.getPosition();
        long allPiecesByRank = position.getOccupied(0);
        empty = ~allPiecesByRank;

        Pawn pawn = new Pawn(side==Piece.WHITE?Color.W:Color.B);
        long advancesOne = pawn.advances(NOSQUARE, position);
        pMoves = advancesOne & ~(side==Piece.WHITE?EIGHTHRANK:FIRSTRANK);
        advanceTwo = side==Piece.WHITE
                ? ((advancesOne & THIRDRANK) << 8) & empty
                : ((advancesOne & SIXTHRANK) >> 8) & empty;
        promoters = (side==Piece.WHITE
                ? advancesOne & EIGHTHRANK
                : advancesOne & FIRSTRANK)
                & empty;
        // Pawn promotions
        while (morePieces(promoters)) {
            to = lowestBitNumber(promoters);
            from = squareBehind(to, side);
            for (int i = Piece.QUEEN; i >= Piece.KNIGHT; i--) {
                moves.add(Util.EncodeMove (from, to, Piece.ENCODED[Piece.PAWN], 0, Piece.ENCODED[i]));
                //g.legalMoves[depth]++;
                //g.addMove (move);
            }
            promoters = clearBit(promoters, to);
        }
        // Pawns advance two squares
        while (morePieces(advanceTwo)) {
            to = lowestBitNumber(advanceTwo);
            from = twoSquaresBehind(to, side);
            int move = Util.EncodeMove (from, to, Piece.ENCODED[Piece.PAWN], 0, 0);
//            if(isLegal(g, move)) //can't move if pinned
            {
                moves.add(move);
            }
            advanceTwo = clearBit(advanceTwo, to);
        }
        // Pawns advance one square
        while (morePieces(pMoves)) {
            to = lowestBitNumber(pMoves);
            from = squareBehind(to, side);
            int move = Util.EncodeMove (from, to, Piece.ENCODED[Piece.PAWN], 0, 0);
//            if(isLegal(g, move)) //can't move if pinned
            {
                moves.add(move);
            }
            pMoves = clearBit(pMoves, to);
        }

        //***************************************************************************
        //*                                                                         *
        //* Add all non-capturing knight, bishop, rook, queen, king moves           *
        //*                                                                         *
        //***************************************************************************

        for (int p = Piece.KNIGHT; p <= Piece.KING; p++) {
            pieces = position.getPieces (side, p);
            Piece piece = PieceFactory.fromBoardPiece((side==0?1:-1)*Piece.ENCODED[p]);
            while (morePieces(pieces)) {
                from = lowestBitNumber(pieces);
                long advances = piece.advances(from, position);
                pMoves = advances & empty;
                while (morePieces(pMoves)) {
                    to = lowestBitNumber(pMoves);
                    int move = Util.EncodeMove(from,to,Piece.ENCODED[p],0,0);
                    if(isLegal(g, move))
                    {
                        moves.add(move);
                    }
                    pMoves = clearBit(pMoves, to);
                }
                pieces = clearBit(pieces, from);
            }
        }

        //***************************************************************************
        //*                                                                         *
        //* Add castling moves, if any                                              *
        //*                                                                         *
        //***************************************************************************
        switch (side) {
        case Piece.WHITE:
            if(canWhiteShortCastle(g)){
                moves.add(castleMove(E1,G1));
            }
            if(canWhiteLongCastle(g)){
                moves.add(castleMove(E1,C1));
            }
            break;
        case Piece.BLACK:
            if(canBlackShortCastle(g)){
                moves.add(castleMove(E8,G8));
            }
            if(canBlackLongCastle(g)){
                moves.add(castleMove(E8,C8));
            }
            break;
        }
    }
    
    private static int castleMove(int fromSquare, int toSquare)
    {
        return Util.EncodeMove(fromSquare,toSquare,Piece.ENCODED[Piece.KING],0,0);
    }

}
