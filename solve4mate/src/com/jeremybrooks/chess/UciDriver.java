package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.base.Square.named;
import static com.jeremybrooks.chess.base.Square.squareOf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Empty;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.PieceFactory;
import com.jeremybrooks.chess.base.Position;
import com.jeremybrooks.chess.search.SearchInfo;
import com.jeremybrooks.chess.search.SearchParams;
import com.jeremybrooks.chess.util.AbstractDisplayer;
import com.jeremybrooks.chess.util.Displayer;
import com.jeremybrooks.chess.util.FenBuilder;
import com.jeremybrooks.chess.util.Util;


public class UciDriver {
    //  position fen rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 moves e2e4 d7d5 g1f3
    //  position fen k7/7P/8/8/8/8/4p3/7K b - - 0 1 moves e2e1q h7h8q a8b7 
    private static final String START_FEN = 
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private InputStream in;
    private PrintStream out;
    private GameState gameState;
    private Solver engine;
    
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0) showUsage();
        
        UciDriver driver = new UciDriver(System.in, System.out, System.err);
        driver.start();
    }
    
    private static void showUsage() {
        System.out.println("java -jar engineJarFile");
        System.exit(0);
    }
    
    public UciDriver(InputStream in, PrintStream out, PrintStream err)
    {
        setIn(in);
        setOut(out);
        engine = new Solver(); //initialize engine (ie, Attacks instance)
        gameState = new GameState();
    }
    
    public void start() throws Exception
    {
       String input = "";
       BufferedReader br = new BufferedReader(new InputStreamReader(in));
       while(true)
       {
           input = br.readLine();
           execute(input);
       }
    }

    public void execute(String input) throws Exception 
    {
        try
        {
            String[] cmd = new String[0];
            cmd = input.split(" ");
            int argIndex = 0;
            String token = cmd[argIndex++];
            switch(token)
            {
            case "uci":
                respond("id name Breaker 0.1 by Jeremy Brooks (c) 2004-2014");
                respond("uciok");
                break;
            case "isready":
                respond("readyok");
                break;
            case "position":
                setPosition(cmd, argIndex);
                break;
            case "go":
                go(cmd);
                break;
            case "stop":
                respond("readyok??");
                break;
            case "ponderhit":
                //TODO: opponent made the move we were pondering on
                //      switch from pondering back to searching
                break;
            case "debug":
                //TODO: turn on/off engine's debug mode
                token = cmd[argIndex++];
                if("on".equals(token))
                {
                    //turn debugging on
                } else if ("off".equals(token)) {
                    //turn it off
                }
                break;
            case "d":
            case "diagram":
                out.println(toDiagram(gameState));
                break;
            case "quit":
                System.exit(0);
            default:
                break;
            }
        } 
        catch (Exception e) {
            // TODO Auto-generated catch block
            throw e;
            //e.printStackTrace();
        }
    }

    public static String toDiagram(GameState g) {
        if(g == null)
            throw new NullPointerException("cannot display null gamestate");
        AbstractDisplayer displayer = new Displayer();
        Position position = g.getPosition();
        return displayer.formatBoard(position);
    }

//    private static int parseTimeLimit(String token) {
//        boolean isSeconds = true;
//        int millisPerSecond = 1000;
//        String tmp = "";
//        if(token.endsWith("ms"))
//        {
//            isSeconds = false;
//            tmp = token.substring(0, token.length()-2);
//        } else {
//            tmp = token;
//        }
//        int limitMillis = readInt(tmp);
//        if(isSeconds) limitMillis *= millisPerSecond;
//        return limitMillis;
//    }

    public void setPosition(String[] cmd, int argIndex) {
        String token;
        token = cmd[argIndex++];
        String fen = "";
        if("startpos".equals(token))
        {
            fen = START_FEN;
        } else if ("fen".equals(token)) {
            //TODO: utilize the FenParser to do this work instead
            //      it would have to be updated to read a token at a time though
            int lastFenField = argIndex + 6;
            while(argIndex < lastFenField)
            {
                token = cmd[argIndex++];
                fen += token + FenBuilder.FIELD_DELIMITER;
            }
        } else {
            throw new IllegalArgumentException(token + " is not allowed; only fen or startpos are allowed.");
        }
        gameState = new GameState(); //can we just clear this instead of a brand new one?
        gameState.set(fen.trim());
        if(argIndex < cmd.length)
            token = cmd[argIndex++]; //read "moves" token
        boolean isWhitesMove = gameState.isWhiteToMove();
        while(argIndex < cmd.length)
        {
            token = cmd[argIndex++];
            int move = parseUciMove(gameState, token);
            gameState.makeMove(move, isWhitesMove);
            isWhitesMove = !isWhitesMove;
        }
    }

    public void go(String[] guiArgs) throws IOException {
        int[] remainingMillis = new int[]{5000, 5000}; //default: 5 seconds each
        int[] incrementMillis = new int[]{   0,    0}; //default: no increment
        int movesToGo = 1;  //default: allow engine use all the default time for this search
        int depth = 0;
        boolean findMate = false;
        
        int argIndex = 1;
        while(argIndex < guiArgs.length)
        {
            String token = guiArgs[argIndex++];
            
            switch(token)
            {
            case "wtime":
                token = guiArgs[argIndex++];
                remainingMillis[Bitmap.WHITE] = readInt(token);
                break;
            case "winc":
                token = guiArgs[argIndex++];
                incrementMillis[Bitmap.WHITE] = readInt(token);
                break;
            case "btime":
                token = guiArgs[argIndex++];
                remainingMillis[Bitmap.BLACK] = readInt(token);
                break;
            case "binc":
                token = guiArgs[argIndex++];
                incrementMillis[Bitmap.BLACK] = readInt(token);
                break;
            case "movestogo":
                token = guiArgs[argIndex++];
                movesToGo = readInt(token);
                break;
            case "depth":
                token = guiArgs[argIndex++];
                depth = readInt(token);
                break;
            case "nodes": 
                break;
            case "mate":
                token = guiArgs[argIndex++];
                depth = (2 * readInt(token)) - 1;
                findMate = true;
                break;
            case "movetime":
                //search exactly X milliseconds "movetime X"
                break;
            case "infinite":
                break;
            case "searchmoves":
                break;
            case "ponder":
                break;
            default:
                break;
            }
        }
        SearchParams params = new SearchParams();
        params.setTime(Bitmap.WHITE, remainingMillis[Bitmap.WHITE]);
        params.setIncrement(Bitmap.WHITE, incrementMillis[Bitmap.WHITE]);
        params.setTime(Bitmap.BLACK, remainingMillis[Bitmap.BLACK]);
        params.setIncrement(Bitmap.BLACK, incrementMillis[Bitmap.BLACK]);
        params.setMovesToGo(movesToGo);
        params.setDepth(depth);
        engine.setSearchParams(params);
        startSearch(depth);
    }

    public static int readInt(String token) {
        int n = Integer.parseInt(token);
        return n;
    }

    public void startSearch(int depth) throws IOException {
        SearchInfo info = engine.search(gameState, depth);
        int bestMove = info.getBestLine().get(0);
        String uciBestMove = toUciMove(bestMove);
        String uciPvMoves = toUciMoves(info.getBestLine());
        String fmt = "info depth %d score %s time %d nodes %d nps %.0f pv %s";
        sendResponse(fmt,
                depth,
                toUciScore(info),
                info.getElapsedTime(),
                info.getNodeCount(),
                info.getNodesPerSecond(),
                uciPvMoves);
        respond("info bestmove " + uciBestMove);
//        respond("info bestline " + info.getSolutionMoves());
    }

    private static String toUciMoves(List<Integer> bestLine) {
        StringBuilder uciMoves = new StringBuilder();
        for(int move: bestLine)
        {
            String uciMove = toUciMove(move);
            uciMoves.append(uciMove);
            uciMoves.append(" ");
        }
        return uciMoves.toString();
    }

    private static String toUciMove(int move) {
        int fromSquare = move & 0x3F;
        int toSquare = (move >> 6) & 0x3F;
        if(fromSquare==toSquare && fromSquare==0)
            return "<none>"; //or "0000" ???
        return named(fromSquare) + named(toSquare);
    }
    
    private static String toUciScore(SearchInfo info)
    {
        //constructs everything after "score "
//        
//      * score
//          * cp <x>
//              the score from the engine's point of view in centipawns.
//          * mate <y>
//            mate in y moves, not plies.
//              If the engine is getting mated use negative values for y.
//          * lowerbound
//              the score is just a lower bound.
//          * upperbound
//               the score is just an upper bound.

        StringBuilder desc = new StringBuilder();
        int centipawnScore = info.getScore();
        if(info.isMateOrMated())
        {
            int depth = info.getPliesInBestLine();
            boolean givingMate = centipawnScore > 0;
            if(givingMate != (depth %2 == 1))
                    throw new IllegalArgumentException("score and depth are out of sync (at odd depths we should be giving mate which means a positive score)");
            int mateInXMoves = toMateInXMoves(depth); 
            desc.append("mate ");
            desc.append(givingMate?"":"-");
            desc.append(mateInXMoves);
            return desc.toString();
        }
        
        if(info.isLowerBound())
            desc.append("lowerbound ");
        else if(info.isUpperBound())
            desc.append("upperbound ");
        
        desc.append("cp ").append(centipawnScore);
        return desc.toString();
    }

    private static int toMateInXMoves(int depth) {
        boolean givingMate = (depth % 2 == 1); //at odd depths
        if(givingMate)
            return (depth+1)/2;
        return depth/2;
    }


    private int parseUciMove(GameState gameState2, String uciMove) {
        /*        
         * The move format is in long algebraic notation.
         * A nullmove from the Engine to the GUI should be sent as 0000.
         * Examples:  e2e4, e7e5, e1g1 (white short castling), e7e8q (for promotion)
         */
        assert(uciMove.length()>=4);
        int fromSquare = squareOf(uciMove.substring(0, 2));
        int toSquare = squareOf(uciMove.substring(2, 4));
        Piece piece = gameState.getPosition().get(fromSquare);
        Piece captured = gameState.getPosition().get(toSquare);
        Piece promoter = new Empty();
        if(uciMove.length() == 5)
        {
            //Color doesn't matter because it's not encoded in the move
            char promotionChar = uciMove.substring(4,5).charAt(0);
            promoter = PieceFactory.create(promotionChar); //FenParser.getPieceFromBoardCharacter(promotionChar);
        }
        int move = encodeMove(fromSquare, toSquare, piece, captured, promoter);
        return move;
    }

    public static int encodeMove(int fromSquare, int toSquare, Piece piece,
            Piece captured, Piece promoter) {
        return Util.EncodeMove(fromSquare, toSquare, piece.encoded(), 
                captured.encoded(),    promoter.encoded());
    }

    public void sendResponse(String formatMessage, Object... args) throws IOException
    {
        String s = String.format(formatMessage, args);
        respond(s);
    }

    public void respond(String s) throws IOException {
        out.println(s);
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public PrintStream getOut() {
        return out;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }
}
