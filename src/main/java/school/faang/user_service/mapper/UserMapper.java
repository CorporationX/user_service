package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipUserDto;

import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    MentorshipUserDto toMentorshipUserDto(User user);

    List<MentorshipUserDto> toMentorshipUserDtos(List<User> users);
}
