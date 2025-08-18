package org.klozevitz.updateProcessor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

public interface UpdateProcessor {
    ArrayList<SendMessage> processUpdate(Update update);
}
