package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipRequestDtoForRequest;
import school.faang.user_service.dto.MentorshipRequestDtoForResponse;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MentorshipRequestDtoForResponse toDto(MentorshipRequest mentorshipRequest);

    List<MentorshipRequestDtoForResponse> toDto(List<MentorshipRequest> requests);

    Stream<MentorshipRequestDtoForResponse> toDto(Stream<MentorshipRequest> requests);

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    MentorshipRequest toEntity(MentorshipRequestDtoForRequest mentorshipRequestDto);
}
