package com.emres.model;

import jakarta.persistence.*;


@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private Integer level;
    private Integer coin;

    @Embedded
    Audit audit;
    public User(Long id, String name, String email, Integer level, Integer coin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.level = level;
        this.coin = coin;
        this.audit = new Audit();
    }

    public User(String name, String email, Integer level, Integer coin) {

        this.name = name;
        this.email = email;
        this.level = level;
        this.coin = coin;
        this.audit = new Audit();
    }

    public User() {
        this.audit = new Audit();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String lastName) {
        this.email = lastName;
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
                ", email='" + email + '\'' +
                ", level=" + level +
                ", coin=" + coin +
                '}';
    }
}
