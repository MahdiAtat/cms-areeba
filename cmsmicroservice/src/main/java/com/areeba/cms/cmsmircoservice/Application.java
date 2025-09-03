package com.cma.api.eventhub.visibility;

import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * @author brt.chabchi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@PropertySource("classpath:fwk-api.properties")
@EnableCaching
@EnableAsync
public class Application extends SpringBootServletInitializer {

    // Allow to deploy an war application into a Tomcat (not embedded)
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    /**
     * @param args the args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ValidatorFactory validator() {
        return new LocalValidatorFactoryBean();
    }

    // To enable jakarta.validation
    // Use local validator for parameter name access
    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidatorFactory(validator());
        return processor;
    }
}
