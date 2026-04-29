package org.example.gateway.Models;

import lombok.Data;

import java.util.Date;

@Data
public class SiteUser {
    public enum UserRole {
        USER,
        PROVIDER,
        ADMIN
    }

    private Long id;
    private String email;
    private String phoneNumber;
    private String name;
    private String password;
    private String bio ;
    private String image ;
    private Date created ;
    private String career;
    private UserRole role;

}
