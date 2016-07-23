package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
import static com.jeremybrooks.chess.util.Util.bool;

public class PositionInfo {
    
    private static final int FOUR_BIT_MASK              = 0x00F;
    private static final int SEVEN_BIT_MASK             = 0x07F;
    private static final int NINE_BIT_MASK              = 0x1FF;
    private static final char WHITE_SHORT_CASTLE_OPTION = 'K';
    private static final char WHITE_LONG_CASTLE_OPTION  = 'Q';
    private static final char BLACK_SHORT_CASTLE_OPTION = 'k';
    private static final char BLACK_LONG_CASTLE_OPTION  = 'q';
    private static final char NO_CASTLING = '-';

    private int infoBits;

    public PositionInfo()
    {
        setMoveNumber(1);
        setReversiblePlies(0);
        removeCastleOptions();
        setEnPassantSquare(NOSQUARE);
    }
    
    public int getMoveNumber() {
        return infoBits & 0x1FF;  //first 9 bits
    }
    
    public void setMoveNumber(int moveNumber) {
        if(moveNumber < 1|| moveNumber > 511)
            throw new IllegalArgumentException("moveNumber must be between 1 and 511");
        int clearedMoveNumberBits = infoBits & ~NINE_BIT_MASK;
        this.infoBits = clearedMoveNumberBits | moveNumber; //clear and reset first 9 bits
    }
    
    public int getReversiblePlies() {
        return (infoBits >> 9) & NINE_BIT_MASK; //next 9 bits
    }
    
    public void setReversiblePlies(int reversiblePlies) {
        if(reversiblePlies < 0 || reversiblePlies > 511)
            throw new IllegalArgumentException("reversiblePlies must be between 0 and 511");
        int clearedExistingBits = infoBits & ~(NINE_BIT_MASK << 9);
        this.infoBits = clearedExistingBits | (reversiblePlies << 9);
    }
    
    public boolean hasShortCastleOption(int side) { 
        int rightShiftedBy = side==Piece.WHITE?19:21; //19th or 21st bit
        int bitmask = 0x1;
        int rightShiftedValue = infoBits >> rightShiftedBy;
        return 1==(rightShiftedValue & bitmask);
    }
    
    public void removeShortCastleOption(int side) {
        int leftShiftedBy = side==Piece.WHITE?19:21;
        int bitmask = 1 << leftShiftedBy;
        infoBits &= ~(bitmask);
    }

    public boolean hasLongCastleOption(int side) { 
        int rightShiftedBy = side==Piece.WHITE?20:22; //20th or 22nd bit
        int bitmask = 0x1;
        int rightShiftedValue = infoBits >> rightShiftedBy;
        return 1==(rightShiftedValue & bitmask);
    }
    
    public void removeLongCastleOption(int side) {
        int leftShiftedBy = side==Piece.WHITE?20:22;
        int bitmask = 1 << leftShiftedBy;
        infoBits &= ~(bitmask);
    }

    public void removeCastleOptions() {
        int leftShiftedBy = 19;
        int bitmask = FOUR_BIT_MASK << leftShiftedBy;
        infoBits &= ~(bitmask);
    }

    public void addCastleOptions(int options) {
        int leftShiftedBy = 19;
        int bitmask = (options & FOUR_BIT_MASK) << leftShiftedBy;
        infoBits |= bitmask;
    }
    
    public void setCastleOptions(int options)
    {
        removeCastleOptions();
        addCastleOptions(options);
    }

    public int getCastleOptions() {
        int rightShiftedValue = infoBits >> 19;
        return (rightShiftedValue & FOUR_BIT_MASK);
    }
    
    public void setCastleOptionsFromFen(String castleFen) {
        if(castleFen == null) 
            throw new NullPointerException();
        
        if(castleFen.length() > 4)
            throw new IllegalArgumentException("'"+castleFen+"' can't be over 4 characters");

        int options = 0;
        for(char c: castleFen.toCharArray())
        {
            switch(c)
            {
            case WHITE_SHORT_CASTLE_OPTION: options |= 1; break;
            case WHITE_LONG_CASTLE_OPTION:  options |= 2; break;
            case BLACK_SHORT_CASTLE_OPTION: options |= 4; break;
            case BLACK_LONG_CASTLE_OPTION:  options |= 8; break;
            case NO_CASTLING:               options  = 0; break;
            default:
                throw new IllegalArgumentException(castleFen + " is an invalid castle options string");
            }
        }
        setCastleOptions(options);
    }

    public String getCastleOptionsAsFen() {
        String optionsAsFen = "";
        int castlingOptions = getCastleOptions();
        if(bool(castlingOptions & 1)) optionsAsFen += WHITE_SHORT_CASTLE_OPTION;
        if(bool(castlingOptions & 2)) optionsAsFen += WHITE_LONG_CASTLE_OPTION;
        if(bool(castlingOptions & 4)) optionsAsFen += BLACK_SHORT_CASTLE_OPTION;
        if(bool(castlingOptions & 8)) optionsAsFen += BLACK_LONG_CASTLE_OPTION;
        if(optionsAsFen.isEmpty())    optionsAsFen += NO_CASTLING;
        return optionsAsFen;
    }


    public int getEnPassantSquare() {
        return (infoBits >> 23) & SEVEN_BIT_MASK; //23rd to 29th bit (7 bits)
    }
    
    public void setEnPassantSquare(int enPassantSquare) {
        int clearedExistingBits = infoBits & ~(SEVEN_BIT_MASK << 23);
        this.infoBits = clearedExistingBits | (enPassantSquare << 23);
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(' ');
        sb.append(getCastleOptionsAsFen());
        sb.append(' ');
        sb.append(Square.named(getEnPassantSquare()));
        sb.append(' ');
        sb.append(getReversiblePlies());
        sb.append(' ');
        sb.append(getMoveNumber());
        return sb.toString();
    }

}
