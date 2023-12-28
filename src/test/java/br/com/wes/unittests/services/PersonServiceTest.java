package br.com.wes.unittests.services;

import br.com.wes.exceptions.RequiredObjectIsNullException;
import br.com.wes.exceptions.ResourceNotFoundException;
import br.com.wes.mapper.ObjectModelMapper;
import br.com.wes.model.Person;
import br.com.wes.repositories.PersonRepository;
import br.com.wes.services.PersonService;
import br.com.wes.unittests.mapper.mocks.PersonMock;
import br.com.wes.vo.v1.PersonVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    PersonMock input;
    @InjectMocks
    private PersonService personService;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private ObjectModelMapper mapper;

    @BeforeEach
    public void setUp() {
        input = new PersonMock();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldCreatePersonWithSuccess() {
        PersonVO personVOMock = input.mockVO();
        Person personMock = input.mockEntity();
        when(mapper.map(personVOMock, Person.class)).thenReturn(personMock);

        Person personPersisted = input.mockEntity(1);
        when(personRepository.save(personMock)).thenReturn(personPersisted);

        PersonVO personVOPersisted = input.mockVO(1);
        when(mapper.map(personPersisted, PersonVO.class)).thenReturn(personVOPersisted);

        PersonVO personVO = personService.create(personVOMock);

        assertNotNull(personVO);
        assertNotNull(personVO.getKey());
        assertNotNull(personVO.getLinks());
        assertTrue(personVO.toString().contains("</person/v1/1>;rel=\"self\""));
        assertEquals("First Name Test1", personVO.getFirstName());
        assertEquals("Last Name Test1", personVO.getLastName());
        assertEquals("Female", personVO.getGender());
        assertEquals("Address Test1", personVO.getAddress());
    }

    @Test
    public void shouldThrowExceptionWhenCreatingPersonWithNullObject() {
        RequiredObjectIsNullException exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            personService.create(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldUpdatePersonWithSuccess() {
        PersonVO personVOMock = input.mockVO(1);
        Person personMock = input.mockEntity(1);
        when(personRepository.findById(personVOMock.getKey())).thenReturn(Optional.of(personMock));
        when(personRepository.save(personMock)).thenReturn(personMock);
        when(mapper.map(personMock, PersonVO.class)).thenReturn(personVOMock);

        PersonVO personVO = personService.update(personVOMock);

        assertNotNull(personVO);
        assertNotNull(personVO.getKey());
        assertNotNull(personVO.getLinks());
        assertTrue(personVO.toString().contains("</person/v1/1>;rel=\"self\""));
        assertEquals("First Name Test1", personVO.getFirstName());
        assertEquals("Last Name Test1", personVO.getLastName());
        assertEquals("Female", personVO.getGender());
        assertEquals("Address Test1", personVO.getAddress());
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingPersonWithNullObject() {
        RequiredObjectIsNullException exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            personService.update(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldThrowExceptionWhenTryingUpdatePersonWithNonExistentObject() {
        PersonVO personVOMock = input.mockVO(1);
        when(personRepository.findById(personVOMock.getKey())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            personService.update(personVOMock);
        });

        String expectedMessage = "No records found for this identifier!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldDeletePersonWithSuccess() {
        Person personMock = input.mockEntity(1);
        when(personRepository.findById(personMock.getId())).thenReturn(Optional.of(personMock));

        personService.delete(personMock.getId());

        verify(personRepository, times(1)).delete(personMock);
    }

    @Test
    public void shouldThrowExceptionWhenDeletingNonExistentPerson() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            personService.delete(1L);
        });

        String expectedMessage = "No records found for this identifier!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldFindPersonByIdWithSuccess() {
        Person personMock = input.mockEntity(1);
        when(personRepository.findById(1L)).thenReturn(Optional.of(personMock));

        PersonVO personVOMock = input.mockVO(1);
        when(mapper.map(personMock, PersonVO.class)).thenReturn(personVOMock);

        var person = personService.findById(1L);

        assertNotNull(person);
        assertNotNull(person.getKey());
        assertNotNull(person.getLinks());
        assertTrue(person.toString().contains("</person/v1/1>;rel=\"self\""));
        assertEquals("First Name Test1", person.getFirstName());
        assertEquals("Last Name Test1", person.getLastName());
        assertEquals("Female", person.getGender());
        assertEquals("Address Test1", person.getAddress());
    }

    @Test
    public void shouldThrowExceptionWhenFindingNonExistentPerson() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            personService.findById(1L);
        });

        String expectedMessage = "No records found for this identifier!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldFindAllPersonWithSuccess() {
        List<Person> peopleMock = input.mockEntities();
        when(personRepository.findAll()).thenReturn(peopleMock);

        List<PersonVO> peopleVOMock = input.mockVOs();
        when(mapper.map(peopleMock, PersonVO.class)).thenReturn(peopleVOMock);

        var people = personService.findAll();

        PersonVO firstPerson = people.get(0);
        assertNotNull(firstPerson);
        assertNotNull(firstPerson.getKey());
        assertNotNull(firstPerson.getLinks());
        assertTrue(people.toString().contains("</person/v1/0>;rel=\"self\""));
        assertEquals("First Name Test0", firstPerson.getFirstName());
        assertEquals("Last Name Test0", firstPerson.getLastName());
        assertEquals("Male", firstPerson.getGender());
        assertEquals("Address Test0", firstPerson.getAddress());

        PersonVO secondPerson = people.get(1);
        assertNotNull(secondPerson);
        assertNotNull(secondPerson.getKey());
        assertNotNull(secondPerson.getLinks());
        assertTrue(people.toString().contains("</person/v1/1>;rel=\"self\""));
        assertEquals("First Name Test1", secondPerson.getFirstName());
        assertEquals("Last Name Test1", secondPerson.getLastName());
        assertEquals("Female", secondPerson.getGender());
        assertEquals("Address Test1", secondPerson.getAddress());

        PersonVO thirdPerson = people.get(2);
        assertNotNull(thirdPerson);
        assertNotNull(thirdPerson.getKey());
        assertNotNull(thirdPerson.getLinks());
        assertTrue(people.toString().contains("</person/v1/2>;rel=\"self\""));
        assertEquals("First Name Test2", thirdPerson.getFirstName());
        assertEquals("Last Name Test2", thirdPerson.getLastName());
        assertEquals("Male", thirdPerson.getGender());
        assertEquals("Address Test2", thirdPerson.getAddress());
    }

    @Test
    public void shouldReturnEmptyResultWhenDoesNotHaveAnyPersonSaved() {
        when(personRepository.findAll()).thenReturn(Collections.emptyList());

        List<PersonVO> people = personService.findAll();

        assertTrue(people.isEmpty());
    }
}