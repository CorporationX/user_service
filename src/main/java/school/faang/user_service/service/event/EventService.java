package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventDto create(EventDto eventDto) {
        try {
            validateEventDto(eventDto);
            Event event = eventRepository.save(EventMapper.INSTANCE.toEntity(eventDto));
            return EventMapper.INSTANCE.toDto(event);
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public EventDto updateEvent(EventDto eventDto) {
        try {
            validateEventDto(eventDto);
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }

        Event event = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        updateField(eventDto.getTitle(), event.getTitle(), event::setTitle);
        updateField(eventDto.getStartDate(), event.getStartDate(), event::setStartDate);
        updateField(eventDto.getEndDate(), event.getEndDate(), event::setEndDate);
        updateField(eventDto.getDescription(), event.getDescription(), event::setDescription);
        updateField(eventDto.getLocation(), event.getLocation(), event::setLocation);
        updateField(eventDto.getMaxAttendees(), event.getMaxAttendees(), event::setMaxAttendees);

        if (eventDto.getRelatedSkills() != null) {
            Set<Skill> skills1 = new HashSet<>(event.getRelatedSkills());
            Set<Skill> skills2 = new HashSet<>(SkillMapper.INSTANCE.toListSkillsEntity(eventDto.getRelatedSkills()));
            if (!skills1.equals(skills2)) {
                event.setRelatedSkills(skills2.stream().toList());
            }
        }

        eventRepository.save(event);
        return EventMapper.INSTANCE.toDto(event);
    }

    private <T> void updateField(T newValue, T oldValue, Consumer<T> updateFunction) {
        if (newValue != null && !Objects.equals(newValue, oldValue)) {
            updateFunction.accept(newValue);
        }
    }

    private void validateEventDto(EventDto eventDto) throws DataFormatException {
        if (eventDto.getId() == null || eventDto.getId() < 1) {
            throw new DataFormatException("Event Id must be greater than 0");
        }
        if (eventDto.getTitle().isBlank()) {
            throw new DataFormatException("Event must have a title");
        }
        if (eventDto.getStartDate() == null) {
            throw new DataFormatException("Event must have a start date");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataFormatException("Event must have a user");
        }
        checkUserContainsSkills(eventDto);
    }

    private void checkUserContainsSkills(EventDto eventDto) throws DataFormatException {
        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<SkillDto> userSkills = SkillMapper.INSTANCE.toListSkillsDTO(user.getSkills());
        if (!new HashSet<>(userSkills).containsAll(eventDto.getRelatedSkills())) {
            throw new DataFormatException("User has no related skills");
        }
    }
}
