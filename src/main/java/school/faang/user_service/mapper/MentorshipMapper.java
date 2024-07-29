package school.faang.user_service.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;

/**
 * Конвертер, из {@link User} в {@link MentorshipDto}
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface MentorshipMapper {

  MentorshipDto mentorshipDtoFromUser(User user);

}
