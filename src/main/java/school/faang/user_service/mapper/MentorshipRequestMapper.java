package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface MentorshipRequestMapper {

    @Mapping(target = "requesterId", source = "mentorshipRequest.requester.id")
    @Mapping(target = "receiverId", source = "mentorshipRequest.receiver.id")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "requesterId", source = "mentorshipRequest.requester.id")
    @Mapping(target = "receiverId", source = "mentorshipRequest.receiver.id")
    List<MentorshipRequestDto> toDto(List<MentorshipRequest> mentorshipRequests);

    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);
}
