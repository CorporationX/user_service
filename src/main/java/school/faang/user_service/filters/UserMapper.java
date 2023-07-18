package school.faang.user_service.filters;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto userToDto(User user);
    User dtoToUser(UserDto userDto);
    List<UserDto> toUserDtoList(List<User> users);
    List<User> toUserList(List<UserDto> userDtoList);
}
