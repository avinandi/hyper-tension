package com.hypertension.runner.ws;

import com.hypertension.runner.exception.TaskQueueFullException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author Avirup
 *
 * Singleton class which maintains all executors and queue for external systems
 */
public enum WSExecutor {

    INSTANCE;
    /**
     * Concurrent map to hold queue for different external systems
     */
    private final Map<WSType, LinkedBlockingQueue<WSTaskWithLatch>> taskQueuesMap = new ConcurrentHashMap<WSType, LinkedBlockingQueue<WSTaskWithLatch>>();
    /**
     * List of thread
     */
    private final List<Thread> workerThreads = new ArrayList<Thread>();

    /**
     * Singleton class execution starting point
     */
    private WSExecutor() {
        /**
         * Initializes taskQueMap with all different external systems
         * Limit: 5000(can be increased or decreased based on the JVM utilization ??)
         */
        for(WSType type: WSType.values()) {
            taskQueuesMap.put(type, new LinkedBlockingQueue<WSTaskWithLatch>(5000));
        }

        startWorkerThreads();
    }

    /**
     * Threads initialization method
     */
    private void startWorkerThreads() {
        // initialization of all the threads for each task type
        for (WSType type: WSType.values()) {
            addWorkers(type, type.numberOfWorkerThread);
        }
    }

    /**
     * @param type
     * @param numberOfThreads
     *
     * Responsible method to set all demon threads and start execution
     */
    private void addWorkers(WSType type, int numberOfThreads) {
        for (int j = 0; j < numberOfThreads; j++) {
            Thread t = new WorkerThread(type); // initializing Worker
            t.setDaemon(true);
            t.setName("WS-Worker-" + type + "-" + type.getCurrentRequestedThreads()); // set the name as WS-Worker-<TYPE>-<Count> e.g. WS-Worker-CRM_API-0
            type.setCurrentRequestedThreads(type.getCurrentRequestedThreads() + 1); // reset the thread id
            t.start(); // start the thread

            workerThreads.add(t); // adding thread to list to maintain the sequence
        }
    }

    /**
     * @param tasks
     * @throws InterruptedException
     *
     * Only public method to be called to add tasks in queue for execution
     */
    public void addTaskToQueueAndWait(final WSTask<?>... tasks) throws InterruptedException, IOException {
        // this is the countDown latch so that the method doesn't return until all the executions are done
        CountDownLatch latch = new CountDownLatch(tasks.length);

        // find the right queue from the taskQueue map and add the job
        for (WSTask<?> t : tasks) {
            LinkedBlockingQueue<WSTaskWithLatch> queue = taskQueuesMap.get(t.wsType);
            if (!queue.offer(new WSTaskWithLatch(t, latch), 20, TimeUnit.SECONDS)) {
                // if the addition of task fails then throw an exception
                throw new TaskQueueFullException("Timeout received for task. Details: " + t);
            }
        }

        // now wait till all the tasks are executed
        latch.await();
    }

    /**
     * Helper class to hold the task
     */
    private final class WSTaskWithLatch {

        private final WSTask<?> task;
        private final CountDownLatch latch;

        private WSTaskWithLatch(WSTask<?> task, CountDownLatch latch) {
            this.task = task;
            this.latch = latch;
        }
    }

    /**
     * This is the actual worker thread that pulls out the task from the relevant queue and executes it.
     */
    private final class WorkerThread extends Thread {

        private final WSType taskTypeToHandle;

        private WorkerThread(WSType taskType) {
            this.taskTypeToHandle = taskType;
        }

        @Override
        public void run() {

            // Execute the loop until it is interrupted
            while (true) {
                WSTaskWithLatch taskWithLatch = null;

                try {
                    // get a job, execute it, and populate the result. (the below operation blocks if there are no job in the queue)
                    taskWithLatch = taskQueuesMap.get(taskTypeToHandle).take();

                    taskWithLatch.task.execute();
                } catch (InterruptedException iex) {
                    iex.printStackTrace(); // Replace with proper logger
                    break; // if thread execution interrupted break the execution and come out
                } catch (Exception ex) {
                    ex.printStackTrace(); // Replace with proper logger
                } finally {
                    taskWithLatch.latch.countDown(); // Reduce the count
                }
            }
        }
    }
}
