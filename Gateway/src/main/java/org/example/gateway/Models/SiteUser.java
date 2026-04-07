package org.example.gateway.Models;

import lombok.Data;

import java.util.Date;

@Data
public class SiteUser {
    private Long id;
    private String email;
    private String phoneNumber;
    private String name;
    private String password;
    private String bio ;
    private String image ;
    private Date created ;
    private String career;
    private boolean isAdmin;
    private boolean isProvider;

}
