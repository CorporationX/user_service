package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);
    User toEntity(UserDto userDto);

    default List<UserDto> toDto(List<User> users){
        List<UserDto> userDtos= new ArrayList<>();
        for (User user:users){
            userDtos.add(toDto(user));
        }
        return userDtos;
    }
}
