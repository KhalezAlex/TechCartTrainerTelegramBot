package org.klozevitz;

public class RabbitQueue {
    private RabbitQueue() {} // Запрещаем создание экземпляров

    public static final String GATEWAY_QUEUE = "gateway_queue";

    // Guest queues
    public static final String GUEST_UPDATE_QUEUE = "guest_update_queue";
    public static final String GUEST_TEXT_UPDATE_QUEUE = "guest_text_update_queue";
    public static final String GUEST_COMMAND_UPDATE_QUEUE = "guest_command_update_queue";
    public static final String GUEST_CALLBACK_UPDATE_QUEUE = "guest_callback_update_queue";

    // Company queues
    public static final String COMPANY_UPDATE_QUEUE = "company_update_queue";
    public static final String COMPANY_TEXT_UPDATE_QUEUE = "company_text_update_queue";
    public static final String COMPANY_COMMAND_UPDATE_QUEUE = "company_command_update_queue";
    public static final String COMPANY_CALLBACK_UPDATE_QUEUE = "company_callback_update_queue";

    // Department queues
    public static final String DEPARTMENT_UPDATE_QUEUE = "department_update_queue";
    public static final String DEPARTMENT_TEXT_UPDATE_QUEUE = "department_text_update_queue";
    public static final String DEPARTMENT_COMMAND_UPDATE_QUEUE = "department_command_update_queue";
    public static final String DEPARTMENT_CALLBACK_UPDATE_QUEUE = "department_callback_update_queue";
    public static final String DEPARTMENT_DOCUMENT_UPDATE_QUEUE = "department_document_update_queue";

    // Employee queues
    public static final String EMPLOYEE_UPDATE_QUEUE = "employee_update_queue";
    public static final String EMPLOYEE_TEXT_UPDATE_QUEUE = "employee_text_update_queue";
    public static final String EMPLOYEE_COMMAND_UPDATE_QUEUE = "employee_command_update_queue";
    public static final String EMPLOYEE_CALLBACK_UPDATE_QUEUE = "employee_callback_update_queue";

    // Answer queue
    public static final String ANSWER_QUEUE = "answer_message_queue";
}
