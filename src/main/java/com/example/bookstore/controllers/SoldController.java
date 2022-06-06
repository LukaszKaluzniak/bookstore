package com.example.bookstore.controllers;

import com.example.bookstore.models.Book2;
import com.example.bookstore.services.BookService;
import com.example.bookstore.services.SoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Controller
public class SoldController {

    @Autowired
    private SoldService soldService;

    @Autowired
    private BookService bookService;

    @RequestMapping("/mybooks")
    public String getMyBooks(Model model) {
        Set<Book2> soldBooks = soldService.getSoldBooks();
        List<String> coverList = new ArrayList<>();

        for (Book2 book2 : soldBooks) {
            if (book2.getCover() != null) {
                coverList.add(Base64.getEncoder().encodeToString(book2.getCover()));
            } else {
                coverList.add("");
            }
        }

        model.addAttribute("mybooks", soldBooks);
        model.addAttribute("covers", coverList);

        return "mybook";
    }

    @GetMapping("/mybooks/download/{bookId}")
    public void downloadfile(@PathVariable Integer bookId, HttpServletResponse response) throws IOException {
        Book2 book2 = bookService.findById2(bookId).orElse(null);
        Set<Book2> soldBooks = soldService.getSoldBooks();

        if (book2 != null && soldBooks.contains(book2)) {
            response.setContentType("application/pdf");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + book2.getTitle() + ".pdf";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream servletOutputStream = response.getOutputStream();
            servletOutputStream.write(book2.getFile());
            servletOutputStream.close();
        }
    }

}
