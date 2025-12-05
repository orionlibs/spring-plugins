package dimi.spring.utils.convert;

import jakarta.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

public abstract class AbstractGenericConverter implements GenericConverter
{
    private final Set<ConvertiblePair> convertiblePairs;


    protected AbstractGenericConverter(Set<ConvertiblePair> convertiblePairs)
    {
        this.convertiblePairs = Set.copyOf(convertiblePairs);
    }


    @Override
    public Set<ConvertiblePair> getConvertibleTypes()
    {
        return Collections.unmodifiableSet(convertiblePairs);
    }


    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
    {
        if(source == null)
        {
            return null;
        }
        return convertNonNull(source, sourceType, targetType);
    }


    /**
     * Convert non-null source for the requested source/target pair.
     * Implementations should either return a converted value or null if unsupported.
     */
    @Nullable
    protected abstract Object convertNonNull(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
}
