package br.com.wes.integrationtests.vo.wrapper;

import br.com.wes.integrationtests.vo.PersonVOIT;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PersonEmbeddedVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9149702316716371870L;

    @JsonProperty("personVOList")
    private List<PersonVOIT> people;

}
