package com.example.bookstore.models;

import com.example.bookstore.security.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer id;

    @Column(nullable=false, length=100, name="first_name")
    private String firstName;

    @Column(nullable=false, length=100, name="last_name")
    private String lastName;

    @Column(nullable=false, unique=true, length=100, name="username")
    private String username;

    @Column(nullable=false)
    private String password;

    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
            @JoinTable(
                    name = "users_roles",
                    joinColumns={@JoinColumn(name="user_id")},
                    inverseJoinColumns={@JoinColumn(name="role_id")}
            )
    Set<Role> roles=new HashSet<>();

    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    @JoinTable(
            name = "users_books2",
            joinColumns={@JoinColumn(name="user_id")},
            inverseJoinColumns={@JoinColumn(name="book2_id")}
    )
    Set<Book2> books2=new HashSet<>();

    @OneToMany
    @JoinColumn(name="user_id")
    List<Order> orders=new ArrayList<>();

}
