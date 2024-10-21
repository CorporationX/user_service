package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.MentorshipAcceptedEventDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.request.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    MentorshipRequest toMentorshipRequest(MentorshipRequestCreationDto mentorshipRequestDto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest);

    List<MentorshipRequestDto> toMentorshipRequestDtos(List<MentorshipRequest> mentorshipRequestList);

    @Mapping(source = "id", target = "requestId")
    @Mapping(source = "requester.id", target = "menteeId")
    @Mapping(source = "receiver.id", target = "mentorId")
    MentorshipAcceptedEventDto toMentorshipAcceptedEventDto(MentorshipRequest mentorshipRequest);
}
