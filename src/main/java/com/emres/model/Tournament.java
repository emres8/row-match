package com.emres.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Status status;
    public static enum Status {
        ACTIVE,
        FINISHED
    }

    // TODO: Scheduler for start finish of tournaments
/*
    private LocalDateTime startTime;

    private LocalDateTime endTime;
*/
    public Tournament() {
    }

    public Tournament(String name) {
        this.name = name;
    }

    public Tournament(Long id, Status status,  String name) {
        this.id = id;
        this.name = name;
        this.status = status;

    }

    public Tournament(String name, Status status) {
        this.name = name;
        this.status = status;

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



    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}