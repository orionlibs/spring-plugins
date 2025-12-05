package dimi.spring.utils.convert;

import jakarta.annotation.Nullable;
import org.springframework.core.convert.converter.Converter;

public abstract class AbstractConverter<S, T> implements Converter<S, T>
{
    private final Class<S> sourceType;
    private final Class<T> targetType;


    protected AbstractConverter(Class<S> sourceType, Class<T> targetType)
    {
        this.sourceType = sourceType;
        this.targetType = targetType;
    }


    public Class<S> getSourceType()
    {
        return sourceType;
    }


    public Class<T> getTargetType()
    {
        return targetType;
    }


    @Override
    @Nullable
    public T convert(@Nullable S source)
    {
        if(source == null)
        {
            return null;
        }
        return convertNonNull(source);
    }


    /**
     * Concrete converters implement this to transform non-null source -> target.
     */
    protected abstract T convertNonNull(S source);


    // helper method to convert collections using another converter
    protected <A, B> java.util.List<B> convertList(java.util.Collection<A> src,
                    java.util.function.Function<A, B> mapper)
    {
        if(src == null || src.isEmpty())
        {
            return java.util.List.of();
        }
        var out = new java.util.ArrayList<B>(src.size());
        for(A item : src)
        {
            out.add(mapper.apply(item));
        }
        return out;
    }
}
