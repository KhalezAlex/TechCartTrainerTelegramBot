package org.klozevitz.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.klozevitz.RabbitQueue.*;
import static org.klozevitz.RabbitQueue.DEPARTMENT_UPDATE_QUEUE;
import static org.klozevitz.RabbitQueue.EMPLOYEE_UPDATE_QUEUE;

@Configuration
public class RabbitMqConfig {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue gatewayQueue() {
        return new Queue(GATEWAY_QUEUE.queue());
    }

    @Bean
    public Queue guestUpdateQueue() {
        return new Queue(GUEST_UPDATE_QUEUE.queue());
    }

    @Bean
    public Queue companyUpdateQueue() {
        return new Queue(COMPANY_UPDATE_QUEUE.queue());
    }

    @Bean
    public Queue departmentUpdateQueue() {
        return new Queue(DEPARTMENT_UPDATE_QUEUE.queue());
    }

    @Bean
    public Queue employeeUpdateQueue() {
        return new Queue(EMPLOYEE_UPDATE_QUEUE.queue());
    }
}
