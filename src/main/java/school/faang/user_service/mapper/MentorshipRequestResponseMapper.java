package school.faang.user_service.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipRequestResponseDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestResponseMapper {

  @Mapping(target = "requester", ignore = true)
  @Mapping(target = "receiver", ignore = true)
  MentorshipRequest toEntity(MentorshipRequestResponseDto mentorshipRequestResponseDto);

  @Mapping(target = "requester", ignore = true)
  @Mapping(target = "receiver", ignore = true)
  List<MentorshipRequest> toEntityList(
      List<MentorshipRequestResponseDto> mentorshipRequestResponseDto);

  @Mapping(source = "requester.id", target = "requesterId")
  @Mapping(source = "receiver.id", target = "receiverId")
  MentorshipRequestResponseDto toDto(MentorshipRequest mentorshipRequest);

  @Mapping(source = "requester.id", target = "requesterId")
  @Mapping(source = "receiver.id", target = "receiverId")
  List<MentorshipRequestResponseDto> toDtoList(List<MentorshipRequest> mentorshipRequest);
}
