package com.emres.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String lastName;
    private Integer level;
    private Integer coin;

    public User(Integer id, String name, String lastName, Integer level, Integer coin) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.level = level;
        this.coin = coin;
    }

    public User(String name, String lastName, Integer level, Integer coin) {

        this.name = name;
        this.lastName = lastName;
        this.level = level;
        this.coin = coin;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", level=" + level +
                ", coin=" + coin +
                '}';
    }
}
