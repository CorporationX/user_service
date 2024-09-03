package school.faang.user_service.mapper_mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requester", target = "requesterId", qualifiedByName = "mapRequester")
    @Mapping(source = "receiver", target = "receiverId", qualifiedByName = "mapReceiver")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    default Long mapRequester(User requester) {
        return requester.getId();
    }

    default Long mapReceiver(User receiver) {
        return receiver.getId();
    }
}
