package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface MentorshipMapper {

    @Mapping(source = "requesterId", target = "requester")
    @Mapping(source = "receiverId", target = "receiver")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(source = "requester", target = "requesterId")
    @Mapping(source = "receiver", target = "receiverId")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);
}
