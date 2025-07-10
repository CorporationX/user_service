package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.EntityNotFoundException;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    @Modifying
    void deleteByUser_Id(long userId);

    default WorkSchedule getByIdOrThrow(long workScheduleId) {
        return findById(workScheduleId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Work schedule %d not found", workScheduleId)));
    }

}
