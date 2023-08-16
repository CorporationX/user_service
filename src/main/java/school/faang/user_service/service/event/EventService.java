package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> filters;


    public EventDto create(EventDto event) {
        checkIfOwnerHasRequiredSkills(event);
        return eventMapper.toDTO(eventRepository.save(eventMapper.toEvent(event)));
    }

    private void checkIfOwnerHasRequiredSkills(EventDto event) {
        User user = userRepository.findById(event.getOwnerId()).orElseThrow(() -> new DataValidationException("Owner doesn't found"));
        if (!ownerHasSkills(event, user)) {
            throw new DataValidationException("Owner hasn't required skills");
        }
    }

    private boolean ownerHasSkills(EventDto event, User user) {
        return user.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet())
                .containsAll(
                        event.getRelatedSkills().stream()
                                .map(SkillDto::getId)
                                .collect(Collectors.toSet()));

    }

    public EventDto getEvent(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no event with this id"));
        return eventMapper.toDTO(event);
    }

    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    public EventDto updateEvent(EventDto eventDto) {
        checkIfOwnerHasRequiredSkills(eventDto);
        Event event = eventRepository.findById(eventDto.getId()).orElseThrow(() -> new DataValidationException("Event not found"));
        Event udatedEvent = eventMapper.update(eventDto, event);
        return eventMapper.toDTO(eventRepository.save(udatedEvent));
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return Optional.ofNullable(eventRepository.findAllByUserId(userId))
                .orElse(new ArrayList<>())
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return Optional.ofNullable(eventRepository.findParticipatedEventsByUserId(userId))
                .orElse(new ArrayList<>())
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> events = eventRepository.findAll().stream();
        List<EventFilter> f = filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .toList();
        for (EventFilter eventFilter : f) {
            events = eventFilter.apply(events, filter);
        }
        return events.map(eventMapper::toDTO).toList();
    }
}
