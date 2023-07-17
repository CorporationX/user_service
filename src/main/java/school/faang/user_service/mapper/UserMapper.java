package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
}
