package org.klozevitz.services.implementations.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.IRegistrar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@Service
@RequiredArgsConstructor
public class CompanyRegisterService implements IRegistrar {
    @Value("${mailService.url}")
    private String mailServiceUrl;


    @Override
    public SendMessage register(Update update) {
        return null;
    }
}
