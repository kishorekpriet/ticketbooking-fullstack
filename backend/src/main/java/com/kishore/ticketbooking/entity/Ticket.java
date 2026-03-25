package com.kishore.ticketbooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail; // Who bought the ticket

    // Link this ticket to one specific Seat
    @OneToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    // Link this ticket to a specific Movie
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    // Automatically set the exact time the ticket was bought
    @PrePersist
    protected void onCreate() {
        bookingTime = LocalDateTime.now();
    }

    // Constructors
    public Ticket() {}

    public Ticket(String userEmail, Seat seat, Movie movie) {
        this.userEmail = userEmail;
        this.seat = seat;
        this.movie = movie;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public LocalDateTime getBookingTime() { return bookingTime; }
}