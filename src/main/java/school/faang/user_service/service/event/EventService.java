package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto event) {
        validateEventSkills(event);
        var eventEntity = eventRepository.save(eventMapper.toEntity(event));
        return eventMapper.toDto(eventEntity);
    }

    public EventDto getEvent(long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id: %d was not found", eventId)));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<Event> events = new ArrayList<>();
        eventRepository.findAll().forEach(events::add);
        var filteredEvents = events.stream()
                .filter(event -> filterMatches(event, filter))
                .map(eventMapper::toDto)
                .toList();
        if (filteredEvents.isEmpty()) {
            throw new NotFoundException("No events with current filters");
        }
        return filteredEvents;
    }

    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    private void validateEventSkills(EventDto event) {
        long ownerId = event.getOwnerId();
        var relatedSkills = event.getRelatedSkills();
        var userSkills = skillRepository.findAllByUserId(ownerId);

        for (SkillDto skill : relatedSkills) {
            boolean userHasSkill = userSkills.stream()
                    .anyMatch(userSkill -> userSkill.getId() == skill.getId());
            if (!userHasSkill) {
                throw new DataValidationException("User does not have the required skills for the event");
            }
        }
    }

    public boolean filterMatches(Event event, EventFilterDto filter) {
        if (filter.getTitle() != null && !event.getTitle().contains(filter.getTitle())) {
            return false;
        }
        if (filter.getStartDate() != null && filter.getStartDate().isAfter(event.getEndDate())) {
            return false;
        }
        if (filter.getEndDate() != null && filter.getEndDate().isBefore(event.getEndDate())) {
            return false;
        }
        if (filter.getOwnerId() != null && !(event.getOwner().getId() == filter.getOwnerId())) {
            return false;
        }
        if (filter.getLocation() != null && !event.getLocation().equals(filter.getLocation())) {
            return false;
        }
        if (filter.getMaxAttendees() != null && filter.getMaxAttendees() > event.getMaxAttendees()) {
            return false;
        }
        if (filter.getRelatedSkillIds() != null && !filter.getRelatedSkillIds().isEmpty() && event.getRelatedSkills() != null) {
            var eventSkillsIds = event.getRelatedSkills()
                    .stream()
                    .map(Skill::getId)
                    .toList();
            // приведение к hashset для повышения производительности
            if (!(new HashSet<>(eventSkillsIds).containsAll(filter.getRelatedSkillIds()))) {
                return false;
            }
        }
        return true;
    }
}
