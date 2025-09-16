package org.klozevitz.entityService.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.users.Company;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Log4j
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final EntityManager entityManager;

    public Optional<Company> findByTelegramUserId(Long telegramUserId) {
        try {
            Company company = (Company) entityManager.createNativeQuery(
                            "SELECT * FROM company " +
                                    "WHERE app_user = :telegramUserId",
                            Company.class)
                    .setParameter("telegramUserId", telegramUserId)
                    .getSingleResult();

            return Optional.ofNullable(company);
        } catch (NoResultException e) {
            log.warn(String.format("Компания с telegramUserId '{%d}' не найден", telegramUserId));
            return Optional.empty();
        } catch (Exception e) {
            log.error(String.format("Ошибка при поиске компании с telegramUserId: {%d}", telegramUserId), e);
            return Optional.empty();
        }
    }

    @Transactional
    public void createNewCompany(long telegramUserId, String email) {
        try {
            entityManager.createNativeQuery(
                    "INSERT INTO company (name, email, app_user) " +
                            "VALUES (:name, :email, :telegramUserId)")
                    .setParameter("name", "")
                    .setParameter("email", email)
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            log.error("Ошибка создания компании для пользователя " + telegramUserId, e);
            throw new RuntimeException("Failed to create new company", e);
        }
    }

    public Optional<Company> findByEmail(String email) {
        try {
            List<Company> result = entityManager.createNativeQuery(
                            "SELECT * FROM company WHERE email = :email", Company.class)
                    .setParameter("email", email)
                    .getResultList();

            return result.stream().findFirst();
        } catch (Exception e) {
            log.error("Ошибка при поиске компании по email: " + email, e);
            throw new RuntimeException("Ошибка при поиске компании по email", e);
        }
    }

    @Transactional
    public void deleteByTelegramUserId(Long telegramUserId) {
        try {
            entityManager.createNativeQuery(
                    "DELETE FROM company " +
                            "WHERE app_user = :telegramUserId")
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            log.error("Ошибка при удалении компании: " + telegramUserId, e);
            throw new RuntimeException("Ошибка при удалении компании.", e);
        }
    }

    @Transactional
    public void setCurrentView(long telegramUserId, String currentView) {
        try {
            entityManager.createNativeQuery(
                            "UPDATE company " +
                                    "SET current_view = :currentView " +
                                    "WHERE app_user = :telegramUserId"
                    )
                    .setParameter("currentView", currentView)
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            log.error(String.format("Ошибка установки view для компании %d: %s", telegramUserId, e.getMessage()), e);
        }
    }

}
