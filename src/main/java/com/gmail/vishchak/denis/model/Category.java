package com.gmail.vishchak.denis.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Data
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "categoryId")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    @Column(name = "id", nullable = false)
    private Long categoryId;

    @Column(name = "name")
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Subcategory>subcategories;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
