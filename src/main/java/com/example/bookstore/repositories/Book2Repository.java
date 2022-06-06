package com.example.bookstore.repositories;

import com.example.bookstore.models.Book2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2Repository extends JpaRepository<Book2, Integer> {

}
