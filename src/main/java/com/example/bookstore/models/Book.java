package com.example.bookstore.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="book_id")
    private Integer id;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private String authors;

    @Column(length=25, nullable=false)
    private String isbn;

    @Column(nullable=false)
    private String language;

    @Column(name="year_of_publication", nullable=false)
    private Integer yearOfPublication;

    @Column(nullable=false)
    private String publisher;

    @Column(name="number_of_pages", nullable=false)
    private Integer numberOfPages;

    @Column(length=2000)
    private String description;

    @Column(nullable=false)
    private Double price;

    @Lob
    @Column(name="book_cover")
    private byte[] cover;

    @Lob
    @Column(name="book_file")
    private byte[] file;

    @Column(nullable = false)
    private boolean hidden = true;

}
