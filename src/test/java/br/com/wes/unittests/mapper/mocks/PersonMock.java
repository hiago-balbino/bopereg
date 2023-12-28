package br.com.wes.unittests.mapper.mocks;

import br.com.wes.model.Person;
import br.com.wes.vo.v1.PersonVO;

import java.util.ArrayList;
import java.util.List;

public class PersonMock {

    public Person mockEntity() {
        return mockEntity(0);
    }

    public PersonVO mockVO() {
        return mockVO(0);
    }

    public List<Person> mockEntities() {
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            people.add(mockEntity(i));
        }
        return people;
    }

    public List<PersonVO> mockVOs() {
        List<PersonVO> peopleVO = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            peopleVO.add(mockVO(i));
        }
        return peopleVO;
    }

    public Person mockEntity(Integer number) {
        Person person = new Person();
        person.setId(number.longValue());
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setAddress("Address Test" + number);
        person.setGender((number % 2) == 0 ? "Male" : "Female");
        return person;
    }

    public PersonVO mockVO(Integer number) {
        PersonVO person = new PersonVO();
        person.setKey(number.longValue());
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setAddress("Address Test" + number);
        person.setGender((number % 2) == 0 ? "Male" : "Female");
        return person;
    }
}
