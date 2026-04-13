package org.example.userapi.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true , nullable = false)
    private String email;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Column(length = 1000)
    private String bio ;
    @Column(nullable = true)
    private String image ;
    @Column(nullable = false)
    private Date created ;
    private String career;
    @Column(nullable = false)
    private boolean isAdmin;
    @Column(nullable = false)
    private boolean isProvider;
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;


}
