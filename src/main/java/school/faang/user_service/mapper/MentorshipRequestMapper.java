package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDtoForRequest;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDtoForResponse;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.event.MentorshipEvent;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MentorshipRequestDtoForResponse toDto(MentorshipRequest mentorshipRequest);

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    MentorshipRequest toEntity(MentorshipRequestDtoForRequest mentorshipRequestDto);

    @Mapping(source = "requester.id", target = "userId")
    @Mapping(source = "receiver.id", target = "mentorId")
    MentorshipEvent toEvent(MentorshipRequest mentorshipRequest);
}
