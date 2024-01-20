package br.com.wes.integrationtests.vo.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class PersonVOITWrapper implements Serializable {

    @Serial
    private static final long serialVersionUID = 8132976730051997953L;

    @JsonProperty("_embedded")
    private PersonEmbeddedVO embedded;

    public PersonVOITWrapper() {
    }

    public PersonEmbeddedVO getEmbedded() {
        return embedded;
    }

    public void setEmbedded(PersonEmbeddedVO embedded) {
        this.embedded = embedded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonVOITWrapper that = (PersonVOITWrapper) o;
        return Objects.equals(embedded, that.embedded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(embedded);
    }
}
