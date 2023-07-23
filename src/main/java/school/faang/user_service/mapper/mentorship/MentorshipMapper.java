package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface MentorshipMapper {
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    MentorshipRequest toEmpty(MentorshipRequestDto mentorshipRequestDto);
}
