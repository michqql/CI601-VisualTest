package me.mp1282.visualtest.ui.diagram.port;

import me.mp1282.visualtest.system.diagram.port.IDataPort;
import me.mp1282.visualtest.system.diagram.port.InputParameter;
import me.mp1282.visualtest.system.diagram.port.OutputReturn;
import me.mp1282.visualtest.system.executable.data.IDataType;
import me.mp1282.visualtest.ui.executable.ExecutableUi;
import me.mp1282.visualtest.ui.executable.ExecutableUiSkin;

/* Provides information about where the data port bounds/area is relative to the UI component */
public class DataPortArea {

    private final IDataPort<? extends IDataType> dataPort;

    /* Port line */
    protected double lineStartX;
    protected double lineStartY;

    /* Rectangle area */
    protected double leftX;
    protected double topY;

    /* Mid-point */
    protected double midX;
    protected double midY;

    protected int direction;

    public DataPortArea(ExecutableUi ui, IDataPort<? extends IDataType> dataPort) {
        this.dataPort = dataPort;

        /* Different calculations depending on whether the data port is an input or output */
        if (dataPort instanceof InputParameter input) {
            final int nInputs = ui.getExecutable().getNumberOfParameters();
            final double inputSpacing = ui.getWidth() / nInputs;

            this.lineStartX = (inputSpacing / 2D) + (inputSpacing * input.getType().getIndex());
            this.lineStartY = ExecutableUiSkin.TOP_BOTTOM_BOX_PADDING -
                    ExecutableUiSkin.DATA_PORT_LENGTH;

            this.leftX = lineStartX - ExecutableUiSkin.TOP_BOTTOM_BOX_PADDING / 2D;
            this.topY = ExecutableUiSkin.TOP_BOTTOM_BOX_PADDING -
                    (ExecutableUiSkin.DATA_PORT_LENGTH * 1.5D);

            this.direction = -1;
        } else if(dataPort instanceof OutputReturn output) {
            final int nOutputs = ui.getExecutable().getNumberOfReturnValues();
            final double outputSpacing = ui.getWidth() / nOutputs;

            this.lineStartX = (outputSpacing / 2D) + (outputSpacing * output.getType().getIndex());
            this.lineStartY = ui.getHeight() - ExecutableUiSkin.TOP_BOTTOM_BOX_PADDING;

            this.leftX = lineStartX - ExecutableUiSkin.BOX_SIDE_LENGTH / 2D + 1D;
            this.topY = lineStartY - (ExecutableUiSkin.DATA_PORT_LENGTH * 0.5D);

            this.direction = 1;
        }

        this.midX = leftX + ExecutableUiSkin.BOX_SIDE_LENGTH / 2D;
        this.midY = topY + ExecutableUiSkin.BOX_SIDE_LENGTH / 2D;
    }

    public IDataPort<? extends IDataType> getDataPort() {
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

    public int getDirection() {
        return direction;
    }

    public boolean isInside(double x, double y) {
        return x >= leftX && x < leftX + ExecutableUiSkin.BOX_SIDE_LENGTH &&
                y >= topY && y < topY + ExecutableUiSkin.BOX_SIDE_LENGTH;
    }
}
