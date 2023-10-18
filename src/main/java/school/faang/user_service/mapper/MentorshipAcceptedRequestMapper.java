package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipAcceptedEventDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface MentorshipAcceptedRequestMapper {
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MentorshipAcceptedEventDto toEventDto(MentorshipRequest mentorshipRequest);
}
