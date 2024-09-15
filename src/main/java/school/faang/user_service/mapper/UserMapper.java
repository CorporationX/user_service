package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.CountDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    CountDto toCountDto(Integer count);
}
