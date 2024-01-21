package br.com.wes.service;

import br.com.wes.controller.BookController;
import br.com.wes.exception.RequiredObjectIsNullException;
import br.com.wes.exception.ResourceNotFoundException;
import br.com.wes.mapper.ObjectModelMapper;
import br.com.wes.model.Book;
import br.com.wes.repository.BookRepository;
import br.com.wes.util.mock.BookMock;
import br.com.wes.vo.v1.BookVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    BookMock input;

    @InjectMocks
    private BookService bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ObjectModelMapper mapper;
    @Mock
    private PagedResourcesAssembler<BookVO> assembler;
    @Mock
    private PagedModel<EntityModel<BookVO>> pagedModel;

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

        String expectedMessage = "It is not allowed to persist a null object";
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

        String expectedMessage = "It is not allowed to persist a null object";
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

        String expectedMessage = "No records found for this identifier";
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

        String expectedMessage = "No records found for this identifier";
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

        String expectedMessage = "No records found for this identifier";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldFindAllBooksWithSuccess() {
        List<Book> books = input.mockEntities();
        Page<Book> pageBooks = new PageImpl<>(books);
        List<BookVO> booksVO = input.mockVOs();
        Page<BookVO> pageBooksVO = new PageImpl<>(booksVO);
        Collection<EntityModel<BookVO>> pagedModelContent = Arrays.asList(
                EntityModel.of(booksVO.get(0)),
                EntityModel.of(booksVO.get(1)),
                EntityModel.of(booksVO.get(2))
        );
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Link link = linkTo(methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        when(bookRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title")))).thenReturn(pageBooks);
        when(mapper.map(books.get(0), BookVO.class)).thenReturn(booksVO.get(0));
        when(mapper.map(books.get(1), BookVO.class)).thenReturn(booksVO.get(1));
        when(mapper.map(books.get(2), BookVO.class)).thenReturn(booksVO.get(2));
        when(assembler.toModel(eq(pageBooksVO), eq(link))).thenReturn(pagedModel);
        when(pagedModel.getContent()).thenReturn(pagedModelContent);
        when(pagedModel.getLinks()).thenReturn(Links.of(link));

        PagedModel<EntityModel<BookVO>> pagedBooks = bookService.findAll(pageable);
        assertEquals("</api/book/v1?page=0&size=10&direction=asc>;rel=\"self\"", pagedBooks.getLinks().toString());

        List<EntityModel<BookVO>> allBooks = pagedBooks.getContent().stream().toList();
        assertFalse(allBooks.isEmpty());

        BookVO firstBook = allBooks.getFirst().getContent();
        assertNotNull(firstBook);
        assertNotNull(firstBook.getKey());
        assertNotNull(firstBook.getLinks());
        assertTrue(firstBook.toString().contains("</api/book/v1/0>;rel=\"self\""));
        assertEquals("Author0", firstBook.getAuthor());
        assertEquals("Title0", firstBook.getTitle());
        assertEquals(1L, firstBook.getPrice());
        assertEquals(BookMock.DEFAULT_DATE, firstBook.getLaunchDate());

        BookVO secondBook = allBooks.get(1).getContent();
        assertNotNull(secondBook);
        assertNotNull(secondBook.getKey());
        assertNotNull(secondBook.getLinks());
        assertTrue(secondBook.toString().contains("</api/book/v1/1>;rel=\"self\""));
        assertEquals("Author1", secondBook.getAuthor());
        assertEquals("Title1", secondBook.getTitle());
        assertEquals(2L, secondBook.getPrice());
        assertEquals(BookMock.DEFAULT_DATE, secondBook.getLaunchDate());

        BookVO thirdBook = allBooks.get(2).getContent();
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
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));

        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(assembler.toModel(eq(new PageImpl<>(Collections.emptyList())), any(Link.class))).thenReturn(pagedModel);
        when(pagedModel.getContent()).thenReturn(Collections.emptyList());
        when(pagedModel.getLinks()).thenReturn(Links.NONE);

        PagedModel<EntityModel<BookVO>> allPeople = bookService.findAll(pageable);

        assertTrue(allPeople.getContent().isEmpty());
        assertTrue(allPeople.getLinks().isEmpty());
    }

    @Test
    public void shouldReturnBooksWithSuccessWhenFindByTitle() {
        List<Book> books = List.of(input.mockEntity());
        Page<Book> pageBook = new PageImpl<>(books);
        List<BookVO> booksVO = List.of(input.mockVO());
        Page<BookVO> pageBooksVO = new PageImpl<>(booksVO);
        Collection<EntityModel<BookVO>> pagedModelContent = List.of(EntityModel.of(booksVO.getFirst()));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Link link = linkTo(methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
        String title = "Title0";

        when(bookRepository.findBooksByTitle(title, pageable)).thenReturn(pageBook);
        when(mapper.map(books.getFirst(), BookVO.class)).thenReturn(booksVO.getFirst());
        when(assembler.toModel(eq(pageBooksVO), eq(link))).thenReturn(pagedModel);
        when(pagedModel.getContent()).thenReturn(pagedModelContent);
        when(pagedModel.getLinks()).thenReturn(Links.of(link));

        PagedModel<EntityModel<BookVO>> pagedBooks = bookService.findBooksByTitle(title, pageable);
        assertEquals("</api/book/v1?page=0&size=10&direction=asc>;rel=\"self\"", pagedBooks.getLinks().toString());

        List<EntityModel<BookVO>> allBooks = pagedBooks.getContent().stream().toList();
        assertFalse(allBooks.isEmpty());

        BookVO book = allBooks.getFirst().getContent();
        assertNotNull(book);
        assertNotNull(book.getKey());
        assertNotNull(book.getLinks());
        assertTrue(book.toString().contains("</api/book/v1/0>;rel=\"self\""));
        assertEquals("Author0", book.getAuthor());
        assertEquals("Title0", book.getTitle());
        assertEquals(1L, book.getPrice());
        assertEquals(BookMock.DEFAULT_DATE, book.getLaunchDate());
    }

    @Test
    public void shouldReturnEmptyResultWhenDoesNotHaveAnyBookWithTitle() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        String title = "Title0";

        when(bookRepository.findBooksByTitle(title, pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(assembler.toModel(eq(new PageImpl<>(Collections.emptyList())), any(Link.class))).thenReturn(pagedModel);
        when(pagedModel.getContent()).thenReturn(Collections.emptyList());
        when(pagedModel.getLinks()).thenReturn(Links.NONE);

        PagedModel<EntityModel<BookVO>> allBooks = bookService.findBooksByTitle(title, pageable);

        assertTrue(allBooks.getContent().isEmpty());
        assertTrue(allBooks.getLinks().isEmpty());
    }
}