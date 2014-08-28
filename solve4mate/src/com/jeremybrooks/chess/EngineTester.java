package com.jeremybrooks.chess;

public class EngineTester {

    public static void main(String[] args) {
        // Read args "test [ seconds X | mate X | depth X ] file.epd
        //
        // where file.epd contains one epd per line and the
        //    dm X (direct mate in X moves) and
        //    bm (best move) opcodes (expected fromat is long algebraic, ie Ba1xb2, etc)
        //
        int index = 0;
        int max = 0;
        String limitName;
        String file;
        String cmd = args[index++];
        if("test".equals(cmd))
        {
            limitName = args[index++];
            switch(limitName)
            {
            case "seconds":
            case "mate":
            case "depth":
                max = Integer.parseInt(args[index++]); 
                break;
            default:
                throw new IllegalArgumentException("unknown argument: " + cmd);
            }
            file = args[index++];

        }
    }

}
