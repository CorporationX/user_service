package school.faang.user_service.mapper;

import com.json.student.PersonSchemaForUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "contactPreference", source = "preferredContact")
    User toEntity(UserDto userDto);

    default PreferredContact mapPreferredContact(PreferredContact preferredContact) {
        return preferredContact != null ? preferredContact : PreferredContact.EMAIL;
    }

    @Mapping(target = "preferredContact", source = "contactPreference.preference")
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    UserDto personToUserDto(PersonSchemaForUser person);

    default CountryDto mapCountry(String country) {
        CountryDto countryDto = new CountryDto();
        countryDto.setTitle(country);
        return countryDto;
    }
}