package school.faang.user_service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.UserDto;
 Murloc-master-stream7
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserDto userToUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    List<UserDto> userListToUserDtoList(List<User> users);
 Murloc-master-stream7
}
