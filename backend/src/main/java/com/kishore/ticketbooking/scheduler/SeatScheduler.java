package com.kishore.ticketbooking.scheduler; 

import com.kishore.ticketbooking.entity.Seat;
import com.kishore.ticketbooking.repository.SeatRepository;
import com.kishore.ticketbooking.repository.TicketRepository; // NEW IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeatScheduler {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository; // WIRING IN THE TICKETS

    // Running every 60 seconds for testing
    @Scheduled(fixedRate = 60000)
    public void resetAllSeatsDaily() {
        System.out.println("⏰ Midnight reached! Running automated cinema reset...");
        
        // 1. BURN THE OLD TICKETS FIRST!
        ticketRepository.deleteAll();
        System.out.println("🗑️ All old tickets have been deleted from the database.");
        
        // 2. Fetch all seats from MySQL
        List<Seat> allSeats = seatRepository.findAll();
        
        // 3. Loop through every seat and un-book them
        for (Seat seat : allSeats) {
            seat.setBooked(false); 
        }
        
        // 4. Save the newly emptied seats back to the database
        seatRepository.saveAll(allSeats);
        
        System.out.println("✅ All seats have been successfully reset to available!");
    }
}