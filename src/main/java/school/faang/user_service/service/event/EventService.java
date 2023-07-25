package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
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
        validate(event);
        return eventMapper.toDTO(eventRepository.save(eventMapper.toEvent(event)));
    }

    private void validate(EventDto event) {
        User user = userRepository.findById(event.getOwnerId()).orElseThrow(() -> new DataValidationException("Owner doesn't found"));
        if (!ownerHasSkills(event, user)) {
            throw new DataValidationException("Owner hasn't required skills");
        }
    }

    private boolean ownerHasSkills(EventDto event, User user) {

        return user.getSkills().stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet())
                .containsAll(
                        event.getRelatedSkills().stream()
                                .map(SkillDto::getTitle)
                                .collect(Collectors.toSet()));

    }

    public EventDto getEvent(long id) {

        if (id <= 0) {
            throw new DataValidationException("ID is incorrect");
        }

        return eventMapper.toDTO(
                eventRepository
                        .findById(id)
                        .orElseThrow(() -> new DataValidationException("There is no event with this id"))
        );
    }

    public void deleteEvent(long id) {
        if (id > 0) {
            eventRepository.deleteById(id);
        }
    }

    public EventDto updateEvent(EventDto eventDto) {
        validate(eventDto);
        Event event = eventRepository.findById(eventDto.getId()).orElseThrow(() -> new DataValidationException("Event not found"));
        return eventMapper.toDTO(eventRepository.save(eventMapper.update(eventDto, event)));
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
        return filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .flatMap(eventFilter -> eventFilter.apply(events, filter))
                .map(eventMapper::toDTO)
                .toList();
    }
}
