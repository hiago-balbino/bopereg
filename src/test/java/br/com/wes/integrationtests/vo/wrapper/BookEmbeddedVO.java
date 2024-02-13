package br.com.wes.integrationtests.vo.wrapper;

import br.com.wes.integrationtests.vo.BookVOIT;
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
public class BookEmbeddedVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6542418013773642085L;

    @JsonProperty("bookVOList")
    private List<BookVOIT> books;

}