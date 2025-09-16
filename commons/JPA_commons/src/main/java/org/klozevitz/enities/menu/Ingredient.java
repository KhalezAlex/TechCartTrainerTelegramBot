package org.klozevitz.enities.menu;

import lombok.*;
import org.klozevitz.enities.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ingredient")
public class Ingredient extends BaseEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "units")
    private String units;
    @ManyToOne
    @JoinColumn(name = "item", referencedColumnName = "id")
    private Item item;
}
