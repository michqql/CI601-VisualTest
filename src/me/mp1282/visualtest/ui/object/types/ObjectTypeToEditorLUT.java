package me.mp1282.visualtest.ui.object.types;

import me.mp1282.visualtest.ui.object.types.primitive.BooleanTypeEditorUi;
import me.mp1282.visualtest.ui.object.types.primitive.number.NumberTypeEditorUi;
import me.mp1282.visualtest.ui.other.NumericFieldUi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ObjectTypeToEditorLUT {

    private static final Map<Class<?>, Supplier<? extends TypeEditorUi<?>>> REGISTRY = new HashMap<>();
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_LUT = Map.of(
            boolean.class, Boolean.class,
            byte.class,    Byte.class,
            short.class,   Short.class,
            int.class,     Integer.class,
            long.class,    Long.class,
            double.class,  Double.class,
            char.class,    Character.class
    );

    static {
        register(Boolean.class, BooleanTypeEditorUi::new);
        register(Byte.class,    () -> new NumberTypeEditorUi<>(NumericFieldUi.BYTE_TYPE));
        register(Short.class,   () -> new NumberTypeEditorUi<>(NumericFieldUi.SHORT_TYPE));
        register(Integer.class, () -> new NumberTypeEditorUi<>(NumericFieldUi.INTEGER_TYPE));
        register(Long.class,    () -> new NumberTypeEditorUi<>(NumericFieldUi.LONG_TYPE));
        register(Float.class,   () -> new NumberTypeEditorUi<>(NumericFieldUi.FLOAT_TYPE));
        register(Double.class,  () -> new NumberTypeEditorUi<>(NumericFieldUi.DOUBLE_TYPE));
        register(String.class , StringTypeEditorUi ::new);
    }

    /* Primitive Types cannot be used as a generic parameter type,
     * therefore, we do not need to try to convert from primitive
     * to wrapper here
     */
    private static <T> void register(Class<T> clazz, Supplier<TypeEditorUi<T>> factory) {
        REGISTRY.put(clazz, factory);
    }

    public static TypeEditorUi<?> createEditor(Class<?> clazz) {
        if(clazz.isPrimitive())
            clazz = PRIMITIVE_TO_WRAPPER_LUT.get(clazz);

        Supplier<? extends TypeEditorUi<?>> factory = REGISTRY.get(clazz);
        return factory == null ? null : factory.get();
    }
}
