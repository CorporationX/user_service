package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.redis.EventStartDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.publisher.EventStartPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.exception.DataValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper = EventMapper.INSTANCE;
    private final SkillMapper skillMapper = SkillMapper.INSTANCE;
    private final List<EventFilter> filters;
    private final EventStartPublisher eventStartPublisher;

    public EventDto create(EventDto eventDto) {
        validateEventDto(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        notifyUsersThatEventStarted(event);
        return eventMapper.toDto(event);
    }

    public EventDto get(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        return EventMapper.INSTANCE.toDto(event);
    }

    public boolean deleteEvent(Long eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
            return true;
        }
        return false;
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilter) {
        Stream<EventDto> eventDtoStream = eventRepository.findAll().stream().map(eventMapper::toDto);
        for (EventFilter filter : filters) {
            if (filter.isApplicable(eventFilter)) {
                eventDtoStream = filter.apply(eventDtoStream, eventFilter);
            }
        }

        return eventDtoStream.toList();
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return EventMapper.INSTANCE.toListDto(eventRepository.findParticipatedEventsByUserId(userId));
    }

    public EventDto updateEvent(EventDto source) {
        validateEventDto(source);

        Event event = eventRepository.findById(source.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found. ID: " + source.getId()));
        EventDto target = eventMapper.toDto(event);

        BeanUtils.copyProperties(source, target, "id", "relatedSkills");

        if (source.getRelatedSkills() != null) {
            List<SkillDto> sourceSkills = new ArrayList<>(source.getRelatedSkills());
            sourceSkills.retainAll(target.getRelatedSkills());
            if (!sourceSkills.isEmpty()) {
                target.setRelatedSkills(source.getRelatedSkills());
            }
        }

        Event result = eventRepository.save(eventMapper.toEntity(target));
        notifyUsersThatEventStarted(result);
        return eventMapper.toDto(result);
    }

    private void validateEventDto(EventDto eventDto) {
        if (eventDto.getId() == null || eventDto.getId() < 1) {
            throw new DataValidException("Event Id must be greater than 0");
        }
        if (eventDto.getTitle().isBlank()) {
            throw new DataValidException("Event must have a title. Id: " + eventDto.getId());
        }
        if (eventDto.getStartDate() == null) {
            throw new DataValidException("Event must have a start date. Id: " + eventDto.getId());
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataValidException("Event must have a user. Id: " + eventDto.getId());
        }
        checkUserContainsSkills(eventDto);
    }

    private void checkUserContainsSkills(EventDto eventDto) {
        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found. ID: " + eventDto.getOwnerId()));

        List<SkillDto> userSkills = skillMapper.toListSkillsDTO(user.getSkills());
        boolean anySkillMissing = eventDto.getRelatedSkills().stream().anyMatch(skill -> !userSkills.contains(skill));
        if (anySkillMissing) {
            throw new DataValidException("User has no related skills. Id: " + eventDto.getOwnerId());
        }
    }

    private void notifyUsersThatEventStarted(Event event) {
        if (event.getStatus().equals(EventStatus.IN_PROGRESS)) {
            List<Long> userIds = event.getAttendees().stream()
                    .map(User::getId)
                    .toList();
            EventStartDto eventStartDto = new EventStartDto(event.getId(), userIds);
            eventStartPublisher.publishMessage(eventStartDto);
        }
    }
}
