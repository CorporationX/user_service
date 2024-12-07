package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.skill.SkillRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    private final static int SKILL_ID = 1;
    private final static int USER_ID = 1;

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillValidator skillValidator;

    @Spy
    private SkillMapperImpl skillMapper;

    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skillDto = new SkillDto();
        skillDto.setTitle("title");
    }

    @Test
    void testCreateThrowsExceptionWhenTitleExists() {
        doThrow(DataValidationException.class).when(skillValidator).validateExistTitle(skillDto.getTitle());

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreateWithMissingTitle() {
        doNothing().when(skillValidator).validateExistTitle(skillDto.getTitle());

        skillService.create(skillDto);

        verify(skillRepository, times(1)).save(skillMapper.dtoToEntity(skillDto));
    }

    @Test
    void testGetUserSkills() {
        skillService.getUserSkills(USER_ID);

        verify(skillRepository, times(1)).findAllByUserId(USER_ID);
    }

    @Test
    void testGetOfferedSkills() {
        List<Skill> skills = List.of(new Skill(), new Skill());
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(skills);

        skillService.getOfferedSkills(USER_ID);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(USER_ID);
    }

    @Test
    void testAcquireSkillFromOffersWithExistSkill() {
        doThrow(DataValidationException.class).when(skillValidator).validateUserSkillExist(SKILL_ID, USER_ID);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
    }

    @Test
    void testAcquireSkillFromOffersNumberOffersLessThree() {
        doThrow(DataValidationException.class).when(skillValidator).validateSkillOfferCount(SKILL_ID, USER_ID);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID));
    }

    @Test
    void testAcquireSKillFromOffersSuccess() {
        Skill skill = new Skill();
        doNothing().when(skillValidator).validateUserSkillExist(SKILL_ID, USER_ID);
        doNothing().when(skillValidator).validateSkillOfferCount(SKILL_ID, USER_ID);
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(new Skill()));
        when(skillMapper.entityToDto(skill)).thenReturn(new SkillDto());

        skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        verify(skillRepository, times(1)).assignSkillToUser(SKILL_ID, USER_ID);
    }

    @Test
    void testAssignSkillToUser() {
        long skillId = 1L;
        long receiverId = 2L;

        skillService.assignSkillToUser(skillId, receiverId);

        verify(skillRepository).assignSkillToUser(skillId, receiverId);

        verifyNoMoreInteractions(skillRepository);
    }

    @Test
    void testCountExisting() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        when(skillRepository.countExisting(ids)).thenReturn(2);

        int result = skillService.countExisting(ids);

        assertEquals(2, result);

        verify(skillRepository).countExisting(ids);

        verifyNoMoreInteractions(skillRepository);
    }

    @Nested
    class GetSkillsByIdIn {

        List<Long> ids;
        List<Skill> skills;

        @BeforeEach
        void setUp() {
            ids = List.of(1L, 2L);
            Skill firstSkill = Skill.builder()
                    .id(1L)
                    .build();
            Skill secondSkill = Skill.builder()
                    .id(2L)
                    .build();
            skills = List.of(firstSkill, secondSkill);
        }

        @Test
        void testGetSkillsByIdIn_SkillExist() {
            when(skillRepository.findByIdIn(ids)).thenReturn(skills);

            List<Skill> returnedSkills = skillService.getSkillsByIdIn(ids);

            assertEquals(skills, returnedSkills);
        }

        @Test
        void testGetSkillsByIdIn_SkillNotExist() {
            ids = List.of(1L, 2L, 3L, 4L);
            when(skillRepository.findByIdIn(ids)).thenReturn(skills);

            assertThrows(EntityNotFoundException.class, () -> skillService.getSkillsByIdIn(ids));
        }
    }
}
