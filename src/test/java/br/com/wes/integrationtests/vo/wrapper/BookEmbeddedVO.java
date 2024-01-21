package br.com.wes.integrationtests.vo.wrapper;

import br.com.wes.integrationtests.vo.BookVOIT;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class BookEmbeddedVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6542418013773642085L;

    @JsonProperty("bookVOList")
    private List<BookVOIT> books;

    public BookEmbeddedVO() {
    }

    public List<BookVOIT> getBooks() {
        return books;
    }

    public void setBooks(List<BookVOIT> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookEmbeddedVO that = (BookEmbeddedVO) o;
        return Objects.equals(books, that.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(books);
    }
}