package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.Person;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    @Mapping(target = "username", expression = "java(person.getFirstName() + ' ' + person.getLastName())")
    @Mapping(source = "contactInfo.email", target = "email")
    @Mapping(source = "contactInfo.phone", target = "phone")
    @Mapping(source = "contactInfo.address.city", target = "city")
    @Mapping(target = "aboutMe", expression = "java(generateAboutMe(person))")
    User personToUser(Person person);

    default String generateAboutMe(Person person) {
        StringBuilder aboutMe = new StringBuilder();
        if (person.getContactInfo().getAddress().getState() != null) {
            aboutMe.append(person.getContactInfo().getAddress().getState()).append(", ");
        }
        if (person.getEducation().getFaculty() != null) {
            aboutMe.append(person.getEducation().getFaculty()).append(", ");
        }
        if (person.getEducation().getYearOfStudy() != null) {
            aboutMe.append(person.getEducation().getYearOfStudy()).append(", ");
        }
        if (person.getEducation().getMajor() != null) {
            aboutMe.append(person.getEducation().getMajor()).append(", ");
        }
        if (person.getEmployer() != null) {
            aboutMe.append(person.getEmployer());
        }

        return aboutMe.toString();
    }
}
