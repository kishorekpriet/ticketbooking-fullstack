package com.kishore.ticketbooking.repository;

import com.kishore.ticketbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // This custom method tells Hibernate to write a "SELECT * FROM users WHERE email = ?" query automatically!
    Optional<User> findByEmail(String email);
}