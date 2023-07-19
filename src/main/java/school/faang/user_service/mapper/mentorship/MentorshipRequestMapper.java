package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapRequestStatusToString")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(source = "requesterId", target = "requester", qualifiedByName = "mapToUser")
    @Mapping(source = "receiverId", target = "receiver", qualifiedByName = "mapToUser")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapStringToRequestStatus")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Named("mapToUser")
    default User mapToUser(Long id) {
        return User.builder().id(id).build();
    }

    @Named("mapStringToRequestStatus")
    default RequestStatus mapToRequestStatus(String status) {
        return RequestStatus.valueOf(status.toUpperCase());
    }

    @Named("mapRequestStatusToString")
    default String mapFromRequestStatus(RequestStatus status) {
        return status.toString().toUpperCase();
    }
}
