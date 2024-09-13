package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventValidation {

    private final EventMapper eventMapper;
    private final SkillRepository skillRepository;

    public void validateOwnerSkills(EventDto eventDto) {

        Long ownerId = eventDto.getOwnerId();
        List<Skill> skillsOwner = skillRepository.findAllByUserId(ownerId);

        Event event = eventMapper.eventDtoToEvent(eventDto);
        List<Skill> skillsEvent = event.getRelatedSkills();

        Set<String> setTitleSkillsOwner = skillsOwner.stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet());

        Set<String> setTitleSkillsEvent = skillsEvent.stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet());

        if (!setTitleSkillsEvent.equals(setTitleSkillsOwner)) {
            throw new DataValidationException("пользователь не может провести такое" +
                    " событие с такими навыками");
        }
    }

    public void validateEventDto(EventDto eventDto) {
        try {
            Objects.requireNonNull(eventDto.getId(), "ID не должен быть null");
            Objects.requireNonNull(eventDto.getStartDate(), "StartDate не должен быть null");
            Objects.requireNonNull(eventDto.getOwnerId(), "OwnerID не должен быть null");
            Objects.requireNonNull(eventDto.getRelatedSkills(), "RelatedSkills не должен быть null");
            if (isBlank(eventDto.getTitle())) {
                throw new DataValidationException("Title не должен быть пустым или null");
            }
        } catch (NullPointerException e) {
            throw new DataValidationException("Данные запроса не валидны: " + e.getMessage());
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
