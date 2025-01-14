package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
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
import school.faang.user_service.repository.adapter.EventParticipationAdapter;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.adapter.EventRepositoryAdapter;
import school.faang.user_service.repository.specification.EventSpecification;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.stream.Stream;

@Validated
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepositoryAdapter eventRepositoryAdapter;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final EventParticipationRepository eventParticipationRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public EventResponseDto createEvent(CreateEventRequestDto createRequest) throws DataValidationException {
        List<Skill> relatedSkills = getSkillsByIds(createRequest.getRelatedSkills());
        Event event = eventMapper.toEntity(createRequest, relatedSkills);
        event.setOwner(userService.getUser(createRequest.getOwnerId()));

        return eventMapper.toResponseDto(eventRepositoryAdapter.save(event));
    }

    @Transactional(readOnly = true)
    public EventResponseDto getEvent(Long eventId) throws DataValidationException {
        Event event = eventRepositoryAdapter.getEventById(eventId);
        return eventMapper.toResponseDto(event);
    }

    @Transactional
    public EventResponseDto updateEvent(UpdateEventRequestDto updateRequest) throws DataValidationException {
        Event existingEvent = eventRepositoryAdapter.getEventById(updateRequest.getId());

        List<Skill> relatedSkills = getSkillsByIds(updateRequest.getRelatedSkills());
        Event updatedEvent = eventMapper.toEntity(updateRequest, relatedSkills);
        updatedEvent.setOwner(userService.getUser(updateRequest.getOwnerId()));

        return eventMapper.toResponseDto(eventRepositoryAdapter.save(updatedEvent));
    }
    @Transactional
    public void deleteEvent(Long eventId) throws DataValidationException {
        Event event = eventRepositoryAdapter.getEventById(eventId);

        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (!participants.isEmpty()) {
            for (User participant : participants) {
                eventParticipationRepository.unregister(eventId, participant.getId());
            }
        }

        eventRepositoryAdapter.delete(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getEventsByFilters(EventFilterDto filterDto) {
        Specification<Event> specification = Stream.of(
                        filterDto.getId() != null ? EventSpecification.hasId(filterDto.getId()) : null,
                        filterDto.getTitle() != null ? EventSpecification.hasTitle(filterDto.getTitle()) : null,
                        filterDto.getDescription() != null ? EventSpecification.hasDescription(filterDto.getDescription()) : null,
                        filterDto.getLocation() != null ? EventSpecification.hasLocation(filterDto.getLocation()) : null,
                        filterDto.getMaxAttendees() != null ? EventSpecification.hasMaxAttendees(filterDto.getMaxAttendees()) : null,
                        filterDto.getStartDate() != null ? EventSpecification.hasStartDate(filterDto.getStartDate()) : null,
                        filterDto.getEndDate() != null ? EventSpecification.hasEndDate(filterDto.getEndDate()) : null,
                        filterDto.getEventType() != null ? EventSpecification.hasEventType(filterDto.getEventType()) : null,
                        filterDto.getEventStatus() != null ? EventSpecification.hasEventStatus(filterDto.getEventStatus()) : null,
                        filterDto.getOwnerId() != null ? EventSpecification.hasOwner(filterDto.getOwnerId()) : null,
                        filterDto.getSkillIds() != null ? EventSpecification.hasSkillIds(filterDto.getSkillIds()) : null,
                        filterDto.getRelatedSkills() != null ? EventSpecification.hasSkillIds(filterDto.getRelatedSkills()) : null
                )
                .filter(spec -> spec != null)
                .reduce(Specification::and)
                .orElse(null);

        List<Event> events = eventRepositoryAdapter.findAll(specification);

        return eventMapper.toResponseDtoList(events);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getEventsByOwner(Long ownerId) {
        List<Event> events = eventRepositoryAdapter.findAllByUserId(ownerId);

        return eventMapper.toResponseDtoList(events);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getEventsByParticipant(Long userId) {
        EventParticipationAdapter eventParticipationAdapter = null;
        List<Event> events = eventParticipationAdapter.findParticipatedEventsByUserId(userId);
        return eventMapper.toResponseDtoList(events);
    }

    private List<Skill> getSkillsByIds(List<Long> skillIds) throws DataValidationException {
        return skillIds.stream()
                .map(skillId -> skillRepository.findById(skillId)
                        .orElseThrow(() -> new DataValidationException("Skill not found with ID: " + skillId)))
                .toList();
    }
}