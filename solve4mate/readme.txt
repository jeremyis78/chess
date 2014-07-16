Ideas for improvement (based on the CleanCode code smell/heuristic)

Search.java: rip out the evaluate function into its own Evaluator class (along with value of pieces
on squares)  (XX?: Looser coupling, smaller classes, easier testability)

MoveGenerator.java: make it an interface, three concrete classes???:  (G5: Remove Duplication, XX?: smaller classes) 
	1) NonCaptureGenerator, 
	2) CaptureGenerator
	3) KingEscapeGenerator