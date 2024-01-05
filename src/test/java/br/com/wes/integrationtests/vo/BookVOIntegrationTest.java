package br.com.wes.integrationtests.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class BookVOIntegrationTest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1976307716533323862L;

    private Long id;
    private String author;
    private String title;
    private Double price;
    @JsonProperty("launch_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate launchDate;

    public BookVOIntegrationTest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookVOIntegrationTest that = (BookVOIntegrationTest) o;
        return Objects.equals(id, that.id) && Objects.equals(author, that.author) && Objects.equals(title, that.title) && Objects.equals(price, that.price) && Objects.equals(launchDate, that.launchDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, title, price, launchDate);
    }
}
