package org.klozevitz.entityService;

import org.klozevitz.enities.users.Guest;

public interface IGuestService {
    Guest findByTelegramUserId(long telegramUserId);
    public void setCurrentView(long telegramUserId, String currentView);
}
