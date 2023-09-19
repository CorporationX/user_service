package school.faang.user_service.mapper;

import com.json.student.PersonSchemaForUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
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
    User toEntity(UserDto userDto);


    @Mapping(target = "followedUserIds", source = "followers", qualifiedByName = "usersToIds")
    @Mapping(target = "preferredContact", source = "contactPreference.preference")
    UserDto toDto(User user);
  
   default PreferredContact mapPreferredContact(PreferredContact preferredContact) {
        return preferredContact != null ? preferredContact : PreferredContact.EMAIL;
    }
  
    List<UserDto> toDto(List<User> users);

    @Named("usersToIds")
    default List<Long> mapUsersToIds(List<User> value) {
        if (value == null) {
            return new ArrayList<>();
        }
        return value.stream().map(User::getId).toList();
    }

    UserDto personToUserDto(PersonSchemaForUser person);

    default CountryDto mapCountry(String country) {
        CountryDto countryDto = new CountryDto();
        countryDto.setTitle(country);
        return countryDto;
    }
}