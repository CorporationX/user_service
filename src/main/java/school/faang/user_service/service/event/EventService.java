package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventDto create(EventDto event) {
        User user = userRepository.findById(event.getOwnerId()).orElseThrow();
        if (!(isUserContainsSkill(event, user))) {
            throw new DataValidationException("The user cannot hold such an event with such skills");
        }
        return eventMapper.toEventDto(eventRepository.save(eventMapper.toEvent(event)));
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toEventDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("User with this id was not found")));
    }

    private boolean isUserContainsSkill(EventDto event, User user) {
        return new HashSet<>(user.getSkills()
                .stream()
                .map(Skill::getTitle)
                .toList())
                .containsAll(event.getRelatedSkills()
                        .stream()
                        .map(SkillDto::getTitle).toList());
    }
}
