package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
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

    public EventDto updateEvent(EventDto eventFormRequest) {
        eventValidator.checkIfUserHasSkillsRequired(eventFormRequest);
        eventRepository.save(eventMapper.toEntity(eventFormRequest));
        return eventFormRequest;
      
    public EventDto getEvent(long id) {
        Event entity = eventRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Event not found"));
        return eventMapper.toDto(entity);
    }
}