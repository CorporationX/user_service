package school.faang.user_service.service.workschedule;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapperImpl;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkScheduleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Spy
    private WorkScheduleMapperImpl workScheduleMapper;  //всегда мапперImpl интерфейс

    @InjectMocks
    private WorkScheduleServiceImpl workScheduleService;  //всегда сервис, на который пишем тест


    @Test
    void testAddWorkSchedule() {
        //инициализация переменных (+/-/0 зависит от сценария теста)
        // Arrange: сначала инициализация
        long userId = 1;
        WorkScheduleDto dto = new WorkScheduleDto(userId,
                LocalTime.of(5, 0, 0),
                LocalTime.of(5, 10, 0),
                LocalTime.of(5, 1, 0),
                LocalTime.of(5, 9, 0),
                "florida");
        User user = User.builder().id(userId).build();
        WorkSchedule result = workScheduleMapper.toEntity(dto);
        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(result);

        // Act: вызов самого метода
        WorkScheduleDto actual = workScheduleService.addWorkSchedule(userId, dto);
        System.out.println(actual);

        // Assert: проверка результата
        Assertions.assertEquals(dto, actual, "Ошибка");
    }

    // + сецнарии этого теста.
    @Test
    void testAddWorkSchedule_ThrowDataValidationException() {
        long userId = 1;
        WorkScheduleDto dto = new WorkScheduleDto(userId,
                LocalTime.of(5, 0, 0),
                LocalTime.of(5, 10, 0),
                LocalTime.of(4, 59, 0),
                LocalTime.of(5, 9, 0),
                "ford_loderdaule");


        assertThrows(DataValidationException.class, () -> workScheduleService.addWorkSchedule(userId, dto));
    }

    @Test
    void testAddWorkSchedule_NullPointerException() {
        long userId = 1;
        WorkScheduleDto dto = new WorkScheduleDto(userId,
                null,
                LocalTime.of(5, 10, 0),
                LocalTime.of(4, 59, 0),
                LocalTime.of(5, 9, 0),
                "ford_loderdaule");

        assertThrows(NullPointerException.class, () -> workScheduleService.addWorkSchedule(userId, dto));
    }

    @Test
    void testUpdateWorkSchedule() {
        long userId = 1;
        long workScheduleId = 1;
        WorkScheduleDto dto = new WorkScheduleDto(userId,
                LocalTime.of(5, 0, 0),
                LocalTime.of(5, 10, 0),
                LocalTime.of(5, 1, 0),
                LocalTime.of(5, 9, 0),
                "florida");

        User user = User.builder().id(userId).build();
        WorkSchedule workSchedule = WorkSchedule.builder().id(workScheduleId).user(user).build();
        WorkSchedule result = workScheduleMapper.toEntity(dto);
        result.setUser(user);

        when(workScheduleRepository.getByIdOrThrow(workScheduleId)).thenReturn(workSchedule);
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(result);

        WorkScheduleDto actual = workScheduleService.updateWorkSchedule(userId, workScheduleId, dto);
        System.out.println(actual);

        Assertions.assertEquals(dto, actual, "Ошибка");
    }

    @Test
    void testUpdateWorkSchedule_ThrowDataValidationException() {
        long userId = 1;
        long workScheduleId = 1;
        WorkScheduleDto dto = new WorkScheduleDto(userId,
                LocalTime.of(5, 0, 0),
                LocalTime.of(4, 10, 0),
                LocalTime.of(5, 1, 0),
                LocalTime.of(5, 9, 0),
                "florida");

        assertThrows(DataValidationException.class,
                () -> workScheduleService.updateWorkSchedule(userId, workScheduleId, dto));
    }

    @Test
    void testUpdateWorkSchedule_NullPointerException() {
        long userId = 1;
        long workScheduleId = 1;
        WorkScheduleDto dto = new WorkScheduleDto(userId,
                null,
                LocalTime.of(4, 10, 0),
                LocalTime.of(5, 1, 0),
                LocalTime.of(5, 9, 0),
                "florida");

        assertThrows(NullPointerException.class,
                () -> workScheduleService.updateWorkSchedule(userId, workScheduleId, dto));
    }

    @Test
    void testUpdateWorkSchedule_ThrowForbiddenException() {
        long userId = 1;
        long workScheduleId = 1;
        WorkScheduleDto dto = new WorkScheduleDto(userId,
                LocalTime.of(5, 0, 0),
                LocalTime.of(5, 10, 0),
                LocalTime.of(5, 1, 0),
                LocalTime.of(5, 9, 0),
                "florida");

        User user = User.builder().id(2L).build();
        WorkSchedule workSchedule = WorkSchedule.builder().id(workScheduleId).user(user).build();
        when(workScheduleRepository.getByIdOrThrow(workScheduleId)).thenReturn(workSchedule);

        assertThrows(ForbiddenException.class,
                () -> workScheduleService.updateWorkSchedule(userId, workScheduleId, dto));
    }


    @Test
    void testGetById() {
        long workScheduleId = 1;
        long userId = 1;
        WorkScheduleDto dto = new WorkScheduleDto(userId,
                LocalTime.of(5, 0, 0),
                LocalTime.of(5, 10, 0),
                LocalTime.of(5, 1, 0),
                LocalTime.of(5, 9, 0),
                "florida");
        WorkSchedule workSchedule = new WorkSchedule(workScheduleId,
                LocalTime.of(5, 0, 0),
                LocalTime.of(5, 10, 0),
                LocalTime.of(5, 1, 0),
                LocalTime.of(5, 9, 0),
                "florida",
                new User());

        when(workScheduleRepository.getByIdOrThrow(workScheduleId))
                .thenReturn(workSchedule);

        when(workScheduleMapper.toDto(workSchedule)).thenReturn(dto);

        WorkScheduleDto workScheduleDto = workScheduleService.getById(workScheduleId);

        Assertions.assertEquals(workScheduleDto, dto);
    }

    @Test
    void testGetById_ThrowEntityNotFoundException() {
        long workScheduleId = 1;

        when(workScheduleRepository.getByIdOrThrow(workScheduleId)).thenThrow(new EntityNotFoundException(""));

        assertThrows(EntityNotFoundException.class,
                () -> workScheduleService.getById(workScheduleId));
    }
}
