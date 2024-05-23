package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.MentorshipResponseDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MentorshipRequestMapper {

    @Mapping(target = "id", source = "mentorshipRequest.id")
    @Mapping(target = "status", source = "mentorshipRequest.status")
    MentorshipResponseDto mentorshipRequestToResponseDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    MentorshipRequestDto toRequestDto(MentorshipRequest mentorshipRequest);


    List<MentorshipRequestDto> toRequestDtoList(List<MentorshipRequest> requests);
}
