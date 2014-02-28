package com.jeremybrooks.chess;

public class TestShift {

    public static void main(String[] args) {
    
        int  i = 0x10;             // decimal 16        
        long l = 0x10L;            // decimal 16
        
        /** Decimal to Binary *************************************/
        System.out.println();
        System.out.println("Binary patterns:");
        System.out.println("----------------");
        System.out.println();
        System.out.println("\t Number  35 \t -> " + Integer.toBinaryString(35));
        System.out.println("\t Number -29 \t -> " +Integer.toBinaryString(-29));
        System.out.println("\t Number  31 \t -> " + Integer.toBinaryString(31));
        System.out.println("\t Number  16 \t -> " + Integer.toBinaryString(16));
        

        /** Shift Masking *****************************************/
        
        System.out.println();
        System.out.println("Integer shift masked by 31 (0x1f): ");
        System.out.println("-----------------------------------");
        System.out.println();
        System.out.println("\t Value of i: \t\t -> " + i);
        System.out.println();
        System.out.println("\t i <<  35 \t\t -> " + (i<<35) );
        System.out.println("\t i << -29 \t\t -> " + (i<<-29) );        
        System.out.println("\t i <<   3 \t\t -> " + (i<<3) );
        System.out.println("\t i >>  35 \t\t -> " + (i>>35) );
        System.out.println("\t i >> -29 \t\t -> " + (i>>-29) );        
        System.out.println("\t i >>   3 \t\t -> " + (i>>3) );
        

        System.out.println();
        System.out.println("Long shift masked by 63 (0x3D): ");
        System.out.println("--------------------------------");
        System.out.println();
        System.out.println("\t Value of l: \t\t -> " + i);
        System.out.println();
        System.out.println("\t l <<  67 \t\t -> " + (i<<67) );
        System.out.println("\t l << -61 \t\t -> " + (i<<-61) );        
        System.out.println("\t l <<   3 \t\t -> " + (i<<3) );
        System.out.println("\t l >>  67 \t\t -> " + (i>>67) );
        System.out.println("\t l >> -61 \t\t -> " + (i>>-61) );        
        System.out.println("\t l >>   3 \t\t -> " + (i>>3) );

        
        /** Left shift **********************************************/
        
        System.out.println();
        System.out.println("Left-shift <<");
        System.out.println("-------------");
        System.out.println();
        System.out.println("\t Value of i: \t\t -> " + i);        
        System.out.println();        
        System.out.println("\t  i <<  5 \t\t -> " + (i<< 5) );
        System.out.println("\t -i <<  5 \t\t -> " + (-i<<5) );
        System.out.println("\t  i * (2^5) \t\t -> " + ( i * (int)Math.pow(2,5)) );
        System.out.println();
 
        /** Right shift ***********************************************/
        System.out.println("Right-shift >> (signed)");
        System.out.println("-----------------------");
        System.out.println();
        System.out.println("\t Value of i: \t\t -> " + i);
        System.out.println();
        System.out.println("\t  i >> 2 \t\t -> " + ( i>>2 ));
        System.out.println("\t -i >> 2 \t\t -> " + (-i>>2));
        System.out.println("\t  i / (2^2) \t\t -> " + (i/(int)Math.pow(2,2)) );
        System.out.println("\t -i / (2^2) \t\t -> " + (-i/(int)Math.pow(2,2)) );    
        System.out.println();
        
        /** Unsigned right-shift ***************************************/
        System.out.println("Unsigned right-shift >>>");
        System.out.println("------------------------");
        System.out.println();
        System.out.println("\t Value of i: \t\t ->" + i);
        System.out.println();
        System.out.println("\t   i >>> 2 \t\t -> " + ( i>>>2));
        System.out.println("\t  -i >>> 2 \t\t -> " + (-i>>>2));
        System.out.println("\t   i >> 2 \t\t -> " + (i>>2));
        System.out.println("\t (-i >> 2) + (2 << ~2) \t -> " + 
                            ((-i>>2) + (2<< ~2)) ); 
    }
}