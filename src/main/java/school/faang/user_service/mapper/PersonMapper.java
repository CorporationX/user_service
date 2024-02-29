package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.student.Person;
import school.faang.user_service.entity.User;

import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
    @Mapping(target = "username", expression = "java(getUsername(person.getFirstName(), person.getLastName()))")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country", expression = "getCountry(person.contactInfo.address.country)")
    @Mapping(target = "aboutMe", expression = "java(getAboutMe(person))")
    User toUser(Person person);

    default String getUsername(String firstname, String lastname) {
        return firstname + " " + lastname;
    }

    @Named("getCountry")
    default Country getCountry(String country) {
        return Country.builder().title(country).build();
    }

    default String getAboutMe(Person person) {
        String lineEnd = ".\n";
        StringBuilder aboutMe = new StringBuilder("About me: ").append(lineEnd);

        if (person.getContactInfo() != null
                && person.getContactInfo().getAddress() != null
                && person.getContactInfo().getAddress().getState() != null) {
            aboutMe.append("State - ").append(person.getContactInfo().getAddress().getState())
                    .append(lineEnd);
        }

        aboutMe.append("Faculty - ").append(person.getEducation().getFaculty())
                .append(lineEnd);
        aboutMe.append("Year of study - ").append(person.getEducation().getYearOfStudy())
                .append(lineEnd);
        aboutMe.append("Major - ").append(person.getEducation().getMajor());

        if (person.getEmployer() != null) {
            aboutMe.append(lineEnd);
            aboutMe.append("Employer - ").append(person.getEmployer());
        }

        return aboutMe.toString();
    }
}
