package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validation.SkillValidator;
import school.faang.user_service.validation.UserValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private UserValidator userValidator;

    @Spy
    private SkillMapperImpl skillMapper;

    private SkillDto skillDto;
    private Skill skill;
    private Long userId;
    private Long skillId;

    @BeforeEach
    public void setUp() {
        skillDto = SkillDto.builder().id(1L).title("title").build();
        skill = Skill.builder().id(1L).title("title").build();
        userId = 1L;
        skillId = 1L;
    }

    @Test
    public void testWithCorrectValidateTitleNonRepetition() {
        assertDoesNotThrow(() -> skillService.create(skillDto));
    }

    @Test
    public void testWithTitleRepetition() {
        doThrow(DataValidationException.class).when(skillValidator).validateTitleRepetition(skillDto.getTitle());
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testCreateSkill() {
        assertDoesNotThrow(() -> userValidator.checkUserInDB(userId));
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.create(skillDto);

        assertEquals(skillDto, result);
        verify(skillRepository).save(skill);
        verify(skillMapper).toDto(skill);
    }

    @Test
    public void testMapperCall() {
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        skillService.create(skillDto);

        verify(skillMapper).toEntity(skillDto);
        verify(skillMapper).toDto(skill);
    }

    @Test
    public void testGetUserSkills() {
        List<Skill> skillList = createSkillList();
        List<SkillDto> skillDtoList = createSkillDtoList();

        assertDoesNotThrow(() -> userValidator.checkUserInDB(userId));
        when(skillRepository.findAllByUserId(userId)).thenReturn(skillList);
        when(skillMapper.toDto(skillList.get(0))).thenReturn(skillDtoList.get(0));
        when(skillMapper.toDto(skillList.get(1))).thenReturn(skillDtoList.get(1));

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertEquals(skillDtoList, result);
        verify(skillRepository).findAllByUserId(userId);
        verify(skillMapper).toDto(skillList.get(0));
        verify(skillMapper).toDto(skillList.get(1));
    }

    @Test
    public void testGetOfferedSkills() {
        List<Skill> skillList = createSkillList();
        List<SkillCandidateDto> skillCandidateDtoList = createSkillCandidateDto();

        doNothing().when(userValidator).checkUserInDB(userId);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skillList);
        when(skillRepository.countOffersByUserIdAndSkillId(userId, skillList.get(0).getId())).thenReturn(1L);
        when(skillRepository.countOffersByUserIdAndSkillId(userId, skillList.get(1).getId())).thenReturn(2L);
        when(skillMapper.skillToSkillCandidateDto(skillList.get(0), 1L)).thenReturn(skillCandidateDtoList.get(0), skillCandidateDtoList.get(0));
        when(skillMapper.skillToSkillCandidateDto(skillList.get(1), 2L)).thenReturn(skillCandidateDtoList.get(1), skillCandidateDtoList.get(1));

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        assertEquals(skillCandidateDtoList, result);
        verify(skillRepository).findSkillsOfferedToUser(userId);
        verify(skillRepository).countOffersByUserIdAndSkillId(userId, skillList.get(0).getId());
        verify(skillRepository).countOffersByUserIdAndSkillId(userId, skillList.get(1).getId());
        verify(skillMapper, times(2)).skillToSkillCandidateDto(skillList.get(0), 1L);
        verify(skillMapper, times(2)).skillToSkillCandidateDto(skillList.get(1), 2L);
    }

    @Test
    public void testGetOfferedSkillsFailValidation() {
        doThrow(DataValidationException.class).when(userValidator).checkUserInDB(userId);
        assertThrows(DataValidationException.class, () -> skillService.getOfferedSkills(userId));
    }

    @Test
    public void testAcquireSkillWhenSkillAlreadyAcquired() {
        assertDoesNotThrow(() -> skillValidator.checkSkillIdAndUserIdInDB(userId, skillId));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void testAcquireSkillWhenNotEnoughSkillOffers() {
        assertDoesNotThrow(() -> skillValidator.checkSkillIdAndUserIdInDB(userId, skillId));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillRepository.countOffersByUserIdAndSkillId(skillId, userId)).thenReturn(2L);

        assertThrows(IllegalStateException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void testAcquireSkillSuccessfully() {
        assertDoesNotThrow(() -> skillValidator.checkSkillIdAndUserIdInDB(userId, skillId));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillRepository.countOffersByUserIdAndSkillId(skillId, userId)).thenReturn(3L);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertNotNull(result);
        assertEquals(skillId, result.getId());
        assertEquals(skill.getTitle(), result.getTitle());
    }

    @Test
    public void setAcquireSkillWhenSkillIdIsEmpty() {
        doThrow(DataValidationException.class).when(skillValidator).checkSkillIdAndUserIdInDB(skillId, userId);
        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void setAcquireSkillWhenUserIdIsEmpty() {
        doThrow(DataValidationException.class).when(skillValidator).checkSkillIdAndUserIdInDB(skillId, userId);
        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    private List<SkillCandidateDto> createSkillCandidateDto() {
        SkillCandidateDto firstSkillCandidateDto = createSkillCandidateDto(createSkillDto(1L, "firstTitle"), 1L);
        SkillCandidateDto secondSkillCandidateDto = createSkillCandidateDto(createSkillDto(2L, "secondTitle"), 2L);

        return Arrays.asList(firstSkillCandidateDto, secondSkillCandidateDto);
    }

    private List<SkillDto> createSkillDtoList() {
        SkillDto firstSkillDto = createSkillDto(1L, "firstTitle");
        SkillDto secondSkillDto = createSkillDto(2L, "secondTitle");

        return Arrays.asList(firstSkillDto, secondSkillDto);
    }

    private List<Skill> createSkillList() {
        Skill firstSkill = createSkill(1L, "firstTitle");
        Skill secondSkill = createSkill(2L, "secondTitle");

        return Arrays.asList(firstSkill, secondSkill);
    }

    private SkillCandidateDto createSkillCandidateDto(SkillDto skillDto, long offersAmount) {
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
        skillCandidateDto.setSkillDto(skillDto);
        skillCandidateDto.setOffersAmount(offersAmount);

        return skillCandidateDto;
    }

    private SkillDto createSkillDto(long id, String title) {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(id);
        skillDto.setTitle(title);

        return skillDto;
    }

    private Skill createSkill(long id, String title) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setTitle(title);

        return skill;
    }
}
