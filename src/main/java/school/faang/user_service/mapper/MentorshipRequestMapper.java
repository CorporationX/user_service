package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(source = "requesterId", target = "requester", qualifiedByName = "mapRequester")
    @Mapping(source = "receiverId", target = "receiver", qualifiedByName = "mapReceiver")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Named("mapRequester")
    default User mapRequester(Long requesterId) {
        if(requesterId == null) {
            return null;
        }
        User requester = new User();
        requester.setId(requesterId);
        return requester;
    }

    @Named("mapReceiver")
    default User mapReceiver(Long receiverId) {
        if(receiverId == null) {
            return null;
        }
        User receiver = new User();
        receiver.setId(receiverId);
        return receiver;
    }
}
