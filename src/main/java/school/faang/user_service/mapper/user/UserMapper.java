package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.student.Person;
import school.faang.user_service.model.dto.user.UserDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.service.impl.user.SafeExtractor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "contactPreference.preference", target = "preference")
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
        return Stream.of(
                        SafeExtractor.extract(person, (p) -> p.getContactInfo().getAddress().getState()),
                        SafeExtractor.extract(person, (p) -> p.getEducation().getFaculty()),
                        SafeExtractor.extract(person, (p) -> p.getEducation().getYearOfStudy()),
                        SafeExtractor.extract(person, (p) -> p.getEducation().getMajor()),
                        SafeExtractor.extract(person, Person::getEmployer)
                )
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }


}
