package org.klozevitz.enities.menu;

import lombok.*;
import org.klozevitz.enities.BaseEntity;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item")
public class Item extends BaseEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "units")
    private String units;
    @ManyToOne
    @JoinColumn(name = "category", referencedColumnName = "id")
    private Category category;
    @OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Ingredient> ingredients;
    @OneToOne(mappedBy = "item", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private TestQuestion testQuestion;
}
