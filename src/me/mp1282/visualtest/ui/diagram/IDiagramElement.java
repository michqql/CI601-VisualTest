package me.mp1282.visualtest.ui.diagram;

/**
 * Any UI element implementing this interface receives the following:
 * - Translation
 * - Z-Ordering
 */
public interface IDiagramElement {
    /**
     * Gets the requested Z-Order of this element. Lower is better.
     * @return The Z-Order index of this UI element.
     */
    int getZOrder();
}
