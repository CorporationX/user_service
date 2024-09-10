package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.EventUserDto;
import school.faang.user_service.dto.MentorshipUserDto;

import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    EventUserDto userToUserDto(User user);
    MentorshipUserDto toMentorshipUserDto(User user);

    User dtoUserToUser(EventUserDto eventUserDto);
    List<MentorshipUserDto> toMentorshipUserDtos(List<User> users);
}

    List<User> userDtosToUsers(List<EventUserDto> eventUserDtos);

    List<EventUserDto> usersToUserDtos(List<User> users);
}