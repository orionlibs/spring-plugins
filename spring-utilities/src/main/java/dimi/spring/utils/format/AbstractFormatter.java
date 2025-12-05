package dimi.spring.utils.format;

import jakarta.annotation.Nullable;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

public abstract class AbstractFormatter<T> implements Formatter<T>
{
    private final Class<T> targetType;


    protected AbstractFormatter(Class<T> targetType)
    {
        this.targetType = targetType;
    }


    public Class<T> getTargetType()
    {
        return targetType;
    }


    @Override
    @Nullable
    public T parse(@Nullable String text, Locale locale) throws ParseException
    {
        if(text == null || text.isBlank())
        {
            return null;
        }
        return parseNonNull(text, locale);
    }


    @Override
    @Nullable
    public String print(@Nullable T object, Locale locale)
    {
        if(object == null)
        {
            return "";
        }
        return printNonNull(object, locale);
    }


    /**
     * Parse a non-null String into T
     */
    protected abstract T parseNonNull(String text, Locale locale) throws ParseException;


    /**
     * Print a non-null T into String
     */
    protected abstract String printNonNull(T object, Locale locale);
}
