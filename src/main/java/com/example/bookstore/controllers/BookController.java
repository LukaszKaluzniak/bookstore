package com.example.bookstore.controllers;

import com.example.bookstore.models.Book;
import com.example.bookstore.models.Book2;
import com.example.bookstore.repositories.UserRepository;
import com.example.bookstore.services.BookService;
import com.example.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/books")
    public String getBooks(Model model) {

        List<Book> bookList = bookService.getBooks();
        bookList.removeIf(Book::isHidden);

        List<String> coverList = new ArrayList<>();

        for (Book book : bookList) {
            if (book.getCover() != null) {
                coverList.add(Base64.getEncoder().encodeToString(book.getCover()));
            } else {
                coverList.add("");
            }
        }

        model.addAttribute("books", bookList);
        model.addAttribute("covers", coverList);

        return "book";
    }

    @GetMapping("/admin/books")
    public String getBooksAdmin(Model model) {

        List<Book> bookList = bookService.getBooks();
        List<String> coverList = new ArrayList<>();

        for (Book book : bookList) {
            if (book.getCover() != null) {
                coverList.add(Base64.getEncoder().encodeToString(book.getCover()));
            } else {
                coverList.add("");
            }
        }

        model.addAttribute("books", bookList);
        model.addAttribute("covers", coverList);

        return "admin/book";
    }

    @RequestMapping(value="/admin/books/coverAndFile/{bookId}", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String coverAndFile(@RequestParam("cover") MultipartFile cover, @RequestParam("file") MultipartFile file, @PathVariable Integer bookId) throws IOException {
        Book book = bookService.findById(bookId).orElse(null);

        if (book != null) {
            byte[] empty = new byte[0];
            if (!Arrays.equals(book.getCover(), empty) && !Arrays.equals(book.getFile(), empty)) {
                book.setHidden(false);
            }

            book.setCover(cover.getBytes());
            book.setFile(file.getBytes());
            bookService.update(book);
        }

        return "redirect:/admin/books";
    }

    @PostMapping("/admin/books/addNew")
    public RedirectView addNew(Book book, RedirectAttributes redirectAttributes) {
        String message = bookService.add(book);
        RedirectView redirectView = new RedirectView("/admin/books", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping("/admin/books/findById")
    @ResponseBody
    public Optional<Book> findById(Integer id) {
        return bookService.findById(id);
    }

    @RequestMapping(value="/admin/books/update", method={RequestMethod.PUT, RequestMethod.GET})
    public RedirectView update(Book book, RedirectAttributes redirectAttributes) {
        String message = bookService.update(book);
        RedirectView redirectView = new RedirectView("/admin/books", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;

    }

    @RequestMapping(value="/admin/books/delete", method={RequestMethod.DELETE, RequestMethod.GET})
    public RedirectView delete(Integer id, RedirectAttributes redirectAttributes) {
        String message = bookService.delete(id);
        RedirectView redirectView = new RedirectView("/admin/books", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping("/books/addToCart/{bookId}")
    public RedirectView addToCart(@PathVariable Integer bookId, RedirectAttributes redirectAttributes) {
        String message = bookService.addToCart(bookId);
        RedirectView redirectView = new RedirectView("/books", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping("/books/removeFromCartCart/{bookId}")
    public RedirectView removeFromCartCart(@PathVariable Integer bookId, RedirectAttributes redirectAttributes) {
        String message = bookService.removeFromCart(bookId);
        RedirectView redirectView = new RedirectView("/cart", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping("/books/removeFromCartBooks/{bookId}")
    public RedirectView removeFromCartBooks(@PathVariable Integer bookId, RedirectAttributes redirectAttributes) {
        String message = bookService.removeFromCart(bookId);
        RedirectView redirectView = new RedirectView("/books", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @GetMapping("/admin/books/download/{bookId}")
    public void downloadfile(@PathVariable Integer bookId, HttpServletResponse response) throws IOException {
        Book2 book2 = bookService.findById2(bookId).orElse(null);

        if (book2 != null && book2.getFile() != null) {
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
