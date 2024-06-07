package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    @Mapping(target = "username", expression = "java(person.getFirstName() + person.getLastName())")
    @Mapping(target = "aboutMe", expression = "java(\" I'm from \" " +
            "+ person.getContactInfo().getAddress().getState() + \", study at \" " +
            "+ person.getEducation().getFaculty() + \", i'm \" " +
            "+ person.getEducation().getYearOfStudy() + \"rd year student\" " +
            "+ \", my major is \"+ person.getEducation().getMajor() + \", my employer is \" + person.getEmployer())")
    @Mapping(target = "email", source = "person.contactInfo.email")
    @Mapping(target = "phone", source = "person.contactInfo.phone")
    @Mapping(target = "city", source = "person.contactInfo.address.city")
    User toUser(Person person);
}
