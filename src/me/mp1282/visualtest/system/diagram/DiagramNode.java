package me.mp1282.visualtest.system.diagram;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import me.mp1282.visualtest.system.executable.DataPort;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.util.Identifiable;
import me.mp1282.visualtest.util.Pair;

public class DiagramNode extends Identifiable {

    private static final int COST_OF_NO_INPUTS          = 0;
    private static final int COST_PER_UNCONNECTED_INPUT = 1;
    private static final int COST_PER_CONNECTED_INPUT   = 2;
    private static final int COST_OF_EXECUTION_PATH     = 5;

    /* The wrapped executable */
    private Executable executable;

    /* The other nodes this node is connected to
     * In the format of:
     * (Source Data Port) => (Target Node, Target Data Port)
     */
    private final ObservableMap<DataPort, Pair<DiagramNode, DataPort>> dataPortConnectionMap;
    /* Execution Path variables */
    private final ObjectProperty<DiagramNode> executionPathNodeBefore;
    private final ObjectProperty<DiagramNode> executionPathNodeAfter;
    /* The cached execution cost, only recalculated when
     * the executable or its connections changes
     */
    private final IntegerProperty cachedExecutionCost;

    /* Executable data - static data that is entered by the user as a property */
    private String comment;

    /* Position data */
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty width;
    private final DoubleProperty height;

    public DiagramNode(Executable executable) {
        super();
        this.executable                  = executable;
        this.dataPortConnectionMap       = FXCollections.observableHashMap();
        this.executionPathNodeBefore     = new SimpleObjectProperty<>();
        this.executionPathNodeAfter      = new SimpleObjectProperty<>();
        this.cachedExecutionCost         = new SimpleIntegerProperty();
        this.x                           = new SimpleDoubleProperty();
        this.y                           = new SimpleDoubleProperty();
        this.width                       = new SimpleDoubleProperty();
        this.height                      = new SimpleDoubleProperty();

        /* Recalculate execution cost when connections change */
        dataPortConnectionMap.addListener(this::handleDataPortConnectionMapChange);
        executionPathNodeBefore.addListener(this::handleExecutionPathNodeBeforeChange);
        calculateExecutionCost(); /* Calculate initial value */
    }

    public Executable getExecutable() {
        return executable;
    }

    public void setExecutable(Executable executable) {
        /* The executable has changed, clear the connection map.
         * This will also trigger recalculation of execution cost.
         */
        this.dataPortConnectionMap.clear();
        this.executable = executable;
    }

    public ObservableMap<DataPort, Pair<DiagramNode, DataPort>> dataConnectionsProperty() {
        return dataPortConnectionMap;
    }

    public IntegerProperty executionCostProperty() {
        return cachedExecutionCost;
    }

    public ObjectProperty<DiagramNode> executionPathNodeBeforeProperty() {
        return executionPathNodeBefore;
    }

    public ObjectProperty<DiagramNode> executionPathNodeAfterProperty() {
        return executionPathNodeAfter;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    /* Execution cost is calculated by these rules:
     * - With no inputs, cost is 0
     * - With inputs (not connected), cost is 1 per input
     * - With inputs (connected), cost is sum of input costs + 1.
     */
    private void calculateExecutionCost() {
        int cost = COST_OF_NO_INPUTS;

        /* Calculate cost for data port inputs */
        if(executable.getNumberOfInputs() > 0) {
            for(DataPort input : executable.getInputs()) {
                Pair<DiagramNode, DataPort> connection = dataPortConnectionMap.get(input);
                if(connection == null) {
                    /* Not connected, add base cost */
                    cost += COST_PER_UNCONNECTED_INPUT;
                    continue;
                } else {
                    /* Connected, add input cost plus extra cost */
                    cost += connection.key().executionCostProperty().get() + COST_PER_CONNECTED_INPUT;
                }
            }
        }

        /* Calculate cost for execution path */
        if(executionPathNodeBefore.get() != null)
            cost += executionPathNodeBefore.get().executionCostProperty().get() + COST_OF_EXECUTION_PATH;

        /* Set final cost */
        cachedExecutionCost.set(cost);
    }

    private void handleDataPortConnectionMapChange(MapChangeListener.Change<
            ? extends DataPort, ? extends Pair<DiagramNode, DataPort>> change) {
        /* Add listener to any new DiagramNode added to the map */
        if(change.wasAdded()) {
            change.getValueAdded().key().executionCostProperty()
                    .addListener(this::handleExecutionCostChangeOfOtherNode);
        }

        /* Remove listener from any DiagramNode removed from the map */
        if(change.wasRemoved()) {
            change.getValueRemoved().key().executionCostProperty()
                    .removeListener(this::handleExecutionCostChangeOfOtherNode);
        }

        /* Recalculate execution cost when connections change */
        calculateExecutionCost();
    }

    private void handleExecutionPathNodeBeforeChange(ObservableValue<? extends DiagramNode> obs,
                                                     DiagramNode oldVal, DiagramNode newVal) {
        /* Add listener to a new DiagramNode */
        if(newVal != null)
            newVal.executionCostProperty().addListener(this::handleExecutionCostChangeOfOtherNode);

        /* Remove listener from the old DiagramNode */
        if(oldVal != null)
            oldVal.executionCostProperty().removeListener(this::handleExecutionCostChangeOfOtherNode);

        /* Recalculate execution cost when the execution path changes */
        calculateExecutionCost();
    }

    private void handleExecutionCostChangeOfOtherNode(ObservableValue<? extends Number> obs,
                                                      Number oldVal, Number newVal) {
        calculateExecutionCost();
    }

    @Override
    public String toString() {
        return "DiagramNode{" +
                "executable=" + executable.getName() +
                ", executionCost=" + cachedExecutionCost.get() +
                '}';
    }
}
