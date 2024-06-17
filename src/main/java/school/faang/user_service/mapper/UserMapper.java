package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDto toDto(User user);

    @Mapping(target = "country", ignore = true)
    @Mapping(target = "contactPreference", ignore = true)
    User toEntity(UserDto user);

    List<UserDto> toDto(List<User> users);
}
