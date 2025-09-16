package org.klozevitz;

public class RabbitQueue {
    private RabbitQueue() {} // Запрещено создание экземпляров

    public static final String GATEWAY_QUEUE = "gateway_queue";

    // Guest queues
    public static final String GUEST_UPDATE_QUEUE = "guest_update_queue";

    // Company queues
    public static final String COMPANY_UPDATE_QUEUE = "company_update_queue";

    // Department queues
    public static final String DEPARTMENT_UPDATE_QUEUE = "department_update_queue";

    // Employee queues
    public static final String EMPLOYEE_UPDATE_QUEUE = "employee_update_queue";

    // Answer queue
    public static final String ANSWER_QUEUE = "answer_message_queue";

    // Bugged messages for deletion queue
    public static final String DELETION_QUEUE = "deletion_queue";
}
