package com.gmail.vishchak.denis.Model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userId")
public class CurrentUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    @Column(name = "id", nullable = false)
    private Long userId;

    @Column(unique = true)
    private String login;

    private String password;

    private String email;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;
}
