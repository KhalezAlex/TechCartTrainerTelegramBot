package org.klozevitz.entityService.menu;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.menu.TestQuestion;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@Log4j
@Service
@RequiredArgsConstructor
public class TestQuestionService {
    private final EntityManager entityManager;

    public List<TestQuestion> findByCategoryId(long categoryId) {
        try {
            return (List<TestQuestion>) entityManager.createNativeQuery(
                    "SELECT tq.* " +
                            "FROM test_question tq " +
                            "JOIN item it ON tq.item = it.id " +
                            "JOIN category cat ON it.category = cat.id " +
                            "WHERE cat.id = :categoryId",
                    TestQuestion.class)
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        } catch (Exception e) {
            log.error("почему-то, тестовые вопросы не были найдены");
            return Collections.emptyList();
        }
    }
}
