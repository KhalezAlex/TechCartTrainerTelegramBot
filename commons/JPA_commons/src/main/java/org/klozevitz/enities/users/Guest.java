package org.klozevitz.enities.users;

import lombok.*;
import org.klozevitz.enities.BaseEntity;
import org.klozevitz.enities.users.enums.views.GuestView;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "guest")
public class Guest extends BaseEntity {
    @Column(name = "previous_view")
    private String previousView;
    @Column(name = "current_view")
    private String currentView;
    @OneToOne
    @JoinColumn(name = "app_user", referencedColumnName = "telegram_user_id")
    private AppUser appUser;
}
