package me.mp1282.visualtest.ui.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;

public class ExecutableBodyMouseEvent extends Event {
    public static final EventType<ExecutableBodyMouseEvent> ANY_MOUSE = new EventType<>(Event.ANY, "EXECUTABLE_BODY_MOUSE_EVENT_ANY");
    public static final EventType<ExecutableBodyMouseEvent> CLICK = new EventType<>(ANY_MOUSE, "EXECUTABLE_BODY_MOUSE_EVENT_CLICK");
    public static final EventType<ExecutableBodyMouseEvent> PRESSED = new EventType<>(ANY_MOUSE, "EXECUTABLE_BODY_MOUSE_EVENT_PRESSED");

    private final DiagramNodeUi ui; /* The diagram node UI that was clicked */
    private final MouseEvent wrappedMouseEvent; /* The mouse event that this event is wrapping */

    public ExecutableBodyMouseEvent(EventType<ExecutableBodyMouseEvent> eventType, DiagramNodeUi ui,
                              MouseEvent wrappedMouseEvent) {
        super(eventType);
        this.ui = ui;
        this.wrappedMouseEvent = wrappedMouseEvent;
    }

    public DiagramNodeUi getDiagramNodeUi() {
        return ui;
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
