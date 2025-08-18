package org.klozevitz.annotationParameters;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UpdateType {
    TEXT("TextUpdateProcessor"),
    COMMAND("CommandUpdateProcessor"),
    CALLBACK("CallbackUpdateProcessor"),
    POLL("PollUpdateProcessor"),
    DOCUMENT("DocumentUpdateProcessor");

    private final String type;

    public String getType() {
        return type;
    }
}
