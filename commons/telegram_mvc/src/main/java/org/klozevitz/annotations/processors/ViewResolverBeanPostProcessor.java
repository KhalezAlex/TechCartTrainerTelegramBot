package org.klozevitz.annotations.processors;

import lombok.RequiredArgsConstructor;
import org.klozevitz.annotations.ViewResolver;
import org.klozevitz.annotations.ViewUpdateProcessor;
import org.klozevitz.updateProcessor.filters.AbstractViewResolver;
import org.klozevitz.updateProcessor.filters.AbstractViewUpdateProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ViewResolverBeanPostProcessor implements BeanPostProcessor {
    private final ApplicationContext appContext;
    private List<AbstractViewUpdateProcessor> viewUpdateProcessors;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Собираем все ViewUpdateProcessor при первом вызове
        if (viewUpdateProcessors == null) {
            viewUpdateProcessors = new ArrayList<>(appContext.getBeansOfType(AbstractViewUpdateProcessor.class).values());
        }

        // Обрабатываем только бины с аннотацией @ViewResolver
        if (bean.getClass().isAnnotationPresent(ViewResolver.class)) {
            processViewResolverBean(bean);
        }

        return bean;
    }

    private void processViewResolverBean(Object bean) {
        Class<?> beanClass = bean.getClass();
        ViewResolver viewResolverAnnotation = beanClass.getAnnotation(ViewResolver.class);

        // 1. Проверяем наследование от AbstractViewResolver
        if (!AbstractViewResolver.class.isAssignableFrom(beanClass)) {
            throw new IllegalStateException("Класс " + beanClass.getName() +
                    " с аннотацией @ViewResolver должен наследовать AbstractViewResolver");
        }

        AbstractViewResolver resolver = (AbstractViewResolver) bean;
        String resolverRole = viewResolverAnnotation.role();
        String[] resolverViews = viewResolverAnnotation.views();

        // 2. Ищем все подходящие ViewUpdateProcessor'ы
        for (AbstractViewUpdateProcessor processor : viewUpdateProcessors) {
            ViewUpdateProcessor processorAnnotation =
                    processor.getClass().getAnnotation(ViewUpdateProcessor.class);

            if (processorAnnotation != null &&
                    processorAnnotation.role().equals(resolverRole) &&
                    containsView(resolverViews, processorAnnotation.view())) {

                // 3. Регистрируем резолвер по команде из процессора
                processor.registerProcessor(viewResolverAnnotation.command(), resolver);
            }
        }
    }

    private boolean containsView(String[] resolverViews, String processorView) {
        for (String view : resolverViews) {
            if (view.equals(processorView)) {
                return true;
            }
        }
        return false;
    }
}
