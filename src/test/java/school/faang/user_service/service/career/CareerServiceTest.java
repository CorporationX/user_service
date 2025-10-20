package school.faang.user_service.service.career;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CareerServiceTest {

    private static final Long USER_ID = 10L;
    private static final Long CAREER_ID = 15L;
    private static final Long ANOTHER_USER_ID = 20L;
    private static final Long NON_EXISTENT_CAREER_ID = 35L;

    private static final String COMPANY = "company";
    private static final String POSITION = "position";
    private static final String OLD_COMPANY = "old company";
    private static final String OLD_POSITION = "old position";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_SPACES = "   ";

    private static final LocalDate START_DATE = LocalDate.of(2021, 6, 15);
    private static final LocalDate END_DATE = LocalDate.of(2023, 12, 31);
    private static final LocalDate OLD_START_DATE = LocalDate.of(2020, 1, 1);
    private static final LocalDate OLD_END_DATE = LocalDate.of(2022, 1, 1);
    private static final LocalDate INVALID_START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate INVALID_END_DATE = LocalDate.of(2022, 1, 1);

    private final User testUser = User.builder().id(USER_ID).build();

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private final CareerMapper careerMapper = Mappers.getMapper(CareerMapper.class);

    @InjectMocks
    private CareerServiceImpl careerService;

    @Test
    void addCareer_WithNullStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(null, END_DATE, COMPANY, POSITION);

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithNullCompanyThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(START_DATE, END_DATE, null, POSITION);

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithBlankCompanyThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(START_DATE, END_DATE, BLANK_SPACES, POSITION);

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithValidDataReturnsCareerDto() {
        CreateCareerDto createDto = new CreateCareerDto(START_DATE, END_DATE, COMPANY, POSITION);
        Career career = Career.builder().id(CAREER_ID).dateFrom(START_DATE).dateTo(END_DATE)
                .company(COMPANY).position(POSITION).build();

        when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(testUser);
        when(careerRepository.save(any(Career.class))).thenReturn(career);

        CareerDto result = careerService.addCareer(USER_ID, createDto);

        assertCareerDto(result);

        verify(careerMapper).toCareer(createDto);
        verify(careerMapper).toCareerDto(career);
        verify(careerRepository).save(any(Career.class));
        verify(userRepository).getByIdOrThrow(USER_ID);
    }

    @Test
    void addCareer_WithFutureStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(LocalDate.now().plusMonths(1), null, COMPANY, POSITION);

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithEndDateBeforeStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(INVALID_START_DATE, INVALID_END_DATE, COMPANY, POSITION);

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithEmptyCompanyThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(START_DATE, END_DATE, EMPTY_STRING, POSITION);

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithEmptyPositionThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(START_DATE, END_DATE, COMPANY, EMPTY_STRING);

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void addCareer_WithCurrentStartDateThrowsDataValidationException() {
        CreateCareerDto createDto = new CreateCareerDto(LocalDate.now(), END_DATE, COMPANY, POSITION);

        assertThrows(DataValidationException.class, () -> careerService.addCareer(USER_ID,
                createDto));
        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    void updateCareer_WithValidDataAndOwnerReturnsUpdatedCareerDto() {
        Career careerFromRepository = createCareerFromRepository();
        UpdateCareerDto updateDto = new UpdateCareerDto(START_DATE, END_DATE, COMPANY, POSITION);
        Career updatedCareer = createUpdatedCareer();

        when(careerRepository.getByIdOrThrow(CAREER_ID)).thenReturn(careerFromRepository);
        when(careerRepository.save(any(Career.class))).thenReturn(updatedCareer);

        CareerDto result = careerService.updateCareer(USER_ID, CAREER_ID, updateDto);

        assertCareerDto(result);

        verify(careerMapper).toCareer(updateDto);
        verify(careerMapper).toCareerDto(updatedCareer);
        verify(careerRepository).getByIdOrThrow(CAREER_ID);
        verify(careerRepository).save(any(Career.class));
    }

    @Test
    void updateCareer_WhenCareerNotFoundThrowsDataValidationException() {
        UpdateCareerDto updateDto = new UpdateCareerDto(START_DATE, END_DATE, COMPANY, POSITION);

        when(careerRepository.getByIdOrThrow(NON_EXISTENT_CAREER_ID))
                .thenThrow(new DataValidationException("Career not found"));
        assertThrows(DataValidationException.class, () -> careerService.updateCareer(USER_ID,
                NON_EXISTENT_CAREER_ID, updateDto));

        verify(careerRepository).getByIdOrThrow(NON_EXISTENT_CAREER_ID);
        verify(careerRepository, never()).save(any(Career.class));
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateCareer_WhenUserNotOwnerThrowsForbiddenException() {
        Career careerFromRepository = createCareerWithDifferentUser();
        UpdateCareerDto updateDto = new UpdateCareerDto(START_DATE, END_DATE, COMPANY, POSITION);

        when(careerRepository.getByIdOrThrow(CAREER_ID)).thenReturn(careerFromRepository);

        assertThrows(ForbiddenException.class, () -> careerService.updateCareer(USER_ID,
                CAREER_ID, updateDto));
        verify(careerRepository).getByIdOrThrow(CAREER_ID);
        verify(careerRepository, never()).save(any(Career.class));
        verify(careerMapper, never()).toCareer(any(UpdateCareerDto.class));
        verify(careerMapper, never()).toCareerDto(any(Career.class));
    }

    @Test
    void getById_WithExistingCareerReturnsCareerDto() {
        Career career = createTestCareer();
        when(careerRepository.getByIdOrThrow(CAREER_ID)).thenReturn(career);

        CareerDto result = careerService.getById(CAREER_ID);

        assertCareerDto(result);

        verify(careerMapper).toCareerDto(career);
        verify(careerRepository).getByIdOrThrow(CAREER_ID);
    }

    @Test
    void getById_WhenCareerNotFoundThrowsDataValidationException() {
        when(careerRepository.getByIdOrThrow(NON_EXISTENT_CAREER_ID))
                .thenThrow(new DataValidationException("Career not found"));

        assertThrows(DataValidationException.class, () -> careerService.getById(
                NON_EXISTENT_CAREER_ID));

        verify(careerRepository).getByIdOrThrow(NON_EXISTENT_CAREER_ID);
        verifyNoInteractions(userRepository);
    }

    private Career createTestCareer() {
        return Career.builder()
                .id(CAREER_ID)
                .user(testUser)
                .dateFrom(START_DATE)
                .dateTo(END_DATE)
                .company(COMPANY)
                .position(POSITION)
                .build();
    }

    private void assertCareerDto(CareerDto result) {
        assertEquals(COMPANY, result.company());
        assertEquals(POSITION, result.position());
        assertEquals(END_DATE, result.to());
        assertEquals(START_DATE, result.from());
        assertEquals(CAREER_ID, result.id());
    }

    private Career createCareerFromRepository() {
        return Career.builder()
                .id(CAREER_ID)
                .user(testUser)
                .dateFrom(OLD_START_DATE)
                .dateTo(OLD_END_DATE)
                .company(OLD_COMPANY)
                .position(OLD_POSITION)
                .build();
    }

    private Career createUpdatedCareer() {
        return Career.builder()
                .id(CAREER_ID)
                .user(testUser)
                .dateFrom(START_DATE)
                .dateTo(END_DATE)
                .company(COMPANY)
                .position(POSITION)
                .build();
    }

    private Career createCareerWithDifferentUser() {
        User anotherUser = User.builder().id(ANOTHER_USER_ID).build();
        return Career.builder()
                .id(CAREER_ID)
                .user(anotherUser)
                .dateFrom(START_DATE)
                .dateTo(END_DATE)
                .company(COMPANY)
                .position(POSITION)
                .build();
    }
}
