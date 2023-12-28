package br.com.wes.config;

import br.com.wes.model.Person;
import br.com.wes.vo.v1.PersonVO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        /*
            This block have been used when have different attribute in each class with the same representation.
            E.g., the 'key' field in PersonVO is the same of 'id' field in Person.
        */
        modelMapper.createTypeMap(Person.class, PersonVO.class).addMapping(Person::getId, PersonVO::setKey);
        modelMapper.createTypeMap(PersonVO.class, Person.class).addMapping(PersonVO::getKey, Person::setId);
        return modelMapper;
    }
}
