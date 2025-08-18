package org.klozevitz.updateProcessor.filters;

import org.klozevitz.annotationParameters.UpdateType;
import org.klozevitz.annotations.BasicUpdateProcessor;
import org.klozevitz.updateProcessor.AbstractUpdateProcessor;
import org.klozevitz.updateProcessor.UpdateProcessor;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBasicUpdateProcessor extends AbstractUpdateProcessor {
    protected final Map<String, UpdateProcessor> viewDispatcher;
    protected AbstractBasicUpdateProcessor() {
        this.viewDispatcher = new HashMap<>();
    }

    public void registerProcessor(String view, UpdateProcessor processor) {
        viewDispatcher.put(view, processor);
    }

    public UpdateType getSupportedUpdateType() {
        BasicUpdateProcessor annotation = this.getClass().getAnnotation(BasicUpdateProcessor.class);
        return annotation != null ? annotation.updateType() : null;
    }

    protected UpdateProcessor viewProcessor(String view) {
        return viewDispatcher.get(view);
    }

}
