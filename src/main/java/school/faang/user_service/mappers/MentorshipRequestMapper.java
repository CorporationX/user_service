package school.faang.user_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {
    @Mapping( source = "requester",target = "requesterId",qualifiedByName = "map")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Named("map")
    default Long map(User user) {
        return user.getId();
    }
}
