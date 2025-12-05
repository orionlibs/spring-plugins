package dimi.spring.utils.empty_bean;

import static org.assertj.core.api.Assertions.assertThat;

import dimi.spring.utils.empty_bean.EmptyBeanTest.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(EmptyBeanTest.TestConfig.class)
public class EmptyBeanTest
{
    @Autowired @Qualifier(value = "myEmptyBean") EmptyClass emptyClass;


    @Test
    void testBeanClassName()
    {
        assertThat(emptyClass.getClass().getSimpleName()).isEqualTo("EmptyClass");
    }


    @Configuration
    @EmptyBean(name = "myEmptyBean")
    static class TestConfig
    {
    }
}
