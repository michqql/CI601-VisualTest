package me.mp1282.visualtest.system.inbuilt.special;

import com.google.gson.JsonObject;
import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.diagram.node.data.SwitchCase;
import me.mp1282.visualtest.system.diagram.node.data.SwitchNodeData;
import me.mp1282.visualtest.system.executable.Executable;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;
import me.mp1282.visualtest.system.executable.iodata.ReturnType;
import me.mp1282.visualtest.system.inbuilt.SpecialExecutable;

import java.util.List;

/**
 * Compares a single input value against a user-configured number of string cases.
 * The matching case's execution path is followed; if no case matches, the last path
 * (DEFAULT) is taken.
 * <p>
 * Each instance has its own {@code caseCount} baked in at construction time, which
 * controls how many execution path output ports the node exposes. The repository holds
 * one "template" singleton (default 2 cases); fresh instances are created for each
 * new diagram node and restored from the {@code extra_config} during persistence load.
 */
public class SwitchExecutable extends SpecialExecutable {

    private static final String CASE_COUNT_KEY = "case_count";
    private static final int    DEFAULT_CASES  = 2;

    /** Number of case paths. The DEFAULT path is at index {@code caseCount}. */
    private final int caseCount;

    /** Template constructor used by the repository (2 cases). */
    public SwitchExecutable() {
        this(DEFAULT_CASES);
    }

    public SwitchExecutable(int caseCount) {
        this.caseCount = caseCount;
    }

    public int getCaseCount() {
        return caseCount;
    }

    // ── Executable overrides ──────────────────────────────────────────────────

    @Override
    protected void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes) {
        parameterTypes.add(new ParameterType.Builder().setIndex(0).setDataType(Object.class).build());
    }

    @Override
    public void execute(Object[] inputs, Object[] outputs, NodeData data) {
        /* No-op: branching handled by getChosenBranchIndex */
    }

    @Override
    public int getExecutionPathOutputCount() {
        return caseCount + 1; /* N cases + 1 DEFAULT */
    }

    @Override
    public int getChosenBranchIndex(Object[] inputs, NodeData data) {
        if (!(data instanceof SwitchNodeData switchData)) return caseCount;
        String inputStr = String.valueOf(inputs[0]);
        List<SwitchCase> cases = switchData.getCases();
        for (int i = 0; i < cases.size(); i++) {
            String caseValue = cases.get(i).getValue();
            if (!caseValue.isEmpty() && caseValue.equals(inputStr))
                return i;
        }
        return caseCount; /* DEFAULT */
    }

    @Override
    public String getExecutionPathLabel(int branchIndex) {
        if (branchIndex == caseCount) return "DEFAULT";
        return null; /* case labels are dynamic — SwitchExecutableSkin renders them */
    }

    @Override
    public NodeData createNodeData() {
        return new SwitchNodeData(caseCount);
    }

    // ── Per-instance persistence hooks ───────────────────────────────────────

    @Override
    public JsonObject getExtraConfig() {
        JsonObject config = new JsonObject();
        config.addProperty(CASE_COUNT_KEY, caseCount);
        return config;
    }

    @Override
    public Executable restoreFromConfig(JsonObject config) {
        int count = config.has(CASE_COUNT_KEY) ? config.get(CASE_COUNT_KEY).getAsInt() : DEFAULT_CASES;
        SwitchExecutable instance = new SwitchExecutable(count);
        instance.init(getHolder());
        return instance;
    }

    @Override
    public Executable createForNode() {
        /* Return a fresh per-node instance so each node has its own caseCount */
        SwitchExecutable instance = new SwitchExecutable(caseCount);
        instance.init(getHolder());
        return instance;
    }

    // ── Identity ─────────────────────────────────────────────────────────────

    @Override
    public String getName() {
        return "Switch";
    }

    @Override
    protected String getNamespace() {
        return "*special";
    }
}
