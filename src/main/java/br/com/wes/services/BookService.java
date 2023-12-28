package br.com.wes.services;

import br.com.wes.controllers.BookController;
import br.com.wes.exceptions.RequiredObjectIsNullException;
import br.com.wes.exceptions.ResourceNotFoundException;
import br.com.wes.mapper.ObjectModelMapper;
import br.com.wes.model.Book;
import br.com.wes.repositories.BookRepository;
import br.com.wes.vo.v1.BookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class BookService {

    private final Logger logger = Logger.getLogger(BookService.class.getName());

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ObjectModelMapper mapper;

    public BookVO create(BookVO book) {
        logger.info("Creating a new book!");
        if (Objects.isNull(book)) throw new RequiredObjectIsNullException();

        var bookToSave = mapper.map(book, Book.class);
        var bookSaved = mapper.map(bookRepository.save(bookToSave), BookVO.class);

        return addBookLinkAndReturn(bookSaved);
    }

    public BookVO update(BookVO book) {
        logger.info("Updating a book!");
        if (Objects.isNull(book)) throw new RequiredObjectIsNullException();

        var bookToUpdate = bookRepository.findById(book.getKey())
                .orElseThrow(ResourceNotFoundException::new);
        bookToUpdate.setAuthor(book.getAuthor());
        bookToUpdate.setTitle(book.getTitle());
        bookToUpdate.setPrice(book.getPrice());
        bookToUpdate.setLaunchDate(book.getLaunchDate());

        var bookUpdated = mapper.map(bookRepository.save(bookToUpdate), BookVO.class);
        return addBookLinkAndReturn(bookUpdated);
    }

    public void delete(Long id) {
        logger.info("Deleting a book!");

        var bookToDelete = bookRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        bookRepository.delete(bookToDelete);
    }

    public BookVO findById(Long id) {
        logger.info("Finding one book!");

        var book = bookRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return addBookLinkAndReturn(mapper.map(book, BookVO.class));
    }

    public List<BookVO> findAll() {
        logger.info("Finding all books!");

        var books = mapper.map(bookRepository.findAll(), BookVO.class);
        books.forEach(this::addBookLinkAndReturn);

        return books;
    }

    private BookVO addBookLinkAndReturn(BookVO bookVO) {
        return bookVO.add(linkTo(methodOn(BookController.class).findById(bookVO.getKey())).withSelfRel());
    }
}
