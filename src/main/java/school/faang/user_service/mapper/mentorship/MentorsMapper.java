package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.GetMentorsResponse;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MentorsMapper {

    GetMentorsResponse toDto(User user);
}
