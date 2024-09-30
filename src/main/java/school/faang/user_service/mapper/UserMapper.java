package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.pojo.Person;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    List<UserDto> toDtoList(List<User> users);

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "person", qualifiedByName = "generateUsername")
    @Mapping(target = "email", source = "person.contactInfo.email")
    @Mapping(target = "phone", source = "person.contactInfo.phone")
    @Mapping(target = "city", source = "person.contactInfo.address.city")
    @Mapping(target = "aboutMe", source = "person", qualifiedByName = "generateAboutMe")
    User toEntity(Person person, Country country, String password);

    @Named("generateUsername")
    default String generateUsername(Person person) {
        return person.getLastName() + "_" + person.getFirstName();
    }

    @Named("generateAboutMe")
    default String generateAboutMe(Person person) {
        StringBuilder builder = new StringBuilder().append(String.format("Faculty: %s | Year of Study: %s | Major: %s",
                person.getEducation().getFaculty(),
                person.getEducation().getYearOfStudy(),
                person.getEducation().getMajor()));
        if (person.getEmployer() != null && !person.getEmployer().isBlank()) {
            builder.append(String.format("Employer: %s", person.getEmployer()));
        }
        return builder.toString();
    }
}