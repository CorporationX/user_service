package school.faang.user_service.mapper.user;

import org.mapstruct.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.country.Country;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "mentees", target = "menteeIds", qualifiedByName = "mapToId")
    @Mapping(source = "mentors", target = "mentorIds", qualifiedByName = "mapToId")
    @Mapping(source = "contactPreference", target = "preference", qualifiedByName = "mapToPreference")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    UserDto toDto(User user);

    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    User toEntity(UserDto userDto);

    @Named("mapToId")
    default List<Long> map(List<User> users) {
        return users.stream().map(User::getId).toList();
    }

    @Named("mapToPreference")
    default PreferredContact map(ContactPreference contactPreference) {
        return contactPreference.getPreference();
    }

    default User toEntity(Person person) {
        return User.builder()
                .username(String.format(person.getFirstName() + person.getLastName()))
                .email(person.getEmail())
                .phone(person.getPhone())
                .city(person.getCity())
                .country(Country.builder().title(person.getCountry()).build())
                .aboutMe(
                        String.format("state: " + person.getState() +
                                "faculty: " + person.getFaculty() +
                                "year of study: " + person.getYearOfStudy() +
                                "major: " + person.getMajor() +
                                "employer: " + person.getEmployer()))
                .build();
    }
}
