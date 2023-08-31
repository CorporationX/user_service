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

    @Mapping(source = "requesterId", target = "requester", qualifiedByName = "mapToUser")
    @Mapping(source = "receiverId", target = "receiver", qualifiedByName = "mapToUser")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Named("mapToUser")
    default User mapToUser(Long id) {
        if (id == null) {
            return null;
        }
        return User.builder().id(id).build();
    }
}
