package school.faang.user_service.mapper;

import java.util.List;
import java.util.UUID;

import com.json.student.Education;
import com.json.student.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(source = "contactInfo.email", target = "email")
    @Mapping(source = "contactInfo.phone", target = "phone")
    @Mapping(source = "contactInfo.address.city", target = "city")
    @Mapping(target = "username", expression = "java(person.getFirstName() + \" \" + person.getLastName())")
    @Mapping(target = "aboutMe", expression = "java(buildAboutMe(person))")
    @Mapping(target = "password", expression = "java(generatedPassword())")
    User toUser(Person person);

    default String buildAboutMe(Person person) {
        Education education = person.getEducation();
        String state = "Not specified";
        String employer = "Not specified";
        if (!person.getContactInfo().getAddress().getState().isEmpty()) {
            state = person.getContactInfo().getAddress().getState();
        }
        if (!person.getEmployer().isEmpty()) {
            employer = person.getEmployer();
        }
        return "State: " + state + "\n Faculty: " + education.getFaculty() + "\n Major: " + education.getMajor() +
                "\n Employer: " + employer + "\n Year of Study: " + education.getYearOfStudy();
    }

    default String generatedPassword() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
