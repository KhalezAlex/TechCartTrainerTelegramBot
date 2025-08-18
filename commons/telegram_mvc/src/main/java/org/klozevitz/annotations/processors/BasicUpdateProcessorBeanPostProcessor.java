package org.klozevitz.annotations.processors;

import lombok.NonNull;
import org.klozevitz.annotations.BasicUpdateProcessor;
import org.klozevitz.updateProcessor.filters.AbstractBasicUpdateProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class BasicUpdateProcessorBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof AbstractBasicUpdateProcessor) {
            Class<?> beanClass = bean.getClass();
            BasicUpdateProcessor annotation = beanClass.getAnnotation(BasicUpdateProcessor.class);

            if (annotation == null) {
                throw new IllegalStateException(
                        "Класс " + beanClass.getSimpleName() + " должен быть аннотирован @BasicUpdateProcessor"
                );
            }
        }

        return bean;
    }
}
