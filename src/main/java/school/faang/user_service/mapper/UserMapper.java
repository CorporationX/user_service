package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.notification.UserChatIdUpdateDto;
import school.faang.user_service.dto.notification.UserNotificationDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    List<UserDto> usersToUserDtos(List<User> users);

    void updateUserChatId(@MappingTarget User user, UserChatIdUpdateDto userChatIdUpdateDto);

    @Mapping(target = "preference", source = "contactPreference.preference")
    UserNotificationDto toNotificationDto(User userById);

    List<MentorshipResponseDto> toMentorshipDtos(List<User> users);
}
