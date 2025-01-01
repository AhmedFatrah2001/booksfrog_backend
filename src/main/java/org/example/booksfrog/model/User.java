package org.example.booksfrog.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Lob
    private byte[] profilePicture;

    private Boolean isPremium;

    @Column(updatable = false)
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "user")
    List<Favorite> favorites;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }

    public boolean isPremium() {
        return this.isPremium;
    }
}
