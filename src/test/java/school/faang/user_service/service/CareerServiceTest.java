package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.adapter.CareerRepositoryAdapter;
import school.faang.user_service.adapter.UserRepositoryAdapter;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.service.career.CareerService;
import school.faang.user_service.validator.CareerValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareerServiceTest {

    @Mock
    private CareerRepository careerRepository;
    @Mock
    private CareerMapper careerMapper;
    @Mock
    private CareerValidator careerValidator;
    @Mock
    private CareerRepositoryAdapter careerRepositoryAdapter;
    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;

    @Spy
    @InjectMocks
    private CareerService careerService;

    @Test
    void testAddCareer_Success() {
        // Подготовка данных
        long userId = 1L;
        CareerDto inputDto = CareerDto.builder()
                .from(LocalDate.of(2020, 1, 1))
                .to(LocalDate.of(2022, 1, 1))
                .company("Test Company")
                .position("Developer")
                .build();

        User user = new User();
        user.setId(userId);

        Career savedCareer = Career.builder()
                .id(1L)
                .dateFrom(inputDto.getFrom())
                .dateTo(inputDto.getTo())
                .company(inputDto.getCompany())
                .position(inputDto.getPosition())
                .user(user)
                .build();

        CareerDto expectedDto = CareerDto.builder()
                .id(1L)
                .from(inputDto.getFrom())
                .to(inputDto.getTo())
                .company(inputDto.getCompany())
                .position(inputDto.getPosition())
                .build();

        // Настройка моков
        doNothing().when(careerValidator).validate(inputDto);
        when(userRepositoryAdapter.getUserById(userId)).thenReturn(user);

        // Используем any() вместо конкретного объекта, так как билдер создает новый экземпляр
        when(careerRepository.save(any(Career.class))).thenReturn(savedCareer);
        when(careerMapper.toCareerDto(savedCareer)).thenReturn(expectedDto);

        // Вызов метода
        CareerDto result = careerService.addCareer(userId, inputDto);

        // Проверки
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(careerValidator).validate(inputDto);
        verify(userRepositoryAdapter).getUserById(userId);

        // Проверяем, что save был вызван с объектом Career с правильными параметрами
        ArgumentCaptor<Career> careerCaptor = ArgumentCaptor.forClass(Career.class);
        verify(careerRepository).save(careerCaptor.capture());

        Career capturedCareer = careerCaptor.getValue();
        assertEquals(inputDto.getFrom(), capturedCareer.getDateFrom());
        assertEquals(inputDto.getTo(), capturedCareer.getDateTo());
        assertEquals(inputDto.getCompany(), capturedCareer.getCompany());
        assertEquals(inputDto.getPosition(), capturedCareer.getPosition());
        assertEquals(user, capturedCareer.getUser());
    }

    @Test
    void testUpdateCareer_Success() {
        // Подготовка данных
        long userId = 1L;
        long careerId = 1L;

        CareerDto inputDto = CareerDto.builder()
                .id(careerId)
                .from(LocalDate.of(2021, 1, 1))
                .to(LocalDate.of(2023, 1, 1))
                .company("Updated Company")
                .position("Senior Developer")
                .build();

        User user = new User();
        user.setId(userId);

        Career existingCareer = Career.builder()
                .id(careerId)
                .dateFrom(LocalDate.of(2020, 1, 1))
                .dateTo(LocalDate.of(2022, 1, 1))
                .company("Old Company")
                .position("Developer")
                .user(user)
                .build();

        Career updatedCareer = Career.builder()
                .id(careerId)
                .dateFrom(inputDto.getFrom())
                .dateTo(inputDto.getTo())
                .company(inputDto.getCompany())
                .position(inputDto.getPosition())
                .user(user)
                .build();

        CareerDto expectedDto = CareerDto.builder()
                .id(careerId)
                .from(inputDto.getFrom())
                .to(inputDto.getTo())
                .company(inputDto.getCompany())
                .position(inputDto.getPosition())
                .build();

        // Настройка моков
        doNothing().when(careerValidator).validate(inputDto);
        when(careerRepositoryAdapter.getCareerById(careerId)).thenReturn(existingCareer);
        when(careerRepository.save(any(Career.class))).thenReturn(updatedCareer);
        when(careerMapper.toCareerDto(updatedCareer)).thenReturn(expectedDto);

        // Вызов метода
        CareerDto result = careerService.updateCareer(userId, inputDto);

        // Проверки
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(careerValidator).validate(inputDto);
        verify(careerRepositoryAdapter).getCareerById(careerId);
        verify(careerRepository).save(any(Career.class));
    }

    @Test
    void testUpdateCareer_UserMismatch_ThrowsException() {
        // Подготовка данных
        long userId = 1L;
        long anotherUserId = 2L;
        long careerId = 1L;

        CareerDto inputDto = CareerDto.builder()
                .id(careerId)
                .build();

        User wrongUser = new User();
        wrongUser.setId(anotherUserId);

        Career existingCareer = Career.builder()
                .id(careerId)
                .user(wrongUser)
                .build();

        // Настройка моков
        doNothing().when(careerValidator).validate(inputDto);
        when(careerRepositoryAdapter.getCareerById(careerId)).thenReturn(existingCareer);

        // Проверка исключения
        assertThrows(DataValidationException.class, () -> careerService.updateCareer(userId, inputDto));


        verify(careerValidator).validate(inputDto);
        verify(careerRepositoryAdapter).getCareerById(careerId);
        verify(careerRepository, never()).save(any());
    }

    @Test
    void testGetById_Success() {
        // Подготовка данных
        long careerId = 1L;
        Career career = Career.builder()
                .id(careerId)
                .build();

        CareerDto expectedDto = CareerDto.builder()
                .id(careerId)
                .build();

        // Настройка моков
        when(careerRepositoryAdapter.getCareerById(careerId)).thenReturn(career);
        when(careerMapper.toCareerDto(career)).thenReturn(expectedDto);

        // Вызов метода
        CareerDto result = careerService.getById(careerId);

        // Проверки
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(careerRepositoryAdapter).getCareerById(careerId);
        verify(careerMapper).toCareerDto(career);
    }
}
