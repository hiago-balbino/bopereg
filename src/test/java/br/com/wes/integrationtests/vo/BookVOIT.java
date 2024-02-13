package br.com.wes.integrationtests.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class BookVOIT implements Serializable {

    @Serial
    private static final long serialVersionUID = 1976307716533323862L;

    private Long id;
    private String author;
    private String title;
    private Double price;
    @JsonProperty("launch_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate launchDate;

}
