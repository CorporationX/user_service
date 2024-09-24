package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.student.Person;
import school.faang.user_service.service.user.SafeExtractor;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    List<UserDto> toDto(List<User> users);

    @Mapping(target = "username", expression = "java(person.getFirstName() + ' ' + person.getLastName())")
    @Mapping(source = "contactInfo.email", target = "email")
    @Mapping(source = "contactInfo.phone", target = "phone")
    @Mapping(source = "contactInfo.address.city", target = "city")
    @Mapping(target = "aboutMe", expression = "java(generateAboutMe(person))")
    User personToUser(Person person);

    default String generateAboutMe(Person person) {
        if (person == null) {
            return "";
        }
        StringBuilder aboutMe = new StringBuilder();


        var state = SafeExtractor.extract(person, (p) -> p.getContactInfo().getAddress().getState());
        if (state != null) {
            aboutMe.append(state).append(", ");
        }

        var faculty = SafeExtractor.extract(person, (p) -> p.getEducation().getFaculty());
        if (faculty != null) {
            aboutMe.append(faculty).append(", ");
        }

        var yearOfStudy = SafeExtractor.extract(person, (p) -> p.getEducation().getYearOfStudy());
        if (yearOfStudy != null) {
            aboutMe.append(yearOfStudy).append(", ");
        }
        var major = SafeExtractor.extract(person, (p) ->p.getEducation().getMajor());
        if (major != null) {
            aboutMe.append(major).append(", ");
        }

        var employer = SafeExtractor.extract(person, Person::getEmployer);
        if (employer != null) {
            aboutMe.append(employer);
        }

        return aboutMe.toString();
    }

}
