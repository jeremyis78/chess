/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess.movegen;

import com.jeremybrooks.chess.base.*;
import com.jeremybrooks.chess.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.jeremybrooks.chess.base.Bitmap.*;

/**
 * @author jeremy
 *
 */
public class Attacks {
    //public static final long[][] R45 = ;
    private static final Logger log = LogManager.getLogger();
    public static final Attacks INSTANCE = new Attacks(); //default scope for testing
    private static Magic[] rookMagics   = new Magic[64];
    private static Magic[] bishopMagics = new Magic[64];;
    private static long[][] rookMovesDatabase;   //indexed by square and magic occupiedIndex
    private static long[][] bishopMovesDatabase; //indexed by square and magic occupiedIndex

    private static final int FILE1 = 0;
    private static final int FILE8 = 7;
    private static final int RANK8 = 7;
    
    static {
    	//rook Magics initialization                                                    Bits/Used	NumTrialsToFindMagicNumber
    	rookMagics[ 0] = new Magic(0x0180001020844000L, 52, 0x000101010101017eL);	// 	12/12    	66284
    	rookMagics[ 1] = new Magic(0x08c0042000409001L, 53, 0x000202020202027cL);	// 	11/11    	3077
    	rookMagics[ 2] = new Magic(0x0300200041004810L, 53, 0x000404040404047aL);	// 	11/11    	38965
    	rookMagics[ 3] = new Magic(0x0a00120048042040L, 53, 0x0008080808080876L);	// 	11/11    	26717
    	rookMagics[ 4] = new Magic(0x0200220008041020L, 53, 0x001010101010106eL);	// 	11/11    	89295
    	rookMagics[ 5] = new Magic(0x0900010002180400L, 53, 0x002020202020205eL);	// 	11/11    	43440
    	rookMagics[ 6] = new Magic(0x4080490000801200L, 53, 0x004040404040403eL);	// 	11/11    	40707
    	rookMagics[ 7] = new Magic(0x010000c420810002L, 52, 0x008080808080807eL);	// 	12/12    	202904
    	rookMagics[ 8] = new Magic(0x2801800020400091L, 53, 0x0001010101017e00L);	// 	11/11    	13207
    	rookMagics[ 9] = new Magic(0x0400400850002000L, 54, 0x0002020202027c00L);	// 	10/10    	13000
    	rookMagics[10] = new Magic(0x0001004013002000L, 54, 0x0004040404047a00L);	// 	10/10    	121098
    	rookMagics[11] = new Magic(0x0000801000080084L, 54, 0x0008080808087600L);	// 	10/10    	2028
    	rookMagics[12] = new Magic(0x8010808048000400L, 54, 0x0010101010106e00L);	// 	10/10    	24138
    	rookMagics[13] = new Magic(0x2004012048100c00L, 54, 0x0020202020205e00L);	// 	10/10    	12066
    	rookMagics[14] = new Magic(0x014c008810020d04L, 54, 0x0040404040403e00L);	// 	10/10    	5283
    	rookMagics[15] = new Magic(0x10088000800d4100L, 53, 0x0080808080807e00L);	// 	11/11    	3969
    	rookMagics[16] = new Magic(0x0004288004400082L, 53, 0x00010101017e0100L);	// 	11/11    	11980
    	rookMagics[17] = new Magic(0x001008404000a000L, 54, 0x00020202027c0200L);	// 	10/10    	38957
    	rookMagics[18] = new Magic(0x0620010044a10090L, 54, 0x00040404047a0400L);	// 	10/10    	51460
    	rookMagics[19] = new Magic(0x2001818008001000L, 54, 0x0008080808760800L);	// 	10/10    	3782
    	rookMagics[20] = new Magic(0x2208014004020040L, 54, 0x00101010106e1000L);	// 	10/10    	92493
    	rookMagics[21] = new Magic(0x2200808004002200L, 54, 0x00202020205e2000L);	// 	10/10    	124933
    	rookMagics[22] = new Magic(0x0000140048020910L, 54, 0x00404040403e4000L);	// 	10/10    	254
    	rookMagics[23] = new Magic(0x48002e0008804504L, 53, 0x00808080807e8000L);	// 	11/11    	36605
    	rookMagics[24] = new Magic(0x040a800080204000L, 53, 0x000101017e010100L);	// 	11/11    	8617
    	rookMagics[25] = new Magic(0x3400824200230a00L, 54, 0x000202027c020200L);	// 	10/10    	17625
    	rookMagics[26] = new Magic(0x0100410100142000L, 54, 0x000404047a040400L);	// 	10/10    	40099
    	rookMagics[27] = new Magic(0x0408420200201008L, 54, 0x0008080876080800L);	// 	10/10    	63982
    	rookMagics[28] = new Magic(0x8008000900050050L, 54, 0x001010106e101000L);	// 	10/10    	80623
    	rookMagics[29] = new Magic(0x000a000200283005L, 54, 0x002020205e202000L);	// 	10/10    	23912
    	rookMagics[30] = new Magic(0x8801006100020014L, 54, 0x004040403e404000L);	// 	10/10    	19344
    	rookMagics[31] = new Magic(0x8000004a000c0081L, 53, 0x008080807e808000L);	// 	11/11    	11129
    	rookMagics[32] = new Magic(0x0000400080800020L, 53, 0x0001017e01010100L);	// 	11/11    	6633
    	rookMagics[33] = new Magic(0x8008802001804000L, 54, 0x0002027c02020200L);	// 	10/10    	2844
    	rookMagics[34] = new Magic(0x0150200481801000L, 54, 0x0004047a04040400L);	// 	10/10    	6619
    	rookMagics[35] = new Magic(0x0001180083801000L, 54, 0x0008087608080800L);	// 	10/10    	9581
    	rookMagics[36] = new Magic(0x0000280101001014L, 54, 0x0010106e10101000L);	// 	10/10    	22069
    	rookMagics[37] = new Magic(0x4044000600800480L, 54, 0x0020205e20202000L);	// 	10/10    	21750
    	rookMagics[38] = new Magic(0x01501e1044000805L, 54, 0x0040403e40404000L);	// 	10/10    	7528
    	rookMagics[39] = new Magic(0x01008008c8800100L, 53, 0x0080807e80808000L);	// 	11/11    	7459
    	rookMagics[40] = new Magic(0x0000800041010020L, 53, 0x00017e0101010100L);	// 	11/11    	4736
    	rookMagics[41] = new Magic(0x4044200150004003L, 54, 0x00027c0202020200L);	// 	10/10    	46124
    	rookMagics[42] = new Magic(0x4100a00041010010L, 54, 0x00047a0404040400L);	// 	10/10    	16750
    	rookMagics[43] = new Magic(0x0004a01005010008L, 54, 0x0008760808080800L);	// 	10/10    	12444
    	rookMagics[44] = new Magic(0x0100080004008080L, 54, 0x00106e1010101000L);	// 	10/10    	15145
    	rookMagics[45] = new Magic(0x0106008004008100L, 54, 0x00205e2020202000L);	// 	10/10    	25484
    	rookMagics[46] = new Magic(0x0050080110040012L, 54, 0x00403e4040404000L);	// 	10/10    	18379
    	rookMagics[47] = new Magic(0x8825008404c20003L, 53, 0x00807e8080808000L);	// 	11/11    	61637
    	rookMagics[48] = new Magic(0x1040800040006080L, 53, 0x007e010101010100L);	// 	11/11    	5058
    	rookMagics[49] = new Magic(0x04220b0080a04200L, 54, 0x007c020202020200L);	// 	10/10    	6834
    	rookMagics[50] = new Magic(0x4008a00440110300L, 54, 0x007a040404040400L);	// 	10/10    	6914
    	rookMagics[51] = new Magic(0x000040200a001200L, 54, 0x0076080808080800L);	// 	10/10    	217
    	rookMagics[52] = new Magic(0x4002800401580080L, 54, 0x006e101010101000L);	// 	10/10    	52084
    	rookMagics[53] = new Magic(0x4002001508900a00L, 54, 0x005e202020202000L);	// 	10/10    	1670
    	rookMagics[54] = new Magic(0x0000800900020080L, 54, 0x003e404040404000L);	// 	10/10    	1574
    	rookMagics[55] = new Magic(0x5000018400412a00L, 53, 0x007e808080808000L);	// 	11/11    	6349
    	rookMagics[56] = new Magic(0x0202004027001082L, 52, 0x7e01010101010100L);	// 	12/12    	21230
    	rookMagics[57] = new Magic(0x00042080c0010011L, 53, 0x7c02020202020200L);	// 	11/11    	33680
    	rookMagics[58] = new Magic(0xd00200088034c022L, 53, 0x7a04040404040400L);	// 	11/11    	51509
    	rookMagics[59] = new Magic(0x0111012824100021L, 53, 0x7608080808080800L);	// 	11/11    	2240
    	rookMagics[60] = new Magic(0x0019000800d00225L, 53, 0x6e10101010101000L);	// 	11/11    	1609
    	rookMagics[61] = new Magic(0x00090004000208d1L, 53, 0x5e20202020202000L);	// 	11/11    	232859
    	rookMagics[62] = new Magic(0x0450101902028804L, 53, 0x3e40404040404000L);	// 	11/11    	18858
    	rookMagics[63] = new Magic(0x0001104084002112L, 52, 0x7e80808080808000L);	// 	12/12    	25612

    	//bishop Magics initialization                                                  Bits/Used	NumTrialsToFindMagicNumber
    	bishopMagics[ 0] = new Magic(0x0008a00410920014L, 58, 0x0040201008040200L);	// 	6/6      	9517
    	bishopMagics[ 1] = new Magic(0x080410208d030000L, 59, 0x0000402010080400L);	// 	5/5      	4913
    	bishopMagics[ 2] = new Magic(0x0008022246010404L, 59, 0x0000004020100a00L);	// 	5/5      	862
    	bishopMagics[ 3] = new Magic(0xa204052600020208L, 59, 0x0000000040221400L);	// 	5/5      	1232
    	bishopMagics[ 4] = new Magic(0x500414202821d804L, 59, 0x0000000002442800L);	// 	5/5      	799
    	bishopMagics[ 5] = new Magic(0x020208840420045aL, 59, 0x0000000204085000L);	// 	5/5      	2100
    	bishopMagics[ 6] = new Magic(0x002c88011010a008L, 59, 0x0000020408102000L);	// 	5/5      	1780
    	bishopMagics[ 7] = new Magic(0x0800240108011000L, 58, 0x0002040810204000L);	// 	6/6      	1003
    	bishopMagics[ 8] = new Magic(0x0082082004140048L, 59, 0x0020100804020000L);	// 	5/5      	593
    	bishopMagics[ 9] = new Magic(0x8100200144128088L, 59, 0x0040201008040000L);	// 	5/5      	385
    	bishopMagics[10] = new Magic(0xc024440800810000L, 59, 0x00004020100a0000L);	// 	5/5      	378
    	bishopMagics[11] = new Magic(0x0106311042000810L, 59, 0x0000004022140000L);	// 	5/5      	510
    	bishopMagics[12] = new Magic(0x0001041c60000110L, 59, 0x0000000244280000L);	// 	5/5      	891
    	bishopMagics[13] = new Magic(0x4402030120101514L, 59, 0x0000020408500000L);	// 	5/5      	859
    	bishopMagics[14] = new Magic(0x0020410090052000L, 59, 0x0002040810200000L);	// 	5/5      	31
    	bishopMagics[15] = new Magic(0x0400004044042000L, 59, 0x0004081020400000L);	// 	5/5      	769
    	bishopMagics[16] = new Magic(0x0010822002900100L, 59, 0x0010080402000200L);	// 	5/5      	973
    	bishopMagics[17] = new Magic(0x0208841012008c04L, 59, 0x0020100804000400L);	// 	5/5      	2169
    	bishopMagics[18] = new Magic(0x4402180404040109L, 57, 0x004020100a000a00L);	// 	7/7      	9250
    	bishopMagics[19] = new Magic(0x1044000804105060L, 57, 0x0000402214001400L);	// 	7/7      	4070
    	bishopMagics[20] = new Magic(0x1024004080a00800L, 57, 0x0000024428002800L);	// 	7/7      	4732
    	bishopMagics[21] = new Magic(0x0002000101008244L, 57, 0x0002040850005000L);	// 	7/7      	2880
    	bishopMagics[22] = new Magic(0x08110a1088084200L, 59, 0x0004081020002000L);	// 	5/5      	613
    	bishopMagics[23] = new Magic(0x0821000024020200L, 59, 0x0008102040004000L);	// 	5/5      	1226
    	bishopMagics[24] = new Magic(0x8120150960140400L, 59, 0x0008040200020400L);	// 	5/5      	913
    	bishopMagics[25] = new Magic(0x0008204062044100L, 59, 0x0010080400040800L);	// 	5/5      	584
    	bishopMagics[26] = new Magic(0x51d2010448004400L, 57, 0x0020100a000a1000L);	// 	7/7      	6986
    	bishopMagics[27] = new Magic(0x0008080000820062L, 55, 0x0040221400142200L);	// 	9/9      	28421
    	bishopMagics[28] = new Magic(0x0010940000806000L, 55, 0x0002442800284400L);	// 	9/9      	22176
    	bishopMagics[29] = new Magic(0x00080a0010608400L, 57, 0x0004085000500800L);	// 	7/7      	6196
    	bishopMagics[30] = new Magic(0x0042008882641018L, 59, 0x0008102000201000L);	// 	5/5      	2688
    	bishopMagics[31] = new Magic(0x000c09010041422eL, 59, 0x0010204000402000L);	// 	5/5      	707
    	bishopMagics[32] = new Magic(0x00082a10023a2000L, 59, 0x0004020002040800L);	// 	5/5      	239
    	bishopMagics[33] = new Magic(0x8020a61000089040L, 59, 0x0008040004081000L);	// 	5/5      	190
    	bishopMagics[34] = new Magic(0x0544840500100042L, 57, 0x00100a000a102000L);	// 	7/7      	1060
    	bishopMagics[35] = new Magic(0x0040080801920a00L, 55, 0x0022140014224000L);	// 	9/9      	25838
    	bishopMagics[36] = new Magic(0x0060028401088020L, 55, 0x0044280028440200L);	// 	9/9      	26174
    	bishopMagics[37] = new Magic(0x0206140640080800L, 57, 0x0008500050080400L);	// 	7/7      	2285
    	bishopMagics[38] = new Magic(0x0104010050020840L, 59, 0x0010200020100800L);	// 	5/5      	2322
    	bishopMagics[39] = new Magic(0x000c008420408401L, 59, 0x0020400040201000L);	// 	5/5      	1916
    	bishopMagics[40] = new Magic(0x0506082054004a52L, 59, 0x0002000204081000L);	// 	5/5      	4593
    	bishopMagics[41] = new Magic(0x0000808818042002L, 59, 0x0004000408102000L);	// 	5/5      	3877
    	bishopMagics[42] = new Magic(0x4001040202000100L, 57, 0x000a000a10204000L);	// 	7/7      	2832
    	bishopMagics[43] = new Magic(0x000a020102400401L, 57, 0x0014001422400000L);	// 	7/7      	1565
    	bishopMagics[44] = new Magic(0x0020010a12001400L, 57, 0x0028002844020000L);	// 	7/7      	502
    	bishopMagics[45] = new Magic(0x8050200808411020L, 57, 0x0050005008040200L);	// 	7/7      	4881
    	bishopMagics[46] = new Magic(0x000424680a000852L, 59, 0x0020002010080400L);	// 	5/5      	227
    	bishopMagics[47] = new Magic(0xa008080280200080L, 59, 0x0040004020100800L);	// 	5/5      	393
    	bishopMagics[48] = new Magic(0x0822222220040002L, 59, 0x0000020408102000L);	// 	5/5      	594
    	bishopMagics[49] = new Magic(0x00408a0801042000L, 59, 0x0000040810204000L);	// 	5/5      	2464
    	bishopMagics[50] = new Magic(0x088400c208440200L, 59, 0x00000a1020400000L);	// 	5/5      	216
    	bishopMagics[51] = new Magic(0x000010c084040200L, 59, 0x0000142240000000L);	// 	5/5      	159
    	bishopMagics[52] = new Magic(0x0000482102048008L, 59, 0x0000284402000000L);	// 	5/5      	61
    	bishopMagics[53] = new Magic(0x2030400408008008L, 59, 0x0000500804020000L);	// 	5/5      	395
    	bishopMagics[54] = new Magic(0x0010200811004040L, 59, 0x0000201008040200L);	// 	5/5      	2027
    	bishopMagics[55] = new Magic(0x11a0042444802000L, 59, 0x0000402010080400L);	// 	5/5      	910
    	bishopMagics[56] = new Magic(0x0209010886200200L, 58, 0x0002040810204000L);	// 	6/6      	2144
    	bishopMagics[57] = new Magic(0x008c002198280800L, 59, 0x0004081020400000L);	// 	5/5      	1543
    	bishopMagics[58] = new Magic(0x2400800824120822L, 59, 0x000a102040000000L);	// 	5/5      	215
    	bishopMagics[59] = new Magic(0x8091900420840400L, 59, 0x0014224000000000L);	// 	5/5      	688
    	bishopMagics[60] = new Magic(0x0084140210020200L, 59, 0x0028440200000000L);	// 	5/5      	547
    	bishopMagics[61] = new Magic(0x0800304030020092L, 59, 0x0050080402000000L);	// 	5/5      	454
    	bishopMagics[62] = new Magic(0x1080040404044400L, 59, 0x0020100804020000L);	// 	5/5      	968
    	bishopMagics[63] = new Magic(0xa1200a5000410040L, 58, 0x0040201008040200L);	// 	6/6      	362
    	
    	RookMagics rm = new RookMagics();
    	rookMovesDatabase = rm.generateMoves(rookMagics);
    	BishopMagics bm = new BishopMagics();
    	bishopMovesDatabase = bm.generateMoves(bishopMagics);
    }

    public short[][] base = new short[8][64]; //datatype needs to be 8bits, using short instead to avoid some problem

    //Masks have a one bit set corresponding to the square on the board.
    //The array index is the square number on the board, an element of {0,1,...,63}
    long mask[] = new long[64];
    public long[] mask90 = new long[64];
    public long[] mask45R = new long[64];
    public long[] mask45L = new long[64];
    public long[] king = new long[64];
    public long[] knight = new long[64];

    //The first index is the square number and
    //the second is the status of the ray the piece is aligned on
    public long[][] rank = new long[64][64];
    public long[][] file = new long[64][64];
    public long[][] R45 = new long[64][64];
    public long[][] L45 = new long[64][64];
    long pawn[][] = new long[2][64];
    public long[] whitepawn = new long[64];
    public long[] blackpawn = new long[64];
    

    public long[] plus1 = new long[64];
    public long[] plus7 = new long[64];
    public long[] plus8 = new long[64];
    public long[] plus9 = new long[64];
    public long[] minus1 = new long[64];
    public long[] minus7 = new long[64];
    public long[] minus8 = new long[64];
    public long[] minus9 = new long[64];

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
                pseudoAttacks |= bishopAttacks(onSquare, position.getOccupied());
            }
            if (slider.slidesLaterally())
            {
                pseudoAttacks |= rookAttacks(onSquare, position.getOccupied());
            }
        }
        return pseudoAttacks;
    }

    private static long bishopAttacks(int fromSquare, long occupied)
    {
    	Magic magic = bishopMagics[fromSquare];
    	long relevantOccupancy = occupied & magic.occupiedMask;
    	int occupiedIndex = (int)((relevantOccupancy * magic.number) >>> magic.shift);
    	return bishopMovesDatabase[fromSquare][occupiedIndex];
    }

//    private static long bishopAttacks(int bishopSquare, long allPieces45Left, long allPieces45Right)
//    {
//        long attacks;
//        int stat1, stat2;
//
//        stat1 = status45L (allPieces45Left, bishopSquare);
//        stat2 = status45R (allPieces45Right, bishopSquare);
//        attacks = INSTANCE.L45[bishopSquare][stat1];
//        attacks |= INSTANCE.R45[bishopSquare][stat2];
//        return attacks;
//    }

    private static long rookAttacks(int fromSquare, long occupied)
    {
    	Magic magic = rookMagics[fromSquare];
    	long relevantOccupancy = occupied & magic.occupiedMask;
    	int occupiedIndex = (int)((relevantOccupancy * magic.number) >>> magic.shift);
    	return rookMovesDatabase[fromSquare][occupiedIndex];
    }

//    private static long rookAttacks(int rookSquare, long allPiecesByRank, long allPiecesByFile)
//    {
//        long attacks;
//        int stat1, stat2;
//
//        stat1 = status (allPiecesByRank, rookSquare);
//        stat2 = status90 (allPiecesByFile, rookSquare);
//        attacks = INSTANCE.rank[rookSquare][stat1];
//        attacks |= INSTANCE.file[rookSquare][stat2];
//        return attacks;
//    }
 
    public static long xrayAttackers(int toSquare, long fromCandidates, long blockers, long occupied)
    {
    	long attacksFromSquare = xrayRookAttacks(toSquare, occupied, blockers)
    	                       | xrayBishopAttacks(toSquare, occupied, blockers);
    	long xrayAttacks = attacksFromSquare & fromCandidates;
		return xrayAttacks;
    }
    
    public static long xrayRookAttacks(int rookSq, long occupied, long blockers) {
    	long attacks = rookAttacks(rookSq, occupied);
    	blockers &= attacks;
    	return attacks ^ rookAttacks(rookSq, occupied ^ blockers);
    }

    public static long xrayBishopAttacks(int bishopSq, long occupied, long blockers) {
    	long attacks = bishopAttacks(bishopSq, occupied);
    	blockers &= attacks;
    	return attacks ^ bishopAttacks(bishopSq, occupied ^ blockers);
    }

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

//                 rankFileAtt = INSTANCE.rank[squareUnderAttack][status (position.getOccupied(0), squareUnderAttack)] |
//                     INSTANCE.file[squareUnderAttack][status90 (position.getOccupied(90), squareUnderAttack)];
                 rankFileAtt = rookAttacks(squareUnderAttack, position.getOccupied());
                 rooksQueens = position.getOpponentRooks(sideUnderAttack) | position.getOpponentQueens(sideUnderAttack);
                 attackers |= rankFileAtt & rooksQueens;

//                 diagAtt = INSTANCE.L45[squareUnderAttack][status45L (position.getOccupied(-45), squareUnderAttack)] |
//                     INSTANCE.R45[squareUnderAttack][status45R (position.getOccupied(45), squareUnderAttack)];
                 diagAtt = bishopAttacks(squareUnderAttack, position.getOccupied());
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

//                 rankFileAtt = INSTANCE.rank[squareUnderAttack][status (position.getOccupied(0), squareUnderAttack)] | 
//                     INSTANCE.file[squareUnderAttack][status90 (position.getOccupied(90), squareUnderAttack)];
                 rankFileAtt = rookAttacks(squareUnderAttack, position.getOccupied());
                 rooksQueens = position.getOpponentRooks(sideUnderAttack) | position.getOpponentQueens(sideUnderAttack);
                 attackers |= rankFileAtt & rooksQueens;

//                 diagAtt = INSTANCE.L45[squareUnderAttack][status45L (position.getOccupied(-45), squareUnderAttack)] |
//                     INSTANCE.R45[squareUnderAttack][status45R (position.getOccupied(45), squareUnderAttack)];
                 diagAtt = bishopAttacks(squareUnderAttack, position.getOccupied());
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
    	OutputBuilder ob = new OutputBuilder();
    	ob.append("    The following attacks are generated by a rook given all");
    	ob.append("possible ways that a rank is occupied by other pieces.");
    	ob.append("We do this by starting a piece (rook or Queen) on A1 and");
    	ob.append("moving it along the first rank one square at a time until it");
    	ob.append("reaches H1. For each square the piece sits on we loop");
    	ob.append("through all possible occupied 'status'es and compute what");
    	ob.append("squares that rook/Queen would attack.");
    	ob.append("    The status is a value [1..63] that represents a bitmap of");
    	ob.append("the middle six bits (B1 through G1).  Since A1 and H1 are attacked");
    	ob.append("whether there is a piece there or not we save space by ignoring those");
    	ob.append("squares/bits when we are calculating how the rank is occupied. A");
    	ob.append("status is converted to an occupied bitmap by shifting it left one.");
    	ob.append("    In the following the piece bitmap represents the rook/Queen's placement.");
    	ob.append("The occupied bitmap represents the how the rank is occupied with other pieces.");
    	ob.append("The attacks bitmap shows what squares the rook/Queen attacks given the");
    	ob.append("current configuration of pieces. A1 is on the left and H1 on the far right.");
    	ob.append("An empty square is denoted with a '-' and an occupied or");
    	ob.append("attacked square is represented with an 'X'");
    	ob.append("");
        for(int square = 0; square <= 7; square++){
            int pieceBitmap = 1 << square;
            for(int occupationCombination = 0; occupationCombination < 64; occupationCombination++){
                int occupiedBitmap = occupationCombination << 1;
                int attacks = base[square][occupationCombination];
                ob.append(Util.formatBaseAttacks(pieceBitmap, occupiedBitmap, attacks));
            }
        }
        return ob.toString().trim();
    }
}
