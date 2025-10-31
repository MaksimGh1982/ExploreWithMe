package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EqualsAndHashCode(of = { "id" })
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 250, nullable = false)
    private String name;

    @Column(name = "email", length = 254, nullable = false, unique = true)
    private String email;

    @Column(name = "created")
    private LocalDateTime created;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }
}
