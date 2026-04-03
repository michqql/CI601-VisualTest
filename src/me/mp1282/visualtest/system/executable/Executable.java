package me.mp1282.visualtest.system.executable;

import javafx.scene.input.DataFormat;
import me.mp1282.visualtest.system.diagram.node.data.NodeData;
import me.mp1282.visualtest.system.executable.iodata.ParameterType;
import me.mp1282.visualtest.system.executable.iodata.ReturnType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract class that wraps an underlying executable piece of code.
 */
public abstract class Executable {

    public static final DataFormat DTO_DATA_FORMAT = new DataFormat(
            Executable.class.getPackageName() + "." +
                    Executable.class.getSimpleName()
    );

    private IExecutableTypeHolder<? extends Executable> holder;
    private List<ParameterType> parameterTypes;
    private List<ReturnType>    returnTypes;
    private String cachedPersistenceId;

    /**
     * The initialization function, should be called by the {@link IExecutableTypeHolder} to pass the
     * reference and allow the {@link Executable} to perform any set-up code required.
     *
     * @param holder - The parent holder.
     */
    public final void init(final IExecutableTypeHolder<? extends Executable> holder) {
        this.holder = holder;

        List<ParameterType> parameterTypes = new ArrayList<>();
        List<ReturnType>    returnTypes = new ArrayList<>();

        /* Allow subclasses to set up other things */
        setup(parameterTypes, returnTypes);

        /* Take the local lists and make them unmodifiable */
        this.parameterTypes = Collections.unmodifiableList(parameterTypes);
        this.returnTypes    = Collections.unmodifiableList(returnTypes);
    }

    /**
     * Allow the underlying implementation to perform specific set-up steps.
     */
    protected abstract void setup(List<ParameterType> parameterTypes, List<ReturnType> returnTypes);

    /**
     * The execute function performs the runtime actions for this {@link Executable}.
     *
     * @param inputs  - A generic list of inputs.
     * @param outputs - A generic list of outputs.
     * @param data    - Extra state data that is passed from the DiagramNode containing this Executable
     *                  to support the execution of the underlying code.
     * @throws Exception - Numerous exceptions can be thrown during the execution of the underlying function.
     */
    public abstract void execute(final Object[] inputs,
                                 final Object[] outputs,
                                 final NodeData data) throws Exception;

    /**
     * Gets the name of the underlying executable.
     *
     * @return The human-readable name of the executable
     */
    public abstract String getName();

    /**
     * Gets the namespace of the underlying executable.
     *
     * @return The namespace of the executable.
     */
    protected abstract String getNamespace();

    /**
     * <p>
     * Gets the ID that represents this executable. The ID can be used for persistence (saving)
     * because the ID will always be the same, as long as the underlying function remains the same.
     * </p>
     * <p>
     * The default persistence ID is in the following form: <br>
     * <em>_!package_name_class_name!method_name!parameter_count#parameter_type_names</em>
     * </p>
     *
     * @return The ID suitable for persistence and lookup between sessions/versions.
     */
    public String getPersistenceId() {
        if(cachedPersistenceId == null) {
            StringBuilder builder = new StringBuilder();

            builder.append("_!")
                    .append(getNamespace())
                    .append('!')
                    .append(getName())
                    .append('!')
                    .append(getNumberOfParameters())
                    .append('#')
                    .append(getNumberOfReturnValues())
                    .append("!_");

            for(ParameterType parameterType : getParameterTypes())
                builder.append(parameterType.getDataType().getSimpleName()).append('_');

            builder.append('#');

            for(ReturnType returnType : getReturnTypes())
                builder.append(returnType.getDataType().getSimpleName()).append('_');

            cachedPersistenceId = builder.toString();
        }

        return cachedPersistenceId;
    }

    public NodeData createNodeData() {
        return new NodeData();
    }

    /**
     * Gets the display label for an execution path port.
     * <p>
     * Returns {@code null} by default, meaning the UI will show a generic "IN" / "OUT" label
     * on hover. Override to provide a specific label (e.g. "TRUE" / "FALSE") that is always
     * shown alongside the port.
     *
     * @param branchIndex {@code -1} for the incoming port; {@code >= 0} for an outgoing branch port.
     * @return A short label string, or {@code null} if no custom label is needed.
     */
    public String getExecutionPathLabel(int branchIndex) {
        return null;
    }

    /**
     * Gets the number of execution path outputs this executable exposes.
     * Most executables have one (sequential flow). A branch executable has two (true / false),
     * and a future switch executable could have arbitrarily many.
     *
     * @return The number of execution path outputs (1 by default).
     */
    public int getExecutionPathOutputCount() {
        return 1;
    }

    /**
     * Given the inputs passed to {@link #execute}, returns the index of the execution path
     * that should be followed. All other paths will be skipped by the runtime.
     * <p>
     * Only meaningful when {@link #getExecutionPathOutputCount()} returns more than one.
     * The default always returns {@code 0} (first / only path).
     *
     * @param inputs The same inputs array that was passed to execute.
     * @return The index into {@link me.mp1282.visualtest.system.diagram.node.DiagramNode#getNodeAfterPaths()}.
     */
    public int getChosenBranchIndex(Object[] inputs) {
        return 0;
    }

    /**
     * Gets the number of parameters that the underlying code requires
     * @return Parameter count
     */
    public final int getNumberOfParameters() {
        return parameterTypes.size();
    }

    /**
     * Gets the list of parameters that the underlying code requires
     * @return Parameter type list
     */
    public final List<ParameterType> getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Gets the number of return values that the underlying code returns
     * @return Return value count
     */
    public final int getNumberOfReturnValues() {
        return returnTypes.size();
    }

    /**
     * Gets the list of return types that the underlying code returns
     * @return Return type list
     */
    public final List<ReturnType> getReturnTypes() {
        return returnTypes;
    }

    /**
     * Gets the data repository that holds this {@link Executable}
     *
     * @return The data repository responsible for the storage of this object
     */
    public final IExecutableTypeHolder<? extends Executable> getHolder() {
        return holder;
    }
}
