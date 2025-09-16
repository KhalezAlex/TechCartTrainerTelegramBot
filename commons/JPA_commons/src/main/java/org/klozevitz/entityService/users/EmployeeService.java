package org.klozevitz.entityService.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.users.AppUser;
import org.klozevitz.enities.users.Department;
import org.klozevitz.enities.users.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Log4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EntityManager entityManager;

    public Employee findByTelegramUserId(long telegramUserId) {
        try {
            return entityManager.createQuery(
                            "SELECT empl " +
                                    "FROM Employee empl " +
                                    "JOIN empl.appUser au " +
                                    "WHERE au.telegramUserId = :telegramUserId",
                            Employee.class)
                    .setParameter("telegramUserId", telegramUserId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Employee> employeesByDepartmentTelegramUserId(long telegramUserId) {
        try {
            return (List<Employee>) entityManager.createNativeQuery(
                            "SELECT empl.* " +
                                    "FROM employee empl " +
                                    "JOIN department dept " +
                                    "ON empl.department = dept.id " +
                                    "WHERE dept.app_user = :telegramUserId",
                            Employee.class)
                    .setParameter("telegramUserId", telegramUserId)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Transactional
    public void merge(Employee employee) {
        try {
            entityManager.merge(employee);
        } catch (Exception e) {
            log.error("ошибка сохранения пользователя", e);
        }
    }

    @Transactional
    public void save(long departmentTelegramUserId, long employeeTelegramUserId) {
        Department department = (Department) entityManager.createNativeQuery(
                        "SELECT *" +
                                "FROM department dept " +
                                "JOIN app_user au " +
                                "ON dept.app_user = au.telegram_user_id " +
                                "WHERE au.telegram_user_id = :departmentTelegramUserId",
                        Department.class)
                .setParameter("departmentTelegramUserId", departmentTelegramUserId)
                .getSingleResult();

        if (department == null) {
            throw new EntityNotFoundException("Company with telegram user ID " + departmentTelegramUserId + " not found");
        }

        AppUser employeeAppUser = AppUser.builder()
                .telegramUserId(employeeTelegramUserId)
                .firstLoginDate(LocalDateTime.now())
                .role("EMPLOYEE")
                .build();
        entityManager.persist(employeeAppUser);

        Employee employee = Employee.builder()
                .appUser(employeeAppUser)
                .department(department)
                .name("")
                .state(department.getState())
                .currentView("nullable_view")
                .build();
        entityManager.persist(employee);
    }

    @Transactional
    public void setCurrentView(long telegramUserId, String currentView) {
        try {
            entityManager.createNativeQuery(
                            "UPDATE employee empl " +
                                    "SET current_view = :currentView " +
                                    "WHERE empl.app_user = :telegramUserId")
                    .setParameter("currentView", currentView)
                    .setParameter("telegramUserId", telegramUserId)
                    .executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to update current view", e);
        }
    }
}
