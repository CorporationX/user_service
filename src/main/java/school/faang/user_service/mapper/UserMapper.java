package school.faang.user_service.mapper;

import com.json.student.PersonSchemaForUser;
import org.mapstruct.*;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    @Mapping(target = "id", ignore = true) // Assuming you generate the ID elsewhere
    @Mapping(target = "password", ignore = true) // Password mapping not provided
    @Mapping(target = "country", source = "country")
    UserDto personToUserDto(PersonSchemaForUser person);

    default CountryDto mapCountry(String country) {
        // Implement your logic to convert the country string to a CountryDto object
        CountryDto countryDto = new CountryDto();
        countryDto.setTitle(country);
        return countryDto;
    }
}