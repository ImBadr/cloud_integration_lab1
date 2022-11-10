package com.example.demo.controller;

import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping("/movies")
    public @ResponseBody Iterable<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> findAMovie(@PathVariable int id) {
        Optional<Movie> movie = movieRepository.findById((long) id);
        return movie.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/movies")
    public Movie createMovie(@Validated @RequestBody Movie movie) {
        return movieRepository.save(movie);
    }

    @PutMapping("/movies/{id}")
    public Movie updateMovie(@RequestBody Movie newMovie, @PathVariable int id) {
        return movieRepository.findById((long) id)
                .map(movies -> {
                    movies.setAuthor(newMovie.getAuthor());
                    movies.setName(newMovie.getName());
                    return movieRepository.save(movies);
                })
                .orElseGet(() -> {
                    newMovie.setId((long) id);
                    return movieRepository.save(newMovie);
                });
    }

    @DeleteMapping("/movies/{id}")
    public Optional<Movie> deleteMovie(@PathVariable int id){
        Optional<Movie> deletedMovie = movieRepository.findById((long) id);
        movieRepository.deleteById((long) id);
        return deletedMovie;
    }

}