package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.pojo.person.Person;

import java.util.Objects;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {

    @Mapping(target = "username", expression = "java(getUsername(person.getFirstName(), person.getLastName()))")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country", source = "contactInfo.address.country", qualifiedByName = "mapCountry")
    @Mapping(target = "aboutMe", expression = "java(getAboutMe(person))")
    User toUser(Person person);

    default String getUsername(String firstname, String lastname) {
        return firstname + "_" + lastname;
    }

    @Named("mapCountry")
    default Country mapCountry(String country) {
        return Country.builder().title(country).build();
    }

    default String getAboutMe(Person person) {
        StringBuilder aboutMe = new StringBuilder("about me: ");

        if (Objects.nonNull(person.getContactInfo())
                && Objects.nonNull(person.getContactInfo().getAddress())
                && Objects.nonNull(person.getContactInfo().getAddress().getState())) {
            String state = person.getContactInfo().getAddress().getState();
            aboutMe.append("state - ").append(state).append(", ");
        }

        aboutMe.append("faculty - ").append(person.getEducation().getFaculty()).append(", ");
        aboutMe.append("year of study - ").append(person.getEducation().getYearOfStudy()).append(", ");
        aboutMe.append("major - ").append(person.getEducation().getMajor());

        if (Objects.nonNull(person.getEmployer())) {
            String employer = person.getEmployer();
            aboutMe.append(", employer - ").append(employer);
        }

        return aboutMe.toString().trim();
    }
}
