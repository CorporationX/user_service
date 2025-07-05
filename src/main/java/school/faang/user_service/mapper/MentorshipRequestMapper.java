package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.user.MentorshipRequest;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {
    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest);
}