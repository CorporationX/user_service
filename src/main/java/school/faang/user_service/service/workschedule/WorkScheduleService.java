package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;
import school.faang.user_service.service.workschedule.validator.WorkScheduleValidator;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkScheduleService {
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final UserContext userContext;

    public WorkSchedule addWorkSchedule(WorkScheduleCreateDto workScheduleCreateDto) {
        WorkScheduleValidator.validateForCreate(workScheduleCreateDto);

        long userId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);
        WorkSchedule workSchedule = WorkScheduleMapper.createScheduleFields(workScheduleCreateDto);
        workSchedule.setUser(user);

        return workScheduleRepository.save(workSchedule);
    }

    public WorkSchedule updateWorkSchedule(long workScheduleId, WorkScheduleUpdateDto workScheduleUpdateDto) {
        WorkSchedule workSchedule = getById(workScheduleId);
        long userId = userContext.getUserId();

        if (workSchedule.getUser() == null) {
            throw new ForbiddenException("!");
        }
        if (!Objects.equals(workSchedule.getUser().getId(), userId)) {
            throw new ForbiddenException("You can only update your own data");
        }

        WorkScheduleMapper.updateScheduleFields(workSchedule, workScheduleUpdateDto);
        WorkScheduleValidator.validateEntity(workSchedule);

        return workScheduleRepository.save(workSchedule);
    }

    public void deleteWorkSchedule(long workScheduleId) {
        WorkSchedule workSchedule = getById(workScheduleId);
        long userId = userContext.getUserId();

        if (!Objects.equals(workSchedule.getUser().getId(), userId)) { //
            throw new ForbiddenException("You can only delete your own data");
        }

        workScheduleRepository.delete(workSchedule);
        log.info("Work schedule of user: {}, has been deleted.", userId);
    }

    public WorkSchedule getById(long workScheduleId) {
        return workScheduleRepository.getByIdOrThrow(workScheduleId);
    }


}