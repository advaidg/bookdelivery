package com.example.demo.logging.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Entity class representing a {@link LogEntity} entry in the database.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOK_DELIVERY_LOGS")
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private HttpStatus status;

    @Column(columnDefinition = "TEXT")
    private String response;

    private LocalDateTime time;
}
