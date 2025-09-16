package org.klozevitz.enities.menu;

import lombok.*;
import org.klozevitz.enities.BaseEntity;
import org.klozevitz.enities.users.Department;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category")
public class Category extends BaseEntity {
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "department", referencedColumnName = "id")
    private Department department;
    @OneToMany(mappedBy = "category",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<Item> menu;
}
