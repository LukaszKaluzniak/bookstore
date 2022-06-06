package com.example.bookstore.services;

import com.example.bookstore.models.Book;
import com.example.bookstore.models.Book2;
import com.example.bookstore.models.Order;
import com.example.bookstore.models.User;
import com.example.bookstore.repositories.Book2Repository;
import com.example.bookstore.repositories.BookRepository;
import com.example.bookstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private Book2Repository book2Repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    public String add(Book book) {
        book.setTitle(book.getTitle().trim());
        book.setAuthors(book.getAuthors().trim());
        book.setIsbn(book.getIsbn().trim());
        book.setLanguage(book.getLanguage().trim());
        book.setPublisher(book.getPublisher().trim());
        book.setDescription(book.getDescription().trim());

        if (book.getTitle().length() < 3 || book.getTitle().length() > 255) {
            return "Tytuł musi zawierać od 3 do 255 znaków";
        }
        if (book.getAuthors().length() < 3 || book.getAuthors().length() > 255) {
            return "Spis autorów musi zawierać od 3 do 255 znaków";
        }
        if (book.getIsbn().length() < 3 || book.getIsbn().length() > 255) {
            return "Numer ISBN musi zawierać od 3 do 255 znaków";
        }
        if (book.getLanguage().length() < 3 || book.getLanguage().length() > 255) {
            return "Język musi zawierać od 3 do 255 znaków";
        }
        if (book.getPublisher().length() < 3 || book.getPublisher().length() > 255) {
            return "Nazwa wydawcy musi zawierać od 3 do 255 znaków";
        }
        if (book.getDescription().length() > 2000) {
            return "Opis nie może być dłuższy niż 2000 znaków";
        }

        Book2 book2 = new Book2(
                book.getId(),
                book.getTitle(),
                book.getAuthors(),
                book.getIsbn(),
                book.getLanguage(),
                book.getYearOfPublication(),
                book.getPublisher(),
                book.getNumberOfPages(),
                book.getDescription(),
                book.getPrice(),
                book.getCover(),
                book.getFile()
        );

        bookRepository.save(book);
        book2Repository.save(book2);

        return "Pomyślnie dodano książkę";
    }

    public String update(Book book) {
        book.setTitle(book.getTitle().trim());
        book.setAuthors(book.getAuthors().trim());
        book.setIsbn(book.getIsbn().trim());
        book.setLanguage(book.getLanguage().trim());
        book.setPublisher(book.getPublisher().trim());
        book.setDescription(book.getDescription().trim());

        if (book.getTitle().length() < 3 || book.getTitle().length() > 255) {
            return "Tytuł musi zawierać od 3 do 255 znaków";
        }
        if (book.getAuthors().length() < 3 || book.getAuthors().length() > 255) {
            return "Spis autorów musi zawierać od 3 do 255 znaków";
        }
        if (book.getIsbn().length() < 10 || book.getIsbn().length() > 25) {
            return "Numer ISBN (łącznie z myślnikami) musi zawierać 10 lub 25 znaków";
        }
        if (book.getLanguage().length() < 3 || book.getLanguage().length() > 255) {
            return "Język musi zawierać od 3 do 255 znaków";
        }
        if (book.getPublisher().length() < 3 || book.getPublisher().length() > 255) {
            return "Nazwa wydawcy musi zawierać od 3 do 255 znaków";
        }
        if (book.getDescription().length() > 2000) {
            return "Opis nie może być dłuższy niż 2000 znaków";
        }

        Book oldBook = bookRepository.getById(book.getId());
        byte[] empty = new byte[0];
        if (Arrays.equals(book.getCover(), empty) && oldBook.getCover() != book.getCover()) {
            book.setCover(oldBook.getCover());
        }
        if (Arrays.equals(book.getFile(), empty) && oldBook.getFile() != book.getFile()) {
            book.setFile(oldBook.getFile());
        }

        Book2 book2 = book2Repository.getById(book.getId());
        book2.setId(book.getId());
        book2.setTitle(book.getTitle());
        book2.setAuthors(book.getAuthors());
        book2.setIsbn(book.getIsbn());
        book2.setLanguage(book.getLanguage());
        book2.setYearOfPublication(book.getYearOfPublication());
        book2.setPublisher(book.getPublisher());
        book2.setNumberOfPages(book.getNumberOfPages());
        book2.setDescription(book.getDescription());
        book2.setPrice(book.getPrice());
        book2.setCover(book.getCover());
        book2.setFile(book.getFile());

        bookRepository.save(book);
        book2Repository.save(book2);

        return "Pomyślnie zaktualizowano książkę";
    }

    public Optional<Book> findById(Integer id) {
        return bookRepository.findById(id);
    }

    public Optional<Book2> findById2(Integer id) {
        return book2Repository.findById(id);
    }

    public String delete(Integer id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            removeFromAllCarts(id);
            bookRepository.deleteById(id);
            return "Książka została usunięta";
        }
        return "Wskazana książka nie istnieje";
    }

    public String addToCart(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        Book2 book2 = book2Repository.findById(bookId).orElse(null);
        User user = userService.getCurrentLoggedInUser();
        List<Order> userOrders = user.getOrders();

        for (Order order : userOrders) {
            if (order.getBooks2().contains(book2)) {
                if (!order.getStatus().equals("Anulowane")) {
                    return "Nie można dodać do koszyka już kupionej lub zamówionej książki!";
                }
            }
        }

        if (book != null && !book.isHidden()) {
            Set<Book2> userBooks2 = user.getBooks2();

            if (userBooks2.contains(book2)) {
                return "Wybrana książka jest już w koszyku";
            }

            userBooks2.add(book2);
            user.setBooks2(userBooks2);

            userRepository.save(user);

            return "Dodano książkę do koszyka";
        }

        return "Nie udało się dodać książki do koszyka";
    }

    public String removeFromCart(Integer bookId) {

        User user = userService.getCurrentLoggedInUser();

        Set<Book2> userBooks2 = user.getBooks2();
        boolean removedSomething = userBooks2.removeIf(x -> x.getId().equals(bookId));

        if (removedSomething) {
            userRepository.save(user);
            return "Usunięto książkę z koszyka";
        }
        return "Wskazanej książki nie ma w koszyku";

    }

    public void removeFromAllCarts(Integer bookId) {

        List<User> users = userRepository.findAll();

        for (User user: users) {
            Set<Book2> userBooks2 = user.getBooks2();
            userBooks2.removeIf(x -> x.getId().equals(bookId));

            userRepository.save(user);
        }
        
    }

    public String removeAllFromCart() {
    User user = userService.getCurrentLoggedInUser();

        Set<Book2> userBooks2 = user.getBooks2();
        userBooks2.clear();

        userRepository.save(user);

        return "Koszyk został opróżniony";
    }

}
