package school.faang.user_service.EventOrganization.mapper.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.EventOrganization.dto.event.EventDto;
import school.faang.user_service.EventOrganization.dto.event.EventFilterDto;
import school.faang.user_service.EventOrganization.mapper.skill.mapper.SkillMapper;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillMapper.class)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "getSkillDto")
    EventDto EventToDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "getSkills")
    Event EventDtoToEvent(EventDto eventDto);

    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "getSkillDto")
    EventFilterDto EventToEventFilterDto(Event event);

    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "getSkills")
    Event EventFilterDtoToEvent(EventFilterDto eventFilterDto);
}