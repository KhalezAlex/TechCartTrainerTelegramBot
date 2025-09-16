package org.klozevitz.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.klozevitz.enities.users.MessageSent;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("gatewayMessageSentService")
@RequiredArgsConstructor
public class MessageSentService {
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public ArrayList<Integer> getMessageIdsByTelegramUserId(Long telegramUserId) {
        try {
            @SuppressWarnings("unchecked")
            List<Number> result = entityManager.createNativeQuery(
                            "SELECT ms.message_id FROM message_sent ms " +
                                    "WHERE ms.app_user = :telegramUserId " +
                                    "ORDER BY ms.message_id ASC")
                    .setParameter("telegramUserId", telegramUserId)
                    .getResultList();

            return result.stream()
                    .filter(Objects::nonNull)
                    .map(Number::intValue)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении message_id пользователя " + telegramUserId, e);
        }
    }

    @Transactional
    public void saveBotMessage(int messageId, BotApiMethodMessage message, long telegramUserId) {
        try {
            String sendMessageJson = message != null
                    ? objectMapper.writeValueAsString(message)
                    : null;

            assert message != null;
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
    public void saveUserMessage(int messageId, long telegramUserId) {
        try {
            entityManager.createNativeQuery(
                            "INSERT INTO message_sent (message_id, app_user) " +
                                    "VALUES (:messageId, :telegramUserId)")
                    .setParameter("messageId", messageId)
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
    public void deleteBugMessage(Long telegramUserId) {
        try {
            entityManager.createNativeQuery(
                            "DELETE FROM message_sent " +
                                    "WHERE app_user = :telegramUserId" +
                                    "AND message_id")
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
                            "INSERT INTO message_sent (app_user, message_id) " +
                                    "VALUES (:telegramUserId, -1)")
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save message", e);
        }
    }

    public MessageSent getBuggedMassageByTelegramUserId(long telegramUserId) {
        try {
            return (MessageSent) entityManager.createNativeQuery(
                            "SELECT ms.* FROM message_sent ms " +
                                    "WHERE ms.app_user = :telegramUserId " +
                                    "AND message_id > 0 " +
                                    "AND send_message is null",
                            MessageSent.class)
                    .setParameter("telegramUserId", telegramUserId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении message_id пользователя " + telegramUserId, e);
        }
    }

}
