package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "Spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {
    @Mapping(source = "requester",target = "requesterId", qualifiedByName = "getUserId")
    @Mapping(source = "receiver",target = "idReceiver", qualifiedByName = "getUserId")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    List<MentorshipRequestDto> toDto(List<MentorshipRequest> mentorshipRequests);

    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Named("getUserId")
    default long getUserId(User user){
        return user.getId();
    }
}
