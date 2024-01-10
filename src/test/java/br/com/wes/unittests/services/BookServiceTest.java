package br.com.wes.unittests.services;

import br.com.wes.exceptions.RequiredObjectIsNullException;
import br.com.wes.exceptions.ResourceNotFoundException;
import br.com.wes.mapper.ObjectModelMapper;
import br.com.wes.model.Book;
import br.com.wes.repositories.BookRepository;
import br.com.wes.services.BookService;
import br.com.wes.utils.mocks.BookMock;
import br.com.wes.vo.v1.BookVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    BookMock input;

    @InjectMocks
    private BookService bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ObjectModelMapper mapper;

    @BeforeEach
    void setUp() {
        input = new BookMock();
    }

    @Test
    public void shouldCreateBookWithSuccess() {
        BookVO bookVOMock = input.mockVO();
        Book bookMock = input.mockEntity();
        when(mapper.map(bookVOMock, Book.class)).thenReturn(bookMock);

        Book bookPersisted = input.mockEntity(1);
        when(bookRepository.save(bookMock)).thenReturn(bookPersisted);

        BookVO bookVOPersisted = input.mockVO(1);
        when(mapper.map(bookPersisted, BookVO.class)).thenReturn(bookVOPersisted);

        BookVO bookVO = bookService.create(bookVOMock);

        assertNotNull(bookVO);
        assertNotNull(bookVO.getKey());
        assertNotNull(bookVO.getLinks());
        assertTrue(bookVO.toString().contains("</api/book/v1/1>;rel=\"self\""));
        assertEquals("Author1", bookVO.getAuthor());
        assertEquals("Title1", bookVO.getTitle());
        assertEquals(2L, bookVO.getPrice());
        assertEquals(BookMock.DEFAULT_DATE, bookVO.getLaunchDate());
    }

    @Test
    public void shouldThrowExceptionWhenCreatingBookWithNullObject() {
        RequiredObjectIsNullException exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            bookService.create(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldUpdateBookWithSuccess() {
        BookVO bookVOMock = input.mockVO(1);
        Book bookMock = input.mockEntity(1);
        when(bookRepository.findById(bookVOMock.getKey())).thenReturn(Optional.of(bookMock));
        when(bookRepository.save(bookMock)).thenReturn(bookMock);
        when(mapper.map(bookMock, BookVO.class)).thenReturn(bookVOMock);

        BookVO bookVO = bookService.update(bookVOMock);

        assertNotNull(bookVO);
        assertNotNull(bookVO.getKey());
        assertNotNull(bookVO.getLinks());
        assertTrue(bookVO.toString().contains("</api/book/v1/1>;rel=\"self\""));
        assertEquals("Author1", bookVO.getAuthor());
        assertEquals("Title1", bookVO.getTitle());
        assertEquals(2L, bookVO.getPrice());
        assertEquals(BookMock.DEFAULT_DATE, bookVO.getLaunchDate());
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingBookWithNullObject() {
        RequiredObjectIsNullException exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            bookService.update(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldThrowExceptionWhenTryingUpdateBookWithNonExistentObject() {
        BookVO bookVOMock = input.mockVO(1);
        when(bookRepository.findById(bookVOMock.getKey())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.update(bookVOMock);
        });

        String expectedMessage = "No records found for this identifier!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldDeleteBookWithSuccess() {
        Book bookMock = input.mockEntity(1);
        when(bookRepository.findById(bookMock.getId())).thenReturn(Optional.of(bookMock));

        bookService.delete(bookMock.getId());

        verify(bookRepository, times(1)).delete(bookMock);
    }

    @Test
    public void shouldThrowExceptionWhenTryingDeleteNonExistentBook() {
        BookVO bookVOMock = input.mockVO(1);
        when(bookRepository.findById(bookVOMock.getKey())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.delete(bookVOMock.getKey());
        });

        String expectedMessage = "No records found for this identifier!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldReturnBookByIdWithSuccess() {
        BookVO bookVOMock = input.mockVO(2);
        Book bookMock = input.mockEntity(2);
        when(bookRepository.findById(bookVOMock.getKey())).thenReturn(Optional.of(bookMock));
        when(mapper.map(bookMock, BookVO.class)).thenReturn(bookVOMock);

        BookVO bookVO = bookService.findById(bookVOMock.getKey());

        assertNotNull(bookVO);
        assertNotNull(bookVO.getKey());
        assertNotNull(bookVO.getLinks());
        assertTrue(bookVO.toString().contains("</api/book/v1/2>;rel=\"self\""));
        assertEquals("Author2", bookVO.getAuthor());
        assertEquals("Title2", bookVO.getTitle());
        assertEquals(4L, bookVO.getPrice());
        assertEquals(BookMock.DEFAULT_DATE, bookVO.getLaunchDate());
    }

    @Test
    public void shouldThrowExceptionWhenTryingFindNonExistentBook() {
        BookVO bookVOMock = input.mockVO();
        when(bookRepository.findById(bookVOMock.getKey())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.findById(bookVOMock.getKey());
        });

        String expectedMessage = "No records found for this identifier!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldReturnAllBooksWithSuccess() {
        List<Book> booksMock = input.mockEntities();
        when(bookRepository.findAll()).thenReturn(booksMock);

        List<BookVO> booksVOMock = input.mockVOs();
        when(mapper.map(booksMock, BookVO.class)).thenReturn(booksVOMock);

        List<BookVO> books = bookService.findAll();

        BookVO firstBook = books.get(0);
        assertNotNull(firstBook);
        assertNotNull(firstBook.getKey());
        assertNotNull(firstBook.getLinks());
        assertTrue(firstBook.toString().contains("</api/book/v1/0>;rel=\"self\""));
        assertEquals("Author0", firstBook.getAuthor());
        assertEquals("Title0", firstBook.getTitle());
        assertEquals(0L, firstBook.getPrice());
        assertEquals(BookMock.DEFAULT_DATE, firstBook.getLaunchDate());

        BookVO secondBook = books.get(1);
        assertNotNull(secondBook);
        assertNotNull(secondBook.getKey());
        assertNotNull(secondBook.getLinks());
        assertTrue(secondBook.toString().contains("</api/book/v1/1>;rel=\"self\""));
        assertEquals("Author1", secondBook.getAuthor());
        assertEquals("Title1", secondBook.getTitle());
        assertEquals(2L, secondBook.getPrice());
        assertEquals(BookMock.DEFAULT_DATE, secondBook.getLaunchDate());

        BookVO thirdBook = books.get(2);
        assertNotNull(thirdBook);
        assertNotNull(thirdBook.getKey());
        assertNotNull(thirdBook.getLinks());
        assertTrue(thirdBook.toString().contains("</api/book/v1/2>;rel=\"self\""));
        assertEquals("Author2", thirdBook.getAuthor());
        assertEquals("Title2", thirdBook.getTitle());
        assertEquals(4L, thirdBook.getPrice());
        assertEquals(BookMock.DEFAULT_DATE, thirdBook.getLaunchDate());
    }

    @Test
    public void shouldReturnEmptyResultWhenDoesNotHaveAnyBookSaved() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        List<BookVO> books = bookService.findAll();

        assertTrue(books.isEmpty());
    }
}