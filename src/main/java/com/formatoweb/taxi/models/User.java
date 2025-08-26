package com.formatoweb.taxi.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(length = 255, nullable = false)
    private String lastname;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(length = 12, nullable = true)
    private String phone;

    @Column(length = 255, nullable = true)
    private String image;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(name = "notification_token", length = 255, nullable = true)
    private String notificationToken;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public User(){}

    @PreUpdate
    private void onUpdated(){
        this.updatedAt = LocalDateTime.now();
    }

}
