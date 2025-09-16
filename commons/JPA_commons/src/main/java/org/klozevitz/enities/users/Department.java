package org.klozevitz.enities.users;

import lombok.*;
import org.klozevitz.enities.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
public class Department extends BaseEntity {
    @Column(name = "address")
    private String address;
    @Column(name = "current_view")
    private String currentView;
    @Column(name = "state")
    private String state;
    @OneToOne
    @JoinColumn(name = "app_user", referencedColumnName = "telegram_user_id")
    private AppUser appUser;
    @ManyToOne
    @JoinColumn(name = "company", referencedColumnName = "id")
    private Company company;
}
