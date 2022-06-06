package com.example.bookstore.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="order_id")
    private Integer id;

    @Column(nullable=false)
    private Date date;

    @Column(nullable=false)
    private String status;

    @Column(nullable=false)
    private Double price;

    @Column(name="order_payment_id", length=128, nullable=false)
    private String paymentId;

    @ManyToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    @JoinTable(
            name = "orders_books2",
            joinColumns = {@JoinColumn(name = "order_id")},
            inverseJoinColumns = {@JoinColumn(name = "book2_id")}
    )
    Set<Book2> books2 = new HashSet<>();

}
