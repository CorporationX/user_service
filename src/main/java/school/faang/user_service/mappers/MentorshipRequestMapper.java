package school.faang.user_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {
    @Mapping(source = "requester", target = "requesterId", qualifiedByName = "requesterMap")
    @Mapping(source = "receiver", target = "receiverId", qualifiedByName = "receiverMap")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Named("requesterMap")
    default Long requesterMap(User user) {
        return user.getId();
    }

    @Named("receiverMap")
    default Long receiverMap(User user) {
        return user.getId();
    }
}
