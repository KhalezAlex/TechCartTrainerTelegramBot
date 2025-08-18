package org.klozevitz.enities.users;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.klozevitz.enities.BaseEntity;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(name = "message_sent")
public class MessageSent extends BaseEntity {
    @Column(name = "message_id")
    private Integer messageId;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "send_message")
    private SendMessage sendMessage;
    @ManyToOne
    @JoinColumn(name = "app_user", referencedColumnName = "telegram_user_id")
    private AppUser appUser;
}
