package school.faang.user_service.validation.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final EventRepository eventRepository;
    private final SkillService skillService;

    //TODO Murzin34 Задействовать обработчик ошибок
    public void eventDatesValidation(EventDto eventDto) {
        if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
            throw new DataValidationException("Start_date cannot be after end_date.");
        }
    }

    public void relatedSkillsValidation(EventDto eventDto) {
        List<SkillDto> ownerSkills = skillService.getUserSkillsList(eventDto.getOwnerId());
        if (!new HashSet<>(ownerSkills).containsAll(eventDto.getRelatedSkills())) {
            throw new DataValidationException("Owner must have valid skills.");
        }
    }

    public void eventExistByDtoValidation(EventDto eventDto) {
        Long id = eventDto.getId();
        if (!eventRepository.existsById(id)) {
            throw new DataValidationException("Event ID: " + id + " dont exist");
        }
    }
    public void eventExistByIdValidation(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new DataValidationException("Event ID: " + id + " dont exist");
        }
    }
}
