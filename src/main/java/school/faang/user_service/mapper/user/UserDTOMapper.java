package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.subscribe.UserDTO;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {

    List<UserDTO> toDTO(List<User> users);

}
