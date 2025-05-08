package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestFilterMapper {
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RequestFilterDto toDto(MentorshipRequest mentorshipRequest);

    List<RequestFilterDto> toListDto(List<MentorshipRequest> mentorshipRequests);
}
