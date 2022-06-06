package com.example.bookstore.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="books2")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book2 {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="book2_id")
    private Integer id;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private String authors;

    @Column(length=25, nullable=false)
    private String isbn;

    @Column(nullable=false)
    private String language;

    @Column(nullable=false, name="year_of_publication")
    private Integer yearOfPublication;

    @Column(nullable=false)
    private String publisher;

    @Column(nullable=false, name="number_of_pages")
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

}
