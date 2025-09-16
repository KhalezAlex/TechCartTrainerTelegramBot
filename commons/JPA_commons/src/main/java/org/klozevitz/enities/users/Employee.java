package org.klozevitz.enities.users;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.klozevitz.enities.BaseEntity;
import org.klozevitz.enities.menu.util.CurrentTest;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(name = "employee")
public class Employee extends BaseEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "current_view")
    private String currentView;
    @Column(name = "state")
    private String state;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "current_test")
    private CurrentTest currentTest;
    @OneToOne
    @JoinColumn(name = "app_user", referencedColumnName = "telegram_user_id")
    private AppUser appUser;
    @ManyToOne
    @JoinColumn(name = "department", referencedColumnName = "id")
    private Department department;
}
