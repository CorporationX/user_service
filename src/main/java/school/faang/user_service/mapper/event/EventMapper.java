package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;


import java.util.Collections;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class EventMapper {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected SkillRepository skillRepository;
    @Mapping(source = "relatedSkills", target = "relatedSkillsIds", qualifiedByName = "mapIdsSkills")
    @Mapping(source = "owner.id", target = "ownerId")
    public abstract EventDto toDto(Event event);

    @Mapping(source = "relatedSkillsIds", target = "relatedSkills", qualifiedByName = "mapSkillIdsToSkills")
    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "mapOwnerIdToOwner")
    public abstract Event toEvent(EventDto eventDto);

    @Mapping(source = "relatedSkills", target = "relatedSkillsIds", qualifiedByName = "mapIdsSkills")
    @Mapping(source = "owner.id", target = "ownerId")
    public abstract List<EventDto> toDto(List<Event> events);

    @Mapping(source = "relatedSkills", target = "relatedSkillsIds", qualifiedByName = "mapIdsSkills")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.username", target = "ownerUsername")
    public abstract List<EventDto> toFilteredEventsDto(List<Event> events);

    @Mapping(source = "event.relatedSkills", target = "relatedSkillsIds", qualifiedByName = "mapIdsSkills")
    @Mapping(source = "event.owner.id", target = "ownerId")
    public abstract EventWithSubscribersDto toEventWithSubscribersDto(Event event, Integer subscribersCount);

    @Named("mapIdsSkills")
    protected List<Long> mapIdsSkills(List<Skill> skills) {
        return skills == null ? Collections.emptyList() : skills.stream()
                .map(Skill::getId)
                .toList();
    }
    @Named("mapOwnerIdToOwner")
    protected User mapOwnerIdToOwner(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }
    @Named("mapSkillIdsToSkills")
    protected List<Skill> mapSkillIdsToSkills(List<Long> relatedSkillsIds) {
        if (isEmpty(relatedSkillsIds)) {
            return Collections.emptyList();
        }
        return skillRepository.findByIds(relatedSkillsIds);
    }
}
