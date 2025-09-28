package school.faang.user_service.service.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final WorkScheduleMapper workScheduleMapper;

    @Override
    public WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto dto) {
        if (!(dto.startTime().isBefore(dto.startLunch()) && dto.endLunch().isBefore(dto.endTime()))) {
            throw new DataValidationException("Не коректные данные!");
        }

        User user = userRepository.getByIdOrThrow(userId);
        WorkSchedule result = workScheduleMapper.toEntity(dto);
        result.setUser(user);
        result = workScheduleRepository.save(result);
        return workScheduleMapper.toDto(result);
    }

    @Override
    public WorkScheduleDto updateWorkSchedule(long userId, long workScheduleId, WorkScheduleDto dto) {
        if (!(dto.startTime().isBefore(dto.startLunch())
                && dto.startLunch().isBefore(dto.endLunch())
                && dto.endLunch().isBefore(dto.endTime()))) {
            throw new DataValidationException("Не коректные данные!");
        }

        WorkSchedule workSchedule = workScheduleRepository.getByIdOrThrow(workScheduleId);
        if (userId != workSchedule.getUser().getId()) {
            throw new ForbiddenException("User не найден!");
        }

        WorkSchedule workScheduleEntity = workScheduleMapper.toEntity(dto);
        workScheduleEntity.setUser(workSchedule.getUser());
        workScheduleRepository.save(workScheduleEntity);
        return workScheduleMapper.toDto(workScheduleEntity);
    }

    @Override
    public WorkScheduleDto getById(long workScheduleId) {
        WorkSchedule actual = workScheduleRepository.getByIdOrThrow(workScheduleId);
        return workScheduleMapper.toDto(actual);

    }
}