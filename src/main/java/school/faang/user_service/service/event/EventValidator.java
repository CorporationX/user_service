package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.SkillRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventValidator {

    private final SkillRepository skillRepository;

    public void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new DataValidationException("startDate and endDate must be provided.");
        }
        if (end.isBefore(start)) {
            throw new DataValidationException("endDate must be after startDate.");
        }
    }

    public void ensureOwner(Event event, long requesterId) {
        long ownerId = event.getOwner() != null ? event.getOwner().getId() : -1L;
        if (ownerId != requesterId) {
            log.warn("Requester {} is not the owner {}", requesterId, ownerId);
            throw new ForbiddenException("Only owner can modify/delete the event.");
        }
    }

    public List<Skill> loadAndValidateSkills(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Skill> skills = skillRepository.findAllById(ids);
        if (skills.size() != new HashSet<>(ids).size()) {
            log.warn("Some skills not found by ids: {}", ids);
            throw new DataValidationException("Some skills not found by ids: " + ids);
        }
        return skills;
    }

    public void ensureOwnerHasAllSkills(User owner, List<Skill> eventSkills) {
        if (eventSkills == null || eventSkills.isEmpty()) {
            return;
        }
        Set<Long> ownerSkillIds = owner.getSkills() == null ? Set.of()
                : owner.getSkills().stream().map(Skill::getId).collect(Collectors.toSet());

        List<Long> missing = eventSkills.stream()
                .map(Skill::getId)
                .filter(id -> !ownerSkillIds.contains(id))
                .toList();

        if (!missing.isEmpty()) {
            log.warn("Owner {} lacks required skills: {}", owner.getId(), missing);
            throw new DataValidationException("Owner lacks required skills: " + missing);
        }
    }
}

