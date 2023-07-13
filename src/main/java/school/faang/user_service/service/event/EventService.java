package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventDto create(EventDto eventDto) {
        try {
            validation(eventDto);
            Event event = eventRepository.save(EventMapper.INSTANCE.toEntity(eventDto));
            return EventMapper.INSTANCE.toDto(event);
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public void validation(EventDto eventDto) throws DataFormatException {
        if (eventDto.getTitle().isBlank()) {
            throw new DataFormatException("Event must have a title");
        }
        if (eventDto.getStartDate() == null) {
            throw new DataFormatException("Event must have a start date");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataFormatException("Event must have a user");
        }
        userContainsSkills(eventDto);
    }

    public void userContainsSkills(EventDto eventDto) throws DataFormatException {
        User user = userRepository.findById(eventDto.getOwnerId()).orElseThrow();
        Set<SkillDto> userSkills = user.getSkills().stream()
                .map(SkillMapper.INSTANCE::toSkillDTO)
                .collect(Collectors.toSet());
        Set<SkillDto> relatedSkills = new HashSet<>(eventDto.getRelatedSkills());

        if (!relatedSkills.containsAll(userSkills)) {
            throw new DataFormatException("User has no related skills");
        }
    }
}
