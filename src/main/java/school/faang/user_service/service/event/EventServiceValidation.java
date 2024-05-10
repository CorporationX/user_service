package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static school.faang.user_service.exception.ExceptionMessage.INAPPROPRIATE_OWNER_SKILLS_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NO_SUCH_EVENT_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NO_SUCH_USER_EXCEPTION;

@Component
@RequiredArgsConstructor
class EventServiceValidation {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;

    public void checkOwnerSkills(EventDto event) {
        Long ownerId = event.getOwnerId();
        checkOwnerPresence(ownerId);

        List<Skill> ownerSkillsList = skillRepository.findAllByUserId(ownerId);
        var ownerSkills = new HashSet<>(skillMapper.toDto(ownerSkillsList));

        if (!ownerSkills.containsAll(event.getRelatedSkills())) {
            throw new DataValidationException(INAPPROPRIATE_OWNER_SKILLS_EXCEPTION.getMessage());
        }
    }

    public void checkOwnerPresence(Long ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);

        if (owner.isEmpty()) {
            throw new DataValidationException(NO_SUCH_USER_EXCEPTION.getMessage());
        }
    }

    public void eventUpdateValidation(EventDto event) {
        checkEventPresence(event.getId());

        checkOwnerSkills(event);
    }

    public void checkEventPresence(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException(NO_SUCH_EVENT_EXCEPTION.getMessage());
        }
    }
}
