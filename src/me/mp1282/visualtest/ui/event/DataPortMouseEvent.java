package me.mp1282.visualtest.ui.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.ui.diagram.node.DiagramNodeUi;

public class DataPortMouseEvent extends Event {

    public static final EventType<DataPortMouseEvent> ANY_MOUSE = new EventType<>(Event.ANY, "DATA_PORT_MOUSE_EVENT_ANY");
    public static final EventType<DataPortMouseEvent> CLICK = new EventType<>(ANY_MOUSE, "DATA_PORT_MOUSE_EVENT_CLICK");

    private final DiagramNodeUi ui;
    private final IDataPort<?> dataPort; /* The data port model of the clicked UI component */
    private final MouseEvent wrappedMouseEvent; /* The mouse event that this event is wrapping */

    public DataPortMouseEvent(EventType<DataPortMouseEvent> eventType, DiagramNodeUi ui, IDataPort<?> dataPort,
                              MouseEvent wrappedMouseEvent) {
        super(eventType);
        this.ui = ui;
        this.dataPort = dataPort;
        this.wrappedMouseEvent = wrappedMouseEvent;
    }

    public DiagramNodeUi getDiagramNodeUi() {
        return ui;
    }

    public IDataPort<?> getDataPort() {
        return dataPort;
    }

    public MouseEvent getWrappedMouseEvent() {
        return wrappedMouseEvent;
    }
}
