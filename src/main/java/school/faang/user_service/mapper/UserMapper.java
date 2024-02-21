package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)

public interface UserMapper {
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDto toDto(User user);

@Mapping(source = "countryId", target = "country.id")
@Mapping(source = "preference", target = "contactPreference.preference")
    User toEntity(UserDto userDto);

    List<UserDto> toDto(List<User> users);
}