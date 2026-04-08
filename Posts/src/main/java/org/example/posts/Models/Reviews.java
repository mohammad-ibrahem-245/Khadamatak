package org.example.posts.Models;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String content;
    @Column(nullable = false)
    private String author;
    @Column(nullable = false)
    private long postId;
    @Column(nullable = false)
    private double rating;


}
