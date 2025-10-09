package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;

    @Override
    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        log.info("Adding work schedule for user: {}", userId);
        User user = userRepository.getByIdOrThrow(userId);
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(user);
        workSchedule = workScheduleRepository.save(workSchedule);
        log.info("Work schedule created with id: {}", workSchedule.getId());
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    @Override
    public WorkScheduleDto updateWorkSchedule(long userId, long workScheduleId, WorkScheduleDto workScheduleDto) {
        log.info("Updating work schedule {} for user: {}", workScheduleId, userId);
        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);
        if (userId != workSchedule.getUser().getId()) {
            log.warn("User {} attempted to update another user's work schedule {}", userId, workScheduleId);
            throw new ForbiddenException("The user wants to update someone else's data.");
        }
        workScheduleMapper.updateWorkScheduleFromDto(workScheduleDto, workSchedule);
        workSchedule = workScheduleRepository.save(workSchedule);
        log.info("Work schedule updated with id: {}", workSchedule.getId());
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    @Override
    public WorkScheduleDto getById(long workScheduleId) {
        return workScheduleMapper.toWorkScheduleDto(
                workScheduleRepository.getByIdOrThrow(workScheduleId)
        );
    }
}