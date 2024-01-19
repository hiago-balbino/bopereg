package br.com.wes.service;

import br.com.wes.controller.PersonController;
import br.com.wes.exception.RequiredObjectIsNullException;
import br.com.wes.exception.ResourceNotFoundException;
import br.com.wes.mapper.ObjectModelMapper;
import br.com.wes.model.Person;
import br.com.wes.repository.PersonRepository;
import br.com.wes.vo.v1.PersonVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {

    private final Logger logger = Logger.getLogger(PersonService.class.getName());
    private final PersonRepository personRepository;
    private final ObjectModelMapper mapper;
    private final PagedResourcesAssembler<PersonVO> assembler;

    public PersonService(PersonRepository personRepository, ObjectModelMapper mapper, PagedResourcesAssembler<PersonVO> assembler) {
        this.personRepository = personRepository;
        this.mapper = mapper;
        this.assembler = assembler;
    }

    public PersonVO create(PersonVO person) {
        logger.info("Creating one person");
        if (Objects.isNull(person)) throw new RequiredObjectIsNullException();

        var personToSave = mapper.map(person, Person.class);
        var personSaved = mapper.map(personRepository.save(personToSave), PersonVO.class);

        return addPersonLinkAndReturn(personSaved);
    }

    public PersonVO update(PersonVO person) {
        logger.info("Updating one person");
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

    @Transactional
    public PersonVO disablePerson(Long id) {
        logger.info("Disabling one person");

        personRepository.disablePerson(id);
        var person = personRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return addPersonLinkAndReturn(mapper.map(person, PersonVO.class));
    }

    public void delete(Long id) {
        logger.info("Deleting one person");

        var personToDelete = personRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        personRepository.delete(personToDelete);
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person");

        var person = personRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return addPersonLinkAndReturn(mapper.map(person, PersonVO.class));
    }

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
        logger.info("Finding all people");

        Page<PersonVO> people = personRepository
                .findAll(pageable)
                .map(p -> mapper.map(p, PersonVO.class));
        people.forEach(this::addPersonLinkAndReturn);


        Link link = linkTo(
                methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
                .withSelfRel();
        return assembler.toModel(people, link);
    }

    private PersonVO addPersonLinkAndReturn(PersonVO personVO) {
        return personVO.add(linkTo(methodOn(PersonController.class).findById(personVO.getKey())).withSelfRel());
    }
}
