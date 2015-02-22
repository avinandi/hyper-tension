package com.hypertension.runner.ws;

/**
 * @Author Avirup
 *
 * Enum which defines different WS components with no of threads it capable of handle
 */
public enum WSType {
    /**
     * Add component names here whenever add new component along with their thread number
     */
    API_UNDER_TEST(10);

    int numberOfWorkerThread = 10;
    int currentRequestedThreads = 0;

    private WSType(int numberOfWorkerThread) {
        this.numberOfWorkerThread = numberOfWorkerThread;
    }

    public int getCurrentRequestedThreads() {
        return currentRequestedThreads;
    }

    public void setCurrentRequestedThreads(int currentRequestedThreads) {
        this.currentRequestedThreads = currentRequestedThreads;
    }
}
