package br.com.wes.util.mock;

import br.com.wes.model.Book;
import br.com.wes.vo.v1.BookVO;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

public class BookMock {

    public static final LocalDate DEFAULT_DATE = LocalDate.of(2023, Month.DECEMBER, 1);

    public Book mockBookEntity() {
        return mockBookEntity(0);
    }

    public BookVO mockBookVO() {
        return mockBookVO(0);
    }

    public List<Book> mockBookEntities() {
        return Stream.of(0, 1, 2).map(this::mockBookEntity).toList();
    }

    public List<BookVO> mockBookVOs() {
        return Stream.of(0, 1, 2).map(this::mockBookVO).toList();
    }

    public Book mockBookEntity(Integer number) {
        Book book = new Book();
        book.setId(number.longValue());
        book.setAuthor("Author" + number);
        book.setTitle("Title" + number);
        book.setPrice(number > 0 ? (double) (2L * number) : (double) 1L);
        book.setLaunchDate(DEFAULT_DATE);
        return book;
    }

    public BookVO mockBookVO(Integer number) {
        BookVO bookVO = new BookVO();
        bookVO.setKey(number.longValue());
        bookVO.setAuthor("Author" + number);
        bookVO.setTitle("Title" + number);
        bookVO.setPrice(number > 0 ? (double) (2L * number) : (double) 1L);
        bookVO.setLaunchDate(DEFAULT_DATE);
        return bookVO;
    }
}
