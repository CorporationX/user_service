package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.withSettings;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    private final SkillMapper skillMapper = SkillMapper.INSTANCE;
    @InjectMocks
    private SkillService skillService;

    @Test
    void createTest_Should_Return_SkillDto() {
        SkillDto skillDto = new SkillDto(1L, "title");
        Skill skill = skillMapper.toEntity(skillDto);

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(skill);

        SkillDto result = skillService.create(skillDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("title", result.getTitle());

        verify(skillRepository).existsByTitle(skillDto.getTitle());
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void createTest_Should_Throw_DataValidException() {
        SkillDto skillDto = new SkillDto(1L, "title");

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        DataValidException dataValidException = assertThrows(DataValidException.class, () -> {
            skillService.create(skillDto);
        });
        assertEquals("Skill already exists", dataValidException.getMessage());

        verify(skillRepository).existsByTitle(skillDto.getTitle());
    }

    @Test
    void getUserSkillsTest() {
        long userId = 1L;
        List<Skill> skills = List.of(
                Skill.builder().title("Skill1").build(),
                Skill.builder().title("Skill2").build()
        );

        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        List<SkillDto> userSkills = skillService.getUserSkills(userId, 1, 2);

        assertEquals(2, userSkills.size());

        verify(skillRepository).findAllByUserId(userId);
    }

    @Test
    void getOfferedSkillsTest() {
        List<SkillOffer> offers = List.of(
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS))
        );

        when(skillOfferRepository.findAllOffersToUser(anyLong())).thenReturn(offers);

        List<SkillCandidateDto> candidates = skillService.getOfferedSkills(anyLong());

        assertNotNull(candidates);
        assertEquals(3, candidates.size());

        verify(skillOfferRepository).findAllOffersToUser(anyLong());
    }

    @Test
    public void testAcquireSkillFromOffers() {
        List<SkillOffer> offers = List.of(
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS))
        );
        Skill skill = mock(Skill.class);
        User user = mock(User.class);
        long skillId = 1L;
        long userId = 2L;

        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        doReturn(skillId).when(skill).getId();
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(user.getId()).thenReturn(userId);
        when(skill.getUsers()).thenReturn(Collections.singletonList(user));

        SkillDto acquiredSkill = skillService.acquireSkillFromOffers(skillId, userId);

        assertNotNull(acquiredSkill);
        assertEquals(skillId, acquiredSkill.getId());

        verify(skillOfferRepository).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        verify(skillRepository).findById(skillId);
        verify(skillOfferRepository, times(3)).deleteById(anyLong());
    }
}