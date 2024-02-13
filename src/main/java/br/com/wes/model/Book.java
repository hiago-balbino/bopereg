package br.com.wes.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "book")
public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = -8883020323492554052L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "author", nullable = false, length = 180)
    private String author;
    @Column(name = "title", nullable = false, length = 250)
    private String title;
    @Column(name = "price", nullable = false)
    private Double price;
    @Column(name = "launch_date", nullable = false)
    private LocalDate launchDate;

}
