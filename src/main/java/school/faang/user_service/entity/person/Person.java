package school.faang.user_service.entity.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Person {

    @JsonProperty("person.firstName")
    private String firstName;

    @JsonProperty("person.lastName")
    private String lastName;

    @JsonProperty("person.employer")
    private String employer;

    @JsonProperty("person.contactInfo.email")
    private String email;

    @JsonProperty("person.contactInfo.phone")
    private String phone;

    @JsonProperty("person.contactInfo.address.city")
    private String city;

    @JsonProperty("person.contactInfo.address.state")
    private String state;

    @JsonProperty("person.contactInfo.address.country")
    private String country;

    @JsonProperty("person.education.faculty")
    private String faculty;

    @JsonProperty("person.education.yearOfStudy")
    private String yearOfStudy;

    @JsonProperty("person.education.major")
    private String major;
}
