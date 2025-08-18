package org.klozevitz.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.klozevitz.enities.users.MessageSent;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageSentService {
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public List<MessageSent> messagesSentByTelegramUserId(Long telegramUserId) {
        try {
            return entityManager.createNativeQuery(
                            "SELECT ms.* FROM message_sent ms " +
                                    "WHERE ms.app_user = :telegramUserId",
                            MessageSent.class)
                    .setParameter("telegramUserId", telegramUserId)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении из БД сообщений пользователя " + telegramUserId, e);
        }
    }

    @Transactional
    public void save(int messageId, SendMessage sendMessage, long telegramUserId) {
        try {
            String sendMessageJson = sendMessage != null
                    ? objectMapper.writeValueAsString(sendMessage)
                    : null;

            assert sendMessage != null;
            entityManager.createNativeQuery(
                            "INSERT INTO message_sent (message_id, send_message, app_user) " +
                                    "VALUES (:messageId, CAST(:sendMessage AS jsonb), :telegramUserId)")
                    .setParameter("messageId", messageId)
                    .setParameter("sendMessage", sendMessageJson)
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save message", e);
        }
    }

    @Transactional
    public void deleteByTelegramUserId(Long telegramUserId) {
        try {
            assert telegramUserId != null;
            entityManager.createNativeQuery(
                    "DELETE FROM message_sent " +
                            "WHERE app_user = :telegramUserId")
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete message", e);
        }
    }

    @Transactional
    public void saveTerminate(long telegramUserId) {
        try {
            entityManager.createNativeQuery(
                            "INSERT INTO message_sent (app_user) " +
                                    "VALUES (:telegramUserId)")
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save message", e);
        }
    }

}
