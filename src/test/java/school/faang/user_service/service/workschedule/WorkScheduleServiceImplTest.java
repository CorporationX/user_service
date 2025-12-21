package school.faang.user_service.service.workschedule;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Slf4j
@ExtendWith(MockitoExtension.class)
public class WorkScheduleServiceImplTest {

    @InjectMocks
    private WorkScheduleServiceImpl workScheduleServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Spy
    private WorkScheduleMapper workScheduleMapper = Mappers.getMapper(WorkScheduleMapper.class);

    @Captor
    private ArgumentCaptor<WorkSchedule> captorWorkSchedule;

    @Captor
    private ArgumentCaptor<Long> captorWorkScheduleId;

    @Test
    public void testAddWorkScheduleWithEmptyOrNullField() {
        long userId = 1;
        WorkScheduleDto workScheduleDto = createIncorrectWorkScheduleWithEmptyField();
        assertThrows(DataValidationException.class,
                () -> workScheduleServiceImpl.addWorkSchedule(userId, workScheduleDto));
    }

    @Test
    public void testAddWorkScheduleWithTimeOrderError() {
        long userId = 1;
        WorkScheduleDto workScheduleDto = createIncorrectWorkScheduleWithIcorrectTimeOrder();
        assertThrows(DataValidationException.class,
                () -> workScheduleServiceImpl.addWorkSchedule(userId, workScheduleDto));
    }

    @Test
    public void testAddWorkScheduleAddsSchedule() {
        long userId = 1;
        WorkScheduleDto workScheduleDto = createCorrectWorkScheduleDtoForTest();
        User user = new User();
        user.setId(1L);
        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);

        workScheduleServiceImpl.addWorkSchedule(userId, workScheduleDto);

        verify(workScheduleRepository, times(1)).save(captorWorkSchedule.capture());
        WorkSchedule newWorkSchedule = captorWorkSchedule.getValue();

        assertEquals(workScheduleDto.id(), newWorkSchedule.getId());
    }

    @Test
    public void testUpdateWorkScheduleWithEmptyOrNullField() {
        long userId = 1;
        long workScheduleId = 1;
        WorkScheduleDto workScheduleDto = createIncorrectWorkScheduleWithEmptyField();
        assertThrows(DataValidationException.class,
                () -> workScheduleServiceImpl.updateWorkSchedule(userId, workScheduleId, workScheduleDto));
    }

    @Test
    public void testUpdateWorkScheduleWithTimeOrderError() {
        long userId = 1;
        long workScheduleId = 1;
        WorkScheduleDto workScheduleDto = createIncorrectWorkScheduleWithIcorrectTimeOrder();
        assertThrows(DataValidationException.class,
                () -> workScheduleServiceImpl.updateWorkSchedule(userId, workScheduleId, workScheduleDto));
    }

    @Test
    public void testUpdateOtherUserWorkSchedule() {
        long workScheduleId = 1;
        WorkSchedule workSchedule = new WorkSchedule();
        User user = new User();
        user.setId(1L);
        workSchedule.setUser(user);
        when(workScheduleRepository.getByIdOrThrow(workScheduleId)).thenReturn(workSchedule);
        long userId = 2;
        WorkScheduleDto workScheduleDto = createCorrectWorkScheduleDtoForTest();

        assertThrows(ForbiddenException.class,
                () -> workScheduleServiceImpl.updateWorkSchedule(userId, workScheduleId, workScheduleDto));
    }

    @Test
    public void testUpdateWorkScheduleUpdatesSchedule() {
        long workScheduleId = 1;
        WorkScheduleDto workScheduleDto = createCorrectWorkScheduleDtoForTest();
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleDto);
        User user = new User();
        user.setId(1L);
        workSchedule.setUser(user);
        when(workScheduleRepository.getByIdOrThrow(workScheduleId)).thenReturn(workSchedule);
        long userId = 1;

        workScheduleServiceImpl.updateWorkSchedule(userId, workScheduleId, workScheduleDto);

        verify(workScheduleRepository, times(1)).save(captorWorkSchedule.capture());
        WorkSchedule newWorkSchedule = captorWorkSchedule.getValue();

        assertEquals(workScheduleDto.id(), newWorkSchedule.getId());
    }

    @Test
    public void testGetById() {
        long workScheduleId = 1L;

        workScheduleServiceImpl.getById(workScheduleId);

        verify(workScheduleRepository, times(1)).getByIdOrThrow(captorWorkScheduleId.capture());
        long result = captorWorkScheduleId.getValue();
        assertEquals(workScheduleId, result);
    }

    private WorkScheduleDto createCorrectWorkScheduleDtoForTest() {
        return new WorkScheduleDto(
                1L,
                LocalTime.of(9, 0, 0, 0),
                LocalTime.of(18, 0, 0, 0),
                LocalTime.of(13, 0, 0, 0),
                LocalTime.of(14, 0, 0, 0),
                "Europe/Moscow"
        );
    }

    private WorkScheduleDto createIncorrectWorkScheduleWithEmptyField() {
        return new WorkScheduleDto(
                1L,
                LocalTime.of(9, 0, 0, 0),
                LocalTime.of(18, 0, 0, 0),
                LocalTime.of(13, 0, 0, 0),
                LocalTime.of(14, 0, 0, 0),
                ""
        );
    }

    private WorkScheduleDto createIncorrectWorkScheduleWithIcorrectTimeOrder() {
        return new WorkScheduleDto(
                1L,
                LocalTime.of(9, 0, 0, 0),
                LocalTime.of(18, 0, 0, 0),
                LocalTime.of(19, 0, 0, 0),
                LocalTime.of(14, 0, 0, 0),
                "Europe/Moscow"
        );
    }
}