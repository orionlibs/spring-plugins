package dimi.spring.utils.bean;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class DynamicBeanUpdater implements ApplicationContextAware
{
    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        this.applicationContext = context;
    }


    /**
     * Update fields of a Spring bean.
     *
     * @param beanName name of the Spring bean
     * @param updates map of field names -> new values
     * @throws IllegalArgumentException if the bean is not found or fields cannot be accessed
     */
    public void updateBeanFields(String beanName, Map<String, Object> updates)
    {
        Objects.requireNonNull(beanName, "beanName cannot be null");
        Objects.requireNonNull(updates, "updates cannot be null");
        Object bean = applicationContext.getBean(beanName);
        Class<?> beanClass = bean.getClass();
        for(Map.Entry<String, Object> entry : updates.entrySet())
        {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();
            try
            {
                Field field = findField(beanClass, fieldName);
                if(field == null)
                {
                    throw new IllegalArgumentException("Field '" + fieldName + "' not found in " + beanClass);
                }
                field.setAccessible(true);
                field.set(bean, newValue);
            }
            catch(IllegalAccessException e)
            {
                throw new IllegalArgumentException("Cannot set field '" + fieldName + "' on bean '" + beanName + "'", e);
            }
        }
    }


    /**
     * Recursively search for a field in the class hierarchy
     */
    private Field findField(Class<?> clazz, String fieldName)
    {
        Class<?> current = clazz;
        while(current != null)
        {
            try
            {
                return current.getDeclaredField(fieldName);
            }
            catch(NoSuchFieldException ignored)
            {
            }
            current = current.getSuperclass();
        }
        return null;
    }
}
