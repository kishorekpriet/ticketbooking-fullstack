package com.kishore.ticketbooking.controller;

import com.kishore.ticketbooking.entity.Movie;
import com.kishore.ticketbooking.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
// This allows your future React frontend to talk to this backend without security blocks
@CrossOrigin(origins = "*") 
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    // POST: This endpoint saves a new movie to the database
    @PostMapping
    public Movie addMovie(@RequestBody Movie movie) {
        return movieRepository.save(movie);
    }

    // GET: This endpoint retrieves all movies from the database
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
}