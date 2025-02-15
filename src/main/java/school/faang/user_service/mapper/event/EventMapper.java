package school.faang.user_service.mapper.event;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.event.CreateEventRequestDto;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.dto.event.UpdateEventRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

@Mapper(
    componentModel = "spring",
    uses = {EventSkillMapper.class, EventUserMapper.class})
public abstract class EventMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "attendees", ignore = true)
  @Mapping(target = "ratings", ignore = true)
  @Mapping(target = "relatedSkills", source = "skills")
  @Mapping(target = "maxAttendees", source = "createRequest.maxAttendees")
  @Mapping(target = "type", source = "createRequest.eventType")
  @Mapping(target = "status", source = "createRequest.eventStatus", defaultValue = "PLANNED")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "owner", ignore = true)
  public abstract Event toEntity(CreateEventRequestDto createRequest, List<Skill> skills);

  @Mapping(target = "id", source = "updateRequest.id")
  @Mapping(target = "attendees", ignore = true)
  @Mapping(target = "ratings", ignore = true)
  @Mapping(target = "relatedSkills", source = "skills")
  @Mapping(target = "maxAttendees", source = "updateRequest.maxAttendees")
  @Mapping(target = "type", source = "updateRequest.eventType")
  @Mapping(target = "status", source = "updateRequest.eventStatus", defaultValue = "PLANNED")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "owner", ignore = true)
  public abstract Event toEntity(UpdateEventRequestDto updateRequest, List<Skill> skills);

  @Mapping(
      target = "relatedSkills",
      source = "relatedSkills",
      qualifiedByName = "mapSkillsToSkillIds")
  @Mapping(target = "ownerId", source = "owner.id")
  @Mapping(target = "eventType", source = "type")
  @Mapping(target = "eventStatus", source = "status")
  @Mapping(target = "createdAt", source = "createdAt")
  public abstract EventResponseDto toResponseDto(Event event);

  public List<EventResponseDto> toResponseDtoList(List<Event> events) {
    return events.stream().map(this::toResponseDto).collect(Collectors.toList());
  }

  @Named("mapSkillsToSkillIds")
  protected List<Long> mapSkillsToSkillIds(List<Skill> skills) {
    if (skills == null) {
      return null;
    }
    return skills.stream().map(Skill::getId).collect(Collectors.toList());
  }
}
