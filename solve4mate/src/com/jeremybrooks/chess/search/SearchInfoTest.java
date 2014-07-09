package com.jeremybrooks.chess.search;

import static org.junit.Assert.*;

import org.junit.Test;

public class SearchInfoTest {

	@Test
	public void testGetNodesPerSecond() {
		int nodeCount = 10000;
		int roughHalfSecondInMillis = 501;       // roughly half second
		double expectedNodesPerSecond = 19960.1; // 10,000 / 0.501 = 19960.079840319362
		double withinSecondsAccurate = 0.1;
		
		SearchInfo i = new SearchInfo();
		i.setNodeCount(nodeCount);
		i.setElapsedTime(roughHalfSecondInMillis);
		double nps = i.getNodesPerSecond();
		System.out.println(expectedNodesPerSecond);
		System.out.println(nps);
		assertEquals(expectedNodesPerSecond, nps, withinSecondsAccurate );
	}

}
