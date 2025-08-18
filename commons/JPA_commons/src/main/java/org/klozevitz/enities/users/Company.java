package org.klozevitz.enities.users;

import lombok.*;
import org.klozevitz.enities.BaseEntity;
import org.klozevitz.enities.users.enums.views.CompanyView;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company")
public class Company extends BaseEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "current_view")
    private CompanyView current_view;
    @OneToOne
    private AppUser appUser;
}
