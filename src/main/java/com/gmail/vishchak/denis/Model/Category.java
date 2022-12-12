package com.gmail.vishchak.denis.Model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long categoryId;

    @Column(name = "name")
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Subcategory>subcategories;
}
