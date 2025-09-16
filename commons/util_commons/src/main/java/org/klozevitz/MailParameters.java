package org.klozevitz;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailParameters {
    private String id;
    private String emailTo;
}
