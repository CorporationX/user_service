package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;
import school.faang.user_service.dto_mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "requester", ignore = true)
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    RejectionDto toDto(long id, MentorshipRequest mentorshipRequest);

    MentorshipRequest toEntity(long id, RejectionDto rejectionDto);
}
