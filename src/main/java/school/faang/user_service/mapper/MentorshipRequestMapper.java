package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRejectDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {
    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest);
    MentorshipRequest MentorshipRequestToEntity(MentorshipRequestDto mentorshipRequestDto);

    MentorshipRejectDto toRejectionDto(MentorshipRequest mentorshipRequest);

    MentorshipRequestMapper INSTANCE = Mappers.getMapper(MentorshipRequestMapper.class);
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "status", target = "status")
    List<RequestFilterDto> toRequestFilterDtoList(List<MentorshipRequest> mentorshipRequestList);

}
