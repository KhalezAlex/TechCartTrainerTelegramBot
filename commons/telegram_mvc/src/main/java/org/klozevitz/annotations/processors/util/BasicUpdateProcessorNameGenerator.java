package org.klozevitz.annotations.processors.util;

import lombok.NonNull;
import org.klozevitz.annotationParameters.UpdateType;
import org.klozevitz.annotations.BasicUpdateProcessor;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

public class BasicUpdateProcessorNameGenerator extends AnnotationBeanNameGenerator {
    @Override
    @NonNull
    public String generateBeanName(@NonNull BeanDefinition definition, @NonNull BeanDefinitionRegistry registry) {
        // Получаем аннотацию из метаданных класса
        AnnotatedBeanDefinition annotatedDef = (AnnotatedBeanDefinition) definition;
        AnnotationMetadata metadata = annotatedDef.getMetadata();

        if (metadata.hasAnnotation(BasicUpdateProcessor.class.getName())) {
            Map<String, Object> attributes = metadata.getAnnotationAttributes(
                    BasicUpdateProcessor.class.getName());

            String name = (String) attributes.get("name");
            if (!name.isEmpty()) {
                return name;
            }

            String role = (String) attributes.get("role");
            UpdateType updateType = (UpdateType) attributes.get("updateType");
            return role + updateType.getType();
        }

        return super.generateBeanName(definition, registry);
    }
}
