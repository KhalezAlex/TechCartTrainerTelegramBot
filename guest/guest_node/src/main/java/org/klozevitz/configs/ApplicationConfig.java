package org.klozevitz.configs;

import lombok.RequiredArgsConstructor;
import org.klozevitz.CryptoTool;
import org.klozevitz.MessageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    @Value("${salt}")
    private String salt;

    @Bean
    protected CryptoTool cryptoTool() {
        return new CryptoTool(salt);
    }

    @Bean
    protected MessageUtil messageUtil() {
        return new MessageUtil();
    }
}
