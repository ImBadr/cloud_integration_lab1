package com.example.demo.Models;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;

@Entity
@EnableAutoConfiguration
public class Movie {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    private String author;

    @Transient
    private Actor[] actors;

    protected Movie() {}

    public Movie(String name, String author) {
        this.name = name;
        this.author = author;
    }

    public Movie(String name, String author, Actor[] actors) {
        this.name = name;
        this.author = author;
        this.actors = actors;
    }

    public Movie(Long id, String name, String author, Actor[] actors) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.actors = actors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Actor[] getActors() {
        return actors;
    }

    public void setActors(Actor[] actors) {
        this.actors = actors;
    }

}