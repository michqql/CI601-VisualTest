package me.mp1282.visualtest.system.event.types;

import me.mp1282.visualtest.system.diagram.Diagram;
import me.mp1282.visualtest.system.event.IEvent;

/**
 * This event is fired when the user selects a new diagram in the DiagramTabUi.
 * This means this event originates from the UI thread.
 */
public record DiagramSelectedEvent(Diagram selectedDiagram) implements IEvent {}