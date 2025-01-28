package school.faang.user_service.mapper.event.partcipation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.participant.EventParticipationDto;
import school.faang.user_service.dto.event.participant.UserParticipationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventParticipationMapper {
    Event toEntity(EventParticipationDto eventParticipationDto);

    EventParticipationDto toDto(Event event);
    List<Event> toEventList(List<EventParticipationDto> events);
}