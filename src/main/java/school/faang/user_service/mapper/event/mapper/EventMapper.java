package school.faang.user_service.mapper.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.skill.mapper.SkillMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillMapper.class)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    EventDto eventToDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    Event eventDtoToEvent(EventDto eventDto);

    EventFilterDto eventToEventFilterDto(Event event);

    Event eventFilterDtoToEvent(EventFilterDto eventFilterDto);
}