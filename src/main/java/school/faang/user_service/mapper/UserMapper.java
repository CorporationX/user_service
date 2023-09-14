package school.faang.user_service.mapper;

import com.json.student.PersonSchemaForUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "preferredContact", target = "contactPreference.preference")
    User toEntity(UserDto userDto);

    @Mapping(source = "contactPreference.preference", target = "preferredContact")
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    UserDto personToUserDto(PersonSchemaForUser person);

    default CountryDto mapCountry(String country) {
        CountryDto countryDto = new CountryDto();
        countryDto.setTitle(country);
        return countryDto;
    }
}