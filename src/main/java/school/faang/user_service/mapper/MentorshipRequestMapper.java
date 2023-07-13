package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.util.validator.FilterRequestStatusValidator;

@Component
@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface MentorshipRequestMapper {

    // не стал тут внедрять зависимость mentorshipRequestService, иначе возникает циклическая зависимость между этим бином и сервисом, т.к. в сервисе вызывается этот бин

    @Mapping(target = "status", expression = "java(school.faang.user_service.entity.RequestStatus.PENDING)") // ставлю по умолчанию pending
    @Mapping(target = "requester", expression = "java(mentorshipRequestService.findUserById(dto.getRequesterId()))")
    @Mapping(target = "receiver", expression = "java(mentorshipRequestService.findUserById(dto.getReceiverId()))")
    MentorshipRequest toEntity(MentorshipRequestDto dto, MentorshipRequestService mentorshipRequestService); // поэтому вынес сервис в аргументы

    @Mapping(target = "requester", expression = "java(validator.validateRequester(dto, mentorshipRequestService))")
    @Mapping(target = "receiver", expression = "java(validator.validateReceiver(dto, mentorshipRequestService))")
    @Mapping(target = "status", expression = "java(validator.validateStatus(dto))")
    MentorshipRequest toEntity(RequestFilterDto dto, MentorshipRequestService mentorshipRequestService,
                               FilterRequestStatusValidator validator); // работает даже если все поля = null

    @Mapping(target = "requesterId", expression = "java(mentorshipRequest.getRequester().getId())")
    @Mapping(target = "receiverId", expression = "java(mentorshipRequest.getReceiver().getId())")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);
}
