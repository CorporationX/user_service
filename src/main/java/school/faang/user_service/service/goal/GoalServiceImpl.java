package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;

    @Override
    @Transactional(readOnly = true)
    public Goal findById(long id) {
        log.debug("Execution of the method findById, parameters: id={}", id);
        return goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No goal found with с id=%d ", id)));
    }
}
