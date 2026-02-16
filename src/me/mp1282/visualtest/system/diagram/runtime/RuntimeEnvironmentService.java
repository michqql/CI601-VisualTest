package me.mp1282.visualtest.system.diagram.runtime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import me.mp1282.visualtest.system.diagram.DiagramNode;
import me.mp1282.visualtest.util.PropertyHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RuntimeEnvironmentService extends Thread {

    protected static final System.Logger RUNTIME_INFO_LOGGER = System.getLogger("DIAGRAM-RTI");

    private final BooleanProperty runningTask;
    private final LinkedList<ExecuteTask> tasks;
    private final ObjectProperty<ExecuteTask> currentTask;
    private final ObservableList<RunStep> runStepList;

    private final Lock lock;
    private final Condition taskAvailableCondition;

    /* Debugging flags */
    private final BooleanProperty stepMode;
    private final AtomicBoolean stepFlag;

    /* Thread safety variables */
    private final BooleanProperty runningTaskThreadSafe;
    private final ObservableList<RunStep> runStepListThreadSafe;

    public RuntimeEnvironmentService() {
        this.runningTask = new SimpleBooleanProperty();
        this.tasks = new LinkedList<>();
        this.currentTask = new SimpleObjectProperty<>();
        this.runStepList = FXCollections.observableArrayList();

        this.lock = new ReentrantLock();
        this.taskAvailableCondition = lock.newCondition();

        this.stepMode = new SimpleBooleanProperty();
        this.stepFlag = new AtomicBoolean();

        this.runningTaskThreadSafe = new SimpleBooleanProperty();
        this.runStepListThreadSafe = FXCollections.observableArrayList();

        runningTask.bind(currentTask.isNotNull());
        PropertyHelper.addListenerThreadSafe(runningTask, runningTaskThreadSafe::set);
        PropertyHelper.bindListThreadSafe(runStepList, runStepListThreadSafe);

        setName("DiagramRuntimeEnvironment");
        start();
    }

    public BooleanProperty stepModeProperty() {
        return stepMode;
    }

    public void setStepFlag() {
        stepFlag.set(true);
    }

    public BooleanProperty runningTaskProperty() {
        return runningTaskThreadSafe;
    }

    public ObservableList<RunStep> getRunStepList() {
        return runStepListThreadSafe;
    }

    @Override
    public void run() {
        System.out.println("Diagram Runtime Environment starting...");
        while(true) {
            try {
                lock.lock();

                final ExecuteTask currentTask = this.currentTask.get();
                if (currentTask != null) { /* Step current task if non-null */
                    if(currentTask.canStep()) {
                        /* (If the step mode is enabled AND the step flag is set) OR
                         * the step mode is not enabled.
                         *
                         * Simplifies to (NOT stepMode) OR stepFlag
                         */
                        if(!stepMode.get() || stepFlag.getAndSet(false))
                            runStepList.add(currentTask.step(RUNTIME_INFO_LOGGER));
                    } else {
                        this.currentTask.set(null); /* Task completed */
                    }

                } else if (!tasks.isEmpty()) { /* Fetch next task from queue */
                    this.currentTask.set(tasks.poll());

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
        if(currentTask.get() != null)
            tasks.addFirst(currentTask.get()); /* Add the current task back to the front of the queue */

        currentTask.set(task); /* Switch to the new task */
    }

    public ExecuteTask getCurrentTask() {
        return currentTask.get();
    }

    public List<DiagramNode> getDiagramNodeQueue() {
        List<DiagramNode> queue = new ArrayList<>();
        for (ExecuteTask task : tasks) {
            queue.addAll(task.getExecutionOrder());
        }
        return queue;
    }
}
