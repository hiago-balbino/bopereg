package br.com.wes.controller;

import br.com.wes.service.PersonService;
import br.com.wes.util.MediaType;
import br.com.wes.vo.v1.PersonVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person/v1")
@Tag(name = "People", description = "Endpoints for Managing People")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @Operation(
            summary = "Add a new person",
            description = "Add a new person by passing in a JSON, XML or YAML representation of the person",
            tags = {"People"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = {
                            @Content(schema = @Schema(implementation = PersonVO.class))
                    }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
//    @CrossOrigin(origins = {"http://localhost:8080", "https://thewes.com.br"})
    @PostMapping(
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML}
    )
    public PersonVO create(@RequestBody PersonVO person) {
        return personService.create(person);
    }

    @Operation(
            summary = "Update a person",
            description = "Update a person by passing in a JSON, XML or YAML representation of the person",
            tags = {"People"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = {
                            @Content(schema = @Schema(implementation = PersonVO.class))
                    }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PutMapping(
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML}
    )
    public PersonVO update(@RequestBody PersonVO person) {
        return personService.update(person);
    }

    @Operation(
            summary = "Delete a person",
            description = "Delete a person by passing the identifier",
            tags = {"People"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Find a person",
            description = "Find a person by passing the identifier",
            tags = {"People"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = {
                            @Content(schema = @Schema(implementation = PersonVO.class))
                    }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
//    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public PersonVO findById(@PathVariable(value = "id") Long id) {
        return personService.findById(id);
    }

    @Operation(
            summary = "Find people",
            description = "Find all people",
            tags = {"People"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = {
                            @Content(
//                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                            )
                    }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public List<PersonVO> findAll() {
        return personService.findAll();
    }
}
