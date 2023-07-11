package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

@Component("eventService")
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public EventDto create(EventDto event) {
        EventDto result;
        User user = userRepository.findById(event.getOwnerId()).orElseThrow();
        boolean userContainsSkill = isUserContainsSkill(event, user);
        if (userContainsSkill) {
            result = EventMapper.INSTANCE.toEventDto(eventRepository.save(EventMapper.INSTANCE.toEvent(event)));
            return result;
        } else {
            throw new DataValidationException("Ошибка");
        }
    }

    private static boolean isUserContainsSkill(EventDto event, User user) {
        return user.getSkills()
                .stream()
                .map(Skill::getTitle)
                .toList()
                .containsAll(event.getRelatedSkills()
                        .stream()
                        .map(SkillDto::getTitle).toList());
    }

    public boolean validation(EventDto event) {
        return event.getTitle() != null && !event.getTitle().isEmpty() && event.getStartDate() != null && event.getOwnerId() != null;
    }
}
