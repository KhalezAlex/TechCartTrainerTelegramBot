#!/bin/bash

docker rm -f test_menu_rabbitmq 2>/dev/null || true

docker run -d \
  --name test_menu_rabbitmq \
  --hostname test_menu_rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3.11.0-management

echo -n "Waiting for RabbitMQ to start"
for i in {1..60}; do
  if docker logs test_menu_rabbitmq 2>&1 | grep -q "Server startup complete"; then
    echo -e "\nRabbitMQ started successfully!"
    break
  fi
  echo -n "."
  sleep 1
  if [ $i -eq 60 ]; then
    echo -e "\nRabbitMQ failed to start within 60 seconds"
    docker logs test_menu_rabbitmq
    exit 1
  fi
done

sleep 5

echo "Creating and configuring user..."
docker exec test_menu_rabbitmq rabbitmqctl add_user klozevitz ItsMyLife
docker exec test_menu_rabbitmq rabbitmqctl set_user_tags klozevitz administrator

#docker exec test_menu_rabbitmq rabbitmqctl add_vhost /
docker exec test_menu_rabbitmq rabbitmqctl set_permissions -p / klozevitz ".*" ".*" ".*"

# Двойная проверка прав
docker exec test_menu_rabbitmq rabbitmqctl list_permissions -p /

# Создаем очереди с проверкой ошибок
declare -a queues=(
  "gateway_queue"
  "guest_update_queue" "guest_command_update_queue" "guest_text_update_queue" "guest_callback_update_queue"
  "company_update_queue" "company_command_update_queue" "company_text_update_queue" "company_callback_update_queue"
  "department_update_queue" "department_command_update_queue" "department_text_update_queue" "department_callback_update_queue" "department_document_update_queue"
  "employee_update_queue" "employee_command_update_queue" "employee_text_update_queue" "employee_callback_update_queue"
  "answer_message_queue"
)

for queue in "${queues[@]}"; do
  echo "Creating queue: $queue"
  if ! docker exec test_menu_rabbitmq rabbitmqadmin declare queue name="$queue" durable=true; then
    echo "Retrying queue creation..."
    sleep 2
    docker exec test_menu_rabbitmq rabbitmqadmin declare queue name="$queue" durable=true
  fi
done

echo "RabbitMQ setup completed successfully!"
echo "Management UI: http://localhost:15672"
echo "Username: klozevitz"
echo "Password: ItsMyLife29101012"