package dimi.spring.utils.empty_bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Place on any class (typically a @Configuration or @SpringBootApplication class).
 * It will import EmptyBeanRegistrar which registers a bean of type
 * dimi.spring.utils.empty_bean.EmptyClass with the requested name.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(dimi.spring.utils.empty_bean.EmptyBeanRegistrar.class)
public @interface EmptyBean
{
    /**
     * Name for the bean in the Spring container.
     * Example: @EmptyBean(name = "myEmptyBean")
     */
    String name() default "emptyBean";
}
