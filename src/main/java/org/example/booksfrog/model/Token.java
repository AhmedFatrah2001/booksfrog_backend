package org.example.booksfrog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId // Indicates that this field is both a primary key and a foreign key
    @JoinColumn(name = "user_id") // Specifies the foreign key column
    private User user;

    @Column(name = "daily_tokens", nullable = false)
    private Integer dailyTokens;

    @Column(name = "total_tokens", nullable = false)
    private Integer totalTokens;

    @Column(name = "last_reset")
    private LocalDateTime lastReset;
}

