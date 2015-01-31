package com.jeremybrooks.chess.eval;

import static com.jeremybrooks.chess.base.Bitmap.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;

public class EvalTester {

    private static final String TEST_POSITIONS_FILE = "test-positions.txt";
    private static Evaluator eval = EvaluatorTest.getEvaluator();

    public static void main(String[] args) {
        if(args.length == 0)
            runScoringTest();
        else if("--speed".equals(args[0]))
            runSpeedTest();
    }

    public static void runScoringTest() {
        List<String> positions = new ArrayList<>(14000);
        try (BufferedReader br = new BufferedReader(new FileReader("test-positions.txt")))
        {
            while(br.ready())
            {
                String line = br.readLine();
                if(line.startsWith("#")) continue;
                positions.add(line.split("\t")[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(String position: positions)
        {
            GameState g = new GameState(2);
            boolean isWhiteToMove = EvaluatorTest.setupState(g, position);
            int searchDepth = 0;
            int actualScore = evaluate(g, isWhiteToMove, searchDepth);
            System.out.println(actualScore + ": " + g.get());
        }
    }
    
    public static void runSpeedTest() {
        /*
         * Run speed tests on the eval functions
         */
        List<String> positions = new ArrayList<>(14000);
        
        try (BufferedReader br = new BufferedReader(new FileReader(TEST_POSITIONS_FILE)))
        {
            while(br.ready())
            {
                String line = br.readLine();
                if(line.startsWith("#")) continue;
                String firstField = line.split("\t")[0];
                positions.add(firstField);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int timesToRun[] = new int[]{50,100,150,250};
        for(int numTimesToRun: timesToRun)
        {
            int numTimesExecuted=0;
            long elapsedNanos = 0L;
            int numPositions = positions.size();
            for(int index=0; index < numPositions; index++)
            {
                int positionIndex = (int) (numPositions * Math.random());
                String position = positions.get(positionIndex);
                for(int times=0; times<numTimesToRun; times++)
                {
                    GameState g = new GameState(2);
                    //System.out.println("index: " + positionIndex);
                    boolean isWhiteToMove = EvaluatorTest.setupState(g, position);
                    int searchDepth = 0;
                    long start = System.nanoTime();
                    evaluate(g, isWhiteToMove, searchDepth);
                    elapsedNanos += System.nanoTime() - start;
                    //System.out.println(actualScore + ": " + g.get());
                    numTimesExecuted++;
                }
            }
            //Needs to run at least 1.5 million executions for good average results 
            double nanosPerSecond = 1e9;
            System.out.println("ran evaluate() " +
                    numTimesExecuted + " times in " + 
                    elapsedNanos/nanosPerSecond + " seconds = " +
                    (elapsedNanos / numTimesExecuted) + " nanos/run");
        }
    }

    private static int evaluate(GameState g, boolean isWhiteToMove, int searchDepth) {
        return eval.evaluate(g, isWhiteToMove?Piece.WHITE:Piece.BLACK, searchDepth, false, false);
    }

}
