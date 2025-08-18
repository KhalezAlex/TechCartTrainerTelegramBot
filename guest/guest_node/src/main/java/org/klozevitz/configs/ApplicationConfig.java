package org.klozevitz.configs;

import lombok.RequiredArgsConstructor;
import org.klozevitz.services.implementations.main.MainService;
import org.klozevitz.services.interfaces.IAnswerProducer;
import org.klozevitz.services.interfaces.IMainService;
import org.klozevitz.updateProcessor.UpdateProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final ApplicationContext appContext;

    @Bean
    protected IMainService mainService() {
        return new MainService(
            appContext.getBean("textGuestProcessor", UpdateProcessor.class),
            appContext.getBean("commandGuestProcessor", UpdateProcessor.class),
            appContext.getBean("callbackGuestProcessor", UpdateProcessor.class),
            appContext.getBean(IAnswerProducer.class)
        );
    }
}
