package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.entity.User;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
@Mapper(componentModel = "spring")
public interface MentorshipMapper {

    MentorshipUserDto toUserDto(User user);

    List<MentorshipUserDto> toListUserDto(List<User> users);
}
