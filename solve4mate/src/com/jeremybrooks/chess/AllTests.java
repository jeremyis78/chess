package com.jeremybrooks.chess;

import junit.framework.Test;
import junit.framework.TestSuite;



/**
 * TestSuite that runs all tests
 *
 */
public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() { // Collect tests manually because we have to test class collection code
        TestSuite suite= new TestSuite("Framework Tests");
//        suite.addTestSuite(UtilTest.class);
//        suite.addTestSuite(BitmapTest.class);
//        suite.addTestSuite(PositionTest.class);
        return suite;
    }

//Came from the sample file (probably don't need)    
//    static boolean isJDK11() {
//        String version= System.getProperty("java.version");
//        return version.startsWith("1.1");
//    }
}