package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MentorshipRequestMapper {
    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest);
}
