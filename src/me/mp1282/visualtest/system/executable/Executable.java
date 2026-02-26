package me.mp1282.visualtest.system.executable;

import javafx.scene.input.DataFormat;
import me.mp1282.visualtest.system.executable.data.ParameterType;
import me.mp1282.visualtest.system.executable.data.ReturnType;

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
     * @param inputs - A generic list of inputs.
     * @param outputs - A generic list of outputs.
     * @throws Exception - Numerous exceptions can be thrown during the execution of the underlying function.
     */
    public abstract void execute(final Object[] inputs, final Object[] outputs) throws Exception;

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
                    .append('#');

            for(ParameterType parameterType : getParameterTypes())
                builder.append(parameterType.getDataType().getSimpleName()).append('_');

            cachedPersistenceId = builder.toString();
        }

        return cachedPersistenceId;
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
