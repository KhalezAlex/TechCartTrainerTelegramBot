package org.klozevitz;

public enum RabbitQueue {
    GATEWAY_QUEUE("gateway_queue"),

    GUEST_UPDATE_QUEUE("guest_queue"),

    COMPANY_UPDATE_QUEUE("company_update_queue"),
    COMPANY_TEXT_UPDATE_QUEUE("company_text_update_queue"),
    COMPANY_COMMAND_UPDATE_QUEUE("company_command_update_queue"),
    COMPANY_CALLBACK_UPDATE_QUEUE("company_callback_update_queue"),
    COMPANY_ANSWER_QUEUE("company_answer_message_queue"),

    DEPARTMENT_UPDATE_QUEUE("department_update_queue"),
    DEPARTMENT_TEXT_UPDATE_QUEUE("department_text_update_queue"),
    DEPARTMENT_COMMAND_UPDATE_QUEUE("department_command_update_queue"),
    DEPARTMENT_CALLBACK_UPDATE_QUEUE("department_callback_update_queue"),
    DEPARTMENT_DOCUMENT_UPDATE_QUEUE("department_document_update_queue"),
    DEPARTMENT_ANSWER_QUEUE("department_answer_message_queue"),

    EMPLOYEE_UPDATE_QUEUE("employee_update_queue"),
    EMPLOYEE_TEXT_UPDATE_QUEUE("employee_text_update_queue"),
    EMPLOYEE_COMMAND_UPDATE_QUEUE("employee_command_update_queue"),
    EMPLOYEE_CALLBACK_UPDATE_QUEUE("employee_callback_update_queue"),
    EMPLOYEE_ANSWER_QUEUE("company_answer_message_queue");

    private final String queue;

    RabbitQueue(String queue) {
        this.queue = queue;
    }

    public String queue() {
        return queue;
    }
}
