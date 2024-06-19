package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDto toDto(User user);

    @Mapping(target = "country", ignore = true)
    @Mapping(target = "contactPreference", ignore = true)
    User toEntity(UserDto user);

    List<UserDto> toDto(List<User> users);

    @Mapping(target = "username", qualifiedByName = "fullName", source = "person")
    @Mapping(target = "aboutMe", qualifiedByName = "aboutInfo", source = "person")
    @Mapping(target = "email", source = "person.contactInfo.email")
    @Mapping(target = "phone", source = "person.contactInfo.phone")
    @Mapping(target = "city", source = "person.contactInfo.address.city")
    User toUser(Person person);

    @Named("fullName")
    default String fullName(Person person) {
        return person.getFirstName() + person.getLastName();
    }

    @Named("aboutInfo")
    default String aboutInfo(Person person) {
        return "I'm from " + person.getContactInfo().getAddress().getState() +
                ", study at " + person.getEducation().getFaculty() +
                ", i'm " + person.getEducation().getYearOfStudy() + "rd year student" +
                ", my major is " + person.getEducation().getMajor() +
                ", my employer is " + person.getEmployer();
    }
}
