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

	
	
	protected static final Attacks att = new Attacks();
	private static final Generator capturesGenerator = new CaptureGenerator();
	private static final Generator nonCapturesGenerator = new NonCaptureGenerator();
	private static final Generator escapeGenerator = new EscapeGenerator();

	public MoveGenerator(){
		
	}
	
	
	// Return the rank number (zero-based)
	private int Rank(int sq){ return (sq / 8); } //integer division

	// Return the file number (zero-based)
	private int File(int sq){ return (sq % 8); }


//	void ClearPiece(long &board, int bit){
//		board &= ~(1L << bit);
//	}

	protected static long ClearPiece(long board, int bit){
		return Bitmap.clearBit(board, bit);
	}

	protected static int FirstPiece(long pieces){
		return Bitmap.lowestBitNumber(pieces);
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
		int yCoordinateOfSquare = rankNumber(sq);
		int shiftby = yCoordinateOfSquare * 8 + 1;

		//SHIFT 'b' RIGHT by ((y * 8) + 1) the AND with 63 (for the 6 bits)
		return (byte) ((b >> shiftby) & 63);
	}

	private static byte Status90(long b, int sq){
		//Compute the x and y coordinates from 'sq' (aka, reverse linear index)
		//
		//The transformation function T for x and y is 
		// T(x1, y1) = (y1, x1)   (x and y are swapped)
		
		int xCoordinateOfSquare = fileNumber(sq);
		//'y' not needed
		int shiftby = xCoordinateOfSquare * 8 + 1;

		//SHIFT 'b' RIGHT by ((x * 8) + 1) then AND with 63 (for the 6 bits)
		return (byte) ((b >> shiftby) & 63);
	}


	private static byte Status45L(long b, int sq){
	    
	    //for diagonals of length 3 or less, status should be zero
	    
	    int x = fileNumber(sq);
	    int y = rankNumber(sq);
	    
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
	    
	    int x = 7 - fileNumber(sq);  
	    int y = rankNumber(sq);
	    
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
		return capturesGenerator.generate(g, moves, side, depth);
	}

	// Side effect: g.legalMoves[depth] has the number of moves
	// found in this function added to it after this function completes
	public int GenerateNonCaptures (GameState g, int moves[], int side, int depth)
	{
		return nonCapturesGenerator.generate(g, moves, side, depth);
	}
	
	public static boolean morePieces(long pieceBoard)
	{
		return pieceBoard != 0;
	}

	public int GenerateKingEscapes (GameState g, int moves[], int side, int depth)
	{
		return escapeGenerator.generate(g, moves, side, depth);
	}


	//
	// Generate moves to any squares that are set in 'targets'
	// Side effect: g.legalMoves[depth] has the number of moves
	// found in this function added to it.
	protected int GenerateInterpositions (GameState g, int moves[], int side, int depth,
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
	    Position position = g.getPosition();
		long allPiecesByRank = position.getAllPieces(0);
		empty = ~allPiecesByRank;

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
	            from = Util.squareBehind(to, side);

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
	            from = Util.twoSquaresBehind(to, side);

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
	            from = Util.squareBehind(to, side);

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
	        pieces = position.getPieces (side, p);
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
	                        long allPieces45Left = position.getAllPieces(-45);
							long allPieces45Right = position.getAllPieces(45);
							pMoves |= bishopAttacks (from, allPieces45Left, allPieces45Right) & empty & targets;
	                    }
	                    if (Util.bool(PIECE[p] & ROOK_OR_QUEEN))
	                    {
	                        long allPiecesByFile = position.getAllPieces(90);
							pMoves |= rookAttacks (from, allPiecesByRank, allPiecesByFile) & empty & targets;
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
	protected long attackers(GameState g, int sideUnderAttack, int squareUnderAttack)
	{
	    // Pretend "sq" contains a Queen AND a Knight.
	    // If that QUEEN/KNIGHT combo can capture a piece from
	    // "square" bitwise-or it into the attackers bitboard.
	    //
	    long attackers = 0;
	    long rankFileAtt, diagAtt;
	    long rooksQueens, bishopsQueens;


	    Position position = g.getPosition();
		switch (sideUnderAttack) {
	         case Bitmap.WHITE:
	             attackers |= att.whitepawn[squareUnderAttack] & position.getOpponentPawns(sideUnderAttack);

//	              if (g.enPassantSq[depth] != NOSQUARE){
//	                  if (/*there's a pawn on either side*/)
//	                      attackers |= att.pawn[side][from] &
//	                          (1L << g.enPassantSq[depth]);

//	              }
	             attackers |= att.knight[squareUnderAttack] & position.getOpponentKnights(sideUnderAttack);
	             attackers |= att.king[squareUnderAttack] & position.getOpponentKing(sideUnderAttack);

	             rankFileAtt = att.rank[squareUnderAttack][Status (position.getAllPieces(0), squareUnderAttack)] |
	                 att.file[squareUnderAttack][Status90 (position.getAllPieces(90), squareUnderAttack)];
	             rooksQueens = position.getOpponentRooks(sideUnderAttack) | position.getOpponentQueens(sideUnderAttack);
	             attackers |= rankFileAtt & rooksQueens;

	             diagAtt = att.L45[squareUnderAttack][Status45L (position.getAllPieces(-45), squareUnderAttack)] |
	                 att.R45[squareUnderAttack][Status45R (position.getAllPieces(45), squareUnderAttack)];
	             bishopsQueens = position.getOpponentBishops(sideUnderAttack) | position.getOpponentQueens(sideUnderAttack);
	             attackers |= diagAtt & bishopsQueens;
	             break;
	        case Bitmap.BLACK:
	             attackers |= att.blackpawn[squareUnderAttack] & position.getOpponentPawns(sideUnderAttack);
//	              if (g.enPassantSq[depth] != NOSQUARE){
//	                  attackers |= att.pawn[side][from] &
//	                      (1L << g.enPassantSq[depth]);
//	              }
	             attackers |= att.knight[squareUnderAttack] & position.getOpponentKnights(sideUnderAttack);
	             attackers |= att.king[squareUnderAttack] & position.getOpponentKing(sideUnderAttack);

	             rankFileAtt = att.rank[squareUnderAttack][Status (position.getAllPieces(0), squareUnderAttack)] | 
	                 att.file[squareUnderAttack][Status90 (position.getAllPieces(90), squareUnderAttack)];
	             rooksQueens = position.getOpponentRooks(sideUnderAttack) | position.getOpponentQueens(sideUnderAttack);
	             attackers |= rankFileAtt & rooksQueens;

	             diagAtt = att.L45[squareUnderAttack][Status45L (position.getAllPieces(-45), squareUnderAttack)] |
	                 att.R45[squareUnderAttack][Status45R (position.getAllPieces(45), squareUnderAttack)];
	             bishopsQueens = position.getOpponentBishops(sideUnderAttack) | position.getOpponentQueens(sideUnderAttack);
	             attackers |= diagAtt & bishopsQueens;
	             break;
	    }
	    return attackers;
	}


	// RookAttacks() returns a bitboard of the squares that 
	// a rook on "from" would attack, including captures.

	static long rookAttacks (int rookSquare, long allPiecesByRank, long allPiecesByFile)
	{
	    long attacks;
	    int stat1, stat2;

	    stat1 = Status (allPiecesByRank, rookSquare);
	    stat2 = Status90 (allPiecesByFile, rookSquare);
	    attacks = att.rank[rookSquare][stat1];
	    attacks |= att.file[rookSquare][stat2];
	    return attacks;
	}


	// BishopAttacks() returns a bitboard of the squares that 
	// a bishop on "from" would attack, including captures.

	static long bishopAttacks (int bishopSquare, long allPieces45Left, long allPieces45Right)
	{
	    long attacks;
	    int stat1, stat2;

	    stat1 = Status45L (allPieces45Left, bishopSquare);
	    stat2 = Status45R (allPieces45Right, bishopSquare);
	    attacks = att.L45[bishopSquare][stat1];
	    attacks |= att.R45[bishopSquare][stat2];
	    return attacks;
	}

	static int isPawnPromotion(int side, int from){
	    switch(side){
	    case Bitmap.WHITE:
	        if(from + 8 >= A8){
	            return 1;
	        }
	        break;
	    case Bitmap.BLACK:
	        if(from - 8 <= H1){
	            return 1;
	        }
	        break;
	    }
	    return 0;
	}


	protected boolean canWhiteShortCastle(GameState g, int side){
	    Position position = g.getPosition();
		if (g.hasShortCastleOption()
	    	&& position.isEmpty(F1)
	        && position.isEmpty(G1) 
	        && !isAttacked (g, side, E1)
	        && !isAttacked (g, side, F1)
	        && !isAttacked (g, side, G1)
	        && !isAttacked (g, side, H1)) {
	        return true;
	    }
	    return false;
	}

	protected boolean canWhiteLongCastle(GameState g, int side){
	    Position position = g.getPosition();
		if (g.hasLongCastleOption() &&
	        position.isEmpty(D1)
	        && position.isEmpty(C1)
	        && position.isEmpty(B1) 
	        && !isAttacked (g, side, E1)
	        && !isAttacked (g, side, D1)
	        && !isAttacked (g, side, C1)
	        && !isAttacked (g, side, B1)
	        && !isAttacked (g, side, A1)) {
	        return true;
	    }
	    return false;
	}

	protected boolean canBlackShortCastle(GameState g, int side){
	    Position position = g.getPosition();
		if (g.hasShortCastleOption()
	        && position.isEmpty(F8)
	        && position.isEmpty(G8) 
	        && !isAttacked (g, side, E8)
	        && !isAttacked (g, side, F8)
	        && !isAttacked (g, side, G8)
	        && !isAttacked (g, side, H8)) {
	        return true;
	    }
	    return false;
	}

	protected boolean canBlackLongCastle(GameState g, int side){
	    Position position = g.getPosition();
		if (g.hasShortCastleOption()
	        && position.isEmpty(D8)
	        && position.isEmpty(C8)
	        && position.isEmpty(B8) 
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
	boolean isLegal(GameState g, int move, int side){
	    boolean legal;

	    //Save the king square in case the king is the moving piece
	    //int kingSq = g.pos.kingSq[side];
	    g.makeMove(move, side);
	    legal = !isAttacked(g, side, g.getPosition().getKingSquare(side));  //use the saved king square
	    g.undoMove(move, side);
	    System.err.println("Is "+Util.displayMoveStr(move, false, false)+" legal? "+legal);
	    return legal;
	}
	
	private long getPawnAdvanceOne(GameState g, int side)
	{
		long advOne = 0;
	    long empty;

	    Position position = g.getPosition();
		empty = ~position.getAllPieces(0);

	    switch (side) {
	    case Bitmap.WHITE:
	        advOne = (position.getPawns(side) << 8) & empty & ~EIGHTHRANK;
	        // 'advOne' is all moves except those to the eighth rank
	        break;
	    case Bitmap.BLACK:
	        advOne = (position.getPawns(side) >> 8) & empty & ~FIRSTRANK;
	        // 'advOne' is all moves except those to the first rank
	        break;
	    }
	    return advOne;
	}

	private long getPawnAdvanceTwo(GameState g, int side)
	{
		long advTwo = 0;
		long empty;

	    Position position = g.getPosition();
		empty = ~position.getAllPieces(0);// all[ALL];

	    switch (side) {
	    case Bitmap.WHITE:
	        advTwo = position.getPawns(side) & SECONDRANK;
	        advTwo = (advTwo << 8) & empty;
	        advTwo = (advTwo << 8) & empty;
	        break;
	    case Bitmap.BLACK:
	        advTwo = position.getPawns(side) & SEVENTHRANK;
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

	    Position position = g.getPosition();
		empty = ~position.getAllPieces(0);

	    switch (side) {
	    case Bitmap.WHITE:
	        prom = (position.getPawns(side) << 8) & empty & EIGHTHRANK;
	        // 'prom' is only the moves to the eighth rank
	        break;
	    case Bitmap.BLACK:
	        prom = (position.getPawns(side) >> 8) & empty & FIRSTRANK;
	        // 'prom' is only the moves to the first rank
	        break;
	    }
	    return prom;
	}

	
	
	//******************************************************************/
	//              The bits of an EncodedMove                         */
	//******************************************************************/
	// +--------+--------+---------+----------+-----------+---------+  */
	// | 0 - 5  | 6 - 11 | 12 - 14 | 15 - 17  |  18 - 20  | 21 - 31 |  */
	// +--------+--------+---------+----------+-----------+---------+  */
	// |  from  |   to   | moving  | captured | promotion | unused  |  */
	// | square | square | piece   | piece    |   piece   |         |  */
	// +--------+--------+---------+----------+-----------+---------+  */
	//                                                                 */
	//******************************************************************/
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
