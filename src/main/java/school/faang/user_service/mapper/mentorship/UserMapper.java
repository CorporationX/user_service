package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper
@Component
public interface UserMapper {
    UserDto toDto(User user);
    List<UserDto> toDtoList(List<User> userList);
}
