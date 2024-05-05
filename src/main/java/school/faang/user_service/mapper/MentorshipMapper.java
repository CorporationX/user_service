package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipMapper extends AbstractMapper<MentorshipRequest, MentorshipRequestDto>{

    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "requesterId", source = "requester.id")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "requesterId",target = "requester.id")
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);
}
