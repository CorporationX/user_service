package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.pojo.Person;

import java.util.Objects;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {

    @Mapping(target = "username", expression = "java(mapToUsername(person.getFirstName(), person.getLastName()))")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country", source = "contactInfo.address.country", qualifiedByName = "mapCountry")
    @Mapping(target = "aboutMe", expression = "java(mapToAboutMe(person))")
    User toUser(Person person);

    default String mapToUsername(String firstname, String lastname) {
        return firstname + "_" + lastname;
    }

    @Named("mapCountry")
    default Country mapCountry(String country) {
        return Country.builder().title(country).build();
    }

    default String mapToAboutMe(Person person) {
        String aboutMe = "about me: ";

        String state = person.getContactInfo().getAddress().getState();
        if (Objects.nonNull(state)) {
            aboutMe += "state - " + state + ", ";
        }

        aboutMe += "faculty - " + person.getEducation().getFaculty() + ", ";
        aboutMe += "year of study - " + person.getEducation().getYearOfStudy() + ", ";
        aboutMe += "major - " + person.getEducation().getMajor();

        String employer = person.getEmployer();
        if (Objects.nonNull(employer)) {
            aboutMe += ", employer - " + employer;
        }

        return aboutMe;
    }
}
