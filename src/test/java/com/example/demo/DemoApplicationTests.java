package com.example.demo;

import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
class DemoApplicationTests {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private MovieRepository movieRepository;

    private static final String BASE_URL = "/movies";

    final Movie movie1 = new Movie("Fast & Furious", "Badr TADJER");
    final Movie movie2 = new Movie("Fast & Furious 2", "Cedric BOIRARD");
    final Movie movie3 = new Movie("Fast & Furious 3", "Clement TOLOIS");

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
        movieRepository.deleteAll();
    }

    @Test
    public void check_get_all_movies_check_return_all_movies() throws Exception {
        movieRepository.saveAll(List.of(movie1, movie2, movie3));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void check_get_movie_check_return_movie() throws Exception {
        Movie movie = movieRepository.save(movie1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{id}", movie.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movie.getId()))
                .andExpect(jsonPath("$.name").value("Fast & Furious"))
                .andExpect(jsonPath("$.author").value("Badr TADJER"));
    }

    @Test
    public void check_create_movie_check_return_a_movie() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL)
                        .content(
                                "{\"name\": \"Interstellar\"," +
                                "\"author\": \"Pierre-Louis SERGENT\"" + "}"
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Interstellar"))
                .andExpect(jsonPath("$.author").value("Pierre-Louis SERGENT"));
    }

    @Test
    public void check_update_movie_check_return_a_movie() throws Exception {
        Movie movie = movieRepository.save(movie1);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/" + movie.getId(), 1)
                        .content(
                                "{\"name\": \"Charlie and the chocolate factory\"," +
                                "\"author\": \"Jean-Charles\"" + "}"
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movie.getId()))
                .andExpect(jsonPath("$.name").value("Charlie and the chocolate factory"))
                .andExpect(jsonPath("$.author").value("Jean-Charles"));
    }

    @Test
    public void check_delete_movie_check_return_200() throws Exception {
        Movie movie = movieRepository.save(movie1);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(BASE_URL + "/" + movie.getId(), 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
