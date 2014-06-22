package com.jeremybrooks.chess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class UciDriver {
	//  position fen rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 moves e2e4 d7d5 g1f3
	//  position fen k7/7P/8/8/8/8/4p3/7K b - - 0 1 moves e2e1q h7h8q a8b7 
	private static final String START_FEN = 
			"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	private static final String EOL = System.getProperty("line.separator");
	private static BufferedReader br;
	private static BufferedWriter bw;
	
	private static GameState gameState;
	
	public static void main(String[] args) throws Exception 
	{
		try
		{
			initIO();
			while(true)
			{
				String guiLine = readGuiCommand();
				String guiArgs[] = guiLine.split(" ");
				int argIndex = 0;
				String token = guiArgs[argIndex++];
				//respond(command);
				if("uci".equals(token))
				{
					respond("id name Breaker 0.1 by Jeremy Brooks (c) 2005-2014");
					//respond("print my engine options for the gui here")
					respond("uciok");
				} else if ("isready".equals(token)) {
					//TODO: get ready
						respond("readyok");
				} else if ("position".equals(token)) {
					//TODO: setup position
					token = guiArgs[argIndex++];
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
							token = guiArgs[argIndex++];
							fen += token + FenBuilder.FIELD_DELIMITER;
						}
					} else {
						throw new Exception(token + " is not allowed; only fen or startpos are allowed.");
					}
					gameState = new GameState();
					gameState.set(fen.trim());
					gameState.display();
					token = guiArgs[argIndex++];
					if(!"moves".equals(token))
						continue;
					boolean isWhitesMove = gameState.isWhiteToMove();
					while(argIndex < guiArgs.length)
					{
						token = guiArgs[argIndex++];
						int move = parseUciMove(gameState, token);
						gameState.makeMove(move, isWhitesMove);
						isWhitesMove = !isWhitesMove;
					}
					gameState.display();
				} else if ("go".equals(token)) {
					//TODO: start searching
					go(guiArgs);
				} else if ("stop".equals(token)) {
					//TODO: stop searching
					respond("readyok??");
				} else if ("ponderhit".equals(token)) {
					//TODO: opponent made the move we were pondering on
					//      switch from pondering back to searching
				} else if ("debug".equals(token)) {
					//TODO: turn on/off engine's debug mode
					token = guiArgs[argIndex++];
					if("on".equals(token))
					{
						//turn debugging on
					} else if ("off".equals(token)) {
						//turn it off
					}
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

	private static void go(String[] guiArgs) {
		int argIndex = 1;
		String token = guiArgs[argIndex++];
		if("infinite".equals(token))
		{
			//info multipv 1 depth 10 seldepth 26 score cp 10 time 2032 nodes 1673940 pv b5c6 b7c6 b1c3 g8f6 g1f3 d8b6 d4b6 a7b6 c1g5 f8c5 g5f6 g7f6 a1d1 d7d5
		}
	}

	private static int parseUciMove(GameState gameState2, String uciMove) {
		/*		
		 * The move format is in long algebraic notation.
		 * A nullmove from the Engine to the GUI should be sent as 0000.
		 * Examples:  e2e4, e7e5, e1g1 (white short castling), e7e8q (for promotion)
		 */
		assert(uciMove.length()>=4);
		int fromSquare = Util.StrToSq(uciMove.substring(0, 2));
		int toSquare = Util.StrToSq(uciMove.substring(2, 4));
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
		return AbstractGenerator.EncodeMove(fromSquare, toSquare, piece.encoded(), 
				captured.encoded(),	promoter.encoded());
	}

	public static String readGuiCommand() throws IOException {
		return br.readLine();
	}

	public static void initIO() {
		br = new BufferedReader(new InputStreamReader(System.in));
		bw = new BufferedWriter(new OutputStreamWriter(System.out));
	}

	public static void respond(String response) throws IOException {
		bw.write(response + EOL);
		bw.flush();
	}

}
