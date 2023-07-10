package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(target = "requester", source = "requesterId", qualifiedByName = "mapRequester")
    @Mapping(target = "receiver", source = "receiverId", qualifiedByName = "mapReceiver")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    User map(Long value);

    @Named("mapRequester")
    default User mapRequester(Long requesterId) {
        if (requesterId == null) {
            return null;
        }
        User requester = new User();
        requester.setId(requesterId);
        return requester;
    }

    @Named("mapReceiver")
    default User mapReceiver(Long receiverId) {
        if (receiverId == null) {
            return null;
        }
        User receiver = new User();
        receiver.setId(receiverId);
        return receiver;
    }
}
