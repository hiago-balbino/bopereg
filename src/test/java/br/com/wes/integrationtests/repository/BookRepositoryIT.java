package br.com.wes.integrationtests.repository;

import br.com.wes.integrationtests.AbstractIT;
import br.com.wes.model.Book;
import br.com.wes.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryIT extends AbstractIT {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Should return all books with success")
    public void shouldReturnAllBooksWithSuccess() {
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "title"));
        List<Book> books = bookRepository.findBooksByTitle("legacy code", pageable).getContent();
        assertFalse(books.isEmpty());

        Book book = books.getFirst();
        assertEquals(1L, book.getId());
        assertEquals("Working effectively with legacy code", book.getTitle());
        assertEquals("Michael C. Feathers", book.getAuthor());
        assertEquals(49.00, book.getPrice());
        assertEquals(LocalDate.of(2017, 11, 29), book.getLaunchDate());
    }

    @Test
    @DisplayName("Should return empty result when not found books by title")
    public void shouldReturnEmptyResultWhenNotFoundBooksByTitle() {
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "title"));
        List<Book> books = bookRepository.findBooksByTitle("Unknown", pageable).getContent();
        assertTrue(books.isEmpty());
    }
}
