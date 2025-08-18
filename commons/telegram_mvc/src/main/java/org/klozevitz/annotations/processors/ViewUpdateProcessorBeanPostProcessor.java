package org.klozevitz.annotations.processors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.klozevitz.annotationParameters.UpdateType;
import org.klozevitz.annotations.BasicUpdateProcessor;
import org.klozevitz.annotations.ViewUpdateProcessor;
import org.klozevitz.updateProcessor.filters.AbstractBasicUpdateProcessor;
import org.klozevitz.updateProcessor.filters.AbstractViewUpdateProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ViewUpdateProcessorBeanPostProcessor implements BeanPostProcessor {
    private final ApplicationContext applicationContext;

    @Override
    public Object postProcessAfterInitialization(Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        // Проверяем, что бин является наследником AbstractViewUpdateProcessor и имеет аннотацию
        if (bean instanceof AbstractViewUpdateProcessor &&
                beanClass.isAnnotationPresent(ViewUpdateProcessor.class)) {

            registerViewProcessor((AbstractViewUpdateProcessor) bean);
        }
        return bean;
    }

    private void registerViewProcessor(AbstractViewUpdateProcessor processor) {
        ViewUpdateProcessor annotation = processor.getClass().getAnnotation(ViewUpdateProcessor.class);
        UpdateType updateType = annotation.updateType();
        String role = annotation.role();
        String view = annotation.view();

        // Ищем BasicUpdateProcessor с совпадающими updateType и role
        Map<String, AbstractBasicUpdateProcessor> basicProcessors =
                applicationContext.getBeansOfType(AbstractBasicUpdateProcessor.class);

        basicProcessors.values().stream()
                .filter(bp -> updateType.equals(bp.getSupportedUpdateType()))
                .filter(bp -> {
                    BasicUpdateProcessor bpAnnotation = bp.getClass().getAnnotation(BasicUpdateProcessor.class);
                    return bpAnnotation != null && role.equals(bpAnnotation.role());
                })
                .forEach(bp -> bp.registerProcessor(view, processor));
    }
}
