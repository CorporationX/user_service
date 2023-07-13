package school.faang.user_service.service.event;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;

    @Autowired
    public EventDto createEvent(EventDto eventDto) {
        eventValidator.checkIfUserHasSkillsRequired(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.findById(id).orElseThrow(() -> new DataValidationException("Event not found"));
        eventRepository.deleteById(id);
    }

    public EventDto updateEvent(Long id, EventDto eventFormRequest) {
        Event eventForUpdate = eventRepository.findById(id).orElseThrow(() -> new DataValidationException("Event not found"));
        EventDto eventDtoForUpdate = eventMapper.toDto(eventForUpdate);
        updateEventInDb(eventDtoForUpdate, eventFormRequest);

        return eventDtoForUpdate;
    }

    private void updateEventInDb(EventDto eventForUpdate, EventDto eventFormRequest) {
        {
            eventValidator.checkIfUserHasSkillsRequired(eventFormRequest);
            if (eventFormRequest.getTitle() != null) {
                eventForUpdate.setTitle(eventFormRequest.getTitle());
            }
            if (eventFormRequest.getStartDate() != null) {
                eventForUpdate.setStartDate(eventFormRequest.getStartDate());
            }
            if (eventFormRequest.getEndDate() != null) {
                eventForUpdate.setEndDate(eventFormRequest.getEndDate());
            }
            if (eventFormRequest.getDescription() != null) {
                eventForUpdate.setDescription(eventFormRequest.getDescription());
            }

            if (eventFormRequest.getLocation() != null) {
                eventForUpdate.setLocation(eventFormRequest.getLocation());
            }

            if (eventFormRequest.getMaxAttendees() >= 0) {
                eventForUpdate.setMaxAttendees(eventFormRequest.getMaxAttendees());
            }

            if (eventFormRequest.getRelatedSkills() != null) {
                eventForUpdate.setRelatedSkills(eventFormRequest.getRelatedSkills());
            }
            eventRepository.save(eventMapper.toEntity(eventForUpdate));
        }

    }
}