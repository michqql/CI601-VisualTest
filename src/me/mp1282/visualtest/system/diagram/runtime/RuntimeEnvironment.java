package me.mp1282.visualtest.system.diagram.runtime;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RuntimeEnvironment extends Thread {

    protected static final System.Logger RUNTIME_INFO_LOGGER = System.getLogger("DIAGRAM-RTI");

    private final LinkedList<ExecuteTask> tasks;
    private ExecuteTask currentTask;

    private final Lock lock;
    private final Condition taskAvailableCondition;

    public RuntimeEnvironment() {
        this.tasks = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.taskAvailableCondition = lock.newCondition();

        setName("DiagramRuntimeEnvironment");
        start();
    }

    @Override
    public void run() {
        System.out.println("Diagram Runtime Environment starting...");
        while(true) {
            try {
                lock.lock();

                if (currentTask != null) { /* Step current task if non-null */
                    if(currentTask.canStep()) {
                        currentTask.step(RUNTIME_INFO_LOGGER);
                    } else {
                        currentTask = null; /* Task completed */
                    }
                } else if (!tasks.isEmpty()) { /* Fetch next task from queue */
                    currentTask = tasks.poll();
                } else { /* Wait for a new task to be submitted */
                    while (tasks.isEmpty())
                        taskAvailableCondition.awaitUninterruptibly();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public void queueTask(ExecuteTask task) {
        try {
            lock.lock();
            tasks.add(task);
            taskAvailableCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void switchCurrentTask(ExecuteTask task) {
        tasks.addFirst(currentTask); /* Add the current task back to the front of the queue */
        currentTask = task; /* Switch to the new task */
    }

    public ExecuteTask getCurrentTask() {
        return currentTask;
    }
}
