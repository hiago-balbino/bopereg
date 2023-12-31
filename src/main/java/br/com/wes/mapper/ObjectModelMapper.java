package br.com.wes.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ObjectModelMapper {

    public final ModelMapper mapper;

    public ObjectModelMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public <O, D> D map(O origin, Class<D> destination) {
        return mapper.map(origin, destination);
    }

    public <O, D> List<D> map(List<O> origins, Class<D> destination) {
        List<D> mappedObjects = new ArrayList<>();
        for (O origin : origins) {
            mappedObjects.add(mapper.map(origin, destination));
        }
        return mappedObjects;
    }
}
