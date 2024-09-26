package school.faang.user_service.mapper.mentorshiprequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "localDateTime")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    @Named("localDateTime")
    default LocalDate localDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }
}
