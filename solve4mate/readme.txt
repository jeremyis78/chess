A chess engine has three main components: 
    1) move generation (the pieces, board and the moves they can make)
    2) search (how we find the best move)
    3) evaluation (static and some dynamic evaluation of the state of the board currently)
 
We do an exhaustive brute force search on all possible moves (to a fixed depth) in the game graph (technically not a tree because there are transpositions)
but we prune the graph drastically by using alpha-beta pruning and caching. (for example, if we leave our queen enprise by making that move we cutoff that line of searching.)
The goal is to do an iterative deepening search (not sure if I have that hooked up currently or not but the code is there) and ultimately do dynamic tree splitting where we parallelize the search
wherever possible (I'm not that far yet). I also want to write the framework to automate testing of two slightly different versions of the engine (a and b).  Play short tournaments between the a and b versions, see which is better and rinse and repeat until the best tuned version of the program bubbles to the top.  Those are the goals, but back to what's there:

1) Move Generation 
com.jeremy_brooks.chess.base.Position: The board and the pieces on it are found here  (these are the bitboards, 64bit ints for each of the major piece types)
com.jeremy_brooks.chess.base.GameState: The state of the game (Position, as well as enpassant square, move number, moves made on the stack, etc) is found here. 
The unit tests for these are instructive (hopefully straightforward and easy to read for a first timer).

com.jeremy_brooks.chess.movegen: This package contains the move generation code (the move generation is done piecemeal: captures, noncaptures, escapes.  Kinda hokey the class structure here. I know you're judging me but don't judge me here, still a work in progress. If you have ideas for how to better structure this, let me know!  Actually don't, I haven't looked at it a while and i bet having a look at it with fresh eyes might present a better solution to the structure. My goal and challenge for myself is to completely design it myself with no outside help, hence the reason it's not open source...at least yet.  Also the piece heirachy is a mess too, way over engineered but I'll figure that out too, eventually.  Trading took over and I focused my efforts there where I could generate income so chess fell to the back burner).

2) Search:
com.jeremy_brooks.chess.search.Search: the alphabeta search functionality with min/max functions (duplicate code as I mentioned previously)
com.jeremy_brooks.chess.search.IterativeDeepeningSearch:  Calls alphabeta sucessively with deeper and deeper depths in to the search space (depth = 1 to depth = N)
SearchTest is a unit test...also instructive in my view.

3) Evaluation
com.jeremy_brooks.chess.eval.Evaluator: contains the logic to evaluate the value (a single int) of a board position in the search graph. This occurs when we hit our max depth, ie at the leaf nodes. 

The packaging process generates engine.jar.  So at a high level it implements (well...on its way to implementing) the UCI protocol for chess engines. 

com.jeremy_brooks.chess.UCIDriver is the main class in the jar.
And UCIDriver calls Solver.search(GameState g, int depth) to initiate a search to the given depth. (The Solver name and the root directory solve4mate are remnants of the original project from college; needs to change obviously since that no longer applies).
The UCI protocol is a stdin/stdout stateless protocol: http://download.shredderchess.com/div/uci.zip

That's basically the tour.  There's a bunch of unit tests, integration tests, and supporting classes (and in places way over engineered) but that's the guts of what's there.  Tons more work to do like actually
getting it to play decent chess (move ordering is crucial and that's not in there currently) but it will be easier going forward having the guts in place with great coverage to back me up. My last measure was it had 74% line code coverage.  When McAfee let me go in February of 2014, code coverage was at 44%.  Here's the numbers:

Authored (and improved) java-based chess engine (git, ant, junit, cobertura)
494% increase in number of unit tests (70 to 416)
68% increase in line coverage (44 to 74)
89% increase in branch coverage (36 to 68)
53% decrease in code complexity (5.604 to 2.63)

Hope that gives a good high level overview.