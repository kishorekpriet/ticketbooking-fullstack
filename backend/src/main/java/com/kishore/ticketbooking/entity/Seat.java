package com.kishore.ticketbooking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String seatNumber; // e.g., "A1", "B4"

    @Column(nullable = false)
    private boolean isBooked = false;

    // THE SECRET WEAPON: Optimistic Locking
    // This prevents double-booking. If User A and User B click "Buy" 
    // at the exact same millisecond, Hibernate checks this version number. 
    @Version
    private Long version; 

    // Constructors
    public Seat() {}

    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean isBooked) { this.isBooked = isBooked; }
    public Long getVersion() { return version; }
}