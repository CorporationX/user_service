package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "rejectionReason", target = "rejectionReason")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "requesterId", target = "requester", qualifiedByName = "mapToUser")
    @Mapping(source = "receiverId", target = "receiver", qualifiedByName = "mapToUser")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "rejectionReason", target = "rejectionReason")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Named("mapToUser")
    default User mapToUser(Long id) {
        if (id == null || id == 0) {
            return null;
        }
        return User.builder().id(id).build();
    }

    List<MentorshipRequestDto> toDto(List<MentorshipRequest> request);
}
