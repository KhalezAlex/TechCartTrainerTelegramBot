package org.klozevitz.repositories;

import org.klozevitz.enities.users.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAppUserRepo extends JpaRepository<AppUser, Long> {
    AppUser findByTelegramUserId(long telegramUserId);
}
