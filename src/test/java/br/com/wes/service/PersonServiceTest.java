package br.com.wes.service;

import br.com.wes.controller.PersonController;
import br.com.wes.exception.RequiredObjectIsNullException;
import br.com.wes.exception.ResourceNotFoundException;
import br.com.wes.mapper.ObjectModelMapper;
import br.com.wes.model.Person;
import br.com.wes.repository.PersonRepository;
import br.com.wes.util.mock.PersonMock;
import br.com.wes.vo.v1.PersonVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    @Nested
    class CreatePersonTest {

        @Test
        @DisplayName("Should create person with success")
        public void shouldCreatePersonWithSuccess() {
            PersonVO personVOMock = input.mockPersonVO();
            Person personMock = input.mockPersonEntity();
            when(mapper.map(personVOMock, Person.class)).thenReturn(personMock);

            Person personPersisted = input.mockPersonEntity(1);
            when(personRepository.save(personMock)).thenReturn(personPersisted);

            PersonVO personVOPersisted = input.mockPersonVO(1);
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
        @DisplayName("Should throw exception when creating person with null object")
        public void shouldThrowExceptionWhenCreatingPersonWithNullObject() {
            RequiredObjectIsNullException exception = assertThrows(RequiredObjectIsNullException.class, () -> {
                personService.create(null);
            });

            String expectedMessage = "It is not allowed to persist a null object";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }

    }

    @Nested
    class UpdatePersonTest {

        @Test
        @DisplayName("Should update person with success")
        public void shouldUpdatePersonWithSuccess() {
            PersonVO personVOMock = input.mockPersonVO(1);
            Person personMock = input.mockPersonEntity(1);
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
        @DisplayName("Should throw exception when updating person with null object")
        public void shouldThrowExceptionWhenUpdatingPersonWithNullObject() {
            RequiredObjectIsNullException exception = assertThrows(RequiredObjectIsNullException.class, () -> {
                personService.update(null);
            });

            String expectedMessage = "It is not allowed to persist a null object";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }

        @Test
        @DisplayName("Should throw exception when trying update person with non-existent object")
        public void shouldThrowExceptionWhenTryingUpdatePersonWithNonExistentObject() {
            PersonVO personVOMock = input.mockPersonVO(1);
            when(personRepository.findById(personVOMock.getKey())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                personService.update(personVOMock);
            });

            String expectedMessage = "No records found for this identifier";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
    }

    @Nested
    class DeletePersonTest {

        @Test
        @DisplayName("Should delete person with success")
        public void shouldDeletePersonWithSuccess() {
            Person personMock = input.mockPersonEntity(1);
            when(personRepository.findById(personMock.getId())).thenReturn(Optional.of(personMock));

            personService.delete(personMock.getId());

            verify(personRepository, times(1)).delete(personMock);
        }

        @Test
        @DisplayName("Should throw exception when trying delete non-existent person")
        public void shouldThrowExceptionWhenDeletingNonExistentPerson() {
            when(personRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                personService.delete(1L);
            });

            String expectedMessage = "No records found for this identifier";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
    }

    @Nested
    class FetchPersonTest {

        @Test
        @DisplayName("Should find person by id with success")
        public void shouldFindPersonByIdWithSuccess() {
            Person personMock = input.mockPersonEntity(1);
            when(personRepository.findById(1L)).thenReturn(Optional.of(personMock));

            PersonVO personVOMock = input.mockPersonVO(1);
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
        @DisplayName("Should throw exception when trying find non-existent person")
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
        @DisplayName("Should find all person with success")
        public void shouldFindAllPersonWithSuccess() {
            List<Person> people = input.mockPersonEntities();
            Page<Person> pagePeople = new PageImpl<>(people);
            List<PersonVO> peopleVO = input.mockPersonVOs();
            Page<PersonVO> pagePeopleVO = new PageImpl<>(peopleVO);
            Collection<EntityModel<PersonVO>> pagedModelContent = Arrays.asList(
                    EntityModel.of(peopleVO.get(0)),
                    EntityModel.of(peopleVO.get(1)),
                    EntityModel.of(peopleVO.get(2))
            );
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "firstName"));
            Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

            when(personRepository.findAll(PageRequest.of(0, 10, Sort.by(Direction.ASC, "firstName")))).thenReturn(pagePeople);
            when(mapper.map(people.get(0), PersonVO.class)).thenReturn(peopleVO.get(0));
            when(mapper.map(people.get(1), PersonVO.class)).thenReturn(peopleVO.get(1));
            when(mapper.map(people.get(2), PersonVO.class)).thenReturn(peopleVO.get(2));
            when(assembler.toModel(eq(pagePeopleVO), eq(link))).thenReturn(pagedModel);
            when(pagedModel.getContent()).thenReturn(pagedModelContent);
            when(pagedModel.getLinks()).thenReturn(Links.of(link));

            PagedModel<EntityModel<PersonVO>> pagedPeople = personService.findAll(pageable);
            assertEquals("</api/person/v1?page=0&size=10&direction=asc>;rel=\"self\"", pagedPeople.getLinks().toString());

            List<EntityModel<PersonVO>> allPeople = pagedPeople.getContent().stream().toList();
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
        @DisplayName("Should return empty result when does not have any person saved")
        public void shouldReturnEmptyResultWhenDoesNotHaveAnyPersonSaved() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "firstName"));

            when(personRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));
            when(assembler.toModel(eq(new PageImpl<>(Collections.emptyList())), any(Link.class))).thenReturn(pagedModel);
            when(pagedModel.getContent()).thenReturn(Collections.emptyList());
            when(pagedModel.getLinks()).thenReturn(Links.NONE);

            PagedModel<EntityModel<PersonVO>> allPeople = personService.findAll(pageable);

            assertTrue(allPeople.getContent().isEmpty());
            assertTrue(allPeople.getLinks().isEmpty());
        }

        @Test
        @DisplayName("Should return people with success when find by first name")
        public void shouldReturnPeopleWithSuccessWhenFindByFirstName() {
            List<Person> people = List.of(input.mockPersonEntity());
            Page<Person> pagePeople = new PageImpl<>(people);
            List<PersonVO> peopleVO = List.of(input.mockPersonVO());
            Page<PersonVO> pagePeopleVO = new PageImpl<>(peopleVO);
            Collection<EntityModel<PersonVO>> pagedModelContent = List.of(EntityModel.of(peopleVO.getFirst()));
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "firstName"));
            Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
            String firstName = "Test0";

            when(personRepository.findPeopleByName(firstName, pageable)).thenReturn(pagePeople);
            when(mapper.map(people.getFirst(), PersonVO.class)).thenReturn(peopleVO.getFirst());
            when(assembler.toModel(eq(pagePeopleVO), eq(link))).thenReturn(pagedModel);
            when(pagedModel.getContent()).thenReturn(pagedModelContent);
            when(pagedModel.getLinks()).thenReturn(Links.of(link));

            PagedModel<EntityModel<PersonVO>> pagedPeople = personService.findPeopleByName(firstName, pageable);
            assertEquals("</api/person/v1?page=0&size=10&direction=asc>;rel=\"self\"", pagedPeople.getLinks().toString());

            List<EntityModel<PersonVO>> allPeople = pagedPeople.getContent().stream().toList();
            assertFalse(allPeople.isEmpty());

            PersonVO person = allPeople.getFirst().getContent();
            assertNotNull(person);
            assertNotNull(person.getKey());
            assertNotNull(person.getLinks());
            assertTrue(person.toString().contains("</api/person/v1/0>;rel=\"self\""));
            assertEquals("First Name Test0", person.getFirstName());
            assertEquals("Last Name Test0", person.getLastName());
            assertEquals("Male", person.getGender());
            assertEquals("Address Test0", person.getAddress());
        }

        @Test
        @DisplayName("Should return empty result when does not have any person with first name")
        public void shouldReturnEmptyResultWhenDoesNotHaveAnyPersonWithFirstName() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "firstName"));
            String firstName = "Test0";

            when(personRepository.findPeopleByName(firstName, pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));
            when(assembler.toModel(eq(new PageImpl<>(Collections.emptyList())), any(Link.class))).thenReturn(pagedModel);
            when(pagedModel.getContent()).thenReturn(Collections.emptyList());
            when(pagedModel.getLinks()).thenReturn(Links.NONE);

            PagedModel<EntityModel<PersonVO>> allPeople = personService.findPeopleByName(firstName, pageable);

            assertTrue(allPeople.getContent().isEmpty());
            assertTrue(allPeople.getLinks().isEmpty());
        }
    }
}