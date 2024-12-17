package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.model.person.Person;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
    @Mapping(target = "username", expression = "java(mapUsername(person))")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country", source = "contactInfo.address.country")
    @Mapping(target = "aboutMe", expression = "java(mapAboutMe(person))")
    User toUser(Person person);

    List<User> toUsers(List<Person> persons);

    default String mapUsername(Person person) {
        return person.getFirstName() + " " + person.getLastName();
    }

    default Country map(String countryName) {
        return new Country(0, countryName, null);
    }


    default String mapAboutMe(Person person) {
        StringBuilder aboutMe = new StringBuilder();
        if (person.getContactInfo() != null && person.getContactInfo().getAddress() != null
                && person.getContactInfo().getAddress().getState() != null) {
            aboutMe.append(person.getContactInfo().getAddress().getState()).append(", ");
        }

        if (person.getEducation() != null) {
            if(person.getEducation().getFaculty() != null) {
                aboutMe.append(person.getEducation().getFaculty())
                        .append(", ");
            }

            if(person.getEducation().getYearOfStudy() != null) {
                aboutMe.append(person.getEducation().getYearOfStudy())
                        .append(", ");
            }

            if(person.getEducation().getMajor() != null) {
                aboutMe.append(person.getEducation().getMajor());
            }
        }

        if (person.getEmployer() != null) {
            aboutMe.append(", ").append(person.getEmployer());
        }
        return aboutMe.toString();
    }
}
