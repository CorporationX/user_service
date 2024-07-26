package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserDto userDto);

    UserDto toDto(User user);
}
