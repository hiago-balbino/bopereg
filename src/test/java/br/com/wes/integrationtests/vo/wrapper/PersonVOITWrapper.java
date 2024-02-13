package br.com.wes.integrationtests.vo.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PersonVOITWrapper implements Serializable {

    @Serial
    private static final long serialVersionUID = 8132976730051997953L;

    @JsonProperty("_embedded")
    private PersonEmbeddedVO embedded;

}
