package school.faang.user_service.mapper;

import com.json.student.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    @Mapping(target = "username", expression = "java(person.getFirstName() + \"_\" + person.getLastName())")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "aboutMe", source = "person", qualifiedByName = "aboutMe")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country.title", source = "contactInfo.address.country")
    User toEntityFromPerson(Person person);

    @Named("aboutMe")
    default String aboutMe(Person person) {
        StringBuilder builder = new StringBuilder();
        if (person.getEducation() != null) {
            builder.append("Education: \n");
            builder.append("faculty: ").append(person.getEducation().getFaculty());
            builder.append("yearOfStudy: ").append(person.getEducation().getYearOfStudy());
            builder.append("major: ").append(person.getEducation().getMajor());
            builder.append("Gpa: ").append(person.getEducation().getGpa());
        }
        if (!person.getEmployer().isBlank()) {
            builder.append("employer: " + person.getEmployer());
        }
        return builder.toString();
    }
}
