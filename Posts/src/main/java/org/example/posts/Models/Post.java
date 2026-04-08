package org.example.posts.Models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false )
    private Long owner;
    @Column
    private String content;
    @Column
    private String image;
    @Column
    private int price ;




}
