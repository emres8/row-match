package com.emres.model;


import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private Integer level;
    private Integer coin;
    private String password;


    public static enum Role {
        USER,
        ADMIN
    }
    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    Audit audit;
    public User(Long id, String name, String email, Integer level, Integer coin, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.level = level;
        this.coin = coin;
        this.password = password;
        this.role = role;
        this.audit = new Audit();
    }

    public User(String name, String email, Integer level, Integer coin, String password, Role role) {

        this.name = name;
        this.email = email;
        this.level = level;
        this.coin = coin;
        this.password = password;
        this.role = role;
        this.audit = new Audit();
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", level=" + level +
                ", coin=" + coin +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", audit=" + audit +
                '}';
    }
}
