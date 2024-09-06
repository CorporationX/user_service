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
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @Spy
    private SkillCandidateMapperImpl skillCandidateMapper;

    @Mock
    private SkillValidator skillValidator;

    @Captor
    private ArgumentCaptor<Skill> skillCaptor;

    private Skill skill;
    private SkillDto skillDto;

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
    }

    @Test
    void create_shouldSaveSkill() {
        // given
        Skill skillEntity = skillMapper.toEntity(skillDto);
        when(skillRepository.save(skillEntity)).thenReturn(skillEntity);
        // when
        SkillDto result = skillService.create(skillDto);
        // then
        verify(skillRepository, times(1)).save(skillCaptor.capture());
        Skill skill = skillCaptor.getValue();
        assertEquals(skillDto.id(), skill.getId());
        assertEquals(skillDto.title(), skill.getTitle());
        assertEquals(skillDto.title(), result.title());
    }

    @Test
    void getUserSkills_shouldReturnSkillDtoList() {
        // given
        long userId = 1L;
        List<Skill> skills = List.of(skill);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);
        // when
        List<SkillDto> result = skillService.getUserSkills(userId);
        // then
        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).title());
        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void getOfferedSkills_shouldReturnSkillCandidateDtoList() {
        // given
        long userId = 1L;
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(skillDto, 1L);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill));
        skillCandidateMapper.toDto(skill, 2L);
        // when
        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);
        // then
        assertEquals(1, result.size());
        assertEquals(skillCandidateDto, result.get(0));
        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    void acquireSkillFromOffers_whenNotEnoughOffers_shouldThrowDataValidationException() {
        // given
        long skillId = 1L;
        long userId = 1L;
        List<SkillOffer> offers = List.of();
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        // when
        doNothing().when(skillValidator).validateOfferedSkill(skillId, userId);
        doThrow(new DataValidationException("Not enough offers")).when(skillValidator).validateSkillByMinSkillOffers(0, skillId, userId);
        // then
        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
    }
}