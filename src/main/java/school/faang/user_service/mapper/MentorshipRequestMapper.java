package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requester.id", target = "requester")
    @Mapping(source = "receiver.id", target = "receiver")
    MentorshipRequestDto toDTO(MentorshipRequest mentorshipEntity);

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipDTO);

    @Mapping(source = "requester.id", target = "requester")
    @Mapping(source = "receiver.id", target = "receiver")
    List<MentorshipRequestDto> toDtoList(List<MentorshipRequest> mentorshipEntity);

}

