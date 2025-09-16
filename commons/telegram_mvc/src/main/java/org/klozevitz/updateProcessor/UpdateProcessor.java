package org.klozevitz.updateProcessor;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

public interface UpdateProcessor {
    ArrayList<BotApiMethod<?>> processUpdate(Update update);
}
