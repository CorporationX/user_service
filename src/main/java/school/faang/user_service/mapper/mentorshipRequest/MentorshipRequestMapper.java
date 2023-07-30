package school.faang.user_service.mapper.mentorshipRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.mentorshipRequest.MentorshipRequestService;
import school.faang.user_service.util.mentorshipRequest.validator.FilterRequestStatusValidator;

@Component
@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface MentorshipRequestMapper {

    @Mapping(target = "status", expression = "java(school.faang.user_service.entity.RequestStatus.PENDING)") // ставлю по умолчанию pending
    @Mapping(target = "requester.id", source = "requesterId")
    @Mapping(target = "receiver.id", source = "receiverId")
    MentorshipRequest toEntity(MentorshipRequestDto dto);

//    @Mapping(target = "requester", expression = "java(validator.validateRequester(dto, mentorshipRequestService))")
//    @Mapping(target = "receiver", expression = "java(validator.validateReceiver(dto, mentorshipRequestService))")
//    @Mapping(target = "status", expression = "java(validator.validateStatus(dto))")
//    MentorshipRequest toEntity(RequestFilterDto dto, MentorshipRequestService mentorshipRequestService,
//                               FilterRequestStatusValidator validator); // работает даже если все поля = null

    @Mapping(target = "requesterId", expression = "java(mentorshipRequest.getRequester().getId())")
    @Mapping(target = "receiverId", expression = "java(mentorshipRequest.getReceiver().getId())")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);
}
