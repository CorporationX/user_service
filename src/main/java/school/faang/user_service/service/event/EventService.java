package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;

    public EventDto createEvent(EventDto event) {
        return event;
    }

    public void create(EventDto event){
        validateUserSkills(event);
        Event eventEntity=mapToEventEntity(event);
        Event saveEntity=eventRepository.save(eventEntity);
    }
    public void validateUserSkills(EventDto event){
    Long ownerID= event.getOwnerId();
    long userId;
    List<SkillDto> eventSkills=event.getRelatedSkills();
    if(!skillRepository.findUserSkill(ownerID)){
        throw new DataValidationException(" User does not have the required skill");
    }
    }

    public Event mapToEventEntity(EventDto event){

        return null;
    }

    public void mapToEventDto(EventDto event){

    }
}
