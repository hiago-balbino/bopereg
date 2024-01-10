package br.com.wes.integrationtests.repository;

import br.com.wes.integrationtests.AbstractIntegrationTest;
import br.com.wes.repository.PersonRepository;
import br.com.wes.util.mock.PersonMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class PersonRepositoryIntegrationTest extends AbstractIntegrationTest {

    private PersonMock input;

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        input = new PersonMock();
    }

    @Test
    public void shouldMapEntityToSavePersonOnDatabase() {
        var person = input.mockEntity();

        var personPersisted = personRepository.save(person);

        assertTrue(personPersisted.getId() > 0);
        assertEquals(person, personPersisted);
    }

    @Test
    public void shouldMapEntityToUpdatePersonOnDatabase() {
        var person = input.mockEntity();

        var personPersisted = personRepository.save(person);
        assertTrue(personPersisted.getId() > 0);
        assertEquals(person, personPersisted);

        personPersisted.setFirstName("UpdatedFirstName");
        var personUpdated = personRepository.save(personPersisted);
        assertEquals("UpdatedFirstName", personUpdated.getFirstName());
    }

    @Test
    public void shouldMapEntityToFindPersonByIdOnDatabase() {
        var personPersisted = personRepository.save(input.mockEntity());

        var personSaved = personRepository.findById(personPersisted.getId());

        assertTrue(personSaved.isPresent());
        assertEquals(personPersisted, personSaved.get());
    }

    @Test
    public void shouldMapEntityToFindAllPeopleOnDatabase() {
        var people = personRepository.findAll();

        assertFalse(people.isEmpty());
    }

    @Test
    public void shouldMapEntityToDeletePersonOnDatabase() {
        var personPersisted = personRepository.save(input.mockEntity());

        personRepository.delete(personPersisted);
        var personDeleted = personRepository.findById(personPersisted.getId());

        assertFalse(personDeleted.isPresent());
    }
}
