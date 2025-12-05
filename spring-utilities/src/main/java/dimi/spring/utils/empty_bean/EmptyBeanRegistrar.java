package dimi.spring.utils.empty_bean;

import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class EmptyBeanRegistrar implements ImportBeanDefinitionRegistrar
{
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry)
    {
        // read attributes of @EmptyBean (if the importing class was annotated)
        Map<String, Object> attrs = importingClassMetadata.getAnnotationAttributes(EmptyBean.class.getName());
        if(attrs == null)
        {
            return;
        }
        String beanName = (String)attrs.get("name");
        if(beanName == null || beanName.isBlank())
        {
            beanName = "emptyBean";
        }
        // if a bean with that name already exists, we skip registration to avoid overriding.
        // (You can change behavior to override by calling registry.removeBeanDefinition(...))
        if(registry.isBeanNameInUse(beanName) || registry.containsBeanDefinition(beanName))
        {
            // skip registration or optionally throw/log
            return;
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(EmptyClass.class);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        BeanDefinition def = builder.getBeanDefinition();
        registry.registerBeanDefinition(beanName, def);
    }
}
