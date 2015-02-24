/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess.movegen;

import static com.jeremybrooks.chess.base.Bitmap.*;

import org.apache.log4j.Logger;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.base.SlidingPiece;
import com.jeremybrooks.chess.base.Square;
import com.jeremybrooks.chess.util.DiagonalIterator;
import com.jeremybrooks.chess.util.LeftDiagonalIterator;
import com.jeremybrooks.chess.util.RightDiagonalIterator;
import com.jeremybrooks.chess.util.Util;

/**
 * @author jeremy
 *
 */
public class Attacks {
    private static final Logger log = Logger.getLogger(Attacks.class);
    static final Attacks INSTANCE = new Attacks(); //default scope for testing

    public static class Magic {
    	public final long number;
    	public final int shift;
		
    	public Magic(long number, int shift) {
			super();
			this.number = number;
			this.shift = shift;
		}
    }
    
    private static final int FILE1 = 0;
    private static final int FILE8 = 7;
    private static final int RANK8 = 7;

    short base[][] = new short[8][64]; //datatype needs to be 8bits, using short instead to avoid some problem

    //Masks have a one bit set corresponding to the square on the board.
    //The array index is the square number on the board, an element of {0,1,...,63}
    long mask[] = new long[64];
    long mask90[] = new long[64];  
    long mask45R[] = new long[64];
    long mask45L[] = new long[64];
    long king[] = new long[64];
    long knight[] = new long[64];

    //The first index is the square number and
    //the second is the status of the ray the piece is aligned on
    long rank[][] = new long[64][64];
    long file[][] = new long[64][64];
    long R45[][] = new long[64][64];
    long L45[][] = new long[64][64];
    long pawn[][] = new long[2][64];
    long whitepawn[] = new long[64];
    long blackpawn[] = new long[64];        

    long plus1[] = new long[64];
    long plus7[] = new long[64];
    long plus8[] = new long[64];
    long plus9[] = new long[64];
    long minus1[] = new long[64];
    long minus7[] = new long[64];
    long minus8[] = new long[64];
    long minus9[] = new long[64];

    /**
     * Generate all squares attacked by the piece (the legality of the attack
     * is not considered; these could be called "pseudoAttacks").
     * 
     * @param piece The attacking piece
     * @param onSquare The square of the attacking piece
     * @param position The current given position
     * @return
     */
    public static final long forPiece(Piece piece, int onSquare, Position position)
    {
        long pseudoAttacks = 0L;
        int color = piece.encodedByColor() > 0 ? Piece.WHITE : Piece.BLACK;
        switch(piece.index())
        {
        case Piece.PAWN:
            pseudoAttacks = INSTANCE.pawn[color][onSquare]; //TODO: is it cleaner to index by piece, aka INSTANCE.attacks[color][piece.index()][onSquare] ??
            break;
        case Piece.KNIGHT:
            pseudoAttacks = INSTANCE.knight[onSquare];
            break;
        case Piece.KING:
            pseudoAttacks = INSTANCE.king[onSquare];
            break;
        case Piece.BISHOP:
        case Piece.ROOK:
        case Piece.QUEEN:
            SlidingPiece slider = (SlidingPiece) piece;
            if (slider.slidesOnDiagonals())
            {
                long allPieces45Left = position.getOccupied(-45);
                long allPieces45Right = position.getOccupied(45);
                pseudoAttacks |= bishopAttacks(onSquare, allPieces45Left, allPieces45Right);
            }
            if (slider.slidesLaterally())
            {
                long allPiecesByRank = position.getOccupied(0);
                long allPiecesByFile = position.getOccupied(90);
                pseudoAttacks |= rookAttacks(onSquare, allPiecesByRank, allPiecesByFile);
            }
        }
        return pseudoAttacks;
    }
    
    private static long bishopAttacks(int bishopSquare, long allPieces45Left, long allPieces45Right)
    {
        long attacks;
        int stat1, stat2;

        stat1 = status45L (allPieces45Left, bishopSquare);
        stat2 = status45R (allPieces45Right, bishopSquare);
        attacks = INSTANCE.L45[bishopSquare][stat1];
        attacks |= INSTANCE.R45[bishopSquare][stat2];
        return attacks;
    }

    private static long rookAttacks(int rookSquare, long allPiecesByRank, long allPiecesByFile)
    {
        long attacks;
        int stat1, stat2;

        stat1 = status (allPiecesByRank, rookSquare);
        stat2 = status90 (allPiecesByFile, rookSquare);
        attacks = INSTANCE.rank[rookSquare][stat1];
        attacks |= INSTANCE.file[rookSquare][stat2];
        return attacks;
    }
 
    public static long attacksTo(int square, long targets, int sideUnderAttack, GameState state)
    {
    	long attackers = Attacks.attackers(state, sideUnderAttack, square);
    	long intersection = attackers & targets;
//    	System.out.println("  attackers: "+Util.displaySquaresStr(attackers));
//    	System.out.println("  targets  : "+Util.displaySquaresStr(targets));
//    	System.out.println("  intersect: "+Util.displaySquaresStr(intersection)); 
		return intersection;
    }
//
//    public static long xrayRookAttacks(long occupied, long blockers, int rookSquare)
//    {
//    	long occupied90Degrees = 0L; //TODO: compute me
//        long attacks = INSTANCE.rank[rookSquare][status (occupied, rookSquare)]
//                	 | INSTANCE.file[rookSquare][status90 (occupied90Degrees, rookSquare)];
//
//    }

    /** 
     * Returns a bitbrd of the pieces (excluding the king) attacking 
     * "square".  "side" represents the color/side whose pieces we want to
     * see that are under attack.
     * To see all the black pieces attacking e4 do this:
     *
     *        attacks = Attackers(g, Color.WHITE, E4);
     *
     * To see all the white pieces attacking g8 do this;
     *
     *        attacks = Attackers(g, Color.BLACK, G8);
     *
     * NOTE: the king is not included in the attackersk
     * 
     * @param g
     * @param sideUnderAttack
     * @param squareUnderAttack
     * @return
     */
    public static long attackers(GameState g, int sideUnderAttack, int squareUnderAttack)
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
             case Piece.WHITE:
                 attackers |= INSTANCE.whitepawn[squareUnderAttack] & position.getOpponentPawns(sideUnderAttack);

//                  if (g.enPassantSq[depth] != NOSQUARE){
//                      if (/*there's a pawn on either side*/)
//                          attackers |= INSTANCE.pawn[side][from] &
//                              (1L << g.enPassantSq[depth]);

//                  }
                 attackers |= INSTANCE.knight[squareUnderAttack] & position.getOpponentKnights(sideUnderAttack);
                 attackers |= INSTANCE.king[squareUnderAttack] & position.getOpponentKing(sideUnderAttack);

                 rankFileAtt = INSTANCE.rank[squareUnderAttack][status (position.getOccupied(0), squareUnderAttack)] |
                     INSTANCE.file[squareUnderAttack][status90 (position.getOccupied(90), squareUnderAttack)];
                 rooksQueens = position.getOpponentRooks(sideUnderAttack) | position.getOpponentQueens(sideUnderAttack);
                 attackers |= rankFileAtt & rooksQueens;

                 diagAtt = INSTANCE.L45[squareUnderAttack][status45L (position.getOccupied(-45), squareUnderAttack)] |
                     INSTANCE.R45[squareUnderAttack][status45R (position.getOccupied(45), squareUnderAttack)];
                 bishopsQueens = position.getOpponentBishops(sideUnderAttack) | position.getOpponentQueens(sideUnderAttack);
                 attackers |= diagAtt & bishopsQueens;
                 break;
            case Piece.BLACK:
                 attackers |= INSTANCE.blackpawn[squareUnderAttack] & position.getOpponentPawns(sideUnderAttack);
//                  if (g.enPassantSq[depth] != NOSQUARE){
//                      attackers |= INSTANCE.pawn[side][from] &
//                          (1L << g.enPassantSq[depth]);
//                  }
                 attackers |= INSTANCE.knight[squareUnderAttack] & position.getOpponentKnights(sideUnderAttack);
                 attackers |= INSTANCE.king[squareUnderAttack] & position.getOpponentKing(sideUnderAttack);

                 rankFileAtt = INSTANCE.rank[squareUnderAttack][status (position.getOccupied(0), squareUnderAttack)] | 
                     INSTANCE.file[squareUnderAttack][status90 (position.getOccupied(90), squareUnderAttack)];
                 rooksQueens = position.getOpponentRooks(sideUnderAttack) | position.getOpponentQueens(sideUnderAttack);
                 attackers |= rankFileAtt & rooksQueens;

                 diagAtt = INSTANCE.L45[squareUnderAttack][status45L (position.getOccupied(-45), squareUnderAttack)] |
                     INSTANCE.R45[squareUnderAttack][status45R (position.getOccupied(45), squareUnderAttack)];
                 bishopsQueens = position.getOpponentBishops(sideUnderAttack) | position.getOpponentQueens(sideUnderAttack);
                 attackers |= diagAtt & bishopsQueens;
                 break;
        }
        return attackers;
    }

    
    private static byte status(long b, int sq){
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

    private static byte status90(long b, int sq){
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


    private static byte status45L(long b, int sq){
        
        //for diagonals of length 3 or less, status should be zero
        
        int x = fileNumber(sq);
        int y = rankNumber(sq);
        
        byte temp = 0;
        switch (x+y){
        case 0: 
            temp =  0;                      //a1-a1 diag
            break;
        case 1: 
            temp =  0; //((b >> 1+1) & 1)   //b1-a2 diag
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
            temp = 0; //(b >> 61+1) & 1;  //h7-g8 diag
            break;
        case 14: 
            temp = 0;                     //h8-h8 diag
            break;
        }
        return temp;
    }


    private static byte status45R(long b, int sq){

        //for diagonals of length 3 or less, status should be zero
        
        //Note the difference in x and y from Status45L()
        //We perform the transformation function T on x and y
        //where T(x1, y1) = (7 - x1, y1)
        
        int x = 7 - fileNumber(sq);  
        int y = rankNumber(sq);
        
        byte temp = 0;
        switch (x+y){
        case 0: 
            temp = 0;                          //h1-h1 diag
            break;
        case 1: 
            temp = 0;                          //g1-h2 diag
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
            temp = (byte)((b >> 49+1) & 7);    //a4-e8 diag
            break;
        case 11: 
            temp = (byte)((b >> 54+1) & 3);    //a5-d8 diag
            break;
        case 12: 
            temp = (byte)((b >> 58+1) & 1);    //a6-c8 diag
            break;
        case 13: 
            temp = 0;                          //a7-b8 diag
            break;
        case 14: 
            temp = 0;                          //a8-a8 diag
            break;
        }
        return temp;
    }
    
    public static final long maskFor(int square){     return INSTANCE.mask[square]; }
    public static final long mask90For(int square){   return INSTANCE.mask90[square]; }
    public static final long mask45RFor(int square){  return INSTANCE.mask45R[square]; }
    public static final long mask45LFor(int square){  return INSTANCE.mask45L[square]; }
    public static final long maskPlus1(int square){   return INSTANCE.plus1[square]; }
    public static final long maskPlus7(int square){   return INSTANCE.plus7[square]; }
    public static final long maskPlus8(int square){   return INSTANCE.plus8[square]; }
    public static final long maskPlus9(int square){   return INSTANCE.plus9[square]; }
    public static final long maskMinus1(int square){  return INSTANCE.minus1[square]; }
    public static final long maskMinus7(int square){  return INSTANCE.minus7[square]; }
    public static final long maskMinus8(int square){  return INSTANCE.minus8[square]; }
    public static final long maskMinus9(int square){  return INSTANCE.minus9[square]; }
    
    public Attacks(){
      generateMasks();
      generateBaseAttacks();
      generateRankAttacks();
      generateFileAttacks();
      generateDiagonal45DegreesRightAttacks();
      generateDiagonal45DegreesLeftAttacks();
      generateWhitePawnAttacks();
      generateBlackPawnAttacks();
      generateKingKnightAttacks();
    }

    private void generateMasks(){
        log.trace("generating masks");
        genMask();
        genMask90();

        genMask45DegreesRight();
        genMask45DegreesLeft();
        genMasksPlusMinus();
    }

    private void genMask() {
        long m = 1;
        for(int currentSquare=Bitmap.A1; currentSquare <= Bitmap.H8; currentSquare++){
            mask[currentSquare] = m << currentSquare;
        }
    }
  
    private void genMask90() {
        int squareIndex = 0;
        for(int lastSquareOnFile=Bitmap.A8; 
                lastSquareOnFile <= Bitmap.H8;
                lastSquareOnFile++)
        {
            for(int bitToSet=lastSquareOnFile; 
                    bitToSet >= 0;
                    bitToSet-=8)
            {
                mask90[squareIndex] = 1L << bitToSet;
                squareIndex++;
            }
        }
    }

    private void genMask45DegreesRight(){
        log.trace("generating single-bit bitmasks rotated 45 deg right");

        int squareIndex = 0;
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
            DiagonalIterator rightDiagIterator = new RightDiagonalIterator(d);
            while(rightDiagIterator.hasNext())
            {
                int bitToSet = rightDiagIterator.next();
                mask45R[squareIndex] = Bitmap.withOneBitSet(bitToSet);
                squareIndex++;
            }
        }
    }
    
    private void genMask45DegreesLeft(){
          log.trace("generating single-bit bitmasks rotated 45 deg left");

        int squareIndex = 0;
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
            LeftDiagonalIterator leftDiagIterator = new LeftDiagonalIterator(d);
            while(leftDiagIterator.hasNext())
            {
                int bitToSet = leftDiagIterator.next();
                mask45L[squareIndex] = Bitmap.withOneBitSet(bitToSet);
                squareIndex++;
            }
        }
    }
    
    private void genMasksPlusMinus(){
        log.trace("generating plus/minus masks");

        //For each square, generate the masks plusN and minusN where N is {1,7,8,9}
        //Also, there should never be more than 7 squares attacked 
        //in the each bitmap
        
        for (int sq=A1; sq<=H8; sq++){
            plus1[sq] = 0;
            for(int bit=sq+1; fileNumber(bit) != FILE1; bit++){
                plus1[sq] |= 1L << bit;
            }
            
            minus1[sq] = 0;
            for(int bit=sq-1; fileNumber(bit) != FILE8 && bit >= 0; bit--){
                minus1[sq] |= 1L << bit;
            }

            plus8[sq] = 0;
            for(int bit=sq+8; Bitmap.rankNumber(bit) <= RANK8; bit+=8){
                plus8[sq] |= 1L << bit;
            }
            
            minus8[sq] = 0;
            for(int bit=sq-8; bit >= 0; bit-=8){
                minus8[sq] |= 1L << bit;
            }
            
            plus9[sq] = 0;
            for (int bit=sq+9; Bitmap.fileNumber(bit) != 0 && Bitmap.rankNumber(bit) != 8; bit+=9){ 
                plus9[sq] |= 1L << bit;
            }

            minus9[sq] = 0;
            for (int bit=sq-9; Bitmap.fileNumber(bit) != 7 && bit >= 0; bit-=9){
                minus9[sq] |= 1L << bit;
            }

            plus7[sq] = 0;
            for(int bit=sq+7; Bitmap.fileNumber(bit) != FILE8 && Bitmap.rankNumber(bit) != 8; bit+=7){
                plus7[sq] |= 1L << bit;
            }

            minus7[sq] = 0;
            for(int bit=sq-7; Bitmap.fileNumber(bit) != 0 && bit >= 0; bit-=7){
                minus7[sq] |= 1L << bit;
            }
        }
    }


    private void generateBaseAttacks(){
    /*
     *  Generate RANK (base) attacks from squares a1 to h1
     *
     *  attacks[][64] is what we will use for the first rank of the chessboard.
     ****************************************************************************  
     *  Generates the rank attacks for a strictly rank-moving piece on each
     *  square of the chessboard given a bitmap (unsigned char) of the occupied
     *  squares on that rank.  It starts at a1 (square 0) and runs to 
     *  h1 (square 7). There are 256 different occupied states for a single 
     *  rank. Since the squares at both edges of the board (a-th and h-th)
     *  are are attacked independent of whether they are occupied or not, 
     *  we only need keep track of the 6 bits in between (bits 1-6 instead of 
     *  all 8 bits).  That means I only need attacks[64][2^6] instead of 
     *  rank_attacks[64][2^8]; 2^6 = 64 and 2^8 = 256 so this means I save 75%
     *  on memory storage. 
     *
     *  ALGORITHM:
     *  
     *  //Generate moves for rank-mover on each square from a1 to h1
     *   FOR each square on chessboard (square = 0 to 7 inclusive)
     *      FOR each status of rank (status = 0 to 63 inclusive)
     *       occupied <-- status BITSHIFT-LEFT 1
     *       left  <-- found bit/square of 1st blocking piece on left
     *       right <-- found bit/square of 1st blocking piece on right
     *         IF (no blocking piece on right and/or left)
     *            Set the bit at the far right and/or left
     *         Set left and right bounds on moves
     *       Set all bits in moves between left and right blocker
     *         Delete piece's current square as a valid move
     *       Store moves
     *       ROF
     *    ROF        
     **************************************************************************
     */
        log.trace("generating base attacks");
        
        //These shouldn't exceeded what would fit in a byte
        //We use int's because shifting in java always upcasts to an int anyway
        int piece = 0x01; //originally an unsigned char in C++
        int occupied = 0;      //originally an unsigned char in C++
        //occ is the occupied squares on the rank BITWISE-ANDed with 01111110b
        
        for (int square=0; piece != 0x100; piece <<= 1, ++square){
            // square = 0  --> piece = 0x01
            // square = 1  --> piece = 0x02
            // square = 2  --> piece = 0x04
            //   .
            //   .
            // square = 7  --> piece = 0x80
            // exit when piece = 0x100
            
            //C5 commented code
            for (int occupationCombination = 0; //G19
                    occupationCombination < 64;
                    occupationCombination++)
            {
                occupied = occupationCombination << 1;
                int leftBlocker = firstLeftSideBlockingPiece(piece, occupied);
                int rightBlocker = firstRightSideBlockingPiece(piece, occupied);
                int attacks = attacksIncludingBlockers(piece, leftBlocker, rightBlocker);    
                base[square][occupationCombination] = (short) attacks;
            }
        }
    }

    private static int attacksIncludingBlockers(int piece, int leftBlocker, int rightBlocker) {
        // 1. attack the blockers
        // 2. attack all bits between blockers 
        // 3. exclude the piece doing the attacking
        int attacks = leftBlocker | rightBlocker; 
        while (leftBlocker > rightBlocker){
            rightBlocker <<= 1;
            leftBlocker >>= 1;  //should this be an unsigned shift >>>= ????
            attacks |= leftBlocker | rightBlocker;
        }
        attacks = attacks ^ piece; //exclude the attacker
        return attacks;
    }

    private static int firstRightSideBlockingPiece(int pieceBit, int occupiedBits) {
        final int RIGHT_END = 0x01;
        int p;
        int right = 0;
        p = (byte) (pieceBit >> 1);
        for (; right == 0 && p != 0; p >>>= 1){ //do an UNSIGNED SHIFT
            right = occupiedBits & p;
        }
        if (right == 0)
            right = RIGHT_END;
        return right;
    }

    private static int firstLeftSideBlockingPiece(int pieceBit, int occupiedBits) {
        final int LEFT_END = 0x80;
        int p;
        int left = 0;
        p = pieceBit << 1;
        for (; left == 0 && p != 0; p <<= 1){
            left = occupiedBits & p;
        }
        if (left == 0){
            left = LEFT_END;
        }
        return left;
    }

    
    private void generateRankAttacks(){
        log.trace("generating rank attacks");

        int shift;
        for (int sq = Bitmap.A1; sq <= Bitmap.H8; ++sq){
            shift = 8 * (sq / 8);   //int division required
            for (int occupationCombination = 0; occupationCombination < 64; ++occupationCombination){
                //board = base[sq % 8][st];
                rank[sq][occupationCombination] = ((long)base[fileNumber(sq)][occupationCombination]) << shift;
            }
        }
    }

    private void generateFileAttacks(){
        log.trace("generating file attacks");
        
        //Generate FILE attacks for all squares (a1 to h8)
        //A graphic of the board rotated 90 degrees left.
        //The position of the square in the graphic represent the bit positions
        //within the bitboard/bitmap.  For example, the 56 square in the lower
        //left represents the 2^0 bit in the bitmap because it's in the LSB's spot.
        //Also, the 18 square represents the 2^21 bit in the bitboard/bitmap.
        //To read the 64bit int from this graphic, start in the lower left (the LSB)
        //and read it from left to right until you reach the     is read from
        //the lower left to the upper right
        //                                                       MSB
        //                                                       /
        //               8    7    6    5    4    3    2    1   /
        //             |----|----|----|----|----|----|----|----|
        //           h | 63 | 55 | 47 | 39 | 31 | 23 | 15 | 07 |
        //             |----|----|----|----|----|----|----|----|
        //           g | 62 | 54 | 46 | 38 | 30 | 22 | 14 | 06 |
        //             |----|----|----|----|----|----|----|----|
        //           f | 61 | 53 | 45 | 37 | 29 | 21 | 13 | 05 |
        //             |----|----|----|----|----|----|----|----|
        //           e | 60 | 52 | 44 | 36 | 28 | 20 | 12 | 04 |
        //             |----|----|----|----|----|----|----|----|    
        //           d | 59 | 51 | 43 | 35 | 27 | 19 | 11 | 03 |    
        //             |----|----|----|----|----|----|----|----|    
        //           c | 58 | 50 | 42 | 34 | 26 | 18 | 10 | 02 |    
        //             |----|----|----|----|----|----|----|----|
        //           b | 57 | 49 | 41 | 33 | 25 | 17 | 09 | 01 |    
        //             |----|----|----|----|----|----|----|----|    
        //           a | 56 | 48 | 40 | 32 | 24 | 16 | 08 | 00 |    
        //             |----|----|----|----|----|----|----|----|
        //            /  8    7    6    5    4    3    2    1
        //           /
        //        LSB
        //
        //

        byte mask = 1;
        int rankNumber, maskindex;
        for (int f = A1; f <= H8; ++f){
            rankNumber = rankNumber(f);
            for (int occupationCombination=0; occupationCombination < 64; ++occupationCombination){
                for (int i=0; i < 8; ++i){
                    maskindex = fileNumber(f) + (i * 8);
                    if((base[rankNumber][occupationCombination] & (mask << i)) > 0)    //if it's set, set bit
                        file[f][occupationCombination] |= 1L << maskindex;
                }
            }
        } 
    }

    private void generateDiagonal45DegreesRightAttacks(){
        log.trace("generating diagonal attacks 45 deg right (a1-h8)");
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
            RightDiagonalIterator rightDiagIterator = new RightDiagonalIterator(d);
            int len = rightDiagIterator.diagonalLength();
            int maxOccupationCombination = getMaxOccupationCombination(len);
            //NOW one loop for each sq in diagonal
            for (int i=0; i < len ; i++){
                //And finally, one loop for each occupation combination
                int sq = rightDiagIterator.next();
                byte occupationCombination = 0;
                for (; occupationCombination <= maxOccupationCombination; occupationCombination++){
                    R45[sq][occupationCombination] = getDiagonalSet(base[i][occupationCombination], new RightDiagonalIterator(d));
                }
            }
        }
    }

    private void generateDiagonal45DegreesLeftAttacks(){
        log.trace("generating diagonal attacks 45 deg left (h1-a8)");
        for(int d = 0; d < 15; d++){ //one loop for each diagonal
            LeftDiagonalIterator leftDiagIterator = new LeftDiagonalIterator(d);
            int len = leftDiagIterator.diagonalLength();
            int maxOccupationCombination = getMaxOccupationCombination(len);
            //NOW one loop for each sq in diagonal
            int i = 0;
            while(leftDiagIterator.hasNext())
            {
                int currentSquare = leftDiagIterator.next();
                //And finally, one loop for each occupied combination
                byte occupationCombination = 0;
                for (; occupationCombination <= maxOccupationCombination; occupationCombination++){
                    L45[currentSquare][occupationCombination] = getDiagonalSet(base[i][occupationCombination], new LeftDiagonalIterator(d));
                }
                i++;
            }
        }
    }

    private long getDiagonalSet(short bitmap, DiagonalIterator iterator){
        //construct a bitboard such that the bits set in the given bitmap
        //are set as the bits within the diagonal defined within the iterator.
        long board = 0;
        int diagonalLength = iterator.diagonalLength();
        for (int bitToCheck=0;
                bitToCheck < diagonalLength;
                bitToCheck++)
        {
            int currentSquare=iterator.next();
            if(isBitSet(bitmap, bitToCheck)) 
                board |= (1L << currentSquare);    //set bit
        }
        //C5 commented code
        return board;
    }

    //get the correct maximum status to count to
    //maxstatus is the diagonal minus the outer bit on each end
    private static int getMaxOccupationCombination(int len) {
        int maxCombinations;
        if (len < 3)
            maxCombinations = 0;
        else
            maxCombinations = (1 << (len-2)) - 1;  //maxstatus = 2^(len-2) - 1
        return maxCombinations;
    }

    private static boolean isBitSet(short bitmap, int index) {
        return (bitmap & (1 << index)) != 0;
    }
    
    private static boolean isBitSet(int bitmap, int index) {
        return (bitmap & (1 << index)) != 0;
    }

    private void generateWhitePawnAttacks(){ //G30 functions should do one thing
        int white = 0;
        int square;
        int offsetForAttackingRight = 9; //G19
        int offsetForAttackingLeft = 7; //G19

        //NOTE: we compute pawn attacks for every square
        //that is not on the eighth rank (not just the ones a pawn
        //can be on legally) because MoveGenerator.isAttacked()
        //uses it.
        
        log.trace("generating white pawn attacks");
        for (square = Bitmap.A1; square <= Bitmap.H7; square++){
            //Use these conditionals below to ensure we don't "go off the board" and
            //all accesses to the mask[64] array are valid. We'll get an AIOOB if 
            //we eliminate the redundancy and pull out descriptive local 
            //variables ie, pawnAttacksLeft, pawnAttacksRight. (G19 again).
            if(Square.isOnLeftEdgeOfBoard(square)){
                whitepawn[square] = pawn[white][square] = mask[square+offsetForAttackingRight];
            } else if (Square.isOnRightEdgeOfBoard(square)) {        
                whitepawn[square] = pawn[white][square] = mask[square+offsetForAttackingLeft];
            } else {
                whitepawn[square] = pawn[white][square] =
                        mask[square+offsetForAttackingLeft] | mask[square+offsetForAttackingRight];
            }
        }
    }

    private void generateBlackPawnAttacks(){ //G30
        int black = 1;
        int square;
        int offsetForAttackingLeft = -7; //G19
        int offsetForAttackingRight = -9; //G19

        //NOTE: we compute pawn attacks for every square
        //that is not on the eighth rank (not just the ones a pawn
        //can be on legally) because MoveGenerator.isAttacked()
        //uses it.

        log.trace("generating black pawn attacks");
        for (square = Bitmap.H8; square >= Bitmap.A2; square--){
            //Use these conditionals below to ensure we don't "go off the board" and
            //all accesses to the mask[64] array are valid. We'll get an AIOOB if 
            //we eliminate the redundancy and pull out descriptive local 
            //variables ie, pawnAttacksLeft, pawnAttacksRight. (G19 again).
            if(Square.isOnLeftEdgeOfBoard(square)){
                blackpawn[square] = pawn[black][square] = mask[square + offsetForAttackingLeft];
            } else if (Square.isOnRightEdgeOfBoard(square)) {        
                blackpawn[square] = pawn[black][square] = mask[square + offsetForAttackingRight];
            } else {
                blackpawn[square] = pawn[black][square] = 
                        mask[square + offsetForAttackingLeft] | mask[square + offsetForAttackingRight];    
            }
        }
    }

    private void generateKingKnightAttacks(){  //violates G30 by doing more than one thing, both king AND knight
        //A 5x5 grid of squares, indexed from 0 to 24, can represent the
        //squares a king or knight can move to.  The piece rests
        //on square 12 (12th bit if zero-indexed) in the 5X5 grid.  
        //
        //For a king the attacks(bits) are as follows:
        //        {6,7,8,11,13,16,17,18} = 0x00A1122A
        //
        //For a knight the attacks(bits) are as follows:
        //        {1,3,5,9,15,19,21,23} = 0x000729C0
        //
        //  The following are the sets of squares
        //that are currently on the board(meaning they don't represent squares
        //that are off-the-board.  For instance, the valid squares when a piece
        //sits on a1 is the intersection (or bitwise ANDing) of the sets
        //rowGrid[0] and columnGrid[0] which yields the following:
        //
        //                0x01FFFC00    // rowGrid[0]
        //    BITWISE-AND   0x01CE739C    // colGrid[0]
        // ---------------------------------
        //                0x01CE7000    // 5x5 grid of possible moves  
        //
        //NOTE: This method should make sure the 12th bit in either the king5x5 
        //      or knight5x5 is always zero because you can't make a move to the 
        //      square you're already on, i.e. Nf3-f3 or Kg7-g7

        log.trace("generating king/knight attacks");

        int king5x5        = 0x000729C0;
        int knight5x5    = 0x00A8822A;

        //A mapping to an offset in the 64-bit bitboard that is
        //indexed by a bit in the 5x5 grid
        int squareOffset[] = {-18, -17, -16, -15, -14, //G21 (the format and naming demonstrates understanding the algorithm??)  
                              -10,  -9,  -8,  -7,  -6,
                               -2,  -1,   0,   1,   2,
                                6,   7,   8,   9,  10,
                               14,  15,  16,  17,  18}; //should be size 25
            
        //The algorithm depends on the initialized array values being zero
        final int sizeOfSet = 8;
        int rowGrid[] = new int[sizeOfSet];
        int colGrid[] = new int[sizeOfSet];
        
        //All bits in the 5x5 grid are set if we're not on
        //any of the two outermost ranks(rows) or files(columns) of the chessboard.
        rowGrid[2] = rowGrid[3] = rowGrid[4] = rowGrid[5] = 0x01FFFFFF; //third to sixth rank 
        rowGrid[0] = 0x01FFFC00;    //first rank
        rowGrid[1] = 0x01FFFFE0;    //second rank
        rowGrid[6] = 0x000FFFFF;    //seventh rank
        rowGrid[7] = 0x00007FFF;    //eighth rank
        
        colGrid[2] = colGrid[3] = colGrid[4] = colGrid[5] = 0x01FFFFFF; // c-file to f-file
        colGrid[0] = 0x01CE739C;    //a-file
        colGrid[1] = 0x01EF7BDE;    //b-file
        colGrid[6] = 0x00F7BDEF;    //g-file
        colGrid[7] = 0x00739CE7;    //h-file

        for (int square = 0; square < 64; square++)
        {
            int rankIndex = rankNumber(square); //G19
            int fileIndex = fileNumber(square); //G19
            int kingGrid = rowGrid[rankIndex] & colGrid[fileIndex] & king5x5;  //G19 use explanatory variables
            int knightGrid = rowGrid[rankIndex] & colGrid[fileIndex] & knight5x5; //G19

            for (int gridIndex = 0; gridIndex < 25; gridIndex++){
                int bitToSet = square + squareOffset[gridIndex]; //G19
                if (isBitSet(kingGrid, gridIndex)){ //G28 encapsulate conditionals
                    king[square] |= Bitmap.withOneBitSet(bitToSet); //N1 choose descriptive names
                }
                if (isBitSet(knightGrid, gridIndex)){ //G28
                    knight[square] |= Bitmap.withOneBitSet(bitToSet); //N1
                }
            }
        }
    }

    public String baseAttacksAsHumanReadableString(){
        String msg =
            "    The following attacks are generated by a rook given all\n" +
            "possible ways that a rank is occupied by other pieces.\n" +
            "We do this by starting a piece (rook or Queen) on A1 and\n" +
            "moving it along the first rank one square at a time until it\n" +
            "reaches H1. For each square the piece sits on we loop\n" +
            "through all possible occupied 'status'es and compute what\n" +
            "squares that rook/Queen would attack.\n" +
            "    The status is a value [1..63] that represents a bitmap of\n" +
            "the middle six bits (B1 through G1).  Since A1 and H1 are attacked\n" +
            "whether there is a piece there or not we save space by ignoring those\n" +
            "squares/bits when we are calculating how the rank is occupied. A\n" +
            "status is converted to an occupied bitmap by shifting it left one.\n" +
            "    In the following the piece bitmap represents the rook/Queen's placement.\n" +
            "The occupied bitmap represents the how the rank is occupied with other pieces.\n" +
            "The attacks bitmap shows what squares the rook/Queen attacks given the\n" +
            "current configuration of pieces. A1 is on the left and H1 on the far right.\n" +
            "An empty square is denoted with a '-' and an occupied or\n" +
            "attacked square is represented with an 'X'\n\n";
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        for(int square = 0; square <= 7; square++){
            int pieceBitmap = 1 << square;
            for(int occupationCombination = 0; occupationCombination < 64; occupationCombination++){
                int occupiedBitmap = occupationCombination << 1;
                int attacks = base[square][occupationCombination];
                sb.append(Util.formatBaseAttacks(pieceBitmap, occupiedBitmap, attacks) + "\n");
            }
        }
        return sb.toString().trim();
    }
}
