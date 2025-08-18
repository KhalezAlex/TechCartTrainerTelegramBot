package org.klozevitz.updateProcessor.filters;

import org.klozevitz.updateProcessor.AbstractUpdateProcessor;
import org.klozevitz.updateProcessor.UpdateProcessor;

public abstract class AbstractViewResolver extends AbstractUpdateProcessor {
    protected UpdateProcessor telegramView;

    public void registerTelegramView(UpdateProcessor telegramView) {
        this.telegramView = telegramView;
    }
}
