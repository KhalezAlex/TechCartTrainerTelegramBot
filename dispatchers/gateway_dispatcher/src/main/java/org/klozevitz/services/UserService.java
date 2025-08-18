package org.klozevitz.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.users.AppUser;
import org.klozevitz.enities.users.Guest;
import org.klozevitz.enities.users.enums.Role;
import org.klozevitz.enities.users.enums.views.GuestView;
import org.klozevitz.entityService.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;

@Log4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void newAppUser(long telegramUserId) {
        // Проверка на существующего пользователя
        if (entityManager.find(AppUser.class, telegramUserId) != null) {
            throw new EntityExistsException("User with telegram ID " + telegramUserId + " already exists");
        }

        AppUser newAppUser = AppUser.builder()
                .telegramUserId(telegramUserId)
                .firstLoginDate(LocalDateTime.now())
                .role(Role.GUEST)
                .build();

        entityManager.persist(newAppUser);

        Guest guest = Guest.builder()
                .currentView(GuestView.NULLABLE_VIEW)
                .previousView(null)
                .appUser(newAppUser)
                .build();

        entityManager.persist(guest);
    }

    @Override
    public AppUser findByTelegramUserId(long telegramUserId) {
        try {
            return
                    entityManager
                            .createQuery(
                                    "SELECT u FROM AppUser u " +
                                            "WHERE u.telegramUserId = :telegramUserId",
                                    AppUser.class
                            )
                            .setParameter("telegramUserId", telegramUserId)
                            .getSingleResult();
        } catch (NoResultException e) {
            log.error(String.format("Пользователь с id \"%d\" не найден!", telegramUserId));
            return null;
        }
    }
}
