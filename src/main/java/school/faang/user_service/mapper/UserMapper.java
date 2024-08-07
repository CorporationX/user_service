package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "country.title", target = "countryTitle")
    UserDto toDto(User user);
    @Mapping(source = "countryTitle", target = "country.title")
    User toEntity(UserDto userDto);
}
