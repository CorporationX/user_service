package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.user.MentorshipRequest;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface MentorshipRequestMapper {

    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MentorshipRequest toEntity(CreateMentorshipRequestDto dto);

}
