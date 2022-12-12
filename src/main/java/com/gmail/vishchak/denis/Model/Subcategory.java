package com.gmail.vishchak.denis.Model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long categoryId;

    @Column(name = "name")
    private String subcategoryName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "subcategory")
    private List<Transaction> transactions;
}
