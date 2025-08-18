package org.klozevitz.services.implementations.entityServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.users.Guest;
import org.klozevitz.entityService.IGuestService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Log4j
@Service
@RequiredArgsConstructor
public class GuestService implements IGuestService {
    private final EntityManager entityManager;

    @Override
    public Guest findByTelegramUserId(long telegramUserId) {
        try {
            return (Guest) entityManager
                    .createNativeQuery(
                            "SELECT g.* FROM guest g " +
                                    "JOIN app_user u ON g.app_user = u.telegram_user_id " +
                                    "WHERE u.telegram_user_id = :telegramUserId",
                            Guest.class)
                    .setParameter("telegramUserId", telegramUserId)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.error(String.format("Гость с telegramUserId: {%d} не найден", telegramUserId));
            return null;
        }
    }

    @Override
    @Transactional
    public void setCurrentView(long telegramUserId, String currentView) {
        try {
            entityManager.createNativeQuery(
                            "UPDATE guest " +
                                    "SET previous_view = current_view, " +
                                    "   current_view = :currentView " +
                                    "WHERE app_user = :telegramUserId"
                    )
                    .setParameter("currentView", currentView)
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            log.error(String.format("Ошибка установки view для пользователя %d: %s", telegramUserId, e.getMessage()), e);
        }
    }
}
