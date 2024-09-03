package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requester.id", target = "userRequesterId")
    @Mapping(source = "receiver.id", target = "userReceiverId")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Mapping(target = "requester", ignore = true )
    @Mapping(target = "receiver", ignore = true )
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);
}
