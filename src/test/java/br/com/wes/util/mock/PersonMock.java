package br.com.wes.util.mock;

import br.com.wes.model.Person;
import br.com.wes.vo.v1.PersonVO;

import java.util.List;
import java.util.stream.Stream;

public class PersonMock {

    public Person mockPersonEntity() {
        return mockPersonEntity(0);
    }

    public PersonVO mockPersonVO() {
        return mockPersonVO(0);
    }

    public List<Person> mockPersonEntities() {
        return Stream.of(0, 1, 2).map(this::mockPersonEntity).toList();
    }

    public List<PersonVO> mockPersonVOs() {
        return Stream.of(0, 1, 2).map(this::mockPersonVO).toList();
    }

    public Person mockPersonEntity(Integer number) {
        Person person = new Person();
        person.setId(number.longValue());
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setAddress("Address Test" + number);
        person.setGender((number % 2) == 0 ? "Male" : "Female");
        person.setEnabled(Boolean.TRUE);
        return person;
    }

    public PersonVO mockPersonVO(Integer number) {
        PersonVO person = new PersonVO();
        person.setKey(number.longValue());
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setAddress("Address Test" + number);
        person.setGender((number % 2) == 0 ? "Male" : "Female");
        person.setEnabled(Boolean.TRUE);
        return person;
    }
}
