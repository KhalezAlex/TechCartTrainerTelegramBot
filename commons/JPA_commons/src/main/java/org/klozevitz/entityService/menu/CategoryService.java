package org.klozevitz.entityService.menu;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.menu.Category;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final EntityManager entityManager;

    public Category findById(long id) {
        try {
            return (Category) entityManager.createNativeQuery(
                            "SELECT * FROM category " +
                                    "WHERE id = :id",
                            Category.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Category> findByDepartmentTelegramUserId(long telegramUserId) {
        try {
            return (List<Category>) entityManager.createNativeQuery(
                            "SELECT c.* " +
                                    "FROM category c " +
                                    "JOIN department d ON c.department = d.id " +
                                    "WHERE d.app_user = :telegramUserId",
                            Category.class
                    )
                    .setParameter("telegramUserId", telegramUserId)
                    .getResultList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Transactional
    public void removeByDepartmentId(long departmentId, String name) {
        try {
            entityManager.createNativeQuery(
                            "DELETE FROM Category cat " +
                                    "WHERE cat.department = :departmentId " +
                                    "AND cat.name = :name"
                    )
                    .setParameter("departmentId", departmentId)
                    .setParameter("name", name)
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }

    @Transactional
    public void saveAll(Set<Category> menu) {
        menu.forEach(this::save);
    }

    public void save(Category category) {
        entityManager.persist(category);
    }


    public Map<Long, String> getResourceNamesMappedByIdsByEmployeeTelegramUserId(long telegramUserId) {
        try {
            return (Map<Long, String>) entityManager.createNativeQuery(
                            "SELECT cat.* FROM category cat " +
                                    "JOIN department dept " +
                                    "ON dept.id = cat.department " +
                                    "JOIN employee empl " +
                                    "ON dept.id = empl.department " +
                                    "WHERE empl.app_user = :telegramUserId",
                            Category.class)
                    .setParameter("telegramUserId", telegramUserId)
                    .getResultList()
                    .stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }


}
