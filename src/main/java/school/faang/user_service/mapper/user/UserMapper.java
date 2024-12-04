package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.pojo.user.Person;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "contactPreference.preference", target = "preference", qualifiedByName = "preferenceToInt")
    UserDto toDto(User user);

    @Mapping(target = "username", expression = "java(person.getFirstName() + \" \" + person.getLastName())")
    @Mapping(target = "aboutMe", expression = "java(getAboutMe(person))")
    @Mapping(target = "country", ignore = true)
    User toUser(Person person);

    default List<UserDto> entityStreamToDtoList(Stream<User> users) {
        return users.map(this::toDto).toList();
    }

    default String getAboutMe(Person person) {
        StringBuilder aboutMe = new StringBuilder();

        if (person.getState() != null && !person.getState().isBlank()) {
            aboutMe.append(person.getState()).append(" ");
        }

        aboutMe.append(person.getFaculty())
                .append(" ")
                .append(person.getYearOfStudy())
                .append(" ")
                .append(person.getMajor())
                .append(" ");

        if (person.getEmployer() != null && !person.getEmployer().isBlank()) {
            aboutMe.append(person.getEmployer());
        }

        return aboutMe.toString().trim();
    }

    default List<Long> usersToIds(List<UserDto> users) {
        return users.stream()
                .map(UserDto::getId)
                .toList();
    }

    @Named("preferenceToInt")
    default int preferenceToInt(PreferredContact preference) {
        return preference != null ? preference.ordinal() : -1;
    }
}
