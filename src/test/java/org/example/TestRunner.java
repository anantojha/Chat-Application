package org.example;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        String resultsOfFailedTests = "";

        Result result = JUnitCore.runClasses(TestSuite.class);

        for (Failure failure : result.getFailures()) {
            resultsOfFailedTests += "\nTest: " + failure.getTestHeader() + "\n" + failure.toString();
        }

        System.out.println("\nAll Test Passed: " + result.wasSuccessful()
                + "\nTests Passed: " + (result.getRunCount() - result.getFailureCount())
                + "\nTests Failed: " + result.getFailureCount()
                + "\nFailed Tests Feedback: " + resultsOfFailedTests);
    }
}
