package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.event.CreateEventRequestDto;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.dto.event.UpdateEventRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {EventSkillMapper.class})
public abstract class EventMapper {

    @Autowired
    private SkillRepository skillRepository;

    public static final EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "relatedSkills", source = "relatedSkills", qualifiedByName = "mapSkillIdsToSkills")
    @Mapping(target = "maxAttendees", source = "maxAttendees")
    @Mapping(target = "type", source = "eventType")
    @Mapping(target = "status", source = "eventStatus", defaultValue = "PLANNED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    public abstract Event toEntity(CreateEventRequestDto createRequest);

    @Mapping(target = "relatedSkills", source = "relatedSkills", qualifiedByName = "mapSkillIdsToSkills")
    @Mapping(target = "maxAttendees", source = "maxAttendees")
    @Mapping(target = "type", source = "eventType")
    @Mapping(target = "status", source = "eventStatus", defaultValue = "PLANNED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    public abstract Event toEntity(UpdateEventRequestDto updateRequest);

    @Mapping(target = "relatedSkills", source = "relatedSkills", qualifiedByName = "mapSkillsToSkillIds")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "eventStatus", source = "status")
    public abstract EventResponseDto toResponseDto(Event event);

    public List<EventResponseDto> toResponseDtoList(List<Event> events) {
        return events.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Named("mapSkillIdsToSkills")
    protected List<Skill> mapSkillIdsToSkills(List<Long> skillIds) {
        if (skillIds == null) {
            return null;
        }
        return skillIds.stream()
                .map(skillId -> skillRepository.findById(skillId)
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skillId)))
                .collect(Collectors.toList());
    }

    @Named("mapSkillsToSkillIds")
    protected List<Long> mapSkillsToSkillIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}