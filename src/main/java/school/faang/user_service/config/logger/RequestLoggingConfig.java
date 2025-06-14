package school.faang.user_service.config.logger;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

    @Bean
    public FilterRegistrationBean<CommonsRequestLoggingFilter> loggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(false);
        filter.setIncludePayload(true);
        filter.setBeforeMessagePrefix("REQUEST START: ");
        filter.setAfterMessagePrefix("REQUEST END: ");
        FilterRegistrationBean<CommonsRequestLoggingFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(Integer.MIN_VALUE);
        return bean;
    }
}
