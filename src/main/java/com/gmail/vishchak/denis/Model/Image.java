package com.gmail.vishchak.denis.Model;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Image")
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long imageId;

    @Column(name = "name")
    private String imageName;

    private String type;

    @Column(name = "image_bytes")
    private byte[] image;

    @OneToOne
    @JoinColumn(name = "user_id")
    private CurrentUser currentUser;

}
