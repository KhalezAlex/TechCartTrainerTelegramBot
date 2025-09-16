package org.klozevitz.config;

import org.klozevitz.MessageUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    protected MessageUtil messageUtil() {
        return new MessageUtil();
    }
}
