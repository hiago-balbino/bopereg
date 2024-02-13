package br.com.wes.mapper;

import br.com.wes.model.Person;
import br.com.wes.util.mock.PersonMock;
import br.com.wes.vo.v1.PersonVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjectModelMapperTest {
    private PersonMock input;

    @InjectMocks
    private ObjectModelMapper objectModelMapper;
    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        input = new PersonMock();
    }

    @Test
    public void mapEntityToValueObject() {
        Person personMock = input.mockPersonEntity();
        PersonVO personVOMock = input.mockPersonVO();
        when(modelMapper.map(personMock, PersonVO.class)).thenReturn(personVOMock);

        PersonVO personVO = objectModelMapper.map(personMock, PersonVO.class);

        assertEquals(Long.valueOf(0L), personVO.getKey());
        assertEquals("First Name Test0", personVO.getFirstName());
        assertEquals("Last Name Test0", personVO.getLastName());
        assertEquals("Address Test0", personVO.getAddress());
        assertEquals("Male", personVO.getGender());
    }

    @Test
    public void mapEntitiesToValueObjects() {
        List<Person> peopleMock = input.mockPersonEntities();
        List<PersonVO> peopleVOMock = input.mockPersonVOs();
        when(modelMapper.map(peopleMock.get(0), PersonVO.class)).thenReturn(peopleVOMock.get(0));
        when(modelMapper.map(peopleMock.get(1), PersonVO.class)).thenReturn(peopleVOMock.get(1));
        when(modelMapper.map(peopleMock.get(2), PersonVO.class)).thenReturn(peopleVOMock.get(2));

        List<PersonVO> peopleVO = objectModelMapper.map(peopleMock, PersonVO.class);

        PersonVO firstPersonVO = peopleVO.get(0);
        assertEquals(Long.valueOf(0L), firstPersonVO.getKey());
        assertEquals("First Name Test0", firstPersonVO.getFirstName());
        assertEquals("Last Name Test0", firstPersonVO.getLastName());
        assertEquals("Address Test0", firstPersonVO.getAddress());
        assertEquals("Male", firstPersonVO.getGender());

        PersonVO secondPersonVO = peopleVO.get(1);
        assertEquals(Long.valueOf(1L), secondPersonVO.getKey());
        assertEquals("First Name Test1", secondPersonVO.getFirstName());
        assertEquals("Last Name Test1", secondPersonVO.getLastName());
        assertEquals("Address Test1", secondPersonVO.getAddress());
        assertEquals("Female", secondPersonVO.getGender());

        PersonVO thirdPersonVO = peopleVO.get(2);
        assertEquals(Long.valueOf(2L), thirdPersonVO.getKey());
        assertEquals("First Name Test2", thirdPersonVO.getFirstName());
        assertEquals("Last Name Test2", thirdPersonVO.getLastName());
        assertEquals("Address Test2", thirdPersonVO.getAddress());
        assertEquals("Male", thirdPersonVO.getGender());
    }

    @Test
    public void mapValueObjectToEntity() {
        PersonVO personVOMock = input.mockPersonVO();
        Person personMock = input.mockPersonEntity();
        when(modelMapper.map(personVOMock, Person.class)).thenReturn(personMock);

        Person person = objectModelMapper.map(personVOMock, Person.class);
        assertEquals(Long.valueOf(0L), person.getId());
        assertEquals("First Name Test0", person.getFirstName());
        assertEquals("Last Name Test0", person.getLastName());
        assertEquals("Address Test0", person.getAddress());
        assertEquals("Male", person.getGender());
    }

    @Test
    public void mapValueObjectsToEntities() {
        List<PersonVO> peopleVOMock = input.mockPersonVOs();
        List<Person> peopleMock = input.mockPersonEntities();
        when(modelMapper.map(peopleVOMock.get(0), Person.class)).thenReturn(peopleMock.get(0));
        when(modelMapper.map(peopleVOMock.get(1), Person.class)).thenReturn(peopleMock.get(1));
        when(modelMapper.map(peopleVOMock.get(2), Person.class)).thenReturn(peopleMock.get(2));

        List<Person> people = objectModelMapper.map(peopleVOMock, Person.class);

        Person firstPerson = people.get(0);
        assertEquals(Long.valueOf(0L), firstPerson.getId());
        assertEquals("First Name Test0", firstPerson.getFirstName());
        assertEquals("Last Name Test0", firstPerson.getLastName());
        assertEquals("Address Test0", firstPerson.getAddress());
        assertEquals("Male", firstPerson.getGender());

        Person secondPerson = people.get(1);
        assertEquals(Long.valueOf(1L), secondPerson.getId());
        assertEquals("First Name Test1", secondPerson.getFirstName());
        assertEquals("Last Name Test1", secondPerson.getLastName());
        assertEquals("Address Test1", secondPerson.getAddress());
        assertEquals("Female", secondPerson.getGender());

        Person thirdPerson = people.get(2);
        assertEquals(Long.valueOf(2L), thirdPerson.getId());
        assertEquals("First Name Test2", thirdPerson.getFirstName());
        assertEquals("Last Name Test2", thirdPerson.getLastName());
        assertEquals("Address Test2", thirdPerson.getAddress());
        assertEquals("Male", thirdPerson.getGender());
    }
}