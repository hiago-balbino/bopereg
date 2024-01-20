package br.com.wes.integrationtests.vo.wrapper;

import br.com.wes.integrationtests.vo.PersonVOIT;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class PersonEmbeddedVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9149702316716371870L;

    @JsonProperty("personVOList")
    private List<PersonVOIT> people;

    public PersonEmbeddedVO() {
    }

    public List<PersonVOIT> getPeople() {
        return people;
    }

    public void setPeople(List<PersonVOIT> people) {
        this.people = people;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonEmbeddedVO that = (PersonEmbeddedVO) o;
        return Objects.equals(people, that.people);
    }

    @Override
    public int hashCode() {
        return Objects.hash(people);
    }
}
