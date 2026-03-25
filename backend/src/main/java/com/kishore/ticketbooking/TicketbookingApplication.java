package com.kishore.ticketbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // Add this!

@SpringBootApplication
@EnableScheduling // And add this!
public class TicketbookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketbookingApplication.class, args);
    }
}