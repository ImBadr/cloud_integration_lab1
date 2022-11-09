package com.example.demo.controller;

import com.example.demo.models.Actor;
import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
class MovieController {

    private final MovieRepository movieRepository;
    final private String URL_ACTORS = "http://fastapi_lab2:8000/movies";
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public MovieController(MovieRepository movieRepository, CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.movieRepository = movieRepository;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @GetMapping("/movies")
    public @ResponseBody Iterable<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> findAMovie(@PathVariable int id) {
        Optional<Movie> movie = movieRepository.findById((long) id);
        if (movie.isEmpty())
            return ResponseEntity.notFound().build();

        RestTemplate restTemplate = new RestTemplate();
        List<Actor> actors = circuitBreakerFactory.create("findAMovie").run(
                () -> List.of(
                        Objects.requireNonNull(
                                restTemplate.getForObject(URL_ACTORS + "/" + id + "/actors", Actor[].class)
                        )
                ), throwable -> List.of()
        );

        return ResponseEntity.ok().body(new Movie(movie.get().getId(), movie.get().getName(), movie.get().getAuthor(), actors));
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