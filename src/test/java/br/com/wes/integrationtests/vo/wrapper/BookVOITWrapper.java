package br.com.wes.integrationtests.vo.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class BookVOITWrapper implements Serializable {

    @Serial
    private static final long serialVersionUID = 6187805632396119992L;

    @JsonProperty("_embedded")
    private BookEmbeddedVO embedded;

    public BookVOITWrapper() {
    }

    public BookEmbeddedVO getEmbedded() {
        return embedded;
    }

    public void setEmbedded(BookEmbeddedVO embedded) {
        this.embedded = embedded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookVOITWrapper that = (BookVOITWrapper) o;
        return Objects.equals(embedded, that.embedded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(embedded);
    }
}
