package br.com.wes.util.mock;

import br.com.wes.model.Book;
import br.com.wes.vo.v1.BookVO;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class BookMock {

    public static final LocalDate DEFAULT_DATE = LocalDate.of(2023, Month.DECEMBER, 1);

    public Book mockEntity() {
        return mockEntity(0);
    }

    public BookVO mockVO() {
        return mockVO(0);
    }

    public List<Book> mockEntities() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            books.add(mockEntity(i));
        }
        return books;
    }

    public List<BookVO> mockVOs() {
        List<BookVO> booksVO = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            booksVO.add(mockVO(i));
        }
        return booksVO;
    }

    public Book mockEntity(Integer number) {
        Book book = new Book();
        book.setId(number.longValue());
        book.setAuthor("Author" + number);
        book.setTitle("Title" + number);
        book.setPrice((double) (2L * number));
        book.setLaunchDate(DEFAULT_DATE);
        return book;
    }

    public BookVO mockVO(Integer number) {
        BookVO bookVO = new BookVO();
        bookVO.setKey(number.longValue());
        bookVO.setAuthor("Author" + number);
        bookVO.setTitle("Title" + number);
        bookVO.setPrice((double) (2L * number));
        bookVO.setLaunchDate(DEFAULT_DATE);
        return bookVO;
    }
}
