package school.faang.user_service.service.workschedule;

import com.amazonaws.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

@Slf4j
@Service
@AllArgsConstructor
@Setter
@Getter
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private UserRepository userRepository;
    private WorkScheduleRepository workScheduleRepository;
    private WorkScheduleMapper workScheduleMapper;

    @Override
    public WorkScheduleDto addWorkSchedule(long userId, @NonNull WorkScheduleDto workScheduleDto) {
        validateWorkScheduleDto(workScheduleDto);
        timeOrderCheck(workScheduleDto);
        User user = userRepository.getByIdOrThrow(userId);
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workSchedule.setUser(user);
        workSchedule = workScheduleRepository.save(workSchedule);
        log.info("Новый график с id {} добавлен.", workSchedule.getId());
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    @Override
    public WorkScheduleDto updateWorkSchedule(
            long userId, long workScheduleId, @NonNull WorkScheduleDto workScheduleDto) {
        validateWorkScheduleDto(workScheduleDto);
        timeOrderCheck(workScheduleDto);
        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);
        if (workSchedule.getUser().getId() != userId) {
            throw new ForbiddenException("Вы можете менять только свой график, этот график - не ваш.");
        }
        WorkSchedule workScheduleToUpdate = workScheduleMapper.toWorkSchedule(workScheduleDto);
        workScheduleToUpdate.setUser(workSchedule.getUser());
        WorkSchedule updatedWorkSchedule = workScheduleRepository.save(workSchedule);
        log.info("График с id {} обновлен.", updatedWorkSchedule.getId());
        return workScheduleMapper.toWorkScheduleDto(updatedWorkSchedule);
    }

    @Override
    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);
        return workScheduleMapper.toWorkScheduleDto(workSchedule);
    }

    public void timeOrderCheck(@NonNull WorkScheduleDto workScheduleDto) {
        if (workScheduleDto.startTime().isAfter(workScheduleDto.startLunch())
                || workScheduleDto.startLunch().isAfter(workScheduleDto.endLunch())
                || workScheduleDto.endLunch().isAfter(workScheduleDto.endTime())) {
            throw new DataValidationException(
                    "Нарушена хронология. Проверьте порядок полей времени в workScheduleDto.");
        }
    }

    public void validateWorkScheduleDto(@NonNull WorkScheduleDto workScheduleDto) {
        validateNullOrEmpty(workScheduleDto.id().toString(), "Id");
        validateNullOrEmpty(workScheduleDto.startTime().toString(), "StartTime");
        validateNullOrEmpty(workScheduleDto.endTime().toString(), "EndTIme");
        validateNullOrEmpty(workScheduleDto.startLunch().toString(), "StartLunch");
        validateNullOrEmpty(workScheduleDto.endLunch().toString(), "EndLunch");
        validateNullOrEmpty(workScheduleDto.timezone(), "Timezone");
    }

    private void validateNullOrEmpty(String value, String param) {
        if (StringUtils.isNullOrEmpty(value)) {
            throw new DataValidationException(param + " - пустое или равно Null.");
        }
    }
}
