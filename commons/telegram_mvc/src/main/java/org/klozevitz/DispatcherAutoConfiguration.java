package org.klozevitz;

import org.klozevitz.annotations.ViewUpdateProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(ViewUpdateProcessor.class)
public class DispatcherAutoConfiguration {

}
