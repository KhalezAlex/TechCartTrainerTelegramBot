#!/bin/bash

# Останавливаем и удаляем старый контейнер
docker rm -f test_menu_postgresql 2>/dev/null

# Запускаем контейнер RabbitMQ
docker run -d \
  --name test_menu_postgresql \
  --hostname test_menu_postgresql \
  -p 6543:5432 \
  -e POSTGRES_USER=klozevitz \
  -e POSTGRES_PASSWORD=ItsMyLife29101012 \
  -e POSTGRES_DB=test_menu \
  --restart=unless-stopped \
  postgres:14.5