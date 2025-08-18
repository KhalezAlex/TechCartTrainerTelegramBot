package org.klozevitz.annotations.processors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.annotations.ViewResolver;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.updateProcessor.filters.AbstractViewResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramViewBeanPostProcessor implements BeanPostProcessor {
    private final ApplicationContext applicationContext;

    @Override
    public Object postProcessAfterInitialization(Object bean, @NonNull String beanName) throws BeansException {
        // Проверяем что бин является AbstractTelegramView с аннотацией TelegramView
        if (bean instanceof AbstractTelegramView && bean.getClass().isAnnotationPresent(TelegramView.class)) {
            TelegramView telegramAnnotation = bean.getClass().getAnnotation(TelegramView.class);
            AbstractTelegramView telegramView = (AbstractTelegramView) bean;

            // Ищем все ViewResolver'ы с подходящими role и command
            var resolvers = applicationContext.getBeansWithAnnotation(ViewResolver.class);
            resolvers.forEach((name, resolver) -> {
                if (resolver instanceof AbstractViewResolver) {
                    ViewResolver resolverAnnotation = resolver.getClass().getAnnotation(ViewResolver.class);

                    // Проверяем точное совпадение role и command
                    if (telegramAnnotation.role().equals(resolverAnnotation.role()) &&
                            telegramAnnotation.command().equals(resolverAnnotation.command())) {

                        ((AbstractViewResolver) resolver).registerTelegramView(telegramView);
                    }
                }
            });
        }
        return bean;
    }
}