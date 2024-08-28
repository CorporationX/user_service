package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.event.mentorship.request.MentorshipAcceptedEvent;
import school.faang.user_service.event.mentorship.request.MentorshipOfferedEvent;
import school.faang.user_service.event.mentorship.request.MentorshipRequestedEvent;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "requestStatus", target = "status")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "status", target = "requestStatus")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "id", target = "mentorshipRequestId")
    MentorshipAcceptedEvent toMentorshipAcceptedEvent(MentorshipRequest mentorshipRequest);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    MentorshipRequestedEvent toMentorshipRequestedEvent(MentorshipRequest mentorshipRequest);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "id", target = "mentorshipOfferId")
    MentorshipOfferedEvent toMentorshipOfferedEvent(MentorshipRequest mentorshipRequest);
}
