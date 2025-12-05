package dimi.spring.data_utils.history;

import java.lang.reflect.Proxy;
import javax.sql.DataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class DataSourceWrappingBeanPostProcessor implements BeanPostProcessor
{
    @Autowired private DatabaseStatementRecorder recorder;


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        // no-op
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        if(bean instanceof DataSource && !(bean instanceof Proxy) && !(bean.getClass().getName().contains("ProxyDataSource")))
        {
            return new ProxyDataSource((DataSource)bean, recorder);
        }
        return bean;
    }
}
