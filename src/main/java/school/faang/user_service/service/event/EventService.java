package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.CreateEventRequestDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.dto.event.UpdateEventRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.specification.EventSpecification;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final EventParticipationRepository eventParticipationRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public EventResponseDto createEvent(CreateEventRequestDto createRequest) throws DataValidationException {
        Event event = eventMapper.toEntity(createRequest);
        event.setOwner(userService.getUser(createRequest.getOwnerId()));
        List<Long> skillIds = createRequest.getRelatedSkills();
        List<Skill> relatedSkills = skillIds.stream()
                .map(skillId -> skillRepository.findById(skillId)
                        .orElseThrow(() -> new DataValidationException("Skill not found with ID: " + skillId)))
                .toList();
        event.setRelatedSkills(relatedSkills);

        return eventMapper.toResponseDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public EventResponseDto getEvent(Long eventId) throws DataValidationException {
        Event event = findEventById(eventId);
        return eventMapper.toResponseDto(event);
    }

    @Transactional
    public EventResponseDto updateEvent(UpdateEventRequestDto updateRequest) throws DataValidationException {
        Event existingEvent = findEventById(updateRequest.getId());

        Event updatedEvent = eventMapper.toEntity(updateRequest);

        List<Skill> relatedSkills = updateRequest.getRelatedSkills().stream()
                .map(skillId -> skillRepository.findById(skillId)
                        .orElseThrow(() -> new DataValidationException("Skill not found with ID: " + skillId)))
                .toList();

        updatedEvent.setRelatedSkills(relatedSkills);

        updatedEvent.setOwner(userService.getUser(updateRequest.getOwnerId()));

        return eventMapper.toResponseDto(eventRepository.save(updatedEvent));
    }

    @Transactional
    public void deleteEvent(Long eventId) throws DataValidationException {
        Event event = findEventById(eventId);

        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (!participants.isEmpty()) {
            for (User participant : participants) {
                eventParticipationRepository.unregister(eventId, participant.getId());
            }
        }

        eventRepository.delete(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getEventsByFilters(EventFilterDto filterDto) {
        Specification<Event> specification = Specification.where(
                        EventSpecification.hasId(filterDto.getId()))
                .and(EventSpecification.hasTitle(filterDto.getTitle()))
                .and(EventSpecification.hasDescription(filterDto.getDescription()))
                .and(EventSpecification.hasLocation(filterDto.getLocation()))
                .and(EventSpecification.hasMaxAttendees(filterDto.getMaxAttendees()))
                .and(EventSpecification.hasStartDate(filterDto.getStartDate()))
                .and(EventSpecification.hasEndDate(filterDto.getEndDate()))
                .and(EventSpecification.hasEventType(filterDto.getEventType()))
                .and(EventSpecification.hasEventStatus(filterDto.getEventStatus()))
                .and(EventSpecification.hasOwner(filterDto.getOwnerId()))
                .and(EventSpecification.hasSkillIds(filterDto.getSkillIds()));

        List<Event> events = eventRepository.findAll(specification);

        return eventMapper.toResponseDtoList(events);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getEventsByOwner(Long ownerId) {
        List<Event> events = eventRepository.findAllByUserId(ownerId);

        return eventMapper.toResponseDtoList(events);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getEventsByParticipant(Long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);

        return eventMapper.toResponseDtoList(events);
    }

    private Event findEventById(Long eventId) throws DataValidationException {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found with ID: " + eventId));
    }
}