package me.mp1282.visualtest.ui.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;

public class ExecutionPathPortMouseEvent extends Event {

    public static final EventType<ExecutionPathPortMouseEvent> ANY_MOUSE =
            new EventType<>(Event.ANY, "EXEC_PATH_PORT_MOUSE_ANY");
    public static final EventType<ExecutionPathPortMouseEvent> CLICK =
            new EventType<>(ANY_MOUSE, "EXEC_PATH_PORT_MOUSE_CLICK");

    private final DiagramNodeUi ui;
    /**
     * The execution path port index. {@code -1} indicates the incoming port (nodeBefore);
     * {@code >= 0} indicates an outgoing port at that branch index.
     */
    private final int branchIndex;
    private final MouseEvent wrappedMouseEvent;

    public ExecutionPathPortMouseEvent(EventType<ExecutionPathPortMouseEvent> eventType,
                                       DiagramNodeUi ui, int branchIndex,
                                       MouseEvent wrappedMouseEvent) {
        super(eventType);
        this.ui               = ui;
        this.branchIndex      = branchIndex;
        this.wrappedMouseEvent = wrappedMouseEvent;
    }

    public DiagramNodeUi getDiagramNodeUi() {
        return ui;
    }

    /** @return {@code -1} for the incoming port, {@code >= 0} for an outgoing branch port. */
    public int getBranchIndex() {
        return branchIndex;
    }

    public boolean isIncoming() {
        return branchIndex < 0;
    }

    public MouseEvent getWrappedMouseEvent() {
        return wrappedMouseEvent;
    }

    @Override
    public void consume() {
        wrappedMouseEvent.consume();
    }

    @Override
    public boolean isConsumed() {
        return wrappedMouseEvent.isConsumed();
    }
}
