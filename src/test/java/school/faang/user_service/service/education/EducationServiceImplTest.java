package school.faang.user_service.service.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EducationMapper;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Optional;

import static java.time.Year.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceImplTest {

    private UserRepository userRepository;
    private EducationRepository educationRepository;
    private EducationMapper educationMapper;

    private EducationServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        educationRepository = mock(EducationRepository.class);
        educationMapper = Mappers.getMapper(EducationMapper.class);
        service = new EducationServiceImpl(userRepository, educationRepository, educationMapper);
    }

    @Test
    void addEducation_happyPath_savesAndReturnsDto() {
        long userId = 42L;
        CreateEducationDto dto = new CreateEducationDto(
                2015,
                2019,
                "KFU",
                "Bachelor",
                "CS"
        );

        User user = new User();
        user.setId(userId);
        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);

        Education saved = new Education();
        saved.setId(100L);
        saved.setUser(user);
        saved.setYearFrom(2015);
        saved.setYearTo(2019);
        saved.setInstitution("KFU");
        saved.setEducationLevel("Bachelor");
        saved.setSpecialization("CS");
        when(educationRepository.save(any(Education.class))).thenReturn(saved);

        EducationDto result = service.addEducation(userId, dto);

        assertEquals(100L, result.id());
        assertEquals(2015, result.yearFrom());
        assertEquals(2019, result.yearTo());
        assertEquals("KFU", result.institution());
        assertEquals("Bachelor", result.educationLevel());
        assertEquals("CS", result.specialization());

        verify(userRepository).getByIdOrThrow(userId);
        verify(educationRepository).save(argThat(e ->
                e.getUser() != null
                        && e.getUser().getId() == userId
                        && "KFU".equals(e.getInstitution())
        ));
        verifyNoMoreInteractions(userRepository, educationRepository);
    }

    @Test
    void addEducation_throws_whenYearFromNullOrFuture() {
        int currentYear = now().getValue();

        CreateEducationDto nullYear = new CreateEducationDto(null, 2019, "Uni", "B", "CS");
        assertThrows(DataValidationException.class, () -> service.addEducation(1L, nullYear));

        CreateEducationDto futureYear = new CreateEducationDto(currentYear + 1, 2019, "Uni", "B", "CS");
        assertThrows(DataValidationException.class, () -> service.addEducation(1L, futureYear));
    }

    @Test
    void addEducation_throws_whenYearToBeforeYearFrom_orInstitutionBlank() {
        CreateEducationDto badYears = new CreateEducationDto(2020, 2019, "Uni", "B", "CS");
        assertThrows(DataValidationException.class, () -> service.addEducation(1L, badYears));

        CreateEducationDto blank1 = new CreateEducationDto(2018, 2020, "   ", "B", "CS");
        assertThrows(DataValidationException.class, () -> service.addEducation(1L, blank1));

        CreateEducationDto blank2 = new CreateEducationDto(2018, 2020, null, "B", "CS");
        assertThrows(DataValidationException.class, () -> service.addEducation(1L, blank2));
    }

    @Test
    void updateEducation_happyPath_nullsAreIgnoredAndOwnerMatches() {
        long userId = 7L;
        long educationId = 77L;

        User owner = new User();
        owner.setId(userId);

        Education existing = new Education();
        existing.setId(educationId);
        existing.setUser(owner);
        existing.setYearFrom(2010);
        existing.setYearTo(2014);
        existing.setInstitution("Old Uni");
        existing.setEducationLevel("Old Level");
        existing.setSpecialization("Old Spec");

        when(educationRepository.findById(educationId)).thenReturn(Optional.of(existing));
        when(educationRepository.save(any(Education.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateEducationDto patch = new UpdateEducationDto(
                null,
                2015,
                null,
                "New Level",
                null
        );

        EducationDto out = service.updateEducation(userId, educationId, patch);

        assertEquals(2010, out.yearFrom());
        assertEquals(2015, out.yearTo());
        assertEquals("Old Uni", out.institution());
        assertEquals("New Level", out.educationLevel());
        assertEquals("Old Spec", out.specialization());

        verify(educationRepository).save(existing);
    }

    @Test
    void updateEducation_throws_whenNotFound_orWrongOwner_orBadYears_orFutureYearFrom() {
        long userId = 1L;
        long educationId = 2L;

        when(educationRepository.findById(educationId)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () ->
                service.updateEducation(userId, educationId, new UpdateEducationDto(null, null, null, null, null))
        );

        User other = new User(); other.setId(99L);
        Education existing = new Education(); existing.setUser(other);
        when(educationRepository.findById(educationId)).thenReturn(Optional.of(existing));
        assertThrows(ForbiddenException.class, () ->
                service.updateEducation(userId, educationId, new UpdateEducationDto(null, null, null, null, null))
        );

        existing.setUser(new User()); existing.getUser().setId(userId);
        when(educationRepository.findById(educationId)).thenReturn(Optional.of(existing));
        UpdateEducationDto badYears = new UpdateEducationDto(2020, 2019, null, null, null);
        assertThrows(DataValidationException.class, () ->
                service.updateEducation(userId, educationId, badYears)
        );

        int currentYear = now().getValue();
        UpdateEducationDto future = new UpdateEducationDto(currentYear + 1, null, null, null, null);
        assertThrows(DataValidationException.class, () ->
                service.updateEducation(userId, educationId, future)
        );
    }
}
