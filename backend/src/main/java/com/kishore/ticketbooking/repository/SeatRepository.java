package com.kishore.ticketbooking.repository;
import com.kishore.ticketbooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    // This custom method automatically writes the SQL to find seats that aren't booked yet!
    List<Seat> findByIsBookedFalse(); 
}