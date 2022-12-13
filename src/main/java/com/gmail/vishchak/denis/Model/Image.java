package com.gmail.vishchak.denis.Model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "imageId")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
