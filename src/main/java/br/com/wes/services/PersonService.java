package br.com.wes.services;

import br.com.wes.controllers.PersonController;
import br.com.wes.exceptions.RequiredObjectIsNullException;
import br.com.wes.exceptions.ResourceNotFoundException;
import br.com.wes.mapper.ObjectModelMapper;
import br.com.wes.model.Person;
import br.com.wes.repositories.PersonRepository;
import br.com.wes.vo.v1.PersonVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {

    private final Logger logger = Logger.getLogger(PersonService.class.getName());
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ObjectModelMapper mapper;

    public PersonVO create(PersonVO person) {
        logger.info("Creating one person!");
        if (Objects.isNull(person)) throw new RequiredObjectIsNullException();

        var personToSave = mapper.map(person, Person.class);
        var personSaved = mapper.map(personRepository.save(personToSave), PersonVO.class);

        return addPersonLinkAndReturn(personSaved);
    }

    public PersonVO update(PersonVO person) {
        logger.info("Updating one person!");
        if (Objects.isNull(person)) throw new RequiredObjectIsNullException();

        var personToUpdate = personRepository.findById(person.getKey())
                .orElseThrow(ResourceNotFoundException::new);
        personToUpdate.setFirstName(person.getFirstName());
        personToUpdate.setLastName(person.getLastName());
        personToUpdate.setAddress(person.getAddress());
        personToUpdate.setGender(person.getGender());

        var personUpdated = mapper.map(personRepository.save(personToUpdate), PersonVO.class);
        return addPersonLinkAndReturn(personUpdated);
    }

    public void delete(Long id) {
        logger.info("Deleting one person!");

        var personToDelete = personRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        personRepository.delete(personToDelete);
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person!");

        var person = personRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return addPersonLinkAndReturn(mapper.map(person, PersonVO.class));
    }

    public List<PersonVO> findAll() {
        logger.info("Finding all people!");

        List<Person> all = personRepository.findAll();
        var peopleVO = mapper.map(all, PersonVO.class);
        peopleVO.forEach(this::addPersonLinkAndReturn);

        return peopleVO;
    }

    private PersonVO addPersonLinkAndReturn(PersonVO personVO) {
        return personVO.add(linkTo(methodOn(PersonController.class).findById(personVO.getKey())).withSelfRel());
    }
}
