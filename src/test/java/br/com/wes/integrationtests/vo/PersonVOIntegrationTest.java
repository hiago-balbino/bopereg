package br.com.wes.integrationtests.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class PersonVOIntegrationTest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3111488046181860539L;

    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String address;
    private String gender;

    public PersonVOIntegrationTest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonVOIntegrationTest personVOIntegrationTest = (PersonVOIntegrationTest) o;
        return Objects.equals(id, personVOIntegrationTest.id) && Objects.equals(firstName, personVOIntegrationTest.firstName) && Objects.equals(lastName, personVOIntegrationTest.lastName) && Objects.equals(address, personVOIntegrationTest.address) && Objects.equals(gender, personVOIntegrationTest.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, address, gender);
    }
}
