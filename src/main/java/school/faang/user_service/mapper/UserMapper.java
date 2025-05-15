package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.RegisterParticipantRequestDto;
import school.faang.user_service.dto.UserResponseDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    RegisterParticipantRequestDto toRegisterParticipantRequestDto(User user);

    List<RegisterParticipantRequestDto> toRegisterParticipantRequestDtoList(List<User> users);

    UserResponseDto toUserResponseDto(User user);
}
