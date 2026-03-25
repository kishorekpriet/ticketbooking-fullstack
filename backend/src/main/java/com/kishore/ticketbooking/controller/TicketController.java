package com.kishore.ticketbooking.controller;

import com.kishore.ticketbooking.entity.Ticket;
import com.kishore.ticketbooking.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // POST: This endpoint triggers the booking logic
    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(
            @RequestParam Long movieId, 
            @RequestParam Long seatId, 
            @RequestParam String userEmail) {
            
        try {
            // Attempt to book the ticket via the Service layer
            Ticket ticket = ticketService.bookTicket(movieId, seatId, userEmail);
            return ResponseEntity.ok(ticket);
            
        } catch (RuntimeException e) {
            // If the seat is taken, send a Bad Request (400) with the error message
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}