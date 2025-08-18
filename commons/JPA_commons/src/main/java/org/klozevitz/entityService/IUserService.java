package org.klozevitz.entityService;

import org.klozevitz.enities.users.AppUser;

public interface IUserService {
    AppUser findByTelegramUserId(long telegramUserId);
    void newAppUser(long telegramUserId);
}
