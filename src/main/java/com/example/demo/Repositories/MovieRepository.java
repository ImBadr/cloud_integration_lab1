package com.example.demo.Repositories;

import com.example.demo.Models.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie, Long> {}