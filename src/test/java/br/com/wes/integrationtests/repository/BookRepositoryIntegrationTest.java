package br.com.wes.integrationtests.repository;

import br.com.wes.integrationtests.AbstractIntegrationTest;
import br.com.wes.repository.BookRepository;
import br.com.wes.util.mock.BookMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class BookRepositoryIntegrationTest extends AbstractIntegrationTest {

    private BookMock input;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        input = new BookMock();
    }

    @Test
    public void shouldMapEntityToSaveBookOnDatabase() {
        var book = input.mockEntity();

        var bookPersisted = bookRepository.save(book);

        assertTrue(bookPersisted.getId() > 0);
        assertEquals(book, bookPersisted);
    }

    @Test
    public void shouldMapEntityToUpdateBookOnDatabase() {
        var book = input.mockEntity();

        var bookPersisted = bookRepository.save(book);
        assertTrue(bookPersisted.getId() > 0);
        assertEquals(book, bookPersisted);

        bookPersisted.setAuthor("New Author");
        var bookUpdated = bookRepository.save(bookPersisted);
        assertEquals("New Author", bookUpdated.getAuthor());
    }

    @Test
    public void shouldMapEntityToFindBookByIdOnDatabase() {
        var bookPersisted = bookRepository.save(input.mockEntity());

        var bookSaved = bookRepository.findById(bookPersisted.getId());

        assertTrue(bookSaved.isPresent());
        assertEquals(bookPersisted, bookSaved.get());
    }

    @Test
    public void shouldMapEntityToFindAllBooksOnDatabase() {
        var books = bookRepository.findAll();

        assertFalse(books.isEmpty());
    }

    @Test
    public void shouldMapEntityToDeleteBookOnDatabase() {
        var bookPersisted = bookRepository.save(input.mockEntity());

        bookRepository.delete(bookPersisted);
        var bookDeleted = bookRepository.findById(bookPersisted.getId());

        assertFalse(bookDeleted.isPresent());
    }
}
