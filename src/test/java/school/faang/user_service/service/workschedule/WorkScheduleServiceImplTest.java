package school.faang.user_service.service.workschedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.WorkScheduleRepository;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkScheduleServiceImplTest {

    @InjectMocks
    private WorkScheduleServiceImpl workScheduleService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private WorkScheduleRepository workScheduleRepository;
    @Mock
    private WorkScheduleMapper workScheduleMapper;

    private static final long USER_ID = 42L;
    private static final long OTHER_USER_ID = 2L;
    private static final long WORK_SCHEDULE_ID = 1L;
    private static final LocalTime START_TIME = LocalTime.of(9, 0);
    private static final LocalTime END_TIME = LocalTime.of(18, 0);
    private static final LocalTime START_LUNCH = LocalTime.of(12, 0);
    private static final LocalTime END_LUNCH = LocalTime.of(13, 0);
    private static final String TIMEZONE = "UTC+3";

    private User user;
    private WorkScheduleDto workScheduleDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);

        workScheduleDto = createWorkScheduleDto(WORK_SCHEDULE_ID);
    }

    @Test
    public void testAddWorkSchedule() {
        WorkSchedule mapped = new WorkSchedule();
        WorkSchedule saved = createWorkScheduleEntity(WORK_SCHEDULE_ID, user);
        WorkScheduleDto expected = createWorkScheduleDto(WORK_SCHEDULE_ID);

        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);
        when(workScheduleMapper.toWorkSchedule(workScheduleDto)).thenReturn(mapped);
        when(workScheduleRepository.save(mapped)).thenReturn(saved);
        when(workScheduleMapper.toWorkScheduleDto(saved)).thenReturn(expected);

        WorkScheduleDto result = workScheduleService.addWorkSchedule(USER_ID, workScheduleDto);

        assertEquals(expected, result);
        verify(userRepository).getByIdOrThrow(USER_ID);
        verify(workScheduleMapper).toWorkSchedule(workScheduleDto);
        verify(workScheduleRepository).save(mapped);
        verify(workScheduleMapper).toWorkScheduleDto(saved);
    }

    @Test
    public void testUpdateWorkScheduleForbidden() {
        User otherUser = new User();
        otherUser.setId(OTHER_USER_ID);
        WorkSchedule workSchedule = createWorkScheduleEntity(WORK_SCHEDULE_ID, otherUser);

        when(workScheduleRepository.getByIdOrThrow(WORK_SCHEDULE_ID)).thenReturn(workSchedule);

        assertThrows(ForbiddenException.class, () ->
                workScheduleService.updateWorkSchedule(USER_ID, WORK_SCHEDULE_ID, workScheduleDto)
        );
    }

    @Test
    public void testUpdateWorkSchedule() {
        WorkSchedule workSchedule = createWorkScheduleEntity(WORK_SCHEDULE_ID, user);

        when(workScheduleRepository.getByIdOrThrow(WORK_SCHEDULE_ID)).thenReturn(workSchedule);
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(workSchedule);
        when(workScheduleMapper.toWorkScheduleDto(workSchedule)).thenReturn(workScheduleDto);

        WorkScheduleDto result = workScheduleService.updateWorkSchedule(USER_ID, WORK_SCHEDULE_ID, workScheduleDto);

        assertEquals(workScheduleDto, result);
        verify(workScheduleRepository).getByIdOrThrow(WORK_SCHEDULE_ID);
        verify(workScheduleMapper).updateWorkScheduleFromDto(workScheduleDto, workSchedule);
        verify(workScheduleRepository).save(workSchedule);
        verify(workScheduleMapper).toWorkScheduleDto(workSchedule);
    }

    @Test
    public void testGetById() {
        WorkSchedule entity = createWorkScheduleEntity(WORK_SCHEDULE_ID, user);
        WorkScheduleDto expected = createWorkScheduleDto(WORK_SCHEDULE_ID);

        when(workScheduleRepository.getByIdOrThrow(WORK_SCHEDULE_ID)).thenReturn(entity);
        when(workScheduleMapper.toWorkScheduleDto(entity)).thenReturn(expected);

        WorkScheduleDto result = workScheduleService.getById(WORK_SCHEDULE_ID);

        assertEquals(expected, result);
        assertNotNull(result);
        verify(workScheduleRepository).getByIdOrThrow(WORK_SCHEDULE_ID);
        verify(workScheduleMapper).toWorkScheduleDto(entity);
    }

    private WorkScheduleDto createWorkScheduleDto(long id) {
        return new WorkScheduleDto(
                id,
                START_TIME,
                END_TIME,
                START_LUNCH,
                END_LUNCH,
                TIMEZONE
        );
    }

    private WorkSchedule createWorkScheduleEntity(long id, User user) {
        WorkSchedule workSchedule = new WorkSchedule();
        workSchedule.setId(id);
        workSchedule.setUser(user);
        return workSchedule;
    }
}