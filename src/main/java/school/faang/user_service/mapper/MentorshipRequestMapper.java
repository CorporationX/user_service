package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.MentorshipAcceptedDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipEventDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipOfferRequestSentDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Component
@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface MentorshipRequestMapper {

    @Mapping(target = "status", expression = "java(school.faang.user_service.entity.RequestStatus.PENDING)") // ставлю по умолчанию pending
    @Mapping(target = "requester.id", source = "requesterId")
    @Mapping(target = "receiver.id", source = "receiverId")
    MentorshipRequest toEntity(MentorshipRequestDto dto);

    @Mapping(target = "requesterId", expression = "java(mentorshipRequest.getRequester().getId())")
    @Mapping(target = "receiverId", expression = "java(mentorshipRequest.getReceiver().getId())")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    MentorshipAcceptedDto toAcceptedDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    MentorshipOfferRequestSentDto toRequestDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "mentorId", source = "receiver.id")
    @Mapping(target = "userId", source = "requester.id")
    MentorshipEventDto toEventDto(MentorshipRequest mentorshipRequest);
}
