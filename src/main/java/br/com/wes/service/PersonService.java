package br.com.wes.service;

import br.com.wes.controller.PersonController;
import br.com.wes.exception.RequiredObjectIsNullException;
import br.com.wes.exception.ResourceNotFoundException;
import br.com.wes.mapper.ObjectModelMapper;
import br.com.wes.model.Person;
import br.com.wes.repository.PersonRepository;
import br.com.wes.vo.v1.PersonVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final ObjectModelMapper mapper;
    private final PagedResourcesAssembler<PersonVO> assembler;

    public PersonVO create(PersonVO person) {
        log.info("Creating one person");
        if (Objects.isNull(person)) throw new RequiredObjectIsNullException();

        var personToSave = mapper.map(person, Person.class);
        var personSaved = mapper.map(personRepository.save(personToSave), PersonVO.class);

        return addPersonLinkAndReturn(personSaved);
    }

    public PersonVO update(PersonVO person) {
        log.info("Updating one person");
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
        log.info("Disabling one person");

        personRepository.disablePerson(id);
        var person = personRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return addPersonLinkAndReturn(mapper.map(person, PersonVO.class));
    }

    public void delete(Long id) {
        log.info("Deleting one person");

        var personToDelete = personRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        personRepository.delete(personToDelete);
    }

    public PersonVO findById(Long id) {
        log.info("Finding one person");

        var person = personRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return addPersonLinkAndReturn(mapper.map(person, PersonVO.class));
    }

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
        log.info("Finding all people");

        Page<PersonVO> people = personRepository
                .findAll(pageable)
                .map(p -> mapper.map(p, PersonVO.class));
        people.forEach(this::addPersonLinkAndReturn);

        Link link = linkTo(
                methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
                .withSelfRel();
        return assembler.toModel(people, link);
    }

    public PagedModel<EntityModel<PersonVO>> findPeopleByName(String firstName, Pageable pageable) {
        log.info("Finding people by first name");

        Page<PersonVO> people = personRepository
                .findPeopleByName(firstName, pageable)
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
