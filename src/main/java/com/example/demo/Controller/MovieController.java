package com.example.demo.Controller;

import com.example.demo.Models.Actor;
import com.example.demo.Models.Movie;
import com.example.demo.Repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
        if (movie.isEmpty())
            return ResponseEntity.notFound().build();

        RestTemplate restTemplate = new RestTemplate();
        String actorResourceUrl = "http://localhost:8000/movies";
        Actor[] actors = restTemplate.getForObject(actorResourceUrl + "/" + id + "/actors", Actor[].class);

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