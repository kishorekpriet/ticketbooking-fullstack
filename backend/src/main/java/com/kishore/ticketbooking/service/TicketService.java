package com.kishore.ticketbooking.service;

import com.kishore.ticketbooking.entity.Movie;
import com.kishore.ticketbooking.entity.Seat;
import com.kishore.ticketbooking.entity.Ticket;
import com.kishore.ticketbooking.repository.MovieRepository;
import com.kishore.ticketbooking.repository.SeatRepository;
import com.kishore.ticketbooking.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private MovieRepository movieRepository;

    // @Transactional ensures that if ANYTHING fails (like a double-booking), 
    // the entire transaction is cancelled and rolled back automatically.
    @Transactional
    public Ticket bookTicket(Long movieId, Long seatId, String userEmail) {
        
        // 1. Find the movie and the seat in the database
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found!"));
                
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found!"));

        // 2. Business Logic: Is the seat already taken?
        if (seat.isBooked()) {
            throw new RuntimeException("Sorry, this seat is already booked!");
        }

        // 3. Mark the seat as booked
        seat.setBooked(true);
        
        // When we save the seat, Hibernate checks the @Version number.
        // If two people click "Buy" at the exact same time, the first save 
        // increases the version. The second save will fail because the version 
        // numbers no longer match! This entirely prevents double-booking.
        seatRepository.save(seat);

        // 4. Generate the Ticket (The Receipt)
        Ticket ticket = new Ticket(userEmail, seat, movie);
        return ticketRepository.save(ticket);
    }
}