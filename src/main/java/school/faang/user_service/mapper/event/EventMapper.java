package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.skill.SkillMapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = SkillMapper.class
)
public interface EventMapper {
    @Mapping(target = "owner", ignore = true)
    Event toEntity(EventDto eventDto);

    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event event);

    List<EventDto> toListDto(List<Event> events);
}