package school.faang.user_service.mapper.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.Mapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class EventCreateEditMapper implements Mapper<EventCreateEditDto, Event> {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Override
    public Event map(EventCreateEditDto fromObject, Event toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    @Override
    public Event map(EventCreateEditDto object) {
        Event event = new Event();
        copy(object, event);
        return event;
    }

    private void copy(EventCreateEditDto objectDto, Event toObject) {
        toObject.setTitle(objectDto.getTitle());
        toObject.setStartDate(objectDto.getStartDate());
        toObject.setEndDate(objectDto.getEndDate());
        toObject.setOwner(getEntity(objectDto.getOwnerId(), userRepository));
        toObject.setDescription(objectDto.getDescription());
        toObject.setLocation(objectDto.getLocation());
        toObject.setRelatedSkills(getEntities(objectDto.getRelatedSkillIds(), skillRepository));
        toObject.setMaxAttendees(objectDto.getMaxAttendees());
        toObject.setStatus(objectDto.getEventStatus());
        toObject.setType(objectDto.getEventType());
    }
}
