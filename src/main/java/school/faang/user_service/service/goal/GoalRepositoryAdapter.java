package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoalRepositoryAdapter {
    private final GoalRepository goalRepository;

    public Goal findById(long id) {
        log.info("Execution of the method findById, parameters: id={}", id);
        return goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No goal found with с id=%d ", id)));
    }
}
