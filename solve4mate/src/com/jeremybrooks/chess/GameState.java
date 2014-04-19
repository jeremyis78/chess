/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 9, 2010
 */
package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * 	  This file contains the implementation for the gamestate class.
 *	  A GameState object contains a 'position' object and flags.
 *	  The flags are castling status, En Passant target squares,
 *	  the halfmove clock and full move number.  All these taken
 *	  together represent the entire state of the chess game at any
 *	  given point in the game
 *
 * @author jeremy
 *
 */
public class GameState {

	//*******************************************************************
	//*                                                                 *
	//* +--------+--------+--------+--------+-------------+             *
	//* |  bit 0 |  bit 1 |  bit 2 |  bit 3 | bits 4 - 7  |             *
	//* +--------+--------+--------+--------+-------------+             *
	//* |      WHITE      |      BLACK      |             |             *
	//* | short  | long   | short  | long   |   unused    |             *
	//* | castle | castle | castle | castle |             |             *
	//* +--------+--------+--------+--------+-------------+             *
	//*                                                                 *
	//*******************************************************************
	public static final int W_SHORT_CASTLE = 1;
	public static final int W_LONG_CASTLE  = 2;
	public static final int B_SHORT_CASTLE = 4;
	public static final int B_LONG_CASTLE  = 8;

	public static final String FEN_START = new String(
	    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

	public static final int MAX_DEPTH = 150;
	public static final int CHECKMATE = 0xffff;
	public static final int CHECK = CHECKMATE / 2;
	public static final int DRAW = 0;

	final byte CASTLE_START = 
		W_SHORT_CASTLE | W_LONG_CASTLE | B_SHORT_CASTLE | B_LONG_CASTLE; 
			
    Position        pos;
    int           	sideToMove = Bitmap.WHITE; //Color.BLACK		
    int             depth;			//current depth of search
    long	    	attacked[] = new long[MAX_DEPTH];
    int				castle[] = new int[MAX_DEPTH + 1];
    byte			enPassantSq[] = new byte[MAX_DEPTH + 1];
    byte			halfMoveClock[] = new byte[MAX_DEPTH + 1];
    byte			fullMoveClock[] = new byte[MAX_DEPTH];//dont' think this needs to be an array
    int				currentLine[] = new int[MAX_DEPTH]; //line we are searching
    int				bestLine[] = new int[MAX_DEPTH];    //the best line of play
    int             numberOfLinesToMate;    //# of lines of play that lead to mate
    int	            numberOfLegalMoves[] = new int[MAX_DEPTH];  //no. of moves at this depth
    int             numberOfLegalMovesToDepth[] = new int[MAX_DEPTH]; //no of moves UP to current depth
    int             moves[] = new int[100];             //legal moves from this state
    int             movesValue[] = new int[100];        //minimax value of the moves from this state 
    int             nodes = 0;                  //nodes searched
    int             best = 0;                   //best value seen in search
    int             alpha = 0;                  //alpha value during/after search
    int             beta = 0;                   //beta value during/after search
    int             movesIndex; 
    int				currentMove; //TODO:  was a move_t in C++, Is this variable really needed?
    int             searchDepth;
    int             phase;   //the phase of the game (opening, middlegame, ending)
    //TODO: can use this to help the eval function	
    //      make a better estimate of the position.
    
    //TODO: need a variable or something to keep track of 
    //      the number of repeated positions 
    //      (for claiming/declaring a draw)
    // I think the only way to do this is to have a transposition table
    // whose key is the current position of the board.
    //
    
    //static byte CASTLE_START;

	
	public GameState()
	{
		pos = new Position();
	    //TODO: fix this so it gets all the array indices 
	    //filled (MAXDEPTH +1) see definitions.h for gamestate
	    searchDepth = 3;
	    numberOfLinesToMate = 0;
	    for (int i = 0; i < MAX_DEPTH; i++){
	        castle[i] = 0;
	        enPassantSq[i] = NOSQUARE;	//No En Passant Target Square at start
	        halfMoveClock[i] = 0;	
	        attacked[i] = 0;		
	        numberOfLegalMoves[i] = 0;
	        numberOfLegalMovesToDepth[i] = 0;
	        currentLine[i] = 0;
	        bestLine[i] = 0;
	    }             
	    castle[0] = CASTLE_START;
	    movesIndex = 0;
	}


	public GameState(final String file)
	{
	    // This function sees if the file "start.fen" exists in the cwd.
	    // If it does it reads in the FEN board position string
	    // from "start.fen" and starts the engine with that as the
	    // initial position.
	    // Otherwise ("start.fen" doesn't exist in cwd), it starts
	    // the engine at the beginning of a chess game.
	    
		final String startFen = 
			"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new FileReader(file));
			line = br.readLine();
			br.close();
		}
		catch (FileNotFoundException e)
		{
			line = startFen;
		}
		catch (IOException e)
		{
			line = startFen;
		}
		set(line);
	    
	    //Don't fill up the zero index (it's already filled
	    //in from the input file....HENCE i starts at one!)
	    //TODO: fix this so it gets all the array indices filled (MAXDEPTH +1) see 
	    //definitions.h for gamestate
	    searchDepth = 3;
	    numberOfLinesToMate = 0;
	    numberOfLegalMoves[0] = 0;
	    for (int i = 1; i < MAX_DEPTH; i++){
	        castle[i] = 0;
	        enPassantSq[i] = NOSQUARE;
	        halfMoveClock[i] = 0;	
	        fullMoveClock[i] = 0;
	        attacked[i] = 0;		
	        numberOfLegalMoves[i] = 0;
	        numberOfLegalMovesToDepth[i] = 0;
	        currentLine[i] = 0;
	        bestLine[i] = 0;
	    }
//	    memset(moves, 0, 100*sizeof(move_t));
//	    memset(movesValue, 0, 100*sizeof(int));
	    Arrays.fill(moves, 0);
	    Arrays.fill(movesValue, 0);
	    
	    movesIndex = 0;
	}

	void setSearchDepth(int d){
	    searchDepth = d;
	}

//	Do I really need this function?
//	boolean readFen(istream &in){
//	    char line[100+1];
//	    bool r = false;
//	    r = in.getline(line, 100);
//	    if (r == true && strlen(line) > 10){
//	        set(line);
//	        return true;
//	    } else {
//	        return false;
//	    }
//	}


	public void set(final String fen){
	    final int numFields = 6;
    
	    // Read the 'fen' array into variables
	    // Anything after the sixth field and the '#' 
	    // character is a comment so ignore it.
	    String[] fields = fen.split(" ");
        if (fields.length < numFields){
        	throw new IllegalArgumentException("FEN: found fewer than 6 fields\n");
        }
              
        //Initialize board, side, castling, en pas, hmc, and fmc.
        //zeroeth index is moves to mate
        setBoard(fields[0]); //board);
        setSide(fields[1]); //side);
        setCastle(fields[2]); //castle);
        setEnPassant(fields[3]); //ep);
        setHalfMoveClock(fields[4]); //half);
        setFullMoveClock(fields[5]); // full);
	}
	
	public String get()
	{
		StringBuilder fen = new StringBuilder();
		fen.append(getBoard());
		fen.append(" ");
		fen.append(getSide());
		fen.append(" ");
		fen.append(getCastle());
		fen.append(" ");
		fen.append(getEnPassant());
		fen.append(" ");
		fen.append(getHalfMoveClock());
		fen.append(" ");
		fen.append(getFullMoveClock());
		return fen.toString();
	}
	
	

	void clear(){
	    pos.clear();
	    sideToMove = Bitmap.WHITE;
	    castle[0] = 0;
	    enPassantSq[0] = NOSQUARE;
	    halfMoveClock[0] = 0;
	    fullMoveClock[0] = 0;
	    for(int i=0; i<MAX_DEPTH; i++){
	        numberOfLegalMoves[i] = 0;
	    }
	    Arrays.fill(currentLine, 0);
	    Arrays.fill(bestLine, 0);
	    numberOfLinesToMate = 0;
	}

	void setBoard(final String s)
	{
	    pos.set(s);  //throws error if any
	}

	/**
	 * Gets the pieces on the board at the current depth.
	 * 
	 * @return
	 */
	private String getBoard()
	{	
		//NOTE: depth isn't used here.
		//As makeMove and undoMove are called the sole position member is updated
		//
		return pos.getFen();
	}

	void setSide(final String s)
	{
	    if ("w".equals(s)) {
	        sideToMove = Bitmap.WHITE;
	        return;
	    } else if ("b".equals(s)) {
	        sideToMove = Bitmap.BLACK;
	        return;
	    }
        throw new IllegalArgumentException("FEN 2nd field: should be 'w' or 'b'");
	}

	private String getSide()
	{
		return (sideToMove == Bitmap.WHITE) ? "w" : "b";
	}

	void setCastle(final String s)
	{
	    if("KQkq".equals(s) || "KQk".equals(s) ||
	       "KQq".equals(s)  || "KQ".equals(s)  ||
	       "Kkq".equals(s)  || "Kk".equals(s)  || 
	       "Kq".equals(s)   || "K".equals(s)   ||
	       "Qkq".equals(s)  || "Qk".equals(s)  || 
	       "Qq".equals(s)   || "Q".equals(s)   ||
	       "kq".equals(s)   || "k".equals(s)   ||
	       "q".equals(s))
	    {
	        for (int i = 0; i < s.length(); i++){
	            switch (s.charAt(i)) {
	            case 'K':
	                castle[0] |= W_SHORT_CASTLE; 			
	                break;
	            case 'Q':
	                castle[0] |= W_LONG_CASTLE; 			
	                break;
	            case 'k':
	                castle[0] |= B_SHORT_CASTLE; 			
	                break;
	            case 'q':
	                castle[0] |= B_LONG_CASTLE; 			
	                break;
	            default:
	                throw new IllegalArgumentException("FEN 3rd field: " +
	                    "only 'K','Q','k','q' allowed");
	            }
	        }
	    } else if ("-".equals(s)){
	        castle[0] = 0;
	    } else { 
	        throw new IllegalArgumentException("FEN 3rd field: " + 
	            "castling field '" + s + "' is invalid");
	    }
	}
	
	/**
	 * Get the castling FEN field at the current state (ie, at current depth)
	 */
	private String getCastle()
	{
		StringBuilder castleFen = new StringBuilder();
		int castleBitmap = castle[depth];
		if(Util.bool(castleBitmap & W_SHORT_CASTLE))
		{
			castleFen.append("K");
		}
		if(Util.bool(castleBitmap & W_LONG_CASTLE))
		{
			castleFen.append("Q");
		}
		if(Util.bool(castleBitmap & B_SHORT_CASTLE))
		{
			castleFen.append("k");
		}
		if(Util.bool(castleBitmap & B_LONG_CASTLE))
		{
			castleFen.append("q");
		}
		
		if(castleFen.length() == 0)
			return "-";
		else
			return castleFen.toString();
	}

	void setEnPassant(final String s)
	{
	    int sq; 

	    if ("-".equals(s)){
	        enPassantSq[0] = NOSQUARE;
	        return;
	    }
	    sq = Util.StrToSq(s);
	    if ((sq >= A3 && sq <= H3 && sideToMove == Bitmap.BLACK) ||
	               (sq >= A6 && sq <= H6 && sideToMove == Bitmap.WHITE) ){
	        enPassantSq[0] = (byte) sq;
	    } else {
	        throw new IllegalArgumentException("FEN fields conflict: " + 
	            "EnPassant (4th field) or side-to-move (2nd field) conflict");
	    }
	}
	
	/**
	 * Get the en-passant FEN field at the current state (ie, at current depth)
	 * The returned value should be on the 3rd or 6th rank (the only valid values)
	 * @return square where capture via en passant can occur
	 */
	String getEnPassant()
	{
		byte epSquare = enPassantSq[depth];
		if(epSquare == NOSQUARE)
			return "-";
		return Util.SqToStr(epSquare);
	}

	void setHalfMoveClock(final String s)
	{
	    byte n = Byte.parseByte(s);
	    halfMoveClock[depth] = n;
	    if (n < 0){
	        throw new IllegalArgumentException("FEN 5th field: " +
	            "Half move clock must be non-negative");
	    }
	}

	/**
	 * Gets the number of halfMoves since the last irreversible 
	 * move (any capture or pawn move) at the current depth.
	 * @return the halfMoveClock
	 */
	private byte getHalfMoveClock() {
		return halfMoveClock[depth];
	}

	void setFullMoveClock(final String s)
	{
	    byte n = Byte.parseByte(s);
	    fullMoveClock[0] = n;
	    if (n <= 0){
	        throw new IllegalArgumentException("FEN 6th field: " +
	            "Full move clock must be greater than zero");
	    }  
	}

	/**
	 * Gets the current move number at the current depth
	 * @return the fullMoveClock
	 */
	private byte getFullMoveClock() {
		return fullMoveClock[depth];
	}

	boolean movesLeft(){
	  if (movesIndex < (numberOfLegalMovesToDepth[depth] + numberOfLegalMoves[depth]))
		return true;
	  else 
		return false;
	}


	boolean makeMove(int move, int side){
	    // makeMove(int move) updates the state of the game by making
	    // 'move' in the gametree
	    // Also updates castling status, enPassant, halfmove & fullmove clocks
	    // Changes sideToMove flag
	    
	    int from, to;           //squares involved
	    int from2 = -1;
	    int to2 = -1;
	    int moving, captured, promotion;  //pieces involved
	    boolean castling = false;
	    boolean pawnAdvTwo = false;

	    from = move & 0x3F;                         //first 6 bits
	    to = (move >> 6) & 0x3F;                    //next 6
	    moving = TO_PIECE[(move >> 12) & 0x7];      //next 3
	    captured = TO_PIECE[(move >> 15) & 0x7];    //next 3
	    promotion = TO_PIECE[(move >> 18) & 0x7];   //next 3
	    pawnAdvTwo = (Math.abs(to - from) == 16);

	    // If it's a king moving, check for castling
	    // If it's a rook moving, remove castling for that side if still available
	    // If it's a pawn capture check for en passant capture 
	    // or advancing two squares

	    //
	    // Check for a castling move
	    //
	    if (moving == KING){
	        //cout << "***** Should reach here ONLY if the KING moves!\n";
	        switch(side){
	            case Bitmap.WHITE:
	                //white king moved; castling is no longer legal for him
	                castle[depth + 1] = castle[depth] & (B_SHORT_CASTLE | B_LONG_CASTLE);
	                if (to == G1 && from == E1)
	                {
	                    from2 = H1;
	                    to2 = F1;
	                    castling = true;
	                }
	                else if (to == C1 && from == E1)
	                {
	                	from2 = A1;
	                    to2 = D1;
	                    castling = true;
	                }
	                break;
	            case Bitmap.BLACK:
	                //black king moved; castling is no longer legal for him
	                castle[depth + 1] = castle[depth] & (W_SHORT_CASTLE | W_LONG_CASTLE);
	                if (to == G8 && from == E8){
	                    //move the white king's rook
	                    from2 = H8;
	                    to2 = F8;
	                    castling = true;
	                } else if (to == C8 && from == E8){
	                    from2 = A8;
	                    to2 = D8;
	                    castling = true;
	                }
	                break;
	        }
	        if (castling){
	            //Move the appropriate rook
	            //pos.movePiece(sideToMove, ROOK, NONE, NONE, from2, to2);
		    pos.erasePiece(from2);
		    pos.placePiece(side, ROOK, to2);
	        }
	    }
	    else if (moving == ROOK)
	    {
	        if (side == Bitmap.WHITE){
	            if (Util.bool(castle[depth] & W_LONG_CASTLE) && from == A1)
	                castle[depth + 1] = castle[depth] & ~W_LONG_CASTLE;
	            else if (Util.bool(castle[depth] & W_SHORT_CASTLE) && from == H1)
	                castle[depth + 1] = castle[depth] & ~W_SHORT_CASTLE;
	        } else {
	            if (Util.bool(castle[depth] & B_LONG_CASTLE) && from == A8)
	                castle[depth + 1] = castle[depth] & ~B_LONG_CASTLE;
	            else if (Util.bool(castle[depth] & B_SHORT_CASTLE) && from == H8)
	                castle[depth + 1] = castle[depth] & ~B_SHORT_CASTLE;
	        }             
	    } else {
	        //EnPassant
	        if( moving == PAWN && captured == PAWN && pos.isEmpty(to))
	        {
	            switch(side){
	            case Bitmap.WHITE:
	                pos.erasePiece(to - 8);
	                captured = NONE;	
	                break;
	            case Bitmap.BLACK:
	                pos.erasePiece(to + 8);
	                captured = NONE;
	                break;
	            }
	        } else if (moving == PAWN && pawnAdvTwo) {
	            //Pawn advances two squares
	        	if(side == Bitmap.WHITE)
	                enPassantSq[depth + 1] = (byte)(from + 8);
	            else
	                enPassantSq[depth + 1] = (byte)(from - 8);
	        }

	        // Castle status remains constant since king and
	        // rook did not move
	        castle[depth + 1] = castle[depth];
	    }

	    //Move the piece
	    if(captured != NONE){
	        pos.erasePiece(to);
	    }
	    pos.erasePiece(from);
	    if(promotion != NONE) {
	    	pos.placePiece(side, promotion, to);
	    } else {
	    	pos.placePiece(side, moving, to);
	    }

	    //Update the halfmove clock 
	    //  Reset it for a pawn or capture move (irreversible move)
	    //  Otherwise, increment it from its previous value
	    if (captured != NONE || moving == PAWN){
	        halfMoveClock[depth + 1] = 0;
	    } else {
	         halfMoveClock[depth + 1] = (byte)(halfMoveClock[depth] + 1);
	    }

	    switch(side){
	    case Bitmap.WHITE:
	        sideToMove = BLACK;
	        fullMoveClock[depth + 1] = fullMoveClock[depth];
	        break;
	    case Bitmap.BLACK:
	        sideToMove = WHITE;
	        fullMoveClock[depth + 1] = (byte)(fullMoveClock[depth] + 1);
	        break;
	    }
	    depth++;
	    
	    //Compute the number of moves that come before
	    //this depth in the moves[] array
	    for(int i = 0; i < depth; i++)
	        numberOfLegalMovesToDepth[depth] += numberOfLegalMoves[i];
	    movesIndex = 0;
	    
	    return false;
	}




	boolean undoMove(int move, int side){
		// undoMove(int move) updates the state of the game by restoring 
		// the state of the board before 'move' was made
	    int from, to;           //squares involved
	    int from2 = -1;
	    int to2 = -1;
	    int moving, captured, promotion;  //pieces involved
	    boolean castling = false;
//	    boolean pawnAdvTwo = false;

	    from = move & 0x3F;                         //first 6 bits
	    to = (move >> 6) & 0x3F;                    //next 6
	    moving = TO_PIECE[(move >> 12) & 0x7];      //next 3
	    captured = TO_PIECE[(move >> 15) & 0x7];    //next 3
	    promotion = TO_PIECE[(move >> 18) & 0x7];   //next 3
//	    pawnAdvTwo = (Math.abs(to - from) == 16);

	    //Undo the sideToMove
	    //if (sideToMove == WHITE){
	    //    sideToMove = BLACK;
	    //} else {
	    //    sideToMove = WHITE;	
	    //}
	    
	    // Undo any moves made here at this depth
	    // by setting legalMoves to zero.
	    numberOfLegalMoves[depth] = 0;
	    halfMoveClock[depth] = 0;
	    
	    //Undo the depth
	    depth--;
	    
	    //Undo the moving piece
	    //pos.movePiece(sideToMove, moving, captured, promotion, to, from);   
	    pos.erasePiece(to);
	    pos.placePiece(side, moving, from);


	    //Replace the captured piece
	    //if(captured != NONE){
	    //    pos.erasePiece((color)Toggle((int)sideToMove), captured, to); 
	        //pos.placePiece((color)Toggle((int)sideToMove), captured, to);
	    //}

	    //Remove the promotion piece
	    //if(promotion != NONE){
	    //    pos.erasePiece((color)Toggle((int)sideToMove), promotion, to);
	    //}


	    //Undo a castling move (that is, undo the rook move)
	    if (moving == KING){
	        switch(side){
	        case Bitmap.WHITE:
	            if (to == G1 && from == E1){
	                from2 = H1;
	                to2 = F1;
	                castling = true;
	            } else if (to == C1 && from == E1){
	                from2 = A1;
	                to2 = D1;
	                castling = true;
	            }
	            break;
	        case Bitmap.BLACK:
	            if (to == G8 && from == E8){
	                from2 = H8;
	                to2 = F8;
	                castling = true;
	            } else if (to == C8 && from == E8){
	                from2 = A8;
	                to2 = D8;
	                castling = true;
	            }
	            break;
	        }
	        if (castling){
	        	//Undo the rook
	        	pos.erasePiece(to2);
	        	pos.placePiece(side, ROOK, from2);
	        }
	    }
	    
	    //Place captured piece back on the board
	    if(captured != NONE){
	        //Put the EnPassant captured pawn back
	        if( moving == PAWN && captured == PAWN && pos.isEmpty(to))
	        {
//	        	pos.placePiece(Util.opp(side), PAWN, (side == WHITE) ? to - 8 : to + 8);
	            switch(side){
	            case Bitmap.WHITE:
	                pos.placePiece(Bitmap.BLACK, PAWN, to - 8);
	                break;
	            case Bitmap.BLACK:
	                pos.placePiece(Bitmap.WHITE, PAWN, to + 8);
	                break;
	            }

	        } else { //Normal capture
		        //Put any other captured piece back
	        	pos.placePiece(Util.opp(side), captured, to);
	        }
	    }

	    sideToMove = side; //we're undoing the move for side so it will still be side's turn to move 
	    return false;
	}

	void display(){
	    AbstractDisplayer displayer = new Displayer();
	    System.out.println(displayer.formatBoard(pos));
	}


	// void displayAndPrompt(){
//	     pos.DisplayBoard();
//	     pos.displayFEN();
//	     cout << ' ';
//	     if (sideToMove == WHITE)
//	         cout << "w ";
//	     else
//	         cout << "b ";
//	     if (W_SHORT_CASTLE & castle[depth])
//	         cout << 'K';
//	     if (W_LONG_CASTLE & castle[depth])
//	         cout << 'Q';
//	     if (B_SHORT_CASTLE & castle[depth])
//	         cout << 'k';
//	     if (B_LONG_CASTLE & castle[depth])
//	         cout << 'q';
//	     if (castle[depth] == 0)
//	         cout << '-';
//	     cout << ' ';
	    
//	     if (enPassantSq[depth] == NOSQUARE){
//	         cout << "- ";
//	     } else {
//	         printf("%c%c ", (char)(enPassantSq[depth] % 8) + 'a',
//	                (char)(enPassantSq[depth] / 8) + '1');
//	     }
//	     cout << halfMoveClock[depth] << " " << fullMoveClock[depth] << endl;
//	     cout << "depth: " << depth << endl;
//	     cout << "legal moves  : " << legalMoves[depth] << endl;
//	     displayMoves();
	    
//	     //Switch sides and prompt for move
//	     switch(sideToMove){
//	     case WHITE:
//	         cout << "White's move: ";
//	         break;
//	     case BLACK:
//	         cout << "Black's move: ";
//	         break;
//	     }
	// }


	// TODO: remove the "&& i < 80" as a stopping condition on displayMoves
	// void displayMoves(){
	//   int i = 0;
	//   move_t move = 0;
	//   setFirstMove();
	//   while(movesLeft()){
//	 	move = getNextMove();
//	 	cout << i++ << ". ";
//	 	displayMove(move, 0, 0);
//	 	cout << endl;
	//   }

	//   /*    for (int i = 0; i < legalMoves[depth] && i < 80; i++){
//	 		cout << i << '\t';
//	         displayMove(moves[i], false, false);
//	         cout << endl;
//	     }
	//   */
	// }





//	public final static void main(String[] args){
//
//		GameState g("start.fen");
//	    char fen[100];
//	    char fen1[] = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
//	    char fen2[] = "rnbqkbnr/pppppppp/3p4/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R";
//	    char fen3[] = "8/1K1pP3/8/2Nn4/8/1kP5/2P5/n7";
//	    char fen4[] = "q1n5/1P3P2/2P5/8/K7/8/k6P/8";
//	    char fen5[] = "kppppppp/pppppppp/8/K2Q3q/pppppppp/8/8/8";
//	    char fen6[] = "rnbqkbnr/pp1ppppp/8/3pP3/2p5/8/PPPP1PPP/RNBQKBNR w KQkq d6 6 2 ";
//	    
//	    // copy in the FEN string
//	    //strncpy(fen, fen6, 100);
//
//		//Generate moves for WHITE
//	    //g.set(fen);
//		g.display();
//		GenerateCaptures(g, g.depth);
//	    GenerateNonCaptures(g, g.depth);
//	    g.displayMoves();    
//
//		cout << "\n\n";
//		//Generate moves for BLACK
//	    g.depth++;
//	    g.sideToMove = BLACK;
//
//		g.display();
//		GenerateCaptures(g, g.depth);
//	    GenerateNonCaptures(g, g.depth);
//	    g.displayMoves();
//		cout << "\n\n";
//		int max = g.legalMovesToDepth[g.depth] + g.legalMoves[g.depth];
//		cout << "moves to depth : " << g.legalMovesToDepth[g.depth] << endl;;
//		cout << "moves at depth : " << g.legalMoves[g.depth] << endl;
//		cout << max << endl;
//		for (int i = 0; i < max; i++){
//		  cout << i << "# "; 
//		  displayMove(g.moves[i], 0, 0);
//		  cout << "\n";
//	    }
//	    return 0;
//
//	}

}
