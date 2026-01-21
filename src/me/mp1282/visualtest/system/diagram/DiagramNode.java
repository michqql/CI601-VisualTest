package me.mp1282.visualtest.system.diagram;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import me.mp1282.visualtest.system.executable.DataPort;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.util.Pair;

import java.util.UUID;

public class DiagramNode {

    private static final int COST_OF_NO_INPUTS          = 0;
    private static final int COST_PER_UNCONNECTED_INPUT = 1;
    private static final int COST_PER_CONNECTED_INPUT   = 2;

    private final UUID uuid;

    /* The wrapped executable */
    private Executable executable;

    /* The other nodes this node is connected to */
    private final ObservableMap<DataPort, Pair<DiagramNode, DataPort>> sourceToTargetConnectionMap;
    /* The cached execution cost, only recalculated when
     * the executable or its connections changes
     */
    private final IntegerProperty cachedExecutionCost;
    /* Flow connections */
    private final ObjectProperty<DiagramNode> executionPathNodeBefore;
    private final ObjectProperty<DiagramNode> executionPathNodeAfter;

    /* Executable data - static data that is entered by the user as a property */
    private String comment;

    /* Position data */
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty width;
    private final DoubleProperty height;

    public DiagramNode(Executable executable) {
        this.uuid                        = UUID.randomUUID();
        this.executable                  = executable;
        this.sourceToTargetConnectionMap = FXCollections.observableHashMap();
        this.cachedExecutionCost         = new SimpleIntegerProperty();
        this.executionPathNodeBefore     = new SimpleObjectProperty<>();
        this.executionPathNodeAfter      = new SimpleObjectProperty<>();
        this.x                           = new SimpleDoubleProperty();
        this.y                           = new SimpleDoubleProperty();
        this.width                       = new SimpleDoubleProperty();
        this.height                      = new SimpleDoubleProperty();

        /* Recalculate execution cost when connections change */
        this.sourceToTargetConnectionMap.addListener(this::handleMapChange);
        calculateExecutionCost(); /* Calculate initial value */
    }

    public Executable getExecutable() {
        return executable;
    }

    public void setExecutable(Executable executable) {
        /* The executable has changed, clear the connection map.
         * This will also trigger recalculation of execution cost.
         */
        this.sourceToTargetConnectionMap.clear();
        this.executable = executable;
    }

    public ObservableMap<DataPort, Pair<DiagramNode, DataPort>> dataConnectionsProperty() {
        return sourceToTargetConnectionMap;
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
        if(executable.getNumberOfInputs() == 0) {
            cachedExecutionCost.set(COST_OF_NO_INPUTS);
        } else {
            int cost = 0;
            for(DataPort input : executable.getInputs()) {
                Pair<DiagramNode, DataPort> connection = sourceToTargetConnectionMap.get(input);
                if(connection == null) {
                    /* Not connected, add base cost */
                    cost += COST_PER_UNCONNECTED_INPUT;
                    continue;
                } else {
                    /* Connected, add input cost plus extra cost */
                    cost += connection.key().executionCostProperty().get() + COST_PER_CONNECTED_INPUT;
                }
            }

            cachedExecutionCost.set(cost);
        }
    }

    private void handleMapChange(MapChangeListener.Change<
            ? extends DataPort, ? extends Pair<DiagramNode, DataPort>> change) {
        /* Add listener to any new DiagramNode added to the map */
        if(change.wasAdded()) {
            change.getValueAdded().key().executionCostProperty()
                    .addListener(this::handleMapInternalChange);
        }

        /* Remove listener from any DiagramNode removed from the map */
        if(change.wasRemoved()) {
            change.getValueRemoved().key().executionCostProperty()
                    .removeListener(this::handleMapInternalChange);
        }

        /* Recalculate execution cost when connections change */
        calculateExecutionCost();
    }

    private void handleMapInternalChange(ObservableValue<? extends Number> obs,
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
