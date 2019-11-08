package petrinet;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

public class StringArrayConverter extends SimpleArgumentConverter {

    @Override
    protected String[] convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        if (source instanceof String && targetType.isAssignableFrom(String[].class)) {
            return ((String) source).split("\\s*,\\s*");
        } else {
            throw new IllegalArgumentException("Conversion from " + source.getClass() + " to "
                    + targetType + " not supported.");
        }
    }

}