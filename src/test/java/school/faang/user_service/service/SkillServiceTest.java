package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    private SkillDto skillDto;

    private Skill skill;
    @Mock
    private SkillMapper mapper;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testCreate() {
        skillDto = new SkillDto(1L, "Programming");
        skillService.create(skillDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(mapper.toEntity(skillDto));
    }

    @Test
    void testIsExistingSkill() {
        Mockito.when(skillRepository.existsByTitle("Programming")).thenReturn(true);
        skillDto = new SkillDto(1L, "Programming");
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testGetUserSkills() {
        long userId = 1L;
        skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Programming");
        SkillDto skillDto1 = new SkillDto(1L, "Programming");
        Mockito.when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill));
        Mockito.when(mapper.toDto(skill)).thenReturn(skillDto1);
        skillService.getUserSkills(userId);
        Mockito.verify(skillRepository).findAllByUserId(userId);
        List<SkillDto> skills = List.of(skillDto1);
        assertEquals(skills, skillService.getUserSkills(userId));
    }

    @Test
    void testThrown() {
        long userId = 1L;
        Mockito.when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(new Skill()));
        skillService.getUserSkills(userId);
        Mockito.verify(skillRepository).findAllByUserId(userId);
    }

    @Test
    void testGetOfferedSkills() {
        Skill skill1 = Skill.builder()
                .id(1L)
                .title("One")
                .build();
        Skill skill2 = Skill.builder()
                .id(1L)
                .title("One")
                .build();
        SkillDto skillDto1 = new SkillDto(1L, "One");
        SkillDto skillDto2 = new SkillDto(1L, "One");
        SkillCandidateDto skillCandidateDto1 = new SkillCandidateDto(skillDto1, 2L);
        List<Skill> skills = new ArrayList<>();
        skills.add(skill1);
        skills.add(skill2);

        Mockito.when(skillRepository.findAllByUserId(1L)).thenReturn(skills);
        Mockito.when(mapper.toDto(skill1)).thenReturn(skillDto1);
        Mockito.when(mapper.toDto(skill2)).thenReturn(skillDto2);

        skillService.getOfferedSkills(1L);

        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(1L);

        assertEquals(skillCandidateDto1, skillService.getOfferedSkills(1L).get(0));
    }

    @Test
    void testAcquireSkillFromOffers() {
        long skillId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(1L);
        Skill skill1 = Skill.builder()
                .id(1L)
                .title("One")
                .users(List.of(user))
                .build();

        SkillDto skillDto1 = new SkillDto(1L, "One");
        List<SkillOffer> offers = List.of(
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS))
        );

        Mockito.when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        Mockito.when(skillRepository.findById(userId)).thenReturn(Optional.of(skill1));
        Mockito.when(mapper.toDto(skill1)).thenReturn(skillDto1);
        Assertions.assertEquals(skillDto1, skillService.acquireSkillFromOffers(skillId, userId));
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(3)).save(any());
        Mockito.verify(skillRepository, Mockito.times(1)).assignSkillToUser(skillId, userId);
    }

    @Test
    void testThrowsNotEnoughRecommendations() {
        long skillId = 1L;
        long userId = 1L;
        List<SkillOffer> offers = List.of(
                new SkillOffer(), new SkillOffer()
        );

        Mockito.when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(skillId, userId));
        Assertions.assertEquals("Not enough offers to add skill", dataValidationException.getMessage());
    }
}