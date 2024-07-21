package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(source = "countryId", target = "country.id")
    User toEntity(UserDto userDto);

    @Mapping(source = "active", target = "isActive")
    List<UserDto> usersToUserDTOs(List<User> users);
}
