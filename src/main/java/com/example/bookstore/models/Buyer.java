package com.example.bookstore.models;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Buyer {

  private String email;
  private String firstName;
  private String lastName;
  private String language;

}
