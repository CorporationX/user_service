package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}
