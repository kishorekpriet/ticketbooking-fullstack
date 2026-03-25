package com.kishore.ticketbooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime showtime;

    // Constructors
    public Movie() {}

    public Movie(String title, LocalDateTime showtime) {
        this.title = title;
        this.showtime = showtime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getShowtime() { return showtime; }
    public void setShowtime(LocalDateTime showtime) { this.showtime = showtime; }
}