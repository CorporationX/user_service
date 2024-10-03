package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventServiceHelper {
    private final EventRepository eventRepository;

    public void eventDatesValidation(EventDto eventDto) {
        if (eventDto.getStartDate().isAfter(eventDto.getEndDate())) {
            throw new DataValidationException("Start_date cannot be after end_date.");
        }
    }

    public void relatedSkillsValidation(EventDto eventDto, Set<SkillDto> ownerSkills) {
        if (!ownerSkills.containsAll(eventDto.getRelatedSkills())) {
            throw new DataValidationException("Owner must have valid skills.");
        }
    }

    public void eventExistByIdValidation(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new DataValidationException("Event by ID: " + id + " dont exist.");
        }
    }

    @Async("eventThreadPool")
    public void deletePastEventsById(List<Long> pastEventsIds) {
        pastEventsIds.forEach(pastEventsId -> {
            log.info("Delete event with ID: " + pastEventsId + " by " + Thread.currentThread().getName());
            eventRepository.deleteById(pastEventsId);
        });
    }
}
