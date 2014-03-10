/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 9, 2010
 */
package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;
/**
 * TODO: Make the move generator functions return an int[]
 * of moves and remove the depth variable being passed in
 * 
 * @author jeremy
 *
 */
public class MoveGenerator {

	public final static int BISHOP_OR_QUEEN = 0x01;  //mask to determine Bishop/Queen            
	public final static int ROOK_OR_QUEEN   = 0x02;  //mask to determine Rook/Queen

	
	
	Attacks att;

	public MoveGenerator(){
		att = new Attacks();
	}
	
	
	// Return the rank number (zero-based)
	private int Rank(int sq){ return (sq / 8); } //integer division

	// Return the file number (zero-based)
	private int File(int sq){ return (sq % 8); }


//	void ClearPiece(long &board, int bit){
//		board &= ~(1L << bit);
//	}

	public long ClearPiece(long board, int bit){
		return Bitmap.clearBit(board, bit);
	}

	public int FirstPiece(long pieces){
		return Bitmap.lowestBitNumber(pieces);
	}

	private int LastPiece(long pieces){
		return Bitmap.highestBitNumber(pieces);
	}

	//unsigned int PieceCount(bitbrd pieces){
//		unsigned int n;
//		for (n = 0; pieces != 0; n++, pieces &= (pieces - 1));
//		return unsigned(n);
	//}


	private boolean Occupied(GameState g, int sq){
		return g.pos.board[sq] != BOARD_EMPTY_SQUARE;
	}


	//These functions return the occupied status (middle six bits)
	//of a Rank, File or Diagonal.  For a diagonal (R45, L45) whose length
	//is not always 8 it returns the diagonal length minus the outer 2 bits
	//for the occupied status  

	private static byte Status(long b, int sq){
		//Compute the x and y coordinates from 'sq' (aka, reverse linear index)
		//
		//No transformation function T is needed since the ranks
		//are already aligned.
		
		//'x' not needed here
		int y = sq / 8;
		int shiftby = y * 8 + 1;

		//SHIFT 'b' RIGHT by ((y * 8) + 1) the AND with 63 (for the 6 bits)
		return (byte) ((b >> shiftby) & 63);
	}

	private static byte Status90(long b, int sq){
		//Compute the x and y coordinates from 'sq' (aka, reverse linear index)
		//
		//The transformation function T for x and y is 
		// T(x1, y1) = (y1, x1)   (x and y are swapped)
		
		int x = sq % 8;
		//'y' not needed
		int shiftby = x * 8 + 1;

		//SHIFT 'b' RIGHT by ((x * 8) + 1) then AND with 63 (for the 6 bits)
		return (byte) ((b >> shiftby) & 63);
	}


	private static byte Status45L(long b, int sq){
	    
	    //for diagonals of length 3 or less, status should be zero
	    
	    int x = sq % 8;
	    int y = sq / 8;
	    
	    byte temp = 0;
	    switch (x+y){
	    case 0: 
	    	temp = (byte) 0;                                      //a1-a1 diag
	    	break;
	    case 1: 
	    	temp = (byte) 0; //(unsigned char)((b >> 1+1) & 1);   //b1-a2 diag
	    	break;
	    case 2: 
	    	temp = (byte)((b >> 3+1) & 1);        //c1-a3 diag
	    	break;
	    case 3: 
	    	temp = (byte)((b >> 6+1) & 3);        //d1-a4 diag
	    	break;
	    case 4: 
	    	temp = (byte)((b >> 10+1) & 7);       //e1-a5 diag
	    	break;
	    case 5: 
	    	temp = (byte)((b >> 15+1) & 15);      //f1-a6 diag
	    	break;
	    case 6: 
	    	temp = (byte)((b >> 21+1) & 31);      //g1-a7 diag
	    	break;
	    case 7: 
	    	temp = (byte)((b >> 28+1) & 63);      //h1-a8 diag
	    	break;
	    case 8: 
	    	temp = (byte)((b >> 36+1) & 31);      //h2-b8 diag
	    	break;
	    case 9: 
	    	temp = (byte)((b >> 43+1) & 15);      //h3-c8 diag
	    	break;
	    case 10: 
	    	temp = (byte)((b >> 49+1) & 7);      //h4-d8 diag
	    	break;
	    case 11: 
	    	temp = (byte)((b >> 54+1) & 3);      //h5-e8 diag
	    	break;
	    case 12: 
	    	temp = (byte)((b >> 58+1) & 1);      //h6-f8 diag
	    	break;
	    case 13: 
	    	temp = (byte) 0; //(unsigned char)((b >> 61+1) & 1); //h7-g8 diag
	    	break;
	    case 14: 
	    	temp = (byte) 0;                                     //h8-h8 diag
	    	break;
	    }
	    return temp;
	}


	private static byte Status45R(long b, int sq){

	    //for diagonals of length 3 or less, status should be zero
	    
	    //Note the difference in x and y from Status45L()
	    //We perform the transformation function T on x and y
	    //where T(x1, y1) = (7 - x1, y1)
	    
	    int x = 7 - (sq % 8);  
	    int y = sq / 8;
	    
	    byte temp = 0;
	    switch (x+y){
	    case 0: 
	    	temp = (byte) 0;                                   //h1-h1 diag
	    	break;
	    case 1: 
	    	temp = (byte) 0;                                   //g1-h2 diag
	    	break;
	    case 2: 
	    	temp = (byte)((b >> 3+1) & 1);     //f1-h3 diag
	    	break;
	    case 3: 
	    	temp = (byte)((b >> 6+1) & 3);     //e1-h4 diag
	    	break;
	    case 4: 
	    	temp = (byte)((b >> 10+1) & 7);    //d1-h5 diag
	    	break;
	    case 5: 
	    	temp = (byte)((b >> 15+1) & 15);   //c1-h6 diag
	    	break;
	    case 6: 
	    	temp = (byte)((b >> 21+1) & 31);   //b1-h7 diag
	    	break;
	    case 7: 
	    	temp = (byte)((b >> 28+1) & 63);   //a1-h8 diag
	    	break;
	    case 8: 
	    	temp = (byte)((b >> 36+1) & 31);   //a2-g8 diag
	    	break;
	    case 9: 
	    	temp = (byte)((b >> 43+1) & 15);   //a3-f8 diag
	    	break;
	    case 10: 
	    	temp = (byte)((b >> 49+1) & 7);   //a4-e8 diag
	    	break;
	    case 11: 
	    	temp = (byte)((b >> 54+1) & 3);   //a5-d8 diag
	    	break;
	    case 12: 
	    	temp = (byte)((b >> 58+1) & 1);   //a6-c8 diag
	    	break;
	    case 13: 
	    	temp = (byte) 0;                                  //a7-b8 diag
	    	break;
	    case 14: 
	    	temp = (byte) 0;                                  //a8-a8 diag
	    	break;
	    }
	    return temp;
	}

	public int[] generate(GameState g, int side, int depth)
	{
	    GenerateCaptures(g, g.moves, side, depth);
	    GenerateNonCaptures(g, g.moves, side, depth);
		return g.moves;
	}
	
	//
	// The move generation functions
	//
	// I followed generating the moves in a piece-wise fashion.
	// The move generation is divided up into three sections:
	//  1) captures (includes captures and all pawn promotions) 
	//  2) non-captures 
	//  3) king escapes (moves for when the king is in check)
	//
	// This allows me the flexibility to add things like quiescent search
	// which helps minimize the horizon effect by extending the search until
	// only a "quiet" position is encountered.  Basically it means to finish
	// of any sequence of captures before evaluating the board position.
	// Having piece-wise move generation allows me to only generate captures
	// when in the future I write the quiescent search.


	// Side effect: g.legalMoves[depth] has the number of moves
	// found in this function added to it after this function completes
	public int GenerateCaptures (GameState g, int moves[], int side, int depth)
	{
	    // This includes pawns that capture to promote to a Q,R,B,N

	    int to = 0;
	    int from = 0;
	    int cap = 0;
	    int pro = 0;
	    int n;       //move index counter
	    int mover;
	    int move;
	    long pieces;
	    long pieceAttacks = 0;    //must be zeroed
	    long attackedPieces;      //as in "the enemy pieces that are attacked"

	    n = g.numberOfLegalMoves[depth];

	    for (int p = PAWN; p <= KING; p++) {
	        mover = PIECE[p];
	        pieces = g.pos.getPieces (side, p);
	        while (morePieces(pieces)) {
	            from = FirstPiece (pieces);
	            pro = 0;
	            pieceAttacks = 0;
	            switch (p) {
	            case PAWN:
	                pieceAttacks = att.pawn[side][from];
	                pro = isPawnPromotion(side, from);

	                //EnPassant captures
	                if (g.enPassantSq[depth] != NOSQUARE)
	                {
	                    if (Util.bool(pieceAttacks & (1L << g.enPassantSq[depth])))
	                    {
	                        to = g.enPassantSq[depth];
	                        cap = PIECE[PAWN];
	                        moves[n++] = EncodeMove (from, to, PIECE[PAWN], cap, 0);
	                    }
	                }
	                break;
	            case KNIGHT:
	                pieceAttacks = att.knight[from];
	                break;
	            case BISHOP:   //fall through
	            case ROOK:     //fall through
	            case QUEEN:
	                if (Util.bool(mover & ROOK_OR_QUEEN)) {
	                    pieceAttacks |= RookAttacks (g, from);
	                }
	                if (Util.bool(mover & BISHOP_OR_QUEEN)) {
	                    pieceAttacks |= BishopAttacks (g, from);
	                }
	                break;
	            case KING:
	                pieceAttacks = att.king[from];
	                break;
	            }

	            //Add or update which squares are currently attacked
	            //Following line doesn't do anythign right now!
	            g.attacked[depth] |= pieceAttacks;

	            attackedPieces = pieceAttacks & g.pos.getOpponentPiecesExceptKing(side);
	            while (morePieces(attackedPieces)) {
	                to = FirstPiece (attackedPieces);
	                cap = Math.abs(g.pos.board[to]);
	                if (!Util.bool(pro)) {     //Capture only
	                	//TODO: make sure king does not move into check!!!!!
	                	move = EncodeMove(from, to, PIECE[p], cap, 0);  
	                	//if((piece)p == KING && !isAttacked(g, side, to)){
	                	if(p == KING && isLegal(g, move, side)){
	                		moves[n++] = move;
	                	} else {
	                		moves[n++] = move;
	                	}
	                } else {        //Capture and promotion
	                	for (int i = QUEEN; i >= KNIGHT; i--) {
	                		moves[n++] = EncodeMove (from, to, PIECE[p], cap, PIECE[i]);
	                	}
	                }
	                attackedPieces = ClearPiece (attackedPieces, to);
	            }
	            pieces = ClearPiece (pieces, from);
	        }
	    }
	    g.numberOfLegalMoves[depth] = n;
	    return g.numberOfLegalMoves[depth];
	}


	// Side effect: g.legalMoves[depth] has the number of moves
	// found in this function added to it after this function completes
	public int GenerateNonCaptures (GameState g, int moves[], int side, int depth)
	{
	    long pieces;
	    long pMoves = 0;
	    long advanceTwo = 0;
	    long promoters = 0;
	    long empty;
	    int n;
	    int to, from;
	    int move = 0; //the encoded move!!!! move_t

	    //***************************************************************************
	    //*                                                                         *
	    //* Add all non-capturing pawn moves (promotions, advance-two, advance-one) *
	    //*                                                                         *
	    //***************************************************************************

	    n = g.numberOfLegalMoves[depth];
	    empty = ~g.pos.all[ALL];

	    switch (side) {
	        case Color.WHITE:
	        	long whitePawns = g.pos.getPawns(Color.WHITE);
	            pMoves = (whitePawns << 8) & empty & ~EIGHTHRANK;
	            // 'pMoves' is all moves except those to the eighth rank
	            promoters = (whitePawns << 8) & empty & EIGHTHRANK;
	            // 'promoters' is only the moves to the eighth rank
	            advanceTwo = whitePawns & SECONDRANK;
	            advanceTwo = (advanceTwo << 8) & empty;
	            advanceTwo = (advanceTwo << 8) & empty;
	            break;
	        case Color.BLACK:
	        	long blackPawns = g.pos.getPawns(Color.BLACK);
	            pMoves = (blackPawns >> 8) & empty & ~FIRSTRANK;
	            // 'pMoves' is all moves except those to the first rank
	            promoters = (blackPawns >> 8) & empty & FIRSTRANK;
	            // 'promoters' is only the moves to the first rank
	            advanceTwo = blackPawns & SEVENTHRANK;
	            advanceTwo = (advanceTwo >> 8) & empty;
	            advanceTwo = (advanceTwo >> 8) & empty;
	            break;
	    }
	    // Pawn promotions
	    while (morePieces(promoters)) {
	        to = FirstPiece (promoters);
	        from = minusOneRank(side, to);
	        for (int i = QUEEN; i >= KNIGHT; i--) {
	            moves[n++] = EncodeMove (from, to, PIECE[PAWN], 0, PIECE[i]);
	            //g.legalMoves[depth]++;
	            //g.addMove (move);
	        }
	        promoters = ClearPiece(promoters, to);
	    }
	    // Pawns advance two squares
	    while (morePieces(advanceTwo)) {
	        to = FirstPiece (advanceTwo);
	        from = minusTwoRank(side, to);
	        moves[n++] = EncodeMove (from, to, PIECE[PAWN], 0, 0);
	        advanceTwo = ClearPiece(advanceTwo, to);
	    }
	    // Pawns advance one square
	    while (morePieces(pMoves)) {
	        to = FirstPiece (pMoves);
	        from = minusOneRank(side, to);
	        moves[n++] = EncodeMove (from, to, PIECE[PAWN], 0, 0);
	        pMoves = ClearPiece (pMoves, to);
	    }

	    //***************************************************************************
	    //*                                                                         *
	    //* Add all non-capturing knight, bishop, rook, queen, king moves           *
	    //*                                                                         *
	    //***************************************************************************

	    for (int p = KNIGHT; p <= KING; p++) {
	        pieces = g.pos.getPieces (side, p);
	        while (morePieces(pieces)) {
	            from = FirstPiece (pieces);
	            switch (p) {
	                case KNIGHT:
	                    pMoves = att.knight[from] & empty;
	                    break;
	                case BISHOP:   //fall through
	                case ROOK:     //fall through
	                case QUEEN:
	                    if (Util.bool(PIECE[p] & BISHOP_OR_QUEEN))
	                    {
	                        pMoves |= BishopAttacks (g, from) & empty;
	                    }
	                    if (Util.bool(PIECE[p] & ROOK_OR_QUEEN))
	                    {
	                        pMoves |= RookAttacks (g, from) & empty;
	                    }
	                    break;
	                case KING:
	                	//exclude moves that are attacked by opponent's king 
	                    pMoves = att.king[from] & empty & ~att.king[g.pos.kingSq[Util.opp(side)]];
	                    break;
	            }
	            while (morePieces(pMoves)) {
	                to = FirstPiece (pMoves);
	                move = EncodeMove(from,to,PIECE[p],0,0);
	                if (p == KING){
	                	if (!isAttacked(g, side, to)){
	                    //if(isLegal(g, move, side)){
	                        moves[n++] = EncodeMove (from, to, PIECE[KING], 0, 0);
	                    }
	                } else {
	                    moves[n++] = EncodeMove (from, to, PIECE[p], 0, 0);
	                }
	                pMoves = ClearPiece (pMoves, to);
	            }
	            pieces = ClearPiece (pieces, from);
	        }
	    }

	    //***************************************************************************
	    //*                                                                         *
	    //* Add castling moves, if any                                              *
	    //*                                                                         *
	    //***************************************************************************
	    switch (side) {
	    case Color.WHITE:
	        if(canWhiteShortCastle(g, side, depth)){
	            moves[n++] = EncodeMove(E1,G1,PIECE[KING],0,0);
	        }
	        if(canWhiteLongCastle(g, side, depth)){
	            moves[n++] = EncodeMove(E1,C1,PIECE[KING],0,0);
	        }
	        break;
	    case Color.BLACK:
	        if(canBlackShortCastle(g, side, depth)){
	            moves[n++] = EncodeMove(E8,G8,PIECE[KING],0,0);
	        }
	        if(canBlackLongCastle(g, side, depth)){
	            moves[n++] = EncodeMove(E8,C8,PIECE[KING],0,0);
	        }
	        break;
	    }

	    g.numberOfLegalMoves[depth] = n;
	    return g.numberOfLegalMoves[depth];
	}
	
	private static boolean morePieces(long pieceBoard)
	{
		return pieceBoard != 0;
	}

	public int GenerateKingEscapes (GameState g, int moves[], int side, int depth)
	{
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
	    kingSq = g.pos.kingSq[side];
	    checkers = attackers(g, side, g.pos.kingSq[side]);
	    switch (side) {
	        case Color.WHITE:
	            promoteRank = EIGHTHRANK;
	            enPassantRank = FIFTHRANK;
	            break;
	        case Color.BLACK:
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


	    if (Util.PieceCount(checkers) == 1) {
	        checker = FirstPiece (checkers);
	        cap = Math.abs(g.pos.board[checker]);

	        //Generate captures to checker's square
	        capturers = attackers (g, Util.opp(side), checker);

	        //Add to 'capturers' pawns that would capture enpassant
	        if(TO_PIECE[cap] == PAWN && g.enPassantSq[depth] != NOSQUARE){
	            attackers = g.pos.getPieces(side, PAWN);
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
	            from = FirstPiece (capturers);
	            mover = Math.abs(g.pos.board[from]);

	            if (TO_PIECE[mover] == PAWN && Util.bool(checkers & promoteRank)) {
	                // Pawn promotion
	                //if (!isPinned(g, from, checker, mover, cap)){
	                move = EncodeMove(from,checker,mover,cap,0);
	                if(isLegal(g, move, side)){
//	    	        	System.err.print("Adding pawn captures and promotes to any piece: ");
//	    	        	Util.displayMove(move, false, false);
	                    for (pro = QUEEN; pro >= KNIGHT; pro--) {
	                        moves[n++] = EncodeMove(from,checker, mover,cap,PIECE[pro]);
	                        //g.legalMoves[depth]++;
	                    }
	                }
	            } else if (TO_PIECE[mover] == PAWN &&
	                       g.enPassantSq[depth] != NOSQUARE){
	                attackers = att.pawn[side][from];
	                if (Util.bool(attackers & (1L << g.enPassantSq[depth]))){
	                    // Pawn captures en Passant
	                    to = g.enPassantSq[depth];
	                    cap = PIECE[PAWN];
	                    move = EncodeMove(from,to,PIECE[PAWN],cap,0);
	                    //if (!isPinned(g, from, to, PIECE[PAWN], cap)){
	                    if (isLegal(g, move, side)){
//	        	        	System.err.print("Added pawn captures via en-passant: ");
//	        	        	Util.displayMove(move, false, false);
	                        moves[n++] = move;
	                        //g.legalMoves[depth]++;
	                    }
	                }                
	            } else {
	                move = EncodeMove(from,checker,mover,cap,pro);
	                //if (!isPinned(g, from, checker, mover, cap)){
	                if (isLegal(g, move, side)){
//	    	        	System.err.print("Added capture the checking piece: ");
//	    	        	Util.displayMove(move, false, false);
	                    moves[n++] = move;
	                    //g.legalMoves[depth]++;
	                    //g.addMove (move);
	                }
	            }
	            capturers = ClearPiece (capturers, from);
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
	    } else if (Util.PieceCount (checkers) == 2) {  //Two pieces checking the king
		// Add king moves that would capture either checking piece
	        kingMoves = att.king[kingSq] & checkers;
	        while (morePieces(kingMoves)){
	            to = FirstPiece(kingMoves);
	            cap = Math.abs(g.pos.board[to]);
	            move = EncodeMove(kingSq, to, PIECE[KING], cap, 0);
	            if (isLegal (g, move, side)) {
	                //cap = abs (g.pos.board[to]);
//		        	System.err.print("Adding king captures one of two checking pieces: ");
//		        	Util.displayMove(move, false, false);
	                moves[n++] = move;//EncodeMove(kingSq, to, PIECE[KING], cap, NONE);
	            }

	            kingMoves = ClearPiece(kingMoves, to);
	        }
	    } 

	    // Add king moves to flight squares (and captures)
	    kingMoves = att.king[kingSq] & ~g.pos.all[ALL];
	    while (morePieces(kingMoves)){
	        to = FirstPiece(kingMoves);
	        //Same reason as above...hafta make sure the king doesn't just
	        //move away from the sliding checking piece.
	        //
	        if (g.pos.board[to] != BOARD_EMPTY_SQUARE){
	            cap = Math.abs(g.pos.board[to]);
	        } else {
	            cap = 0;
	        }
	        move = EncodeMove(kingSq, to, PIECE[KING], cap, 0);
	        if ( isLegal(g, move, side)){
//	        	System.err.print("Adding king escapes via flight square: ");
//	        	Util.displayMove(move, false, false);
	            moves[n++] = move;
	        }
	        kingMoves = ClearPiece(kingMoves, to);
	    }

	    //cout << "moves added: " << n << endl;
	    g.numberOfLegalMoves[depth] = n;
	    //cout << "legalMoves: " << g.legalMoves[depth] << endl;
	    return g.numberOfLegalMoves[depth];
	}


	//
	// Generate moves to any squares that are set in 'targets'
	// Side effect: g.legalMoves[depth] has the number of moves
	// found in this function added to it.
	private int GenerateInterpositions (GameState g, int moves[], int side, int depth,
	                             long targets)
	{
	    //TODO: finished this function...now just call it from
	    //      GenerateInCheckMoves() where appropriate.
		if(!Util.bool(targets))
		{
			System.out.println("There's no interposing squares (or targets); no interposition moves");
			return 0;
		}
	    long pieces;
	    long pMoves;
	    long advanceTwo;
	    long promoters;
	    long empty;
	    int to, from;
	    int n;

	    int numip = 0;

	    //***************************************************************************
	    //*                                                                         *
	    //* Add all pawn moves (promotions, advance-two, advance-one)               *
	    //*                                                                         *
	    //***************************************************************************

	    n = g.numberOfLegalMoves[depth];
	    //n = 0;
	    empty = ~g.pos.all[ALL];

	    //getPawnMoves(g, side, pMoves, promoters, advanceTwo);
	    pMoves = getPawnAdvanceOne(g, side);
	    advanceTwo = getPawnAdvanceTwo(g, side);
	    promoters = getPawnPromotions(g, side);


	    //TODO:
	    // AND 'promoters' and 'targets' to limit even more
	    // AND 'advanceTwo' and 'targets' "   "    "    "
	    // AND 'pMoves' and 'targets'     "   "    "    "
	    //Do this before passing them on to the while loops
	    //below (then remove the 'if((1L << to) & targets){' checks

	    //Pawn promotions
	    while (morePieces(promoters)) {
	        to = FirstPiece (promoters);
	        //Add move ONLY if the move is to 'targets'
	        if (Util.bool((1L << to) & targets))
	        {
	            from = minusOneRank(side, to);

	            //Only add an interposer if it's not pinned to the King
	            //if (!isPinned(g, from, to, PIECE[PAWN], 0)){
	            if (isLegal(g, EncodeMove(from, to, PIECE[PAWN], 0, 0), side))
	            {    
	                for (int i = QUEEN; i >= KNIGHT; i--)
	                {
	                    moves[n++] = EncodeMove (from, to, PIECE[PAWN], 0, PIECE[i]);
	                    numip++;
	                    //g.legalMoves[depth]++;
	                    //g.addMove (move);
	                }
	            }
	        }
	        promoters = ClearPiece (promoters, to);
	    }
	    // Pawns advance two squares
	    while (morePieces(advanceTwo))
	    {
	        to = FirstPiece (advanceTwo);
	        //Add move ONLY if the move is to 'targets'
	        if(Util.bool((1L << to) & targets)){ 
	            from = minusTwoRank(side, to);

	            //Only add an interposer if it's not pinned to the King
	            //if (!isPinned(g, from, to, PIECE[PAWN], 0)){
	            if(isLegal(g, EncodeMove(from, to, PIECE[PAWN], 0, 0), side)){
	                moves[n++] = EncodeMove (from, to, PIECE[PAWN], 0, 0);
	                numip++;
	            }
	        }
	        advanceTwo = ClearPiece (advanceTwo, to);
	    }
	    // Pawns advance one square
	    while (morePieces(pMoves)) {
	        to = FirstPiece (pMoves);
	        //Add move ONLY if the move is to 'targets'
	        if(Util.bool((1L << to) & targets))
	        {
	            from = minusOneRank(side, to);

	            //Only add an interposer if it's not pinned to the King
	            //if (!isPinned(g, from, to, PIECE[PAWN], 0)){
	            int encodedMove = EncodeMove(from, to, PIECE[PAWN], 0, 0);
	            if(isLegal(g, encodedMove, side)){
	                moves[n++] = encodedMove;
	                numip++;
	            }
	        }
	        pMoves = ClearPiece (pMoves, to);
	    }

	    //***************************************************************************
	    //*                                                                         *
	    //* Add all knight, bishop, rook, queen moves (no king moves since he's in  *
	    //* check)                                                                  *
	    //***************************************************************************

	    for (int p = KNIGHT; p <= QUEEN; p++) {
	        pieces = g.pos.getPieces (side, p);
	        while (morePieces(pieces)) {
	            from = FirstPiece (pieces);
	            //now make pMoves only those moves which will interpose
	            //between the king and the checker (by ANDing with targets).
	            switch (p) {
	                case KNIGHT:
	                    pMoves = att.knight[from] & empty & targets;
	                    break;
	                case BISHOP:   //fall through
	                case ROOK:     //fall through
	                case QUEEN:
	                    if (Util.bool(PIECE[p] & BISHOP_OR_QUEEN))
	                    {
	                        pMoves |= BishopAttacks (g, from) & empty & targets;
	                    }
	                    if (Util.bool(PIECE[p] & ROOK_OR_QUEEN))
	                    {
	                        pMoves |= RookAttacks (g, from) & empty & targets;
	                    }
	                    break;
	            }
	            while (morePieces(pMoves)) {
	                to = FirstPiece (pMoves);
	                //Add move ONLY if it is to 'targets'
	                //if ((1L << to) & targets) {

	                //Only add an interposer if it's not pinned to the King
	                //if (!isPinned(g, from, to, PIECE[p], 0)){
	                int encodedMove = EncodeMove(from, to, PIECE[p], 0, 0);
	                if(isLegal(g, encodedMove, side)){
	                   moves[n++] = encodedMove;
	                   numip++;
	                   //g.legalMoves[depth]++;
	                   //g.addMove (move);
	                }
	                pMoves = ClearPiece (pMoves, to);
	            }
	            pieces = ClearPiece (pieces, from);
	        }
	    }
	    //g.legalMoves[depth] = n;
	    return numip;//g.legalMoves[depth];
	}

	public boolean isAttacked(GameState g, int side, int sq)
	{
		return Util.bool(attackers(g, side, sq));
	}

	// Returns a bitbrd of the pieces (excluding the king) attacking 
	// "square".  "side" represents the color/side whose pieces we want to
	// see that are under attack.
	// To see all the black pieces attacking e4 do this:
	//
    //	    attacks = Attackers(g, Color.WHITE, E4);
	//
	// To see all the white pieces attacking g8 do this;
	//
    //	    attacks = Attackers(g, Color.BLACK, G8);
	//
	//NOTE: the king is not included in the attackers
	private long attackers(GameState g, int sideUnderAttack, int squareUnderAttack)
	{
	    // Pretend "sq" contains a Queen AND a Knight.
	    // If that QUEEN/KNIGHT combo can capture a piece from
	    // "square" bitwise-or it into the attackers bitboard.
	    //
	    long attackers = 0;
	    long rankFileAtt, diagAtt;
	    long rooksQueens, bishopsQueens;


	    switch (sideUnderAttack) {
	         case Color.WHITE:
	             attackers |= att.whitepawn[squareUnderAttack] & g.pos.getOpponentPawns(sideUnderAttack);

//	              if (g.enPassantSq[depth] != NOSQUARE){
//	                  if (/*there's a pawn on either side*/)
//	                      attackers |= att.pawn[side][from] &
//	                          (1L << g.enPassantSq[depth]);

//	              }
	             attackers |= att.knight[squareUnderAttack] & g.pos.getOpponentKnights(sideUnderAttack);
	             attackers |= att.king[squareUnderAttack] & g.pos.getOpponentKing(sideUnderAttack);

	             rankFileAtt = att.rank[squareUnderAttack][Status (g.pos.all[ALL], squareUnderAttack)] |
	                 att.file[squareUnderAttack][Status90 (g.pos.all[ALL90], squareUnderAttack)];
	             rooksQueens = g.pos.getOpponentRooks(sideUnderAttack) | g.pos.getOpponentQueens(sideUnderAttack);
	             attackers |= rankFileAtt & rooksQueens;

	             diagAtt = att.L45[squareUnderAttack][Status45L (g.pos.all[ALL45L], squareUnderAttack)] |
	                 att.R45[squareUnderAttack][Status45R (g.pos.all[ALL45R], squareUnderAttack)];
	             bishopsQueens = g.pos.getOpponentBishops(sideUnderAttack) | g.pos.getOpponentQueens(sideUnderAttack);
	             attackers |= diagAtt & bishopsQueens;
	             break;
	        case Color.BLACK:
	             attackers |= att.blackpawn[squareUnderAttack] & g.pos.getOpponentPawns(sideUnderAttack);
//	              if (g.enPassantSq[depth] != NOSQUARE){
//	                  attackers |= att.pawn[side][from] &
//	                      (1L << g.enPassantSq[depth]);
//	              }
	             attackers |= att.knight[squareUnderAttack] & g.pos.getOpponentKnights(sideUnderAttack);
	             attackers |= att.king[squareUnderAttack] & g.pos.getOpponentKing(sideUnderAttack);

	             rankFileAtt = att.rank[squareUnderAttack][Status (g.pos.all[ALL], squareUnderAttack)] | 
	                 att.file[squareUnderAttack][Status90 (g.pos.all[ALL90], squareUnderAttack)];
	             rooksQueens = g.pos.getOpponentRooks(sideUnderAttack) | g.pos.getOpponentQueens(sideUnderAttack);
	             attackers |= rankFileAtt & rooksQueens;

	             diagAtt = att.L45[squareUnderAttack][Status45L (g.pos.all[ALL45L], squareUnderAttack)] |
	                 att.R45[squareUnderAttack][Status45R (g.pos.all[ALL45R], squareUnderAttack)];
	             bishopsQueens = g.pos.getOpponentBishops(sideUnderAttack) | g.pos.getOpponentQueens(sideUnderAttack);
	             attackers |= diagAtt & bishopsQueens;
	             break;
	    }
	    return attackers;
	}


	// RookAttacks() returns a bitboard of the squares that 
	// a rook on "from" would attack, including captures.

	private long RookAttacks (GameState g, int from)
	{
	    long attacks;
	    int stat1, stat2;

	    stat1 = Status (g.pos.all[ALL], from);
	    stat2 = Status90 (g.pos.all[ALL90], from);
	    attacks = att.rank[from][stat1];
	    attacks |= att.file[from][stat2];
	    return attacks;
	}


	// BishopAttacks() returns a bitboard of the squares that 
	// a bishop on "from" would attack, including captures.

	private long BishopAttacks (GameState g, int from)
	{
	    long attacks;
	    int stat1, stat2;

	    stat1 = Status45L (g.pos.all[ALL45L], from);
	    stat2 = Status45R (g.pos.all[ALL45R], from);
	    attacks = att.L45[from][stat1];
	    attacks |= att.R45[from][stat2];
	    return attacks;
	}

	private static int isPawnPromotion(int side, int from){
	    switch(side){
	    case Color.WHITE:
	        if(from + 8 >= A8){
	            return 1;
	        }
	        break;
	    case Color.BLACK:
	        if(from - 8 <= H1){
	            return 1;
	        }
	        break;
	    }
	    return 0;
	}


	// minusOneRank
	//
	// Returns the from square given the square
	// the pawn moved to. (pawn advanced one square)
	//
	private static int minusOneRank(int side, int to){
	    if (side == Color.WHITE) {
	        return (to - 8);
	    } else {
	        return (to + 8);
	    }
	}


	// minusTwoRank
	//
	// Returns the from square given the square
	// the pawn moved to. (pawn advanced two squares)
	//
	private static int minusTwoRank(int side, int to){
	    if (side == Color.WHITE) {
	        return (to - 16);
	    } else {
	        return (to + 16);
	    }
	}

	private boolean canWhiteShortCastle(GameState g, int side, int depth){
	    if (Util.bool(g.castle[depth] & GameState.W_SHORT_CASTLE)
	        && g.pos.board[F1] == BOARD_EMPTY_SQUARE
	        && g.pos.board[G1] == BOARD_EMPTY_SQUARE 
	        && !isAttacked (g, side, E1)
	        && !isAttacked (g, side, F1)
	        && !isAttacked (g, side, G1)
	        && !isAttacked (g, side, H1)) {
	        return true;
	    }
	    return false;
	}

	private boolean canWhiteLongCastle(GameState g, int side, int depth){
	    if (Util.bool(g.castle[depth] & GameState.W_LONG_CASTLE) &&
	        g.pos.board[D1] == BOARD_EMPTY_SQUARE
	        && g.pos.board[C1] == BOARD_EMPTY_SQUARE
	        && g.pos.board[B1] == BOARD_EMPTY_SQUARE 
	        && !isAttacked (g, side, E1)
	        && !isAttacked (g, side, D1)
	        && !isAttacked (g, side, C1)
	        && !isAttacked (g, side, B1)
	        && !isAttacked (g, side, A1)) {
	        return true;
	    }
	    return false;
	}

	private boolean canBlackShortCastle(GameState g, int side, int depth){
	    if (Util.bool(g.castle[depth] & GameState.B_SHORT_CASTLE)
	        && g.pos.board[F8] == BOARD_EMPTY_SQUARE
	        && g.pos.board[G8] == BOARD_EMPTY_SQUARE 
	        && !isAttacked (g, side, E8)
	        && !isAttacked (g, side, F8)
	        && !isAttacked (g, side, G8)
	        && !isAttacked (g, side, H8)) {
	        return true;
	    }
	    return false;
	}

	private boolean canBlackLongCastle(GameState g, int side, int depth){
	    if (Util.bool(g.castle[depth] & GameState.B_LONG_CASTLE)
	        && g.pos.board[D8] == BOARD_EMPTY_SQUARE
	        && g.pos.board[C8] == BOARD_EMPTY_SQUARE
	        && g.pos.board[B8] == BOARD_EMPTY_SQUARE 
	        && !isAttacked (g, side, E8)
	        && !isAttacked (g, side, D8)
	        && !isAttacked (g, side, C8)
	        && !isAttacked (g, side, A8)) {
	        return true;
	    }
	    return false;  
	}


	/*

	// isPinned()
	// 
	// Returns true if the move by 'mover' from square 'from' to 'to'
	// exposes king to check.  Returns false otherwise.
	bool isPinned(gamestate &g, int from, int to, int mover, int cap){
	    int move;
	    bool pinned;
	    //int savedNumMoves;

	    //It doesn't matter what the piece promotes to...hence
	    //a zero for the promotion piece below.
	    move = EncodeMove(from, to, mover, cap, 0);
	    //cout << "Before make move:\n";
	    //g.display();
	    //printf("rooks : %0llx\n", g.pos.pieces[Color.WHITE][Pieces.ROOKS]);
	    //printf("queens: %0llx\n", g.pos.pieces[Color.WHITE][Pieces.QUEENS]);

	    g.makeMove(move);
	    //makeMove changes the side...so change it back
	    //g.sideToMove = Toggle(g.sideToMove);

	    pinned = isAttacked(g, g.sideToMove, g.pos.kingSq[g.sideToMove]);

	    g.undoMove(move);
	    //cout << "After undo move:\n";
	    //g.display();
	    //printf("rooks : %0llx\n", g.pos.pieces[Color.WHITE][Pieces.ROOKS]);
	    //printf("queens: %0llx\n", g.pos.pieces[Color.WHITE][Pieces.QUEENS]);
	   
	    return pinned;
	}
	*/



	// isLegal()
	// 
	// Returns true if the move 'move' is legal (doesn't exposes/leaves the
	// king in check). This is for use when the king is moving.  We have to
	// save the king square upfront...then make the king move with the saved
	// value.
	// Returns false if the king is in check after 'move' is made.
	private boolean isLegal(GameState g, int move, int side){
	    boolean legal;

	    //Save the king square in case the king is the moving piece
	    //int kingSq = g.pos.kingSq[side];
	    g.makeMove(move, side);
	    legal = !isAttacked(g, side, g.pos.kingSq[side]);  //use the saved king square
	    g.undoMove(move, side);
	    System.err.println("Is "+Util.displayMoveStr(move, false, false)+" legal? "+legal);
	    return legal;
	}
	
	private long getPawnAdvanceOne(GameState g, int side)
	{
		long advOne = 0;
	    long empty;

	    empty = ~g.pos.all[ALL];

	    switch (side) {
	    case Color.WHITE:
	        advOne = (g.pos.getPawns(side) << 8) & empty & ~EIGHTHRANK;
	        // 'advOne' is all moves except those to the eighth rank
	        break;
	    case Color.BLACK:
	        advOne = (g.pos.getPawns(side) >> 8) & empty & ~FIRSTRANK;
	        // 'advOne' is all moves except those to the first rank
	        break;
	    }
	    return advOne;
	}

	private long getPawnAdvanceTwo(GameState g, int side)
	{
		long advTwo = 0;
		long empty;

	    empty = ~g.pos.all[ALL];

	    switch (side) {
	    case Color.WHITE:
	        advTwo = g.pos.getPawns(side) & SECONDRANK;
	        advTwo = (advTwo << 8) & empty;
	        advTwo = (advTwo << 8) & empty;
	        break;
	    case Color.BLACK:
	        advTwo = g.pos.getPawns(side) & SEVENTHRANK;
	        advTwo = (advTwo >> 8) & empty;
	        advTwo = (advTwo >> 8) & empty;
	        break;
	    }
	    return advTwo;
	}
	
	private long getPawnPromotions(GameState g, int side)
	{
		long prom = 0;
		long empty;

	    empty = ~g.pos.all[ALL];

	    switch (side) {
	    case Color.WHITE:
	        prom = (g.pos.getPawns(side) << 8) & empty & EIGHTHRANK;
	        // 'prom' is only the moves to the eighth rank
	        break;
	    case Color.BLACK:
	        prom = (g.pos.getPawns(side) >> 8) & empty & FIRSTRANK;
	        // 'prom' is only the moves to the first rank
	        break;
	    }
	    return prom;
	}

	
	
	/*package scope just to test it; ideally private*/
	static int EncodeMove (int from, int to, int mov, int cap, int pro)
	{
	    return (from | (to << 6) | (mov << 12) | (cap << 15) | (pro << 18));
	}



//	//
//	// TEST DRIVER for movegen2
//	//
//
//	//#define DEBUG
//
//	#ifdef DEBUG
//	#include <iostream>
//	#include <fstream>
//	#include "utility.h"
//	using namespace std;
//
//	int main (int argc, char *argv[]){
//	    long checkers;
//	    gamestate g;
//	    const int MAX = 101;
//	    char line[MAX];
//	    bool good;
//	    ifstream fin;
//	    int side = 0; //white
//
//	    // Open FEN file
//	    if (argc != 2) {
//	        cerr << "I need a file name for an argument\n";
//	    }
//	    fin.open(argv[1]);
//	    if(!fin.is_open()){
//	        cerr << "can't open file " << argv[1] << endl;
//	        exit(1);
//	    }    
//	    
//	    while (fin.getline(line, MAX+1)){
//	        good = g.set2(line);
//	        if(!good){
//	            cerr << "can't read FEN...skipping this one\n";
//	            g.clear();
//	            continue;
//	        }
//	        side = Color.WHITE;
//	        //for (int i=0; i<=1; i++){
//	        //  for(int j=0; j<=1; j++){
//	                checkers = Attackers (g, side, g.pos.kingSq[side]);
//	                g.display();
//	                cout << "Checkers' squares: ";
//	                displaySquares(checkers);
//	                //    }
//	                //}
//	        g.clear();
//	    }
//
//	    return -1;
//	}
//
//
//
//	// char fen1[] = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//	// char fen2[] = "rnbqkbnr/pppppppp/3p4/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R";
//	// char fen3[] = "8/1K1pP3/8/2Nn4/8/1kP5/2P5/n7";
//	// char fen4[] = "q1n5/1P3P2/2P5/8/K7/8/k6P/8";
//	// char fen5[] = "kppppppp/pppppppp/8/K2Q3q/pppppppp/8/8/8";
//	// char fen6[] = "rnbqkbnr/pp1ppppp/8/3pP3/2p5/8/PPPP1PPP/RNBQKBNR";
//
//
//
//
//
//	/*
//	int main (int argc, char *argv[])
//	{
//
////	    if (argc != 2){
////	        cerr << "Give me a FEN string of the chessboard as an argument\n";
////	        exit (1);
////	    }
//
//	int depth = 0;
//	gamestate g ("start.fen");
//	char fen[100];
//	char fen1[] = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//	    char fen2[] = "rnbqkbnr/pppppppp/3p4/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R";
//	    char fen3[] = "8/1K1pP3/8/2Nn4/8/1kP5/2P5/n7";
//	    char fen4[] = "q1n5/1P3P2/2P5/8/K7/8/k6P/8";
//	    char fen5[] = "kppppppp/pppppppp/8/K2Q3q/pppppppp/8/8/8";
//	    char fen6[] = "rnbqkbnr/pp1ppppp/8/3pP3/2p5/8/PPPP1PPP/RNBQKBNR";
//
//	    int moves[60];
//	    int n = 0;
//
//	    // copy in the FEN string
//	    //strncpy(fen, fen6, 100);
//
//	    //Generate moves for Color.WHITE
//	    //g.pos.Set(fen);
//
//	    n = GenerateCaptures(g, moves, Color.WHITE, depth);
//	    n += GenerateNonCaptures(g, moves + (n - 1), Color.WHITE, depth);
//	    g.display();
//	    cout << "No. of moves: " << n << endl;
//	    displayMoves(g, moves, n-1, depth);
//	    
////	     g.sideToMove = Color.WHITE;
////	     GenerateCaptures (g, Color.WHITE, depth);
////	     GenerateNonCaptures (g, Color.WHITE, depth);
////	     g.display ();
////	     g.displayMoves ();
//
//	    //Generate moves for Color.BLACK
//	    depth++;
////	     g.sideToMove = Color.BLACK;
////	     GenerateCaptures (g, Color.BLACK, depth);
////	     GenerateNonCaptures (g, Color.BLACK, depth);
////	     g.display ();
////	     g.displayMoves ();
//
//	    return 0;
//	}
//	*/
//	#endif

}
