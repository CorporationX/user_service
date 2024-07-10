package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "active", target = "isActive")
    UserDto toDto(User user);

    @Mapping(source = "isActive", target = "active")
    User toEntity(UserDto userDto);

    List<UserDto> usersToUserDTOs(List<User> users);
}
