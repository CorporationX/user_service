package school.faang.user_service.mapper;

import java.util.List;
import java.util.Optional;
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

        String state = Optional.ofNullable(person.getContactInfo().getAddress().getState())
                .filter(s -> !s.isEmpty()).orElse("Not specified");
        String employer = Optional.ofNullable(person.getEmployer())
                .filter(e -> !e.isEmpty()).orElse("Not specified");

        return String.format(
                "State: %s%nFaculty: %s%nMajor: %s%nEmployer: %s%nYear of Study: %d",
                state,
                education.getFaculty(),
                education.getMajor(),
                employer,
                education.getYearOfStudy()
        );
    }

    default String generatedPassword() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
