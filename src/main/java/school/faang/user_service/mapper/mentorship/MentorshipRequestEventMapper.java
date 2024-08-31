package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.event.MentorshipRequestEvent;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestEventMapper {


    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "requestedAt", source = "createdAt")
    MentorshipRequestEvent toEvent(MentorshipRequest mentorshipRequest);
}
