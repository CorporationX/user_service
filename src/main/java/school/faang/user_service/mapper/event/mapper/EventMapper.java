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
        //@Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "getSkillDto")
    EventDto eventToDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
        // @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "getSkills")
    Event eventDtoToEvent(EventDto eventDto);

    //@Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "getSkillDto")
    EventFilterDto eventToEventFilterDto(Event event);

    //@Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "getSkills")
    Event eventFilterDtoToEvent(EventFilterDto eventFilterDto);
}