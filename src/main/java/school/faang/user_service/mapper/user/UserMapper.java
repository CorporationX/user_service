package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Locale;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    List<UserDto> toDtoList(List<User> users);

    @Mapping(source = "country", target = "locale", qualifiedByName = "mapCountryToLocale")
    @Mapping(source = "contactPreference.preference", target = "preferredContact")
    UserDto toDto(User user);

    User toEntity(UserRegistrationDto userRegistrationDto);

    @Named("mapCountryToLocale")
    default Locale mapCountryToLocale(Country country) {
        if (country == null || country.getLocaleCode() == null) {
            return null;
        }
        return Locale.forLanguageTag(country.getLocaleCode());
    }
}
