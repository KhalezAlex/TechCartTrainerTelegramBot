package org.klozevitz.annotations.processors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.annotations.ViewDispatcher;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.updateProcessor.filters.AbstractViewDispatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
public class TelegramViewPostProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;
    private final Map<String, AbstractViewDispatcher> dispatcherCache = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        // 1. Сначала собираем все ViewDispatcher'ы
        if (bean instanceof AbstractViewDispatcher) {
            ViewDispatcher annotation = AnnotationUtils.findAnnotation(bean.getClass(), ViewDispatcher.class);
            if (annotation != null) {
                dispatcherCache.put(annotation.role(), (AbstractViewDispatcher) bean);
            }
        }

        // 2. Затем регистрируем TelegramView в соответствующий dispatcher
        TelegramView annotation = AnnotationUtils.findAnnotation(bean.getClass(), TelegramView.class);
        if (annotation != null && bean instanceof AbstractTelegramView) {
            AbstractTelegramView telegramView = (AbstractTelegramView) bean;
            String viewName = annotation.name();
            String role = annotation.role();

            AbstractViewDispatcher dispatcher = dispatcherCache.get(role);
            if (dispatcher != null) {
                dispatcher.registerView(viewName, telegramView);
            } else {
                throw new IllegalStateException("Не найден ViewDispatcher для роли: " + role);
            }
        }

        return bean;
    }
}