package com.kishore.ticketbooking.component;

import com.kishore.ticketbooking.entity.Movie;
import com.kishore.ticketbooking.entity.Seat;
import com.kishore.ticketbooking.repository.MovieRepository;
import com.kishore.ticketbooking.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Auto-generate a Movie if the database is empty
        if (movieRepository.count() == 0) {
            movieRepository.save(new Movie("The Matrix", LocalDateTime.now().plusDays(3)));
            System.out.println("🎬 Auto-generated 'The Matrix' into the database!");
        }

        // 2. Auto-generate 20 empty seats (Seat-1 to Seat-20)
        if (seatRepository.count() == 0) {
            for (int i = 1; i <= 20; i++) {
                seatRepository.save(new Seat("Seat-" + i));
            }
            System.out.println("💺 Auto-generated 20 empty seats into the database!");
        }
    }
}