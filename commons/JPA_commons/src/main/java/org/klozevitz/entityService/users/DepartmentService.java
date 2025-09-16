package org.klozevitz.entityService.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.users.AppUser;
import org.klozevitz.enities.users.Company;
import org.klozevitz.enities.users.Department;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Log4j
@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final EntityManager entityManager;

    @Transactional
    public void save(long companyTelegramUserId, long departmentTelegramUserId) {
        // Находим компанию по telegramUserId
        Company company = (Company) entityManager.createNativeQuery(
                        "SELECT * " +
                                "FROM company comp " +
                                "JOIN app_user au " +
                                "ON comp.app_user = au.telegram_user_id " +
                                "WHERE au.telegram_user_id = :companyTelegramUserId",
                        Company.class)
                .setParameter("companyTelegramUserId", companyTelegramUserId)
                .getSingleResult();

        if (company == null) {
            throw new EntityNotFoundException("Company with telegram user ID " + companyTelegramUserId + " not found");
        }

        AppUser departmentAppUser = AppUser.builder()
                .telegramUserId(departmentTelegramUserId)
                .firstLoginDate(LocalDateTime.now())
                .role("DEPARTMENT")
                .build();
        entityManager.persist(departmentAppUser);

        Department department = Department.builder()
                .address("")
                .currentView("nullable_view")
                .state("PAID")
                .appUser(departmentAppUser)
                .company(company)
                .build();
        entityManager.persist(department);
    }

    public List<Department> departmentsByCompanyTelegramUserId(long telegramUserId) {
        try {
            return entityManager.createQuery(
                            "SELECT dept " +
                                    "FROM Department dept " +
                                    "JOIN dept.company comp " +
                                    "JOIN comp.appUser au " +
                                    "WHERE au.telegramUserId = :telegramUserId",
                            Department.class)
                    .setParameter("telegramUserId", telegramUserId)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Department findByTelegramUserId(long telegramUserId) {
        try {
            return entityManager.createQuery(
                    "SELECT dept " +
                            "FROM Department dept " +
                            "JOIN dept.appUser au " +
                            "WHERE au.telegramUserId = :telegramUserId",
                    Department.class)
                    .setParameter("telegramUserId", telegramUserId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public void setCurrentView(long telegramUserId, String currentView) {
        try {
            entityManager.createNativeQuery(
                            "UPDATE department d " +
                                    "SET current_view = :currentView " +
                                    "FROM app_user au " +
                                    "WHERE d.app_user = :telegramUserId")
                    .setParameter("currentView", currentView)
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to update current view", e);
        }
    }

}