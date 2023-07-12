package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;

@Component
@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface MentorshipRequestMapper {

    // не стал тут внедрять зависимость mentorshipRequestService, иначе возникает циклическая зависимость между этим бином и сервисом, т.к. в сервисе вызывается этот бин

    @Mapping(target = "status", expression = "java(school.faang.user_service.entity.RequestStatus.PENDING)") // ставлю по умолчанию pending
    @Mapping(target = "requester", expression = "java(mentorshipRequestService.findUserById(dto.getRequesterId()))")
    @Mapping(target = "receiver", expression = "java(mentorshipRequestService.findUserById(dto.getReceiverId()))")
    MentorshipRequest toEntity(MentorshipRequestDto dto, MentorshipRequestService mentorshipRequestService); // поэтому вынес сервис в аргументы
}
