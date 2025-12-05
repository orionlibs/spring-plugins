package dimi.spring.utils.rest_recorder;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RESTRecorderConfiguration
{
    @Bean
    public FilterRegistrationBean<RESTRecorderFilter> restRecorderFilterRegistration(RESTCallRecorder recorder)
    {
        RESTRecorderFilter filter = new RESTRecorderFilter(recorder);
        FilterRegistrationBean<RESTRecorderFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setName("restRecorderFilter");
        reg.addUrlPatterns("/*");
        // high precedence so it wraps request/response before controllers
        reg.setOrder(Integer.MIN_VALUE + 10);
        return reg;
    }
}
