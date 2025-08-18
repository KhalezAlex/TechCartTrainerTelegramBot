package org.klozevitz.enities.users;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.klozevitz.enities.users.enums.Role;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @Column(name = "telegram_user_id")
    private Long telegramUserId;
    @CreationTimestamp
    @Column(name = "first_login_date")
    private LocalDateTime firstLoginDate;
    @Column(name = "username")
    private String username;
    @Enumerated(EnumType.STRING)
    private Role role;
}
