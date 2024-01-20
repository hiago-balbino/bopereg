package br.com.wes.integrationtests.repository;

import br.com.wes.integrationtests.AbstractIT;
import br.com.wes.model.Person;
import br.com.wes.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PersonRepositoryIT extends AbstractIT {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void shouldFindPeopleByNameWithSuccess() {
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
        List<Person> people = personRepository.findPeopleByName("Ayr", pageable).getContent();
        assertFalse(people.isEmpty());

        Person person = people.getFirst();
        assertEquals(1L, person.getId());
        assertEquals("Ayrton", person.getFirstName());
        assertEquals("Senna", person.getLastName());
        assertEquals("Male", person.getGender());
        assertEquals("São Paulo", person.getAddress());
        assertTrue(person.getEnabled());
    }

    @Test
    public void shouldDisablePersonWithSuccess() {
        personRepository.disablePerson(1L);
        Optional<Person> personOpt = personRepository.findById(1L);
        assertTrue(personOpt.isPresent());

        Person person = personOpt.get();
        assertFalse(person.getEnabled());
    }
}
