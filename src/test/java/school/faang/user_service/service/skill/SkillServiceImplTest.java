package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.model.dto.skill.SkillCandidateDto;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.impl.skill.SkillServiceImpl;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {
    @InjectMocks
    private SkillServiceImpl skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @Spy
    private SkillCandidateMapperImpl skillCandidateMapper;

    @Mock
    private SkillValidator skillServiceValidator;

    @Captor
    private ArgumentCaptor<Skill> skillCaptor;

    private Skill skill;
    private SkillDto skillDto;

    private List<String> titleSkills;
    private List<Skill> skills;
    private long skillId;
    private long userId;
    private long goalId;

    @BeforeEach
    void setUp() {
        skill = Skill.builder()
                .id(1L)
                .title("Title")
                .build();
        skillDto = SkillDto.builder()
                .id(1L)
                .title("Title")
                .build();

        titleSkills = List.of("title");
        skills = List.of(new Skill());
        skillId = 1;
        userId = 1;
        goalId = 1;
    }

    @Test
    void create_shouldSaveSkill() {
        Skill skillEntity = skillMapper.toEntity(skillDto);
        when(skillRepository.save(skillEntity)).thenReturn(skillEntity);
        SkillDto result = skillService.create(skillDto);
        verify(skillRepository, times(1)).save(skillCaptor.capture());
        Skill capturedSkill = skillCaptor.getValue();
        assertEquals(skillDto.id(), capturedSkill.getId());
        assertEquals(skillDto.title(), capturedSkill.getTitle());
        assertEquals(skillDto.title(), result.title());
    }

    @Test
    void getUserSkills_shouldReturnSkillDtoList() {
        long userId = 1L;
        List<Skill> skills = List.of(skill);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);
        List<SkillDto> result = skillService.getUserSkills(userId);
        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).title());
        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void getOfferedSkills_shouldReturnSkillCandidateDtoList() {
        long userId = 1L;
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(skillDto, 1L);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill));
        skillCandidateMapper.toDto(skill, 2L);
        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);
        assertEquals(1, result.size());
        assertEquals(skillCandidateDto, result.get(0));
        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    void acquireSkillFromOffers_whenNotEnoughOffers_shouldThrowDataValidationException() {
        long skillId = 1L;
        long userId = 1L;
        List<SkillOffer> offers = List.of();
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        doNothing().when(skillServiceValidator).validateOfferedSkill(skillId, userId);
        doThrow(new DataValidationException("Not enough offers")).when(skillServiceValidator).validateSkillByMinSkillOffers(0, skillId, userId);
        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
    }

    // Merged test methods from SkillServiceTest

    @Test
    void getSkillsByIdsThenNotEmptyList() {
        Skill expectedSkill = new Skill();
        skill.setId(1L);
        List<Skill> skillsList = List.of(expectedSkill);
        when(skillRepository.findAllById(anyList()))
                .thenReturn(skillsList);

        var result = skillService.getSkillsByIds(List.of(1L));

        assertEquals(skillsList, result);
        verify(skillRepository, times(1)).findAllById(anyList());
    }

    @Test
    void getSkillsByIdsThenException() {
        List<Skill> skillsList = new ArrayList<>();
        when(skillRepository.findAllById(anyList()))
                .thenReturn(skillsList);

        assertThrows(DataValidationException.class, () ->
                skillService.getSkillsByIds(List.of(1L)));
        verify(skillRepository, times(1)).findAllById(anyList());
    }

    @Test
    void assignSkillToUser() {
        skillService.assignSkillToUser(skillId, userId);
        verify(skillRepository).assignSkillToUser(skillId, userId);
    }

    @Test
    void deleteSkillFromGoal() {
        when(skillRepository.findSkillsByGoalId(goalId)).thenReturn(skills);
        skillService.deleteSkillFromGoal(goalId);
        verify(skillRepository).findSkillsByGoalId(goalId);
        verify(skillRepository).deleteAll(skills);
    }
}
