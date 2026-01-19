package me.mp1282.visualtest.ui.diagram.port;

import me.mp1282.visualtest.system.executable.DataPort;
import me.mp1282.visualtest.system.executable.FlowToken;
import me.mp1282.visualtest.ui.executable.ExecutableUi;
import me.mp1282.visualtest.ui.executable.ExecutableUiSkin;

/* Provides information about where the data port bounds/area is relative to the UI component */
public class DataPortArea {

    private final DataPort dataPort;

    /* Port line */
    protected double lineStartX;
    protected double lineStartY;

    /* Rectangle area */
    protected double leftX;
    protected double topY;

    /* Mid-point */
    protected double midX;
    protected double midY;

    protected FlowToken flowToken;

    public DataPortArea(ExecutableUi ui, DataPort dataPort) {
        this.dataPort = dataPort;

        /* Different calculations depending on whether the data port is an input or output */
        if (dataPort.inputPort()) {
            final int nInputs = ui.getExecutable().getNumberOfInputs();
            final double inputSpacing = ui.getWidth() / nInputs;

            this.lineStartX = (inputSpacing / 2D) + (inputSpacing * dataPort.portIndex());
            this.lineStartY = ExecutableUiSkin.TOP_BOTTOM_BOX_PADDING -
                    ExecutableUiSkin.DATA_PORT_LENGTH;

            this.leftX = lineStartX - ExecutableUiSkin.TOP_BOTTOM_BOX_PADDING / 2D;
            this.topY = ExecutableUiSkin.TOP_BOTTOM_BOX_PADDING -
                    (ExecutableUiSkin.DATA_PORT_LENGTH * 1.5D);

            this.flowToken = FlowToken.INPUT;
        } else {
            final int nOutputs = ui.getExecutable().getNumberOfOutputs();
            final double outputSpacing = ui.getWidth() / nOutputs;

            this.lineStartX = (outputSpacing / 2D) + (outputSpacing * dataPort.portIndex());
            this.lineStartY = ui.getHeight() - ExecutableUiSkin.TOP_BOTTOM_BOX_PADDING;

            this.leftX = lineStartX - ExecutableUiSkin.BOX_SIDE_LENGTH / 2D + 1D;
            this.topY = lineStartY - (ExecutableUiSkin.DATA_PORT_LENGTH * 0.5D);

            this.flowToken = FlowToken.OUTPUT;
        }

        this.midX = leftX + ExecutableUiSkin.BOX_SIDE_LENGTH / 2D;
        this.midY = topY + ExecutableUiSkin.BOX_SIDE_LENGTH / 2D;
    }

    public DataPort getDataPort() {
        return this.dataPort;
    }

    public double getLineStartX() {
        return lineStartX;
    }

    public double getLineStartY() {
        return lineStartY;
    }

    public double getLeftX() {
        return leftX;
    }

    public double getTopY() {
        return topY;
    }

    public double getMidX() {
        return midX;
    }

    public double getMidY() {
        return midY;
    }

    public FlowToken getFlow() {
        return flowToken;
    }

    public boolean isInside(double x, double y) {
        return x >= leftX && x < leftX + ExecutableUiSkin.BOX_SIDE_LENGTH &&
                y >= topY && y < topY + ExecutableUiSkin.BOX_SIDE_LENGTH;
    }
}
