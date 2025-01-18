package ru.royal.fileExchanger.entities;


import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * сущность пользователь
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String email;
    @Column
    private boolean emailVerified = false;

    @Column(unique = true)
    private String username;
    @Column
    private String password;
    @Column
    private String dateOfRegistration;
    @Column
    private String verificationToken;
    @Column
    private Timestamp tokenCreationDate;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column
    private Set<Role> userRole = new HashSet<Role>();

    public User() {

    }
    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }



    public Long getId() {
        return id;
    }

    public Set<Role> getUserRole() {
        return userRole;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }


    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Timestamp getTokenCreationDate() {
        return tokenCreationDate;
    }

    public void setTokenCreationDate(Timestamp tokenCreationDate) {
        this.tokenCreationDate = tokenCreationDate;
    }

    public void setUserRole(Set<Role> userRole) {
        this.userRole = userRole;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(String dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", dateOfRegistration='" + dateOfRegistration + '\'' +
                '}';
    }
}
