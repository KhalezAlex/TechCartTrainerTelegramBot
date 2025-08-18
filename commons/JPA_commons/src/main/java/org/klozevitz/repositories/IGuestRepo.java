package org.klozevitz.repositories;

import org.klozevitz.enities.users.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IGuestRepo extends JpaRepository<Guest, Long> {
    @Query(
            value = "SELECT * FROM guest " +
                    "WHERE app_user = :telegramUserId",
            nativeQuery = true
    )
    Optional<Guest> findByTelegramUserId(@Param("telegramUserId") long telegramUserId);
}
