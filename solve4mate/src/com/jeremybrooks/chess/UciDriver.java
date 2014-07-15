package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.base.Square.named;
import static com.jeremybrooks.chess.base.Square.squareOf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.log4j.BasicConfigurator;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Empty;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.PieceFactory;
import com.jeremybrooks.chess.search.SearchInfo;
import com.jeremybrooks.chess.search.SearchParams;
import com.jeremybrooks.chess.util.FenBuilder;
import com.jeremybrooks.chess.util.Util;


public class UciDriver {
    //  position fen rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 moves e2e4 d7d5 g1f3
    //  position fen k7/7P/8/8/8/8/4p3/7K b - - 0 1 moves e2e1q h7h8q a8b7 
    private static final String START_FEN = 
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static final String EOL = System.getProperty("line.separator");
    private static BufferedReader br;
    private static BufferedWriter bw;
    
    private static GameState gameState;
    private static Solver engine;
    
    public static void main(String[] args) throws Exception 
    {
        try
        {
            init();
            //Read an optional uci "position" command from the command line
            if(args.length > 0) {
                int index = 0;
                setPosition(args, index);
            }
            while(true)
            {
                String[] cmd = new String[0];
                cmd = parseInput();
                int argIndex = 0;
                String token = cmd[argIndex++];
                if("uci".equals(token))
                {
                    respond("id name Breaker 0.1 by Jeremy Brooks (c) 2004-2014");
                    respond("uciok");
                } else if ("isready".equals(token)) {
                    respond("readyok");
                } else if ("position".equals(token)) {
                    setPosition(cmd, argIndex);
                } else if ("go".equals(token)) {
                    go(cmd);
                } else if ("stop".equals(token)) {
                    respond("readyok??");
                } else if ("ponderhit".equals(token)) {
                    //TODO: opponent made the move we were pondering on
                    //      switch from pondering back to searching
                } else if ("debug".equals(token)) {
                    //TODO: turn on/off engine's debug mode
                    token = cmd[argIndex++];
                    if("on".equals(token))
                    {
                        //turn debugging on
                    } else if ("off".equals(token)) {
                        //turn it off
                    }
                } else if ("d".equals(token) || "diagram".equals(token)) {
                    if(gameState != null)
                        gameState.display();
                } else if("quit".equals(token)) {
                    System.exit(0);
                }
            }
        } 
        catch (Exception e) {
            // TODO Auto-generated catch block
            throw e;
            //e.printStackTrace();
        }
    }

    public static void setPosition(String[] cmd, int argIndex) throws Exception {
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
            throw new Exception(token + " is not allowed; only fen or startpos are allowed.");
        }
        gameState = new GameState();
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

    private static void go(String[] guiArgs) {
        int[] remainingMillis = new int[2];
        int[] incrementMillis = new int[2];
        int depth = 0;
        boolean findMate = false;
        int movesToGo = 0;
        
        int argIndex = 1;
        while(argIndex < guiArgs.length)
        {
            String token = guiArgs[argIndex++];
            if("wtime".equals(token)) {
                token = guiArgs[argIndex++];
                remainingMillis[Bitmap.WHITE] = Integer.parseInt(token);
            } else if("winc".equals(token)) {
                token = guiArgs[argIndex++];
                incrementMillis[Bitmap.WHITE] = Integer.parseInt(token);
            } else if("btime".equals(token)) {
                token = guiArgs[argIndex++];
                remainingMillis[Bitmap.BLACK] = Integer.parseInt(token);
            } else if("binc".equals(token)) {
                token = guiArgs[argIndex++];
                incrementMillis[Bitmap.BLACK] = Integer.parseInt(token);
            } else if("movestogo".equals(token)) {
                token = guiArgs[argIndex++];
                movesToGo = Integer.parseInt(token);
            } else if("depth".equals(token)) {
                token = guiArgs[argIndex++];
                depth = Integer.parseInt(token);
            } else if("nodes".equals(token)) {
                
            } else if("mate".equals(token)) {
                findMate = true;
            } else if("movetime".equals(token)) {
                
            } else if("infinite".equals(token)) {
                
            } else if("searchmoves".equals(token)) {
                
            } else if("ponder".equals(token)) {
                
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
        
        if(findMate)
        {
            SearchInfo info = engine.search(gameState, depth);
            String fmt = "info depth %d time %d nodes %d nps %.0f";
            sendResponse(fmt,
                    depth,
                    info.getElapsedTime(),
                    info.getNodeCount(),
                    info.getNodesPerSecond());
            int bestMove = info.getBestLine()[0].getMove();
            String uciBestMove = formatUciMove(bestMove);
            sendResponse("info bestmove " + uciBestMove);
            sendResponse("info bestline " + info.getSolutionMoves());
        }
    }

    private static String formatUciMove(int move) {
        int fromSquare = move & 0x3F;
        int toSquare = (move >> 6) & 0x3F;
        return named(fromSquare) + named(toSquare);
    }

    private static int parseUciMove(GameState gameState2, String uciMove) {
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

    public static String[] parseInput() throws IOException {
        return br.readLine().split(" ");
    }

    public static void init() {
        BasicConfigurator.configure(); //setup logger
        br = new BufferedReader(new InputStreamReader(System.in));
        bw = new BufferedWriter(new OutputStreamWriter(System.out));
        engine = new Solver(); //initialize engine (ie, Attacks instance)
    }

    public static void respond(String response) throws IOException {
        bw.write(response + EOL);
        bw.flush();
    }
    
    public static void sendResponse(String formatMessage, Object... args)
    {
        String s = String.format(formatMessage, args);
        System.out.println(s);
    }

}
