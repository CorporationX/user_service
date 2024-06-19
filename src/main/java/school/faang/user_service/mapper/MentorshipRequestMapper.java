package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper
public interface MentorshipRequestMapper {
    @Mapping(source = "requester.id", target = "userId")
    @Mapping(source = "receiver.id", target = "mentorId")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);
}