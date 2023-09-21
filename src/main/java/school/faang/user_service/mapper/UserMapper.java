package school.faang.user_service.mapper;

import com.json.student.PersonSchemaForUser;
import org.mapstruct.*;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "contactPreference", source = "preferredContact")
    //todo при добавление ментора нужно добавлять объект заполненного юзера,
        // а не просто оболочку с id
    User toEntity(UserDto userDto);

    default PreferredContact mapPreferredContact(PreferredContact preferredContact) {
        return preferredContact != null ? preferredContact : PreferredContact.EMAIL;
    }
    @Mapping(target = "preferredContact", source = "contactPreference.preference")
    @Mapping(target = "mentorIds", source = "mentors", qualifiedByName = "usersToIds")
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    UserDto personToUserDto(PersonSchemaForUser person);

    @Named("usersToIds")
    default List<Long> mapUsersToIds(List<User> value) {
        if (value == null) {
            return new ArrayList<>();
        }
        return value.stream().map(User::getId).toList();
    }

    default CountryDto mapCountry(String country) {
        CountryDto countryDto = new CountryDto();
        countryDto.setTitle(country);
        return countryDto;
    }
}