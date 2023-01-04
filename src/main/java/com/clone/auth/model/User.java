package com.clone.auth.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @Column(unique = true)
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Column(length = 1000)
    @Size(min = 6, max = 200)
    private String password;

    public User(String name, String surname, String username,String email,String password){
        setId((new Long(4)));
        setName(name);
        setSurname(surname);
        setUsername(username);
        setEmail(email);
        setPassword(password);
    }
}