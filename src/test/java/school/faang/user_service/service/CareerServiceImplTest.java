package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import school.faang.user_service.service.career.CareerServiceImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CareerServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CareerRepository careerRepository;

    @Spy
    private CareerMapper careerMapper = Mappers.getMapper(CareerMapper.class);

    @InjectMocks
    private CareerServiceImpl careerService;

    private static final long USER_ID = 1L;
    private static final long CAREER_ID = 10L;
    private static final LocalDate VALID_FROM_DATE = LocalDate.now().minusDays(25);
    private static final LocalDate INVALID_FROM_DATE = LocalDate.now().plusDays(1);
    private static final long INVALID_USER_ID = 2L;
    private final Career career = new Career();
    private final User user = new User();
    private final CareerDto expectedCareerDto = new CareerDto(CAREER_ID,
            VALID_FROM_DATE, LocalDate.now(),
            "Test_company", "Test_position");

    @BeforeEach
    void setUp() {
        user.setId(USER_ID);
        career.setId(CAREER_ID);
    }

    @Test
    void testAddCareerSuccess() {
        CreateCareerDto createCareerDto = new CreateCareerDto(VALID_FROM_DATE, LocalDate.now(),
                "Test_company", "Test_position");

        career.setCompany(createCareerDto.company());
        career.setPosition(createCareerDto.position());
        career.setDateFrom(createCareerDto.from());
        career.setDateTo(createCareerDto.to());
        career.setUser(user);

        Mockito.when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(user);
        Mockito.when(careerRepository.save(Mockito.any(Career.class))).thenReturn(career);

        CareerDto result = careerService.addCareer(USER_ID, createCareerDto);

        Mockito.verify(userRepository).getByIdOrThrow(USER_ID);
        Mockito.verify(careerMapper).toCareer(createCareerDto);
        assertEquals(expectedCareerDto, result);
    }

    @Test
    void testAddCareerCompanyIsBlank() {
        CreateCareerDto companyIsBlankDto = new CreateCareerDto(VALID_FROM_DATE,
                LocalDate.now(), "", "Test_position");

        assertThrows(DataValidationException.class, () ->
                careerService.addCareer(USER_ID, companyIsBlankDto), "company should be present!");
    }

    @Test
    void testAddCareerPositionIsBlank() {
        CreateCareerDto positionIsBlankDto = new CreateCareerDto(VALID_FROM_DATE,
                LocalDate.now(), "Test_company", "");

        assertThrows(DataValidationException.class, () ->
                careerService.addCareer(USER_ID, positionIsBlankDto), "position should be present!");
    }

    @Test
    void testAddCareerFromDateIsNull() {
        CreateCareerDto fromDateIsNullDto = new CreateCareerDto(null, LocalDate.now(),
                "Test_company", "Test_company");

        assertThrows(DataValidationException.class, () ->
                careerService.addCareer(USER_ID, fromDateIsNullDto), "from date should be present!");
    }

    @Test
    void testAddCareerInvalidFromDate() {
        CreateCareerDto invalidFromDateDto = new CreateCareerDto(INVALID_FROM_DATE,
                LocalDate.now(), "Test_company", "Test_position");

        assertThrows(DataValidationException.class, () ->
                        careerService.addCareer(USER_ID, invalidFromDateDto),
                "From date should not be later than today!");
    }

    @Test
    void testUpdateCareerSuccess() {
        UpdateCareerDto updateCareerDto = new UpdateCareerDto(VALID_FROM_DATE, LocalDate.now(),
                "Test_company", "Test_position");

        career.setPosition(updateCareerDto.position());
        career.setCompany(updateCareerDto.company());
        career.setDateFrom(updateCareerDto.from());
        career.setDateTo(updateCareerDto.to());
        career.setUser(user);

        Mockito.when(careerRepository.getByIdOrThrow(CAREER_ID)).thenReturn(career);

        CareerDto result = careerService.updateCareer(USER_ID, CAREER_ID, updateCareerDto);

        Mockito.verify(careerRepository).save(career);
        assertEquals(expectedCareerDto, result);
    }

    @Test
    void testUpdateCareerCompanyIsBlank() {
        UpdateCareerDto companyIsBlankDto = new UpdateCareerDto(VALID_FROM_DATE,
                LocalDate.now(), "", "Test_position");

        assertThrows(DataValidationException.class, () ->
                        careerService.updateCareer(USER_ID, CAREER_ID, companyIsBlankDto),
                "company should be present!");
    }

    @Test
    void testUpdateCareerPositionIsBlank() {
        UpdateCareerDto positionIsBlankDto = new UpdateCareerDto(VALID_FROM_DATE,
                LocalDate.now(), "Test_company", "");

        assertThrows(DataValidationException.class, () ->
                        careerService.updateCareer(USER_ID, CAREER_ID, positionIsBlankDto),
                "position should be present!");
    }

    @Test
    void testUpdateCareerFromDateIsNull() {
        UpdateCareerDto fromDateIsNullDto = new UpdateCareerDto(null, LocalDate.now(),
                "Test_company", "Test_company");

        assertThrows(DataValidationException.class, () ->
                        careerService.updateCareer(USER_ID, CAREER_ID, fromDateIsNullDto),
                "from date should be present!");
    }

    @Test
    void testUpdateCareerInvalidFromDate() {
        UpdateCareerDto invalidFromDateDto = new UpdateCareerDto(INVALID_FROM_DATE,
                LocalDate.now(), "Test_company", "Test_position");

        assertThrows(DataValidationException.class, () ->
                        careerService.updateCareer(USER_ID, CAREER_ID, invalidFromDateDto),
                "From date should not be later than today!");
    }

    @Test
    void testUpdateCareerInvalidUserId() {
        User user = new User();
        user.setId(USER_ID);
        Career career = new Career();
        career.setUser(user);
        career.setId(CAREER_ID);
        UpdateCareerDto updateCareerDto = new UpdateCareerDto(VALID_FROM_DATE, LocalDate.now(),
                "Test_company", "Test_position");

        Mockito.when(careerRepository.getByIdOrThrow(CAREER_ID)).thenReturn(career);
        assertThrows(ForbiddenException.class, () ->
                        careerService.updateCareer(INVALID_USER_ID, CAREER_ID, updateCareerDto),
                "ID mismatch: updating this career's details is not allowed for this user!");
    }

    @Test
    void testGetById() {
        career.setPosition(expectedCareerDto.position());
        career.setCompany(expectedCareerDto.company());
        career.setDateFrom(expectedCareerDto.from());
        career.setDateTo(expectedCareerDto.to());
        career.setUser(user);

        Mockito.when(careerRepository.getByIdOrThrow(CAREER_ID)).thenReturn(career);

        CareerDto result = careerService.getById(CAREER_ID);

        Mockito.verify(careerMapper).toCareerDto(career);
        assertEquals(expectedCareerDto, result);
    }
}