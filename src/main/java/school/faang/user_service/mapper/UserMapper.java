package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.EventUserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    EventUserDto userToUserDto(User user);

    User dtoUserToUser(EventUserDto eventUserDto);

    List<User> usersDtoToUsers(List<EventUserDto> eventUserDtoList);

    List<EventUserDto> usersToUsersDto(List<User> userList);
}
