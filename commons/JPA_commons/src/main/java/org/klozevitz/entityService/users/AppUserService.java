package org.klozevitz.entityService.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.users.AppUser;
import org.klozevitz.enities.users.Guest;
import org.klozevitz.enities.users.enums.Role;
import org.klozevitz.enities.users.enums.views.GuestView;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Log4j
@Service
@RequiredArgsConstructor
public class AppUserService {
    private final EntityManager entityManager;

    public Optional<AppUser> findByTelegramUserId(Long telegramUserId) {
        try {
            AppUser user = (AppUser) entityManager.createNativeQuery(
                            "SELECT * FROM app_user " +
                                    "WHERE telegram_user_id = :telegramUserId",
                            AppUser.class)
                    .setParameter("telegramUserId", telegramUserId)
                    .getSingleResult();

            return Optional.ofNullable(user);
        } catch (NoResultException e) {
            log.warn(String.format("Пользователь с telegramUserId '{%d}' не найден", telegramUserId));
            return Optional.empty();
        } catch (Exception e) {
            log.error(String.format("Ошибка при поиске пользователя с telegramUserId: {%d}", telegramUserId), e);
            return Optional.empty();
        }
    }

    @Transactional
    public void newAppUser(long telegramUserId) {
        // Проверка на существующего пользователя
        if (entityManager.find(AppUser.class, telegramUserId) != null) {
            throw new EntityExistsException("User with telegram ID " + telegramUserId + " already exists");
        }

        AppUser newAppUser = AppUser.builder()
                .telegramUserId(telegramUserId)
                .firstLoginDate(LocalDateTime.now())
                .role("GUEST")
                .build();

        entityManager.persist(newAppUser);

        Guest guest = Guest.builder()
                .currentView(GuestView.NULLABLE_VIEW)
                .previousView(null)
                .appUser(newAppUser)
                .build();

        entityManager.persist(guest);
    }

    @Transactional
    public void setCompanyRole(Long telegramUserId) {
        try {
            entityManager
                    .createNativeQuery(
                            "UPDATE app_user " +
                                    "SET role = :role " +
                                    "WHERE telegram_user_id = :telegramUserId"
                    )
                    .setParameter("role", "COMPANY")
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при установке роли КОМПАНИЯ пользователю " + telegramUserId, e);
        }
    }
}
