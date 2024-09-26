package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.pojo.student.Person;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    List<UserDto> toDtos(List<User> users);

    List<User> toEntities(List<Person> persons);

    @Mapping(target = "username", expression = "java(person.getFirstName() + \" \" + person.getLastName())")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country.title", source = "contactInfo.address.country")
    @Mapping(target = "aboutMe", expression = "java(mapAboutMeFromPerson(person))")
    User toEntity(Person person);

    default String mapAboutMeFromPerson(Person person) {
        return concatAboutMeInformation(person);
    }

    private String concatAboutMeInformation(Person person) {
        StringBuilder sb = new StringBuilder();

        if (person.getContactInfo().getAddress().getState() != null &&
                !person.getContactInfo().getAddress().getState().isBlank()) {
            sb.append("Address: { State - ")
                    .append(person.getContactInfo().getAddress().getState())
                    .append(" } ");
        }
        if (person.getEducation() != null) {
            if (person.getEducation().getFaculty() != null &&
                    !person.getEducation().getFaculty().isBlank()) {
                sb.append("\nFaculty - ").append(person.getEducation().getFaculty());
            }
            if (person.getEducation().getYearOfStudy() > 0) {
                sb.append("\nYear of study - ").append(person.getEducation().getYearOfStudy());
            }
            if (person.getEducation().getMajor() != null &&
                    !person.getEducation().getMajor().isBlank()) {
                sb.append("\nMajor - ").append(person.getEducation().getMajor());
            }
        }
        if (person.getEmployer() != null &&
                !person.getEmployer().isBlank()) {
            sb.append("\nEmployer - ").append(person.getEmployer());
        }

        return sb.toString();
    }
}
