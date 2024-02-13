package br.com.wes.service;

import br.com.wes.controller.BookController;
import br.com.wes.exception.RequiredObjectIsNullException;
import br.com.wes.exception.ResourceNotFoundException;
import br.com.wes.mapper.ObjectModelMapper;
import br.com.wes.model.Book;
import br.com.wes.repository.BookRepository;
import br.com.wes.vo.v1.BookVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ObjectModelMapper mapper;
    private final PagedResourcesAssembler<BookVO> assembler;

    public BookVO create(BookVO book) {
        log.info("Creating a new book");
        if (Objects.isNull(book)) throw new RequiredObjectIsNullException();

        var bookToSave = mapper.map(book, Book.class);
        var bookSaved = mapper.map(bookRepository.save(bookToSave), BookVO.class);

        return addBookLinkAndReturn(bookSaved);
    }

    public BookVO update(BookVO book) {
        log.info("Updating a book");
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
        log.info("Deleting a book");

        var bookToDelete = bookRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        bookRepository.delete(bookToDelete);
    }

    public BookVO findById(Long id) {
        log.info("Finding one book");

        var book = bookRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return addBookLinkAndReturn(mapper.map(book, BookVO.class));
    }

    public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {
        log.info("Finding all books");

        Page<BookVO> books = bookRepository
                .findAll(pageable)
                .map(b -> mapper.map(b, BookVO.class));
        books.forEach(this::addBookLinkAndReturn);

        Link link = linkTo(
                methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
                .withSelfRel();
        return assembler.toModel(books, link);
    }

    public PagedModel<EntityModel<BookVO>> findBooksByTitle(String title, Pageable pageable) {
        log.info("Finding books by title");

        Page<BookVO> books = bookRepository
                .findBooksByTitle(title, pageable)
                .map(b -> mapper.map(b, BookVO.class));
        books.forEach(this::addBookLinkAndReturn);

        Link link = linkTo(
                methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
                .withSelfRel();
        return assembler.toModel(books, link);
    }

    private BookVO addBookLinkAndReturn(BookVO bookVO) {
        return bookVO.add(linkTo(methodOn(BookController.class).findById(bookVO.getKey())).withSelfRel());
    }
}
