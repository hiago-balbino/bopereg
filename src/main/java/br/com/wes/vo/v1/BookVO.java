package br.com.wes.vo.v1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder({"id", "author", "title", "price", "launch_date"})
public class BookVO extends RepresentationModel<BookVO> implements Serializable {

    @Serial
    private static final long serialVersionUID = -3211791978972137439L;

    @JsonProperty("id")
    private Long key;
    @JsonProperty("author")
    private String author;
    @JsonProperty("title")
    private String title;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("launch_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate launchDate;

}
