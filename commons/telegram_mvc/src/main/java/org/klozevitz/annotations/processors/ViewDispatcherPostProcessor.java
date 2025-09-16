package org.klozevitz.annotations.processors;

import lombok.NonNull;
import org.klozevitz.annotations.ViewDispatcher;
import org.klozevitz.updateProcessor.filters.AbstractViewDispatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ViewDispatcherPostProcessor implements BeanPostProcessor {
    private final Set<String> registeredRoles = new HashSet<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, @NonNull String beanName) throws BeansException {
        ViewDispatcher annotation = AnnotationUtils.findAnnotation(bean.getClass(), ViewDispatcher.class);

        if (annotation != null) {
            // Проверка наследования
            if (!(bean instanceof AbstractViewDispatcher)) {
                throw new IllegalStateException("Бин " + beanName + " с аннотацией @ViewDispatcher " +
                        "должен наследоваться от AbstractViewDispatcher");
            }

            // Проверка уникальности роли
            String role = annotation.role();
            if (registeredRoles.contains(role)) {
                throw new IllegalStateException("Найдено несколько диспетчеров для роли '" + role + "'");
            }

            registeredRoles.add(role);
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
