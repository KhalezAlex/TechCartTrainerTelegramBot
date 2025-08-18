package org.klozevitz;

import lombok.RequiredArgsConstructor;
import org.klozevitz.updateProcessor.UpdateProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
@RequiredArgsConstructor
public class Controller {
    private final ApplicationContext appContext;

    @GetMapping("/")
    public String get() {
        UpdateProcessor basicProcessor = appContext.getBean("commandGuestProcessor", UpdateProcessor.class);

        UpdateProcessor nullableViewProcessor = appContext.getBean("nullableViewCommandGuestProcessor", UpdateProcessor.class);
        UpdateProcessor welcomeViewProcessor = appContext.getBean("welcomeViewCommandGuestProcessor", UpdateProcessor.class);

        return "ok";
    }
}
