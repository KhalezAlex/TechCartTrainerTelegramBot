package org.klozevitz.entityService.menu;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.menu.Item;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@Log4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final EntityManager entityManager;

    public List<Item> findByCategoryId(long categoryId) {
        try {
            return (List<Item>) entityManager.createNativeQuery(
                    "SELECT * FROM item " +
                            "WHERE category = :categoryId",
                    Item.class)
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
