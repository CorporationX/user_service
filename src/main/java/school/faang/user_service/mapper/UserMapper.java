package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.mentee.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    List<UserDto> toUserListDto (List<User> userList);
    UserDto toUserDto (User user);
}
