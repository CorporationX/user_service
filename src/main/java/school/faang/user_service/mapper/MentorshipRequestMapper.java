package school.faang.user_service.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = MentorshipRequestMapperDecorator.class)
@DecoratedWith(MentorshipRequestMapperDecorator.class)
public interface MentorshipRequestMapper {

    @Mapping(target = "requester", source = "requesterId", qualifiedByName = "mapUserIdToUser")
    @Mapping(target = "receiver", source = "receiverId", qualifiedByName = "mapUserIdToUser")
    @Mapping(target = "status", constant = "PENDING")
    MentorshipRequest toEntity(MentorshipRequestDto dto);

    @Mapping(target = "status", constant = "REJECTED")
    MentorshipRequest updateRequestFromDto(RejectionDto rejectionDto,
                                           @MappingTarget MentorshipRequest mentorshipRequest);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    MentorshipRequestDto toDto(MentorshipRequest request);

}