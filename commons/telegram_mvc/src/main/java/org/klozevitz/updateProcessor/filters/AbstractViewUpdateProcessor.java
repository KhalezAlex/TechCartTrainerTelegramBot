package org.klozevitz.updateProcessor.filters;

import org.klozevitz.annotationParameters.UpdateType;
import org.klozevitz.annotations.ViewUpdateProcessor;
import org.klozevitz.updateProcessor.AbstractUpdateProcessor;
import org.klozevitz.updateProcessor.UpdateProcessor;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractViewUpdateProcessor extends AbstractUpdateProcessor {
    protected final Map<String, UpdateProcessor> commandDispatcher;

    protected AbstractViewUpdateProcessor() {
        this.commandDispatcher = new HashMap<>();
    }

    public void registerProcessor(String view, UpdateProcessor processor) {
        this.commandDispatcher.put(view, processor);
    }

    public UpdateType getSupportedUpdateType() {
        ViewUpdateProcessor annotation = this.getClass().getAnnotation(ViewUpdateProcessor.class);
        return annotation != null ? annotation.updateType() : null;
    }

    protected UpdateProcessor viewResolver(String command) {
        return commandDispatcher.get(command);
    }
}
