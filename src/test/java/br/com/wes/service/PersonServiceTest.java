package br.com.wes.service;

import br.com.wes.exception.RequiredObjectIsNullException;
import br.com.wes.exception.ResourceNotFoundException;
import br.com.wes.mapper.ObjectModelMapper;
import br.com.wes.model.Person;
import br.com.wes.repository.PersonRepository;
import br.com.wes.util.mock.PersonMock;
import br.com.wes.vo.v1.PersonVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    PersonMock input;
    @InjectMocks
    private PersonService personService;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private ObjectModelMapper mapper;
    @Mock
    private PagedResourcesAssembler<PersonVO> assembler;
    @Mock
    private PagedModel<EntityModel<PersonVO>> pagedModel;

    @BeforeEach
    public void setUp() {
        input = new PersonMock();
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
        assertTrue(personVO.toString().contains("</api/person/v1/1>;rel=\"self\""));
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

        String expectedMessage = "It is not allowed to persist a null object";
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
        assertTrue(personVO.toString().contains("</api/person/v1/1>;rel=\"self\""));
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

        String expectedMessage = "It is not allowed to persist a null object";
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

        String expectedMessage = "No records found for this identifier";
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

        String expectedMessage = "No records found for this identifier";
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
        assertTrue(person.toString().contains("</api/person/v1/1>;rel=\"self\""));
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

        String expectedMessage = "No records found for this identifier";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldFindAllPersonWithSuccess() {
        List<Person> people = input.mockEntities();
        Page<Person> pagePeople = new PageImpl<>(people);
        List<PersonVO> peopleVO = input.mockVOs();
        Page<PersonVO> pagePeopleVO = new PageImpl<>(peopleVO);
        Collection<EntityModel<PersonVO>> pagedModelContent = Arrays.asList(
                EntityModel.of(peopleVO.get(0)),
                EntityModel.of(peopleVO.get(1)),
                EntityModel.of(peopleVO.get(2))
        );
        Pageable pageable = PageRequest.of(0, 10);

        when(personRepository.findAll(PageRequest.of(0, 10))).thenReturn(pagePeople);
        when(mapper.map(people.get(0), PersonVO.class)).thenReturn(peopleVO.get(0));
        when(mapper.map(people.get(1), PersonVO.class)).thenReturn(peopleVO.get(1));
        when(mapper.map(people.get(2), PersonVO.class)).thenReturn(peopleVO.get(2));
        when(assembler.toModel(eq(pagePeopleVO), any(Link.class))).thenReturn(pagedModel);
        when(pagedModel.getContent()).thenReturn(pagedModelContent);

        List<EntityModel<PersonVO>> allPeople = personService.findAll(pageable).getContent().stream().toList();
        assertFalse(allPeople.isEmpty());

        PersonVO firstPerson = allPeople.getFirst().getContent();
        assertNotNull(firstPerson);
        assertNotNull(firstPerson.getKey());
        assertNotNull(firstPerson.getLinks());
        assertTrue(firstPerson.toString().contains("</api/person/v1/0>;rel=\"self\""));
        assertEquals("First Name Test0", firstPerson.getFirstName());
        assertEquals("Last Name Test0", firstPerson.getLastName());
        assertEquals("Male", firstPerson.getGender());
        assertEquals("Address Test0", firstPerson.getAddress());

        PersonVO secondPerson = allPeople.get(1).getContent();
        assertNotNull(secondPerson);
        assertNotNull(secondPerson.getKey());
        assertNotNull(secondPerson.getLinks());
        assertTrue(secondPerson.toString().contains("</api/person/v1/1>;rel=\"self\""));
        assertEquals("First Name Test1", secondPerson.getFirstName());
        assertEquals("Last Name Test1", secondPerson.getLastName());
        assertEquals("Female", secondPerson.getGender());
        assertEquals("Address Test1", secondPerson.getAddress());

        PersonVO thirdPerson = allPeople.get(2).getContent();
        assertNotNull(thirdPerson);
        assertNotNull(thirdPerson.getKey());
        assertNotNull(thirdPerson.getLinks());
        assertTrue(thirdPerson.toString().contains("</api/person/v1/2>;rel=\"self\""));
        assertEquals("First Name Test2", thirdPerson.getFirstName());
        assertEquals("Last Name Test2", thirdPerson.getLastName());
        assertEquals("Male", thirdPerson.getGender());
        assertEquals("Address Test2", thirdPerson.getAddress());
    }

    @Test
    public void shouldReturnEmptyResultWhenDoesNotHaveAnyPersonSaved() {
        Pageable pageable = PageRequest.of(0, 10);

        when(personRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(assembler.toModel(eq(new PageImpl<>(Collections.emptyList())), any(Link.class))).thenReturn(pagedModel);
        when(pagedModel.getContent()).thenReturn(Collections.emptyList());

        PagedModel<EntityModel<PersonVO>> allPeople = personService.findAll(pageable);

        assertTrue(allPeople.getContent().isEmpty());
    }
}