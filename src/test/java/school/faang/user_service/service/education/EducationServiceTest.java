package school.faang.user_service.service.education;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EducationMapperImpl;
import school.faang.user_service.repository.adapter.EducationRepositoryAdapter;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EducationServiceTest {

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;

    @Mock
    private EducationRepositoryAdapter educationRepositoryAdapter;

    @Mock
    private EducationMapperImpl educationMapper;

    @InjectMocks
    private EducationService educationService;

    @Test
    public void addEducation_validInput_returnsEducationDto() {
        long userId = 1L;
        EducationDto educationDto = new EducationDto();
        educationDto.setYearFrom(2020);
        educationDto.setInstitution("University");
        educationDto.setEducationLevel("Bachelor");
        educationDto.setSpecialization("Computer Science");

        User user = new User();
        user.setId(userId);

        Education educationBeforeSave = Education.builder()
                .yearFrom(2020)
                .institution("University")
                .educationLevel("Bachelor")
                .specialization("Computer Science")
                .user(user)
                .build();

        Education savedEducation = Education.builder()
                .id(1L)
                .yearFrom(2020)
                .institution("University")
                .educationLevel("Bachelor")
                .specialization("Computer Science")
                .user(user)
                .build();

        EducationDto expectedDto = new EducationDto();
        expectedDto.setId(1L);
        expectedDto.setYearFrom(2020);
        expectedDto.setInstitution("University");
        expectedDto.setEducationLevel("Bachelor");
        expectedDto.setSpecialization("Computer Science");

        when(userRepositoryAdapter.getById(userId)).thenReturn(user);
        when(educationMapper.toEducation(educationDto)).thenReturn(educationBeforeSave);
        when(educationRepositoryAdapter.save(any(Education.class))).thenReturn(savedEducation);
        when(educationMapper.toEducationDto(savedEducation)).thenReturn(expectedDto);

        EducationDto actualDto = educationService.addEducation(userId, educationDto);

        assertEquals(expectedDto, actualDto);
        verify(userRepositoryAdapter, times(1)).getById(userId);
        verify(educationMapper, times(1)).toEducation(educationDto);
        verify(educationRepositoryAdapter, times(1)).save(any(Education.class));
        verify(educationMapper, times(1)).toEducationDto(savedEducation);
    }


    @Test
    public void addEducation_userNotFound_throwsEntityNotFoundException() {
        long userId = 1L;
        EducationDto educationDto = new EducationDto();
        educationDto.setYearFrom(2020);
        educationDto.setInstitution("University");
        educationDto.setEducationLevel("Bachelor");
        educationDto.setSpecialization("Computer Science");

        when(userRepositoryAdapter.getById(userId))
                .thenThrow(new EntityNotFoundException("User not found with id:" + userId));

        assertThrows(EntityNotFoundException.class,
                () -> educationService.addEducation(userId, educationDto));
        verify(userRepositoryAdapter, times(1)).getById(userId);
        verify(educationMapper, never()).toEducation(any());
        verify(educationRepositoryAdapter, never()).save(any());
        verify(educationMapper, never()).toEducationDto(any());
    }

    @Test
    public void updateEducation_validInput_returnsEducationDto() {
        long userId = 1L;
        EducationDto educationDto = new EducationDto();
        educationDto.setId(1L);
        educationDto.setYearFrom(2021);
        educationDto.setYearTo(2024);
        educationDto.setInstitution("New University");
        educationDto.setEducationLevel("Master");
        educationDto.setSpecialization("Software Engineering");

        User user = new User();
        user.setId(userId);

        Education existingEducation = Education.builder()
                .id(1L).yearFrom(2020)
                .yearTo(2023)
                .institution("Old University")
                .educationLevel("Bachelor")
                .specialization("Computer Science")
                .user(user).build();

        Education updatedEducationEntity = Education.builder()
                .id(1L)
                .yearFrom(2021)
                .yearTo(2024)
                .institution("New University")
                .educationLevel("Master")
                .specialization("Software Engineering")
                .user(user).build();

        Education savedEducation = Education.builder()
                .id(1L)
                .yearFrom(2021)
                .yearTo(2024)
                .institution("New University")
                .educationLevel("Master")
                .specialization("Software Engineering")
                .user(user).build();

        EducationDto expectedDto = new EducationDto();
        expectedDto.setId(1L);
        expectedDto.setYearFrom(2021);
        expectedDto.setYearTo(2024);
        expectedDto.setInstitution("New University");
        expectedDto.setEducationLevel("Master");
        expectedDto.setSpecialization("Software Engineering");

        when(educationRepositoryAdapter.getById(educationDto.getId())).thenReturn(existingEducation);
        when(educationMapper.toEducation(educationDto)).thenReturn(updatedEducationEntity);
        when(educationRepositoryAdapter.save(any(Education.class))).thenReturn(savedEducation);
        when(educationMapper.toEducationDto(savedEducation)).thenReturn(expectedDto);

        EducationDto actualDto = educationService.updateEducation(userId, educationDto);

        assertEquals(expectedDto, actualDto);
        verify(educationRepositoryAdapter, times(1)).getById(educationDto.getId());
        verify(educationMapper, times(1)).toEducation(educationDto);
        verify(educationRepositoryAdapter, times(1)).save(any(Education.class));
        verify(educationMapper, times(1)).toEducationDto(savedEducation);
    }

    @Test
    public void updateEducation_educationNotFound_throwsEntityNotFoundException() {
        long userId = 1L;
        EducationDto educationDto = new EducationDto();
        educationDto.setId(1L);
        educationDto.setYearFrom(2020);
        educationDto.setInstitution("University");
        educationDto.setEducationLevel("Bachelor");
        educationDto.setSpecialization("Computer Science");

        when(educationRepositoryAdapter.getById(educationDto.getId()))
                .thenThrow(new EntityNotFoundException("Education not found with id:" + educationDto.getId()));

        assertThrows(EntityNotFoundException.class,
                () -> educationService.updateEducation(userId, educationDto));

        verify(educationRepositoryAdapter, times(1)).getById(educationDto.getId());
        verify(educationMapper, never()).toEducation(any());
        verify(educationRepositoryAdapter, never()).save(any());
        verify(educationMapper, never()).toEducationDto(any());
    }

    @Test
    public void updateEducation_notOwner_throwsDataValidationException() {
        long userId = 2L;

        EducationDto educationDto = new EducationDto();
        educationDto.setId(1L);
        educationDto.setYearFrom(2020);
        educationDto.setInstitution("University");
        educationDto.setEducationLevel("Bachelor");
        educationDto.setSpecialization("Computer Science");

        User owner = new User();
        owner.setId(1L);

        Education existingEducation = Education.builder()
                .id(1L).yearFrom(2020)
                .institution("University")
                .educationLevel("Bachelor")
                .specialization("Computer Science")
                .user(owner).build();

        when(educationRepositoryAdapter.getById(educationDto.getId()))
                .thenThrow(new DataValidationException("Education not owned by user with id:" + userId));

        assertThrows(DataValidationException.class,
                () -> educationService.updateEducation(userId, educationDto));

        verify(educationRepositoryAdapter, times(1)).getById(educationDto.getId());
        verify(educationMapper, never()).toEducation(any());
        verify(educationRepositoryAdapter, never()).save(any());
        verify(educationMapper, never()).toEducationDto(any());
    }

    @Test
    public void getById_validId_returnsEducationDto() {
        long educationId = 1L;

        User user = new User();
        user.setId(1L);

        Education education = Education.builder()
                .id(educationId)
                .yearFrom(2020)
                .institution("University")
                .educationLevel("Bachelor")
                .specialization("Computer Science")
                .user(user).build();

        EducationDto expectedDto = new EducationDto();
        expectedDto.setId(educationId);
        expectedDto.setYearFrom(2020);
        expectedDto.setInstitution("University");
        expectedDto.setEducationLevel("Bachelor");
        expectedDto.setSpecialization("Computer Science");

        when(educationRepositoryAdapter.getById(educationId)).thenReturn(education);
        when(educationMapper.toEducationDto(education)).thenReturn(expectedDto);

        EducationDto actualDto = educationService.getById(educationId);

        assertEquals(expectedDto, actualDto);
        verify(educationRepositoryAdapter, times(1)).getById(educationId);
        verify(educationMapper, times(1)).toEducationDto(education);
    }

    @Test
    public void getById_invalidId_throwsEntityNotFoundException() {
        long educationId = 1L;

        when(educationRepositoryAdapter.getById(educationId))
                .thenThrow(new EntityNotFoundException("Education not found with id:" + educationId));

        assertThrows(EntityNotFoundException.class, () -> educationService.getById(educationId));
        verify(educationRepositoryAdapter, times(1)).getById(educationId);
        verify(educationMapper, never()).toEducationDto(any());
    }
}
