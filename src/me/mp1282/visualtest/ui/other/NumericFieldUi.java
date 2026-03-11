package me.mp1282.visualtest.ui.other;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.Function;

public class NumericFieldUi<T extends Number> extends TextField {

    public record Type<T>(Class<T> clazz, Function<String, T> parser, char[] valid) {}

    private static final char[] INTEGER_VALID_CHARS = { '-' };
    private static final char[] FLOAT_VALID_CHARS = { '-', '.' };

    public static final Type<Byte>    BYTE_TYPE    = new Type<>(Byte.class,    Byte::parseByte,     INTEGER_VALID_CHARS);
    public static final Type<Short>   SHORT_TYPE   = new Type<>(Short.class,   Short::parseShort,   INTEGER_VALID_CHARS);
    public static final Type<Integer> INTEGER_TYPE = new Type<>(Integer.class, Integer::parseInt,   INTEGER_VALID_CHARS);
    public static final Type<Long>    LONG_TYPE    = new Type<>(Long.class,    Long::parseLong,     INTEGER_VALID_CHARS);
    public static final Type<Float>   FLOAT_TYPE   = new Type<>(Float.class,   Float::parseFloat,   FLOAT_VALID_CHARS);
    public static final Type<Double>  DOUBLE_TYPE  = new Type<>(Double.class,  Double::parseDouble, FLOAT_VALID_CHARS);

    private final Type<T> type;
    private final ObjectProperty<T> value;

    private boolean updatingText;

    public NumericFieldUi(Type<T> type) {
        this.type = type;
        this.value = new SimpleObjectProperty<>();

        setTextFormatter(new TextFormatter<>(change -> {
            final String newText = change.getControlNewText();
            final String changedText = change.getText();

            /* Allow empty text */
            if(newText.isEmpty())
                return change;

            /* Validate characters in new text */
            for(char c : changedText.toCharArray()) {
                if(!Character.isDigit(c) && !isValidChar(c))
                    return null;
            }

            /* Validate the number can be parsed from the text */
            try {
                T parsed = type.parser.apply(newText);
                value.set(parsed);
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
    }

    public ReadOnlyObjectProperty<T> valueProperty() {
        return value;
    }

    public Number getValue() {
        return value.get();
    }

    @SuppressWarnings("unchecked")
    public void setValue(Number number) {
        Class<T> clazz = type.clazz;
        T value;
        if (clazz == Integer.class) {
            value = (T) Integer.valueOf(number.intValue());
        } else if (clazz == Long.class) {
            value = (T) Long.valueOf(number.longValue());
        } else if (clazz == Float.class) {
            value = (T) Float.valueOf(number.floatValue());
        } else if (clazz == Double.class) {
            value = (T) Double.valueOf(number.doubleValue());
        } else if (clazz == Short.class) {
            value = (T) Short.valueOf(number.shortValue());
        } else if (clazz == Byte.class) {
            value = (T) Byte.valueOf(number.byteValue());
        } else {
            throw new IllegalArgumentException("Unsupported numeric type: " + clazz);
        }

        this.value.set(value);
        setText(value.toString());
    }

    private boolean isValidChar(char c) {
        for(char valid : type.valid)
            if(c == valid)
                return true;

        return false;
    }
}
